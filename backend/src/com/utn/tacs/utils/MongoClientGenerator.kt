package com.utn.tacs.utils

import com.mongodb.MongoClient
import com.mongodb.client.MongoDatabase
import com.typesafe.config.ConfigFactory


object MongoClientGenerator {
    private const val defaultDb = "tacs"
    private val url:String = ConfigFactory.load().getString("db.url")
    private val port:Int = ConfigFactory.load().getInt("db.port")

    fun getMongoClient(): MongoClient = MongoClient(url, port)

    fun getDataBase(name: String): MongoDatabase = getMongoClient().getDatabase(name)

    fun getDataBase(): MongoDatabase = getMongoClient().getDatabase(defaultDb)
}