package com.example.niyaz.firebasedeneme

import android.support.v7.app.AppCompatActivity

class KopekBilgileri : AppCompatActivity {

    var kupe_no: Int? = null
    var cins: String? = null
    var cinsiyet: String? = null
    var renk: String? = null
    var kisirlastirma_tarihi: String? = null
    var asi_tarihi: String? = null

    constructor(kupe_no: Int, cins: String,cinsiyet: String,renk: String,kisirlastirma_tarihi: String,asi_tarihi: String){

        this.kupe_no = kupe_no
        this.cins = cins
        this.cinsiyet = cinsiyet
        this.renk = renk
        this.kisirlastirma_tarihi = kisirlastirma_tarihi
        this.asi_tarihi = asi_tarihi
    }

    constructor(){}

}
