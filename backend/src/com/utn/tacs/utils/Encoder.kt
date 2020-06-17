package com.utn.tacs.utils

import org.mindrot.jbcrypt.BCrypt

object Encoder {
    fun encode(string: String) : String = BCrypt.hashpw(string, BCrypt.gensalt())
    fun matches(string: String, encoded: String?) : Boolean = BCrypt.checkpw(string, encoded)
}