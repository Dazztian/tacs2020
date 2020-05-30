package com.utn.tacs

//Retorna una tabla con los elementos del array
//Se divide en strings de 4084 caracteres (4096 - 12 caracteres a agregar) por el max de char por mensaje en telegram
fun buildTableArray(list :List<RequestModel>?) :List<String> {
    return if (list == null || list.isEmpty()){
        emptyList()
    }else{
        organizarEnCaracteres(listOf(list[0].tableHeader()) + list.map { p -> p.toTableRowString() }, 4084)
                .map { row -> "<pre>\n$row</pre>" }
    }
}

fun organizarEnCaracteres(list :List<String>, maxCharPorString :Int) :List<String>{
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