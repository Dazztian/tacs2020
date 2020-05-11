package com.utn.tacs.utils

import com.mongodb.client.MongoClient
import com.mongodb.client.MongoDatabase
import org.litote.kmongo.*
import com.typesafe.config.ConfigFactory

object MongoClientGenerator {
    private val defaultDb = ConfigFactory.load().getString("db.name")
    private val url:String = ConfigFactory.load().getString("db.url")
    private val port:Int = ConfigFactory.load().getInt("db.port")

    fun getMongoClient(): MongoClient = KMongo.createClient("mongodb://${url}:${port}")

    fun getDataBase(name: String): MongoDatabase = getMongoClient().getDatabase(name)

    fun getDataBase(): MongoDatabase = getMongoClient().getDatabase(defaultDb)

}