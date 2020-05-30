package com.utn.tacs.exception

class UnAuthorizedException(message: String? = "Unauthorized request") : Exception(message)

class UserAlreadyExistsException(message: String? = "User already exists") : Exception(message)