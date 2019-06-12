package com.example.niyaz.firebasedeneme

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bilgiBtn.setOnClickListener {

            val Bintent = Intent(this, RecognitionActivity::class.java)
            startActivity(Bintent)

        }

        problemBtn.setOnClickListener {

            val Sintent = Intent(this, ProblemActivity::class.java)
            startActivity(Sintent)

        }

        adminproblemBtn.setOnClickListener {
            val Apintent = Intent (this, AdminProblem::class.java)
            startActivity(Apintent)
        }
    }
}
