package com.utn.tacs

import org.junit.Test
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.test.assertEquals

class Tests {

    @Test
    fun organizarEnCaracteresTest(){
        val result = organizarEnCaracteres(listOf("12345", "123456", "123", "45", "123", "12345", "123", "123456"), 5)

        assertEquals(listOf("12345", "12345", "123", "12345", "123"), result)
    }

    @Test
    fun createTableRowStringTest(){
        val result = createTableRowString(mapOf("Nombre" to 7,
                                                "Paises" to 7,
                                                "Creacion" to 10))

        assertEquals("| Nombre | Paises | Creacion  |\n", result)

        val result2 = createTableRowString(mapOf("Nombre Nombre" to 5,
                                                 "Creacion Creacion" to 10))


        assertEquals(result2,   "| Nombr| Creacion C|\n" +
                                "| e Nom| reacion   |\n" +
                                "| bre  |           |\n")
    }

    @Test
    fun buildTableArrayTest(){
        val countriesList = CountriesList("id", "userId", "name", setOf(), LocalDate.now())

        assertEquals(
            buildTableArray(listOf(countriesList))[0],
            "<pre>\n${countriesList.tableHeader()}" +
            "| name               | 0         |\n</pre>")
    }
}