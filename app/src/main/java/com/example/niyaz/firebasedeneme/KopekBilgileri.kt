package com.example.niyaz.firebasedeneme
class KopekBilgileri {

    var kupeno: String? = null
    var cins: String? = null
    var cinsiyet: String? = null
    var renk: String? = null
    var kısırlastırma: String? = null
    var asıtarihi: String? = null

    constructor(kupe_no: String,cins: String,cinsiyet: String,renk: String,kısırlastırma: String,asıtarihi: String){

        this.kupeno = kupe_no
        this.cins = cins
        this.cinsiyet = cinsiyet
        this.renk = renk
        this.kısırlastırma = kısırlastırma
        this.asıtarihi = asıtarihi
    }

    constructor(){}

}
