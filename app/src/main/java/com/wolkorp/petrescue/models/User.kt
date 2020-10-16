package com.wolkorp.petrescue.models


class User(userName: String?, email: String?,  id: String?, pais : String?, phoneNumber : String?){
    var userName: String = ""
    var email: String = ""
    var id: String =""
    var pais: String = ""
    var phoneNumber = ""


    constructor() : this("","","","","")

    init {
        this.userName = userName!!
        this.email= email!!
        this.id=id!!
        this.pais=pais!!
        this.phoneNumber=phoneNumber!!
    }

}