package com.smartsoft.csp.ast

class SpaceK {


}

fun foo() {

}

val Space.topXorPrefixes: Set<String> get() = Prefix.topXor.map { it.toString() }.toSet()

val Space.firstXor: Xor get() = coreXors.first()

val Space.coreXors: Sequence<Xor>
    get() = sequence {
        getXor(PLConstants.YR_PREFIX).let { if (it != null) yield(it.asXor) }
        getXor(PLConstants.SER_PREFIX).let { if (it != null) yield(it.asXor) }
        getXor(PLConstants.MDL_PREFIX).let { if (it != null) yield(it.asXor) }
        getXor(PLConstants.XCOL_PREFIX).let { if (it != null) yield(it.asXor) }
        getXor(PLConstants.ICOL_PREFIX).let { if (it != null) yield(it.asXor) }
    }


val Space.topXors: Sequence<Xor>
    get() = coreXors.filter { it.prefix in topXorPrefixes }
