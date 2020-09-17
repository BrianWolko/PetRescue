package com.wolkorp.petrescue.models
import android.os.Parcel
import android.os.Parcelable


class Post(nombre: String?, hora: String?, texto: String?, id: String?) {
    var nombre: String = ""
    var hora: String = ""
    var texto: String = ""
    var id: String =""

    init {
        this.nombre = nombre!!
        this.hora= hora!!
        this.texto=texto!!
        this.id=id!!
    }


}