package com.wolkorp.petrescue.models


class Post(val nombreUsuario: String, val hora: String, val texto: String, val urlImg : String, val categoria : String, val idUsuario: String) {


    constructor() : this("","","","","","")

}