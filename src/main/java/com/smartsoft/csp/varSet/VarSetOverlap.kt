package com.smartsoft.csp.varSet

fun _overlap(s1: VarSet, s2: VarSet): VarSet {

    val space = s1.space
    return if (s1.isEmpty() || s2.isEmpty()) {
        space.mkEmptyVarSet()
    } else if (s1 is SingletonVarSet && s2 is SingletonVarSet) {
        if (s1.vr == s2.vr) {
            s1
        } else {
            space.mkEmptyVarSet()
        }
    } else if (s1 is SingletonVarSet) {
        if (s2.containsVar(s1.vr)) {
            s1;
        } else {
            space.mkEmptyVarSet()
        }
    } else if (s2 is SingletonVarSet) {
        if (s1.containsVar(s2.vr)) {
            s2;
        } else {
            space.mkEmptyVarSet()
        }
    } else if (s1 is VarPair) {
        when {
            s2.containsBoth(s1) -> s1
            s2._contains(s1.vr1) -> s1.vr1.mkSingletonVarSet()
            s2._contains(s1.vr2) -> s1.vr2.mkSingletonVarSet()
            else -> space.mkEmptyVarSet()
        }
    } else if (s2 is VarPair) {
        when {
            s1.containsBoth(s2) -> s2
            s1._contains(s2.vr1) -> s2.vr1.mkSingletonVarSet()
            s1._contains(s2.vr2) -> s2.vr2.mkSingletonVarSet()
            else -> space.mkEmptyVarSet()
        }
    } else if (s1 is VarSetBuilder && s2 is VarSetBuilder) {
        VarSetBuilderK.overlap(s1, s2)
    } else if (s1 is VarNSet && s2 is VarNSet) {
        VarNSet.overlap(s1, s2)
    } else {
        throw IllegalStateException()
    }


}

fun _anyOverlap(s1: VarSet, s2: VarSet): Boolean {

    if (s1 === s2) return true

    return if (s1.isEmpty()) {
        false
    } else if (s2.isEmpty()) {
        false
    } else if (s1 is SingletonVarSet) {
        s2.containsVar(s1.vr)
    } else if (s2 is SingletonVarSet) {
        s1.containsVar(s2.vr)
    } else if (s1 is VarPair) {
        s2.containsEitherVar(s1)
    } else if (s2 is VarPair) {
        s1.containsEitherVar(s2)
    } else if (s1 is VarSetBuilder && s2 is VarSetBuilder) {
        return VarSetBuilderK.anyOverlap(s1, s2)
    } else if (s1 is VarNSet && s2 is VarNSet) {
        return VarNSet.anyOverlap(s1, s2)
    } else {
        throw IllegalStateException()
    }
}