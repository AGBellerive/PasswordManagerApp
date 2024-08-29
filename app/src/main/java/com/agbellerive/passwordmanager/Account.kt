package com.agbellerive.passwordmanager

import kotlinx.serialization.Serializable

@Serializable
//This allows the class to have JSON deserialize into an object
class Account(
    var Site: String, var Username: String, var Email: String, var Password: String, var Other: String
) {

}