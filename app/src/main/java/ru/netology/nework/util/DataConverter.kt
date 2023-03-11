@file:Suppress("NAME_SHADOWING")

package ru.netology.nework.util

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@SuppressLint("NewApi")
object DataConverter {
    fun convertDataTime(dateTime: String): String {
        return if (dateTime == "") {
            ""
        } else {
            val parsedDate = LocalDateTime.parse(dateTime, DateTimeFormatter.ISO_DATE_TIME)
            return parsedDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun convertDataTimeJob(dateTime: String): String {
        return if (dateTime == "") {
            ""
        } else {
            val parsedDate = LocalDateTime.parse(dateTime, DateTimeFormatter.ISO_DATE_TIME)
            return parsedDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun convertDataInput(date: List<Int>): String {
        val x = date[0]
        val y = date[1] + 1
        val z = date[2]
        val day = when (x) {
            in 1..9 -> "0$x"
            else -> {
                "$x"
            }
        }
        val month = when (y) {
            in 1..9 -> "0$y"
            else -> {
                "$y"
            }
        }
        val year = "$z"
        val newDate = "$day.$month.$year"
        val formatter = SimpleDateFormat("dd.MM.yyyy")
        val date = formatter.parse(newDate)
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss'Z'")
        return sdf.format(date!!)
    }

    @SuppressLint("SimpleDateFormat")
    fun convertDateToLocalDate(date: List<Int>): String {
        val x = date[0]
        val y = date[1] + 1
        val z = date[2]
        val h = date[3]
        val m = date[4]
        val day = when (x) {
            in 1..9 -> "0$x"
            else -> {
                "$x"
            }
        }
        val month = when (y) {
            in 1..9 -> "0$y"
            else -> {
                "$y"
            }
        }
        val year = "$z"
        val hour = when (h) {
            in 1..9 -> "0$h"
            else -> "$h"
        }
        val minute = when (m) {
            in 1..9 -> "0$m"
            else -> "$m"
        }
        val newDate = "$day.$month.$year $hour:$minute"
        val formatter = SimpleDateFormat("dd.MM.yyyy HH:mm")
        val date = formatter.parse(newDate)
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss'Z'")
        return sdf.format(date!!)
    }
}