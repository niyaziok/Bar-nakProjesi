package com.example.niyaz.firebasedeneme

class ProblemBilgileri {
    var kullanici_id: String? = null
    var sorun_resmi: String? = null
    var problem: String? = null
    var konum: String? = null
    var zaman: String? = null

    constructor(kullanici_id: String?, sorun_resmi: String?, problem: String?, konum: String?,zaman: String?) {
        this.kullanici_id = kullanici_id
        this.sorun_resmi = sorun_resmi
        this.problem = problem
        this.konum = konum
        this.zaman = zaman
    }

    constructor() {}
}
