package com.darrylbayliss.simonsays.utils

import android.content.Context
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

fun Context.getTextFromFile(fileName: String): List<String> {
    var responses = listOf<String>()
    val reader: BufferedReader? = null
    try {
        val inputStream = assets.open(fileName)
        responses = BufferedReader(InputStreamReader(inputStream)).readLines()
    } catch (e: IOException) {
        e.printStackTrace() // Or log the error more appropriately
        println("Error reading simon_says_responses.txt: ${e.message}")
    } finally {
        try {
            reader?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
    return responses
}