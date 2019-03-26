package com.example.niyaz.firebasedeneme

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_register.*


class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

            txtlogin.setOnClickListener {
                val Lintent = Intent(this, LoginActivity::class.java)
                startActivity(Lintent)
            }

            registerBtn.setOnClickListener{

                if(txtemail.text.isNotEmpty() && txtpw.text.isNotEmpty() && txtpwagain.text.isNotEmpty()){
                    if(txtpw.text.toString().equals(txtpwagain.text.toString())){

                        newUser(txtemail.text.toString(),txtpw.text.toString())


                    }else{
                        Toast.makeText(this,"Password Doesn't Match",Toast.LENGTH_SHORT).show()
                    }


                }else{
                    Toast.makeText(this,"Fill Empty Spaces!", Toast.LENGTH_SHORT).show()
                }

        }


    }

    private fun newUser(email: String, password: String) {

        showProgressBar()

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener(object : OnCompleteListener<AuthResult>{
                override fun onComplete(p0: Task<AuthResult>) {
                   if(p0.isSuccessful) {
                       Toast.makeText(this@RegisterActivity, "Successfully Registered!", Toast.LENGTH_SHORT).show()
                       sendMail()
                       FirebaseAuth.getInstance().signOut()
                   }else{
                       Toast.makeText(this@RegisterActivity, "Register Failed!" +p0.exception?.message, Toast.LENGTH_SHORT).show()

                   }
                }


            })

        hideProgressBar()
    }
    private fun sendMail(){
        Log.e("Hata burda","HATA")
        var user =FirebaseAuth.getInstance().currentUser

        if(user != null) {

                user.sendEmailVerification().
                    addOnCompleteListener(object : OnCompleteListener<Void> {
                        override fun onComplete(p0: Task<Void>) {

                            if (p0.isSuccessful) {
                            Toast.makeText(this@RegisterActivity, "Check Your Email Address!", Toast.LENGTH_SHORT).show()

                            } else {
                            Toast.makeText(this@RegisterActivity, "Error  :" + p0.exception?.message, Toast.LENGTH_SHORT).show()
                        }


                    }
                })

            }else{
            Toast.makeText(this@RegisterActivity, "Hata", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showProgressBar(){
        progressBar.visibility = View.VISIBLE

    }

    private fun hideProgressBar(){
        progressBar.visibility = View.INVISIBLE

    }
}
