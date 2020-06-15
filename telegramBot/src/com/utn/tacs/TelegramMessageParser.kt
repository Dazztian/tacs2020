package com.utn.tacs

import kotlin.math.ceil
import kotlin.math.min


/**
 * Arma la fila con un map de los campos y el espacio por linea de cada campo
 * En el caso que un campo sea mas largo que el total de su espacio en la linea
 * se crean mas lineas y se llena el resto del campo ahi
 *
 * @param nameXvalue Map<String, Int>
 * @return String
 */
fun createTableRowString(nameXvalue:Map<String, Int>): String{
    val nLines = nameXvalue.map { ceil(it.key.length.toDouble() / it.value) }.max()!!.toInt()
    val lines = MutableList(nLines){ "" }

    for(i in 0 until nLines){
        for ((field, lengthFieldRow) in nameXvalue){
            val currentIndex = i * lengthFieldRow
            lines[i] += "| " +
                    field.substring(
                            min(field.length, currentIndex),
                            min(field.length, currentIndex+lengthFieldRow))
                            .padEnd(lengthFieldRow)
        }
    }

    return lines.joinToString(separator = "|\n", postfix = "|\n")
}

//Se divide en strings de 4084 (4096 - 12) caracteres por el max de char por mensaje en telegram
private fun buildRows(list: List<String>) :List<String> = organizeInCharacters(list, 4084).map { row -> "<pre>\n$row</pre>" }

/**
 * Retorna una tabla con los elementos de la lista
 *
 * @param list List<RequestModel>
 * @return List<String>
 */
fun buildTableArray(list :List<RequestModel>?) :List<String> {
    return if (list == null || list.isEmpty())
        emptyList()
    else
        buildRows(listOf(list[0].tableHeader()) + list.map { p -> p.toTableRowString() })
}

fun buildTableTimeseries(list :List<CountryResponseTimeseries>) :List<String>{
    return if (list.isEmpty())
        emptyList()
    else{
        val rows = MutableList(1){TimeSerie.tableHeader()}
        list.forEach { country ->
            if (country.timeseries != null){
                rows.add("${country.countryregion!!}:\n")
                country.timeseries?.forEach { timeserie ->
                    rows.add(timeserie.toTableRowString())
                }
            }
        }

        buildRows(rows)
    }
}

/**
 * Retorna una lista con los joineando los strings de la lissta pasada hasta alcanzar el maxCharPorString
 *
 * @param list List<RequestModel>
 * @param maxCharPorString Int
 * @return List<String>
 */
fun organizeInCharacters(list :List<String>, maxCharPorString :Int) :List<String>{
    val ret = mutableListOf<String>()
    var acc = ""
    for(s in list){
        if(s != "" && s.length <= maxCharPorString){
            when {
                acc.length + s.length > maxCharPorString -> {
                    ret.add(acc)
                    acc = s
                }
                acc.length + s.length < maxCharPorString -> {
                    acc += s
                }
                else -> {
                    ret.add(acc+s)
                    acc = ""
                }
            }
        }
    }
    if(acc != ""){
        ret.add(acc)
    }

    return ret.toList()
}