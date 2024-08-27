package com.agbellerive.passwordmanager

import kotlinx.serialization.Serializable

@Serializable
class Account(
    private var Site: String,
    private var Username: String,
    private var Email: String,
    private var Password: String,
    private var Others: String
) {
}