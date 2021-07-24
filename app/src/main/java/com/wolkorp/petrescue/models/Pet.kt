package com.wolkorp.petrescue.models

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint
import java.util.*

class Pet(val descripcion: String, val latitud: Double, val longitud: Double, val fecha: Timestamp, val imageURL: String, val id: String, val idUsuario: String, val activo: Boolean) : Parcelable {

    constructor() : this("", 0.0,0.0, Timestamp(Date()), "", "", "", true)


    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readDouble()!!,
        parcel.readDouble()!!,
        parcel.readParcelable(Timestamp::class.java.classLoader)!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readByte() != 0.toByte()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(descripcion)
        parcel.writeDouble(latitud)
        parcel.writeDouble(longitud)
        parcel.writeParcelable(fecha, flags)
        parcel.writeString(imageURL)
        parcel.writeString(id)
        parcel.writeString(idUsuario)
        parcel.writeByte(if (activo) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Pet> {
        override fun createFromParcel(parcel: Parcel): Pet {
            return Pet(parcel)
        }

        override fun newArray(size: Int): Array<Pet?> {
            return arrayOfNulls(size)
        }
    }


}

