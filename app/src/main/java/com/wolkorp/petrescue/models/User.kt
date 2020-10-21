package com.wolkorp.petrescue.models


class User(userName: String?, email: String?,  uid: String?, pais : String?, phoneNumber : String?, profileImageUrl:String?){
    var userName: String = ""
    var email: String = ""
    var uid: String =""
    var pais: String = ""
    var phoneNumber = ""
    var profileImageUrl = ""


    constructor() : this("","","","","","")

    init {
        this.userName = userName!!
        this.email= email!!
        this.uid=uid!!
        this.pais=pais!!
        this.phoneNumber=phoneNumber!!
        this.profileImageUrl=profileImageUrl!!
    }

}