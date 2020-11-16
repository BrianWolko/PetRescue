package com.wolkorp.petrescue.models

import com.google.firebase.Timestamp
import java.util.*
import kotlin.collections.ArrayList

class Comentario(val texto : String, val id : String, val idUsuario : String,val hora :Timestamp) {

    constructor() : this("","","",Timestamp(Date()))

}