package com.example.niyaz.firebasedeneme

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.*
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import java.io.ByteArrayOutputStream
import java.text.DateFormat
import java.util.*

class ProblemActivity : AppCompatActivity() {

    private var problemfotografiBtn: ImageButton? = null
    private var imageBitmap: Bitmap? = null
    private var problemResmi: ImageView? = null
    private var problembildirBtn: Button? = null
    private var problemTxt: EditText? = null

    var photoUrl: String? = null
    var veritabaninagonder = ProblemBilgileri()
    var problemId: String? = "P1"


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
        .child("Fotograflar/Kullanıcı Problem : "+ problemId)

        val uploadPhoto = storageReference.putBytes(file!!)

        uploadPhoto.addOnFailureListener(OnFailureListener {

        }).addOnSuccessListener(OnSuccessListener<UploadTask.TaskSnapshot> {


            var urlTask = uploadPhoto.continueWithTask(object : Continuation<UploadTask.TaskSnapshot, Task<Uri>> {

                override fun then(task: Task<UploadTask.TaskSnapshot>): Task<Uri> {

                    if (!task.isSuccessful()) {


                        throw task.getException()!!;

                    }


                    return storageReference.getDownloadUrl();

                }

            }).addOnCompleteListener {

                if (it.isSuccessful) {

                    photoUrl= it.result.toString()


                    FirebaseDatabase.getInstance().reference.child("Problemler").child(problemId.toString())
                        .child("sorun_resmi").setValue(photoUrl)
                }

            }

            })

        var intent=Intent(this,MainActivity::class.java)
        startActivity(intent)
        finish()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_problem)

        problemfotografiBtn = findViewById(R.id.problemfotoBtn)
        problemResmi = findViewById(R.id.problemResmi)
        problembildirBtn = findViewById(R.id.problembildirBtn)
        problemTxt = findViewById(R.id.problemTxt)

        val sorgu =  FirebaseDatabase.getInstance().reference.child("Problemler")
        sorgu.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {

                if (p0.hasChildren()){
                    val problemNumber = p0.childrenCount + 1
                    problemId = "P" + FirebaseAuth.getInstance().currentUser?.uid +"_"+problemNumber.toString()
                    Log.d("Problem","ProblemId: " + problemNumber)
                }else{
                    problemId = "P" + FirebaseAuth.getInstance().currentUser?.uid + "_1"
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }

        })

        problemfotografiBtn!!.setOnClickListener { takeproblemPicture() }
        problembildirBtn!!.setOnClickListener { sendtoDatabase()}


    }


    private fun takeproblemPicture() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val extras = data.extras
            imageBitmap = extras!!.get("data") as Bitmap
            problemResmi!!.setImageBitmap(imageBitmap)
        }

    }

    private fun sendtoDatabase() {


        if (imageBitmap != null && problemTxt!!.text.isNotEmpty()){

            val calendar = Calendar.getInstance()
            val realdate = DateFormat.getDateTimeInstance().format(calendar.time)


            veritabaninagonder.kullanici_id = FirebaseAuth.getInstance().currentUser?.uid
            veritabaninagonder.problem = problemTxt?.text.toString()
            veritabaninagonder.sorun_resmi = "url"
            veritabaninagonder.zaman = realdate


            FirebaseDatabase.getInstance().reference.child("Problemler")
                .child(problemId.toString())
                .setValue(veritabaninagonder).addOnCompleteListener { task ->

                    if (task.isSuccessful) {
                        Toast.makeText(this@ProblemActivity, "Problem Başarıyla Bildirildi ", Toast.LENGTH_SHORT).show()

                    }else{
                        Toast.makeText(this@ProblemActivity, "Bir Hata Oluştu ", Toast.LENGTH_SHORT).show()

                    }

                }

        }else{
            Toast.makeText(this@ProblemActivity, "Boş alan bırakmayınız.", Toast.LENGTH_SHORT).show()
        }

        photoCompressed(imageBitmap!!)

    }

    private fun photoCompressed(imageBitmap: Bitmap) {
        val compressed = CompressPhotos(imageBitmap)
        compressed.execute()

    }


    companion object {
            internal val REQUEST_IMAGE_CAPTURE = 1
        }

}
