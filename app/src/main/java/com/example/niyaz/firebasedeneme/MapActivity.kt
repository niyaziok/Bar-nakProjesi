package com.example.niyaz.firebasedeneme

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_map.*


class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    val ref = FirebaseDatabase.getInstance().reference.child("Problemler")
    var problemId: String? = null
    var problemLat: Double? = null
    var problemLong: Double? = null
    //
    var probidTxt: TextView? = null
    var problemTanim: TextView? = null
    var problemZaman: TextView? = null
    var problemKid: TextView? = null
    var problemresmi: ImageView? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        problemTanim = findViewById(R.id.prbtanımTxt)
        problemZaman = findViewById(R.id.prbzamanTxt)
        problemKid = findViewById(R.id.prbkullaniciidTxt)
        probidTxt = findViewById(R.id.prbidTxt)
        problemresmi = findViewById(R.id.problemresmiIV)



        geriBtn.setOnClickListener {

            val Gintent = Intent(this, AdminProblem::class.java)
            startActivity(Gintent)

        }
        getproblemInformation()
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


    }

    private fun getproblemInformation() {

        problemId = intent.getStringExtra("Id")
        probidTxt?.text = problemId
        ref.child(problemId!!)
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {}
                override fun onDataChange(p0: DataSnapshot) {
                    if (p0.exists()) {
                        var secilenProblem = p0.getValue(ProblemBilgileri::class.java)

                        problemTanim?.text = secilenProblem?.problem.toString()
                        problemKid?.text= secilenProblem?.kullanici_id.toString()
                        problemZaman?.text= secilenProblem?.zaman.toString()
                        Glide.with(this@MapActivity).load(secilenProblem?.sorun_resmi).into(problemresmi!!)



                        var problemLong1 = secilenProblem?.konum_long
                        var problemLat1 = secilenProblem?.konum_lat

                        converter(problemLat1,problemLong1)

                    } else {
                        Toast.makeText(this@MapActivity, "Bir şeyler hatalı.", Toast.LENGTH_LONG).show()
                    }

                }
            })    }

    private fun converter(problemLat1: Double?, problemLong1: Double?) {

        problemLat = problemLat1
        problemLong = problemLong1

        Log.d("Konum","Geldi mi? " + problemLat)

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap


        val problemKonum = LatLng(37.20657462,28.40457635)

        mMap.addMarker(MarkerOptions().position(problemKonum).title(problemId))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(problemKonum, 17.5f))
    }


}
