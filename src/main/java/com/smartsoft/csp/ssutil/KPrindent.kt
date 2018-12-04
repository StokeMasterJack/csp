package com.smartsoft.csp.ssutil

fun prident(depth: Int, msg: String) {
    println(Strings.indent(depth) + msg)
}


fun prident(depth: Int, msg: Int) {
    println(Strings.indent(depth) + msg)
}