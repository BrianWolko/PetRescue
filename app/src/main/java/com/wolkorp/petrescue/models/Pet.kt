package com.wolkorp.petrescue.models

 class Pet(val descripcion: String, val latidud: Double, val longitud: Double, val fecha: String, val hora: String, val imageURL: String) {

     constructor() : this("",0.0,0.0,"","","")

 }