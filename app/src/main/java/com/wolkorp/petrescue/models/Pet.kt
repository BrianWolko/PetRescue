package com.wolkorp.petrescue.models

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint
import java.util.*

data class Pet(val descripcion: String, val latitud: Double, val longitud: Double, val fecha: Timestamp, val imageURL: String) : Parcelable {



    constructor() : this("", 0.0,0.0, Timestamp(Date()), "")



    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readParcelable(Timestamp::class.java.classLoader)!!,
        parcel.readString()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(descripcion)
        parcel.writeDouble(latitud)
        parcel.writeDouble(longitud)
        parcel.writeParcelable(fecha, flags)
        parcel.writeString(imageURL)
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

