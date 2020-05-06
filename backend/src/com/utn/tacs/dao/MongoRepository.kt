package com.utn.tacs.dao

import com.mongodb.MongoClient
import com.mongodb.client.MongoDatabase

val mongoClient: MongoClient = MongoClient("172.21.0.3", 27017)
val db: MongoDatabase = mongoClient.getDatabase("tacs")
