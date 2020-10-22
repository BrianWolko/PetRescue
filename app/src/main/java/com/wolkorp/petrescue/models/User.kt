package com.wolkorp.petrescue.models

import com.google.firebase.Timestamp
import java.util.*


class User( val uid: String, val userName: String, val userLastName: String, val email: String, val pais : String, val phoneNumber : String, val profileImageUrl:String) {

    constructor() : this("", "","","","","", "")

}
