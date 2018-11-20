package com.smartsoft.csp.ssutil

val ktlog = LogUtil.createLogger("TT")

/*
val tt =  KT()
loadText()
tt.t("load text");

compileDnnf()
tt.t("compileDnnf");


val x = profile{ foo() }
 */

inline fun <T> tt(lbl: String? = null, block: () -> T): T {
    val t1 = millis
    val ret = block()
    val t2 = millis
    val prefix = if (lbl == null) "" else "$lbl "
    System.err.println("${prefix}delta: ${t2 - t1}")
    return ret
}


class KTT {

    private var t = millis

    fun t(lbl: String): Long {
        val delta = System.currentTimeMillis() - t
        ktlog.info("$lbl delta: $delta")
        t = System.currentTimeMillis()
        return delta
    }


}


