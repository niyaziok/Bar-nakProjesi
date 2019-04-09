package com.example.niyaz.firebasedeneme

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
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
    private var tanimaBtn: Button? = null
    private var kuperesmi: ImageView? = null
    private var bilgi: TextView? = null
    private var imageBitmap: Bitmap? = null

    var kopek = KopekBilgileri()
    var kopekbilgi = BilgiFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recognition)

        fotoBtn = findViewById(R.id.ftgrafcekBtn)
        tanimaBtn = findViewById(R.id.tanıBtn)
        kuperesmi = findViewById(R.id.köpekResmi)
        bilgi = findViewById(R.id.bilgiText)

        fotoBtn!!.setOnClickListener { dispatchTakePictureIntent() }
        tanimaBtn!!.setOnClickListener { detectTxt() }

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
            kuperesmi!!.setImageBitmap(imageBitmap)
        }
    }

    private fun showFragment(){
        kopekbilgi.show(supportFragmentManager, "Kopek Bilgileri")
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



            val information = FirebaseDatabase.getInstance().reference
            val check=information.child("köpek").orderByKey().equalTo(txt)

                check.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(p0: DataSnapshot) {

                        if (p0.hasChildren()) {

                            for (singleSnapShot in p0.children) {
                                val okunankupe = singleSnapShot.getValue(KopekBilgileri::class.java)

                                kopekbilgi.kupeno?.text = okunankupe?.kupe_no.toString()
                                kopekbilgi.cins?.text = okunankupe?.cins
                                kopekbilgi.cinsiyet?.text = okunankupe?.cinsiyet
                                kopekbilgi.renk?.text = okunankupe?.renk
                                kopekbilgi.kisirlastirmatar?.text = okunankupe?.kisirlastirma_tarihi
                                kopekbilgi.asitar?.text = okunankupe?.asi_tarihi

                            }

                            showFragment()

                        }
                        else{
                            Toast.makeText(this@RecognitionActivity, "Köpek Sisteme Kayıtlı Değil", Toast.LENGTH_LONG).show()
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

