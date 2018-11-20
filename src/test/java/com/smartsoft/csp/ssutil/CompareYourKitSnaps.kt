package com.smartsoft.csp.ssutil

import java.io.File
import kotlin.test.Test

val f1 = File("/Users/dford/Downloads/Call-counting-1.csv")
val f2 = File("/Users/dford/Downloads/Call-counting-2.csv")

fun File.toSet(): Set<String> = readLines().map { it.split("\",\"")[0] }.filter { it.contains("smartsoft") }.toSet()

fun Set<String>.print() {
    for (s in this) {
        println("  $s")
    }
}

class CompareYourKitSnaps {


    @Test
    fun test() {
        val s1 = f1.toSet()
        val s2 = f2.toSet()

        val s12 = s1 - s2
        val s21 = s2 - s1


        println("s12")
        s12.print()

        println()

        println("s21")
        s21.print()
    }
}