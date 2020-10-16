package com.wolkorp.petrescue.models


class Post(nombre: String?, hora: String?, texto: String?, id: String?, urlImg : String?,categoria : String?,activo : Boolean){
    var nombre: String = ""
    var hora: String = ""
    var texto: String = ""
    var id: String =""
    var urlImg: String = ""
    var categoria : String = ""
    var activo: Boolean = true

    constructor() : this("","","","","","",true)

    init {
        this.nombre = nombre!!
        this.hora= hora!!
        this.texto=texto!!
        this.id=id!!
        this.urlImg=urlImg!!
        this.categoria=categoria!!
        this.activo=activo!!
    }

}