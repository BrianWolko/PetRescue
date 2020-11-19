package com.wolkorp.petrescue.models


import com.google.firebase.Timestamp
import java.util.*
import kotlin.collections.ArrayList

class ComentarioFull(val texto : String, val idUsuario : String,val hora :Timestamp,val userName : String, val userImageLink : String, val id : String) {

    constructor() : this("","",Timestamp(Date()),"","","")

}