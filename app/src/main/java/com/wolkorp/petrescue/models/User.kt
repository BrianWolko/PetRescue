package com.wolkorp.petrescue.models

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.Timestamp
import java.util.*


class User( val uid: String, val userName: String, val userLastName: String, val email: String, val pais : String, val phoneNumber : String, val profileImageUrl:String):
    Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    ) {
    }

    constructor() : this("", "","","","","", "")

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(uid)
        parcel.writeString(userName)
        parcel.writeString(userLastName)
        parcel.writeString(email)
        parcel.writeString(pais)
        parcel.writeString(phoneNumber)
        parcel.writeString(profileImageUrl)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }

}
