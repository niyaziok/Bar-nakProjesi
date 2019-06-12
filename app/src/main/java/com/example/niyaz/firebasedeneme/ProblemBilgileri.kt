package com.example.niyaz.firebasedeneme

class ProblemBilgileri {
    var kullanici_id: String? = null
    var sorun_resmi: String? = null
    var problem: String? = null
    var konum_long: Double? = null
    var konum_lat: Double? = null
    var zaman: String? = null

    constructor(kullanici_id: String?, sorun_resmi: String?, problem: String?, konum_long: Double?,konum_lat: Double?,zaman: String?) {
        this.kullanici_id = kullanici_id
        this.sorun_resmi = sorun_resmi
        this.problem = problem
        this.konum_long = konum_long
        this.konum_lat = konum_lat
        this.zaman = zaman
    }

    constructor() {}
}
