package com.tms.csp.util

import com.tms.csp.It
import com.tms.csp.ast.Exp
import com.tms.csp.util.ints.IndexedEntry
import com.tms.csp.util.ints.TreeSequence

typealias ExpEntry = IndexedEntry<Exp>

private fun <R> empty(): Iterator<R> = It.emptyIter()

fun TreeSequence<Exp>?.entryIter(): Iterator<ExpEntry> = this?.iterator() ?: empty()

fun TreeSequence<Exp>?.expIter(): Iterator<Exp> {
    return if (this == null) {
        empty()
    } else {
        object : Iterator<Exp> {
            val iter1 = entryIter()
            override fun hasNext() = iter1.hasNext()
            override fun next() = iter1.next().value()
        }
    }
}




