package com.utn.tacs.utils

import com.utn.tacs.User
import org.litote.kmongo.newId

object GoogleOauthDataParser {

    fun parse(data: Map<String, Any?>): User {
        return User(newId(), data["name"] as String, data["email"] as String, null, false)
    }
}