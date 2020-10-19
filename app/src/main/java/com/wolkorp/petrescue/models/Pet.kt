package com.wolkorp.petrescue.models

import android.os.Parcel
import android.os.Parcelable

data class Pet(val descripcion: String, val latidud: Double, val longitud: Double, val fecha: String, val hora: String, val imageURL: String) : Parcelable {


    constructor() : this("", 0.0,0.0,"","", "")



    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(descripcion)
        parcel.writeDouble(latidud)
        parcel.writeDouble(longitud)
        parcel.writeString(fecha)
        parcel.writeString(hora)
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