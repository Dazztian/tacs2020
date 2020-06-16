package com.utn.tacs.utils

import com.mongodb.client.MongoClient
import com.mongodb.client.MongoDatabase
import org.litote.kmongo.KMongo

object MongoClientGenerator {
    private lateinit var defaultDb: String
    private lateinit var connectionUrl: String

    private fun getMongoClient(): MongoClient = KMongo.createClient(connectionUrl)

    fun getDataBase(name: String): MongoDatabase = getMongoClient().getDatabase(name)

    fun getDataBase(): MongoDatabase = getMongoClient().getDatabase(defaultDb)

    fun setProperties(db: String, url: String, port: Int) {
        this.connectionUrl = "mongodb://${url}:${port}"
        this.defaultDb = db
    }

}