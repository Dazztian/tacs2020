package com.utn.tacs.dao

import com.mongodb.MongoClient

val mongoClient: MongoClient = MongoClient("172.17.0.2", 27017)

const val DB_MONGO_DATABASE_NAME = "tacs"