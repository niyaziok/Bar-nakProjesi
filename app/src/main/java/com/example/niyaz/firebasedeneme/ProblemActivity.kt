package com.example.niyaz.firebasedeneme

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.widget.*
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import java.io.ByteArrayOutputStream

class ProblemActivity : AppCompatActivity() {

    private var problemfotografiBtn: ImageButton? = null
    private var imageBitmap: Bitmap? = null
    private var problemResmi: ImageView? = null
    private var problembildirBtn: Button? = null
    private var problemTxt: EditText? = null

    var photoUrl: String? = null
    var veritabaninagonder = ProblemBilgileri()


    inner class CompressPhotos : AsyncTask<Uri, Double, ByteArray?>{

        var myBitmap:Bitmap? = null

        constructor(bm:Bitmap){
            if (bm != null){
                myBitmap = bm
            }
        }

        override fun onPreExecute() {
            super.onPreExecute()
        }

        override fun doInBackground(vararg params: Uri?): ByteArray? {
            var photoBytes:ByteArray? = null
            for (i in 1..5){
                photoBytes = convertBitmaptoBytes(myBitmap,100/i)
                publishProgress(photoBytes!!.size.toDouble())
            }
            return photoBytes
        }

        private fun convertBitmaptoBytes(myBitmap: Bitmap?, i: Int): ByteArray? {
            var stream = ByteArrayOutputStream()
            myBitmap?.compress(Bitmap.CompressFormat.JPEG, i, stream)
            return stream.toByteArray()

        }

        override fun onProgressUpdate(vararg values: Double?) {
            super.onProgressUpdate(*values)

        }

        override fun onPostExecute(result: ByteArray?) {
            super.onPostExecute(result)
            uploadphotointoFireBase(result)
        }



    }

    private fun uploadphotointoFireBase(result: ByteArray?) {

        val file = result

        val storageReference = FirebaseStorage.getInstance().getReference()
        .child("Fotograflar/Kullanıcı Problem : "+FirebaseAuth.getInstance().currentUser?.uid+ "Problem 1")

        val uploadPhoto = storageReference.putBytes(result!!)

        uploadPhoto.addOnFailureListener(OnFailureListener {

        }).addOnSuccessListener(OnSuccessListener<UploadTask.TaskSnapshot> {


            var urlTask = uploadPhoto.continueWithTask(object : Continuation<UploadTask.TaskSnapshot, Task<Uri>> {

                override fun then(task: Task<UploadTask.TaskSnapshot>): Task<Uri> {

                    if (!task.isSuccessful()) {


                        throw task.getException()!!;

                    }

                    // Continue with the task to get the download URL

                    return storageReference.getDownloadUrl();

                }

            }).addOnCompleteListener {

                if (it.isSuccessful) {

                    photoUrl= it.result.toString()
                    
                }
            }

            })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_problem)

        problemfotografiBtn = findViewById(R.id.problemfotoBtn)
        problemResmi = findViewById(R.id.problemResmi)
        problembildirBtn = findViewById(R.id.problembildirBtn)
        problemTxt = findViewById(R.id.problemTxt)


        problemfotografiBtn!!.setOnClickListener { takeproblemPicture() }
        problembildirBtn!!.setOnClickListener { sendtoDatabase(photoUrl)}


    }

    private fun takeproblemPicture() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(takePictureIntent, ProblemActivity.REQUEST_IMAGE_CAPTURE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (requestCode == ProblemActivity.REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val extras = data.extras
            imageBitmap = extras!!.get("data") as Bitmap
            problemResmi!!.setImageBitmap(imageBitmap)
        }

    }

    private fun sendtoDatabase(Url: String?) {

            veritabaninagonder.kullanici_id = FirebaseAuth.getInstance().currentUser?.uid
            veritabaninagonder.problem = problemTxt?.text.toString()
            veritabaninagonder.sorun_resmi = Url

        if (imageBitmap != null){
            
            photoCompressed(imageBitmap!!)

        }else{
            Toast.makeText(this@ProblemActivity, "Bilgiler hatalı.Kontrol ediniz.", Toast.LENGTH_SHORT).show()
        }


        FirebaseDatabase.getInstance().reference.child("Problemler")
            .child("12345")
            .setValue(veritabaninagonder).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this@ProblemActivity, "Problem Başarıyla Bildirildi ", Toast.LENGTH_SHORT).show()

                }else{
                    Toast.makeText(this@ProblemActivity, "Bir Hata Oluştu ", Toast.LENGTH_SHORT).show()

                }

            }

    }

    private fun photoCompressed(imageBitmap: Bitmap) {
        val compressed = CompressPhotos(imageBitmap)
        val uri: Uri? = null
        compressed.execute(uri)

    }


    companion object {
            internal val REQUEST_IMAGE_CAPTURE = 1
        }

}
