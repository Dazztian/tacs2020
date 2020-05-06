package com.utn.tacs.utils

import com.mongodb.MongoClient
import com.typesafe.config.ConfigFactory


object MongoClientGenerator {
    private val url:String = ConfigFactory.load().getString("db.url")
    private val port:Int = ConfigFactory.load().getInt("db.port")

    fun getMongoClient(): MongoClient = MongoClient(url, port)
}