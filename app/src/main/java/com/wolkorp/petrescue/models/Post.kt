package com.wolkorp.petrescue.models

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.Timestamp
import java.util.*
import kotlin.collections.ArrayList


class Post(val id : String ,val nombreUsuario: String,
           val hora: Timestamp, val texto: String,
           val urlImg : String, val categoria : String,
           val idUsuario: String, val activo: Boolean): Parcelable {

    constructor() : this("","", Timestamp(Date()),"","","","", true)

    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readParcelable(Timestamp::class.java.classLoader)!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readByte() != 0.toByte()

    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(nombreUsuario)
        parcel.writeParcelable(hora, flags)
        parcel.writeString(texto)
        parcel.writeString(urlImg)
        parcel.writeString(categoria)
        parcel.writeString(idUsuario)
        parcel.writeByte(if (activo) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Post> {
        override fun createFromParcel(parcel: Parcel): Post {
            return Post(parcel)
        }

        override fun newArray(size: Int): Array<Post?> {
            return arrayOfNulls(size)
        }
    }
}


