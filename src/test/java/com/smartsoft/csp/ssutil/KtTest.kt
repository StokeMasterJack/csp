package com.smartsoft.csp.ssutil

import kotlin.test.Test


fun tmp() {
    val x: Int = tt("foo") { foo() }
    tt { boo() }
    println("x = ${x}")
}

fun foo(): Int {
    println("foo")
    return 33
}

fun boo() {
    println("boo")
}


class KtTest {
    @Test
    fun test() {
        println(11)
        BitSetJ()
        println(22)
    }
}