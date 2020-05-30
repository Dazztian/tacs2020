package com.utn.tacs

import kotlin.math.ceil
import kotlin.math.min

interface RequestModelInterface{
    fun toTableRowString() :String
    fun tableHeader() :String
}
sealed class RequestModel :RequestModelInterface

data class CountriesList (
        val _id: String?,
        val userId: String?,
        val name: String?,
        val countries: Set<String>?,
        val creationDate: String?
) :RequestModel() {

    override fun toTableRowString(): String = createTableRowString(mapOf(   (name ?: "") to 20,
                                                                            (countries?.size?.toString() ?: "0") to 8,
                                                                            (creationDate ?: "") to 12))

    override fun tableHeader(): String {
        return  "|       Nombre       | Paises |  Creacion  |\n" +
                "|:------------------:|:------:|:----------:|\n"
    }
}

//Arma la fila con un map de los campos y el espacio por linea de cada campo
//En el caso que un campo sea mas largo que el total de su espacio en la linea
//se crean mas lineas y se llena el resto del campo ahi
fun createTableRowString(nameXvalue:Map<String, Int>): String{
    val nLines = nameXvalue.map { ceil(it.key.length.toDouble() / it.value) }.max()!!.toInt()
    val lines = MutableList(nLines){ "" }

    for(i in 0 until nLines){
        for ((field, lengthFieldRow) in nameXvalue){
            val currentIndex = i * lengthFieldRow
            lines[i] += "|" +
                    field.substring(
                                    min(field.length, currentIndex),
                                    min(field.length, currentIndex+lengthFieldRow))
                            .padEnd(lengthFieldRow)
        }
    }

    return lines.joinToString(separator = "|\n", postfix = "|\n")
}