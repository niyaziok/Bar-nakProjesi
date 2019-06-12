package com.example.niyaz.firebasedeneme

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AdminProblem : AppCompatActivity() {

    val ref = FirebaseDatabase.getInstance().reference.child("Problemler")
    val problemList:ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_problem)





        ref.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()){

                    for (p in p0.children){

                        problemList.add(p.key.toString())
                    }
                }
                createList(problemList)

            }

        })
    }
    private fun createList(problemList: ArrayList<String>) {
        val prbAdapter = ArrayAdapter(this,R.layout.problemlayout, this.problemList)
        val prbListView: ListView = findViewById(R.id.problemListView)

        prbListView.setAdapter(prbAdapter)
        prbListView.onItemClickListener = object : AdapterView.OnItemClickListener{
            override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                val prbId = prbListView.getItemAtPosition(position) as String
                adminchooseProblem(prbId)


            }

        }

    }

    private fun adminchooseProblem(prbId: String) {

        val PDintent = Intent(this, MapActivity::class.java)
        PDintent.putExtra("Id",prbId)
        startActivity(PDintent)







    }

}
