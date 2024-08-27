package com.agbellerive.passwordmanager

import kotlinx.serialization.Serializable

@Serializable
class Account(
    var Site: String, var Username: String, var Email: String, var Password: String, var Others: String
) {

}