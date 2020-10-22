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
/* Asi era el modelo usuario en la branch master agregar al nuevo modelo el campo userLastName y asegurarse que estan en el 
   correcto orden en el constructor
data class User(val uid: String, val userName: String, val userLastName: String, val email: String, val phoneNumber: String ,val profileImageUrl: String)
*/
