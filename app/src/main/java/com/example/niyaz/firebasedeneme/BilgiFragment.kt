package com.example.niyaz.firebasedeneme


import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView


//private const val ARG_String = "String"
//private const val ARG_Number = "Number"

class BilgiFragment : DialogFragment() {

    var kupeno: TextView? = null
    var cins: TextView? = null
    var renk: TextView? = null
    var asitar: TextView? = null
    var cinsiyet: TextView? = null
    var kisirlastirmatar: TextView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        val view = inflater.inflate(R.layout.fragment_bilgi, container, false)

        kupeno = view?.findViewById(R.id.küpenoTxt)
        cins = view?.findViewById(R.id.cinsTxt)
        renk = view?.findViewById(R.id.renkTxt)
        asitar = view?.findViewById(R.id.asıtarihiTxt)
        cinsiyet = view?.findViewById(R.id.cinsiyetTxt)
        kisirlastirmatar = view?.findViewById(R.id.kısırlasmatarihiTxt)


        return view

    }


}
