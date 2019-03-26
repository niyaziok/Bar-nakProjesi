package com.example.niyaz.firebasedeneme

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity() {

    lateinit var mAuthStateListener: FirebaseAuth.AuthStateListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        initMyAuthStateListener()

        txtRegister.setOnClickListener {
            val Rintent = Intent(this, RegisterActivity::class.java)
            startActivity(Rintent)
        }

        loginBtn.setOnClickListener {

            if (emailText.text.isNotEmpty() && passwordText.text.isNotEmpty()) {
                showProgressBar()

                FirebaseAuth.getInstance()
                    .signInWithEmailAndPassword(emailText.text.toString(), passwordText.text.toString())
                    .addOnCompleteListener(object : OnCompleteListener<AuthResult> {
                        override fun onComplete(p0: Task<AuthResult>) {

                            if (p0.isSuccessful) {
                                hideProgressBar()
                                Toast.makeText(
                                    this@LoginActivity, "Successfull Login. Welcome  "
                                            + FirebaseAuth.getInstance().currentUser?.email, Toast.LENGTH_SHORT
                                ).show()


                            } else {
                                hideProgressBar()
                                Toast.makeText(
                                    this@LoginActivity, "Try Again!  "
                                            + p0.exception?.message, Toast.LENGTH_SHORT
                                ).show()


                            }
                        }
                    })

            } else {
                Toast.makeText(this@LoginActivity, "Fill Empty Spaces!", Toast.LENGTH_SHORT).show()

            }
        }

    }

    private fun showProgressBar() {
        progressBarLog.visibility = View.VISIBLE

    }

    private fun hideProgressBar() {
        progressBarLog.visibility = View.INVISIBLE

    }

    private fun initMyAuthStateListener() {

        mAuthStateListener = object : FirebaseAuth.AuthStateListener {

            override fun onAuthStateChanged(p0: FirebaseAuth) {
                var user = p0.currentUser

                if (user != null) {

                    if (user.isEmailVerified) {
                        var intent = Intent(this@LoginActivity,MainActivity::class.java)
                        startActivity(intent)
                        finish()

                    }else {
                        Toast.makeText(this@LoginActivity, "Verify Your Email Address!", Toast.LENGTH_SHORT).show()

                    }
                }
            }
            }
        }

        override fun onStart() {
            super.onStart()
            FirebaseAuth.getInstance().addAuthStateListener(mAuthStateListener)
        }

        override fun onStop() {
            super.onStop()
            FirebaseAuth.getInstance().addAuthStateListener(mAuthStateListener)
        }

    }


