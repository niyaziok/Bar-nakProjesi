package com.example.niyaz.firebasedeneme

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.text.FirebaseVisionText

class RecognitionActivity : AppCompatActivity() {

    private var fotoBtn: Button? = null
    private var tanımaBtn: Button? = null
    private var küperesmi: ImageView? = null
    private var bilgi: TextView? = null
    private var imageBitmap: Bitmap? = null

    var asdasd=KopekBilgileri()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recognition)
        fotoBtn = findViewById(R.id.ftgrafcekBtn)
        tanımaBtn = findViewById(R.id.tanıBtn)
        küperesmi = findViewById(R.id.köpekResmi)
        bilgi = findViewById(R.id.bilgiText)


        fotoBtn!!.setOnClickListener { dispatchTakePictureIntent() }
        tanımaBtn!!.setOnClickListener { detectTxt() }
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val extras = data.extras
            imageBitmap = extras!!.get("data") as Bitmap
            küperesmi!!.setImageBitmap(imageBitmap)
        }
    }

    private fun detectTxt() {
        val image = FirebaseVisionImage.fromBitmap(imageBitmap!!)
        val detector = FirebaseVision.getInstance().onDeviceTextRecognizer
        detector.processImage(image).addOnSuccessListener { firebaseVisionText -> processTxt(firebaseVisionText) }
            .addOnFailureListener { }
    }

    private fun processTxt(text: FirebaseVisionText) {
        val blocks = text.textBlocks
        if (blocks.size == 0) {
            Toast.makeText(this@RecognitionActivity, "No Text :(", Toast.LENGTH_LONG).show()
            return
        }
        for (block in text.textBlocks) {
            val txt = block.text
            bilgi!!.textSize = 24f
            bilgi!!.text = txt

            var köpekbilgigöster = BilgiFragment()
            köpekbilgigöster.show(supportFragmentManager,"Hello")

            //
            var information= FirebaseDatabase.getInstance().reference
            var check=information.child("köpek").orderByKey().equalTo(txt)
            check.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    Log.e("Hata","HATA BURADA")
                    for(singleSnapShot in p0.children){
                        var okunanküpe = singleSnapShot.getValue(KopekBilgileri::class.java)

                    }
                }

                override fun onCancelled(p0: DatabaseError) {

                }

            })

        }
    }

    companion object {

        internal val REQUEST_IMAGE_CAPTURE = 1
    }

    }

