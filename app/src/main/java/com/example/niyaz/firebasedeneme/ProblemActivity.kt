package com.example.niyaz.firebasedeneme

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
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

private const val PERMISSION_REQUEST = 10

class ProblemActivity : AppCompatActivity() {
    //location
    lateinit var locationManager: LocationManager
    private var hasGps = false
    private var hasNetwork = false
    private var locationGps: Location? = null
    private var locationNetwork: Location? = null
    private var permissions = arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION)

    private var problemfotografiBtn: ImageButton? = null
    private var imageBitmap: Bitmap? = null
    private var problemResmi: ImageView? = null
    private var problembildirBtn: Button? = null
    private var problemTxt: EditText? = null

    var photoUrl: String? = null
    var konumlat: Double? = null
    var konumlong: Double? = null
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
        .child("Problem Fotografları/Kullanıcı Problem : "+ problemId)

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

        val Fintent=Intent(this,MainActivity::class.java)
        startActivity(Fintent)
        finish()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_problem)
        disableView()

        problemfotografiBtn = findViewById(R.id.problemfotoBtn)
        problemResmi = findViewById(R.id.problemResmi)
        problembildirBtn = findViewById(R.id.problembildirBtn)
        problemTxt = findViewById(R.id.problemTxt)




        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkPermission(permissions)) {
                enableView()
            } else {
                requestPermissions(permissions, PERMISSION_REQUEST)
            }
        } else {
            enableView()
        }


        val check =  FirebaseDatabase.getInstance().reference.child("Problemler")
        check.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {

                if (p0.hasChildren()){
                    val problemNumber = p0.childrenCount + 1
                    problemId = "Prb_" + FirebaseAuth.getInstance().currentUser?.uid?.take(8) +"_"+problemNumber.toString()
                    Log.d("Problem","ProblemId: " + problemNumber)
                }else{
                    problemId = "Prb_" + FirebaseAuth.getInstance().currentUser?.uid?.take(8) + "_1"
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }

        })

        problemfotografiBtn!!.setOnClickListener { takeproblemPicture() }
        problembildirBtn!!.setOnClickListener {sendtoDatabase()}



    }

    private fun enableView() {
        problembildirBtn?.isEnabled = true
        problembildirBtn?.alpha = 1F

        Toast.makeText(this, "İzin Verildi", Toast.LENGTH_SHORT).show()

    }


    private fun disableView() {
        problembildirBtn?.isEnabled = false
        problembildirBtn?.alpha = 0.5F

    }
    @SuppressLint("MissingPermission")

    private fun getLocation() {

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        hasGps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        hasNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        if (hasGps || hasNetwork) {

            if (hasGps) {

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0F, object :
                    LocationListener {
                    override fun onLocationChanged(location: Location?) {
                        if (location != null) {
                            locationGps = location

                        }
                    }

                    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

                    }

                    override fun onProviderEnabled(provider: String?) {

                    }

                    override fun onProviderDisabled(provider: String?) {

                    }

                })

                val localGpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                if (localGpsLocation != null)
                    locationGps = localGpsLocation
            }
            if (hasNetwork) {

                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0F, object :
                    LocationListener {
                    override fun onLocationChanged(location: Location?) {
                        if (location != null) {
                            locationNetwork = location

                        }
                    }

                    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

                    }

                    override fun onProviderEnabled(provider: String?) {

                    }

                    override fun onProviderDisabled(provider: String?) {

                    }

                })

                val localNetworkLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                if (localNetworkLocation != null)
                    locationNetwork = localNetworkLocation
            }

            if(locationGps!= null && locationNetwork!= null){

                if(locationGps!!.accuracy > locationNetwork!!.accuracy){

                    konumlat = locationGps!!.latitude
                    konumlong = locationGps!!.longitude


                }else{

                    konumlat = locationNetwork!!.latitude
                    konumlong = locationNetwork!!.longitude

                }
            }

        } else {
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
        }


    }



    private fun takeproblemPicture() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val extras = data?.extras
            imageBitmap = extras!!.get("data") as Bitmap
            problemResmi!!.setImageBitmap(imageBitmap)
        }

    }

    private fun sendtoDatabase() {


        if (imageBitmap != null && problemTxt!!.text.isNotEmpty()){

            getLocation()
            val calendar = Calendar.getInstance()
            val realdate = DateFormat.getDateTimeInstance().format(calendar.time)


            veritabaninagonder.kullanici_id = FirebaseAuth.getInstance().currentUser?.uid
            veritabaninagonder.problem = problemTxt?.text.toString()
            veritabaninagonder.sorun_resmi = "url"
            veritabaninagonder.konum_long = konumlong
            veritabaninagonder.konum_lat = konumlat
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

    private fun checkPermission(permissionArray: Array<String>): Boolean {
        var allSuccess = true
        for (i in permissionArray.indices) {
            if (checkCallingOrSelfPermission(permissionArray[i]) == PackageManager.PERMISSION_DENIED)
                allSuccess = false
        }
        return allSuccess
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST) {
            var allSuccess = true
            for (i in permissions.indices) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    allSuccess = false
                    val requestAgain = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && shouldShowRequestPermissionRationale(permissions[i])
                    if (requestAgain) {
                        Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Go to settings and enable the permission", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            if (allSuccess)
                enableView()

        }
    }

}
