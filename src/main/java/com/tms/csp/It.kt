package com.tms.csp

import com.google.common.collect.Iterators

object ArrayTo {

    @JvmStatic
    fun <T> iter(a: Array<T>): Iterator<T> = object : Iterator<T> {
        var index = 0
        override fun hasNext() = index < a.size
        override fun next() = a[index]!!
    }

    @JvmStatic
    fun <T, R> iter(a: Array<T>, f: (T) -> R): Iterator<R> = object : Iterator<R> {
        var index = 0
        override fun hasNext() = index < a.size
        override fun next(): R {
            val el = a[index]!!
            val ret = f(el)
            index++
            return ret
        }
    }

    @JvmStatic
    fun <T, R> it(a: Array<T>, f: (T) -> R): Iterable<R> = Iterable { iter(a, f) }

    @JvmStatic
    fun <T> it(a: Array<T>): Iterable<T> = Iterable { iter(a) }


}

object IterTo {

    @JvmStatic
    fun <T, R> iter(it1: Iterator<T>, f: (T) -> R): Iterator<R> = object : Iterator<R> {
        override fun hasNext() = it1.hasNext()
        override fun next() = f(it1.next())
    }

    @JvmStatic
    fun <T, R> it(it1: Iterator<T>, f: (T) -> R): Iterable<R> = Iterable { iter(it1, f) }

    @JvmStatic
    fun <T> it(it1: Iterator<T>): Iterable<T> = Iterable { it1 }

    @JvmStatic
    fun <T> nonNullIter(it1: Iterator<T?>): Iterator<T> = iter(it1, NonNull<T>())

    @JvmStatic
    fun <T> nonNullIt(it1: Iterator<T?>): Iterable<T> = it(it1, NonNull<T>())

}

object ItTo {

    @JvmStatic
    fun <T, R> it(it1: Iterable<T>, f: (T) -> R): Iterable<R> = IterTo.it(it1.iterator(), f)

    @JvmStatic
    fun <T> nonNullIt(it1: Iterable<T?>): Iterable<T> = it(it1, NonNull<T>())

}

object It {

    @JvmStatic
    fun <R> emptyIter(): Iterator<R> {
        return Iterators.emptyIterator()
    }


    @JvmStatic
    fun <R> emptyIt(): Iterable<R> {
        return Iterable { emptyIter<R>() }
    }


}




