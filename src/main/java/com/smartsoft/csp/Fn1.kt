package com.smartsoft.csp

import com.smartsoft.csp.ast.Exp
import com.smartsoft.csp.ast.Lit
import com.smartsoft.csp.ast.Var
import com.smartsoft.csp.fm.dnnf.products.Cube
import com.smartsoft.csp.util.ints.IndexedEntry
import com.smartsoft.csp.util.ints.TreeSequence
import com.smartsoft.csp.util.varSets.VarSet

typealias ExpIter = Iterator<Exp>
typealias ExpIt = Iterable<Exp>
typealias LitIt = Iterable<Lit>

typealias ExpFn = (Exp) -> Exp;
typealias ExpFnJvm = java.util.function.Function<Exp, Exp>
typealias VarFn = (Var) -> Var;

typealias Entry = IndexedEntry<Exp>

object Its {
    val emptyIt: ExpIt = It.emptyIt<Exp>();
}

fun Iterable<Exp>.flipAll(): Iterable<Exp> = transform(Fn.flipper)

fun Iterable<Exp>.transform(f: ExpFn): Iterable<Exp> {
    return ItTo.it(this, f)
}

/*


    public static Iterable<Exp> expItFromExpArray(Exp[] args) {
        Iterable<Exp> xx = () -> expIteratorFromExpArray(args);
        Iterable<Exp> yy = It.nonNullIt(xx);
        return yy;
    }

    public static Iterable<Lit> litItFromExpArray(Exp[] lits) {
        return () -> litIteratorFromExpArray(lits);
    }

 */



object VarTo {
    val nonNull: (Var?) -> Var = { it -> it!! }

    fun lit(sign: Boolean): (Var) -> Lit = { it.lit(sign) }
    val pLit: (Var) -> Lit = lit(true)
    val nLit: (Var) -> Lit = lit(false)

    fun exp(sign: Boolean): (Var) -> Exp = { it.lit(sign) }
    val pExp: (Var) -> Exp = exp(true)
    val nExp: (Var) -> Exp = exp(false)

    @JvmStatic
    val varCode: (Var) -> String = { it.varCode }


}


object VarToVar {
    val identity: (Var) -> (Var) = { it }
}

object NullableTo {

    fun <T> nonNull(): (T?) -> T = NonNull<T>()

//    fun <T> nonNullFunction(): (T?) -> T = { it!! }

}

class NonNull<T> : (T?) -> T {
    override operator fun invoke(x: T?): T = x!!
}

fun java.util.function.Function<Exp, Exp>.toKFunction(): (Exp) -> Exp = Fn.toKFunction(this)
fun Function1<Exp, Exp>.toJvmFunction(): java.util.function.Function<Exp, Exp> = Fn.toJvmFunction(this)

object Fn {

    val identity: ExpFn = { e: Exp -> e }
    val identityJvm = toJvmFunction(identity)

    @JvmStatic
    val identity1: ExpFn = { e: Exp -> e }
    val identity2: VarFn = { v: Var -> v }

    @JvmStatic
    fun condition(cube: Cube): ExpFn = { arg: Exp -> arg.condition(cube) }

    @JvmStatic
    fun condition(lit: Lit): ExpFn = { arg: Exp -> arg.condition(lit) }


    @JvmStatic
    fun conditionJvm(cube: Cube) = toJvmFunction(condition(cube))


    @JvmStatic
    fun conditionJvm(lit: Lit) = toJvmFunction(condition(lit))


    @JvmStatic
    fun condition(sLits: String): ExpFn = { arg: Exp -> arg.condition(sLits) }


    val expToLit: (Exp) -> Lit = { it.asLit }

    val flipper: (Exp) -> Exp = { it.flip }

    @JvmStatic
    fun <T, R> toJvmFunction(ff: (T) -> R) = java.util.function.Function<T, R> { ff(it) }

    @JvmStatic
    fun <T, R> toKFunction(ff: java.util.function.Function<T, R>): (T) -> R = { ff.apply(it) }


}

class EntryToExp : (Entry) -> Exp {
    override operator fun invoke(entry: Entry): Exp = entry.value()
}

class EntryToLit : (Entry) -> Lit {
    override operator fun invoke(entry: Entry): Lit = entry.value().asLit
}


object EntryIterTo {

    fun litIt(iter1: Iterator<Entry>): LitIt = IterTo.it(iter1, EntryToLit())

    fun expIt(iter1: Iterator<Entry>): ExpIt = IterTo.it(iter1, EntryToExp())

    fun expIter(iter1: Iterator<Entry>): ExpIter = IterTo.iter(iter1, EntryToExp())

}

object TreeSeqTo {

    fun litIt(t: TreeSequence<Exp>): LitIt {
        val iter: Iterator<Entry> = t.iterator()
        return EntryIterTo.litIt(iter)
    }

    fun expIt(t: TreeSequence<Exp>): ExpIt {
        val iter: Iterator<Entry> = t.iterator()
        return EntryIterTo.expIt(iter)
    }

    fun expIter(t: TreeSequence<Exp>): ExpIter {
        val iter: Iterator<Entry> = t.iterator()
        return EntryIterTo.expIter(iter)
    }

}

object ExpArrayTo {

    @JvmStatic
    fun expIter(a: Array<out Exp>): Iterator<Exp> = ArrayTo.iter(a)

    @JvmStatic
    fun expIt(a: Array<out Exp>): Iterable<Exp> = ArrayTo.it(a)

    @JvmStatic
    fun litIter(a: Array<Exp>): Iterator<Lit> = ArrayTo.iter(a) { it.asLit }

    @JvmStatic
    fun litIt(a: Array<Exp>): Iterable<Lit> = ArrayTo.it(a) { it.asLit }


}

object VarItTo {
    fun expIt(args: Iterable<Var>, sign: Boolean = true): ExpIt = ItTo.it(args, VarTo.exp(sign))
    fun litIt(args: Iterable<Var>, sign: Boolean = true): LitIt = ItTo.it(args, VarTo.lit(sign))
}

object VarSetTo {
    fun expIt(vs: VarSet): ExpIt = Iterable { vs.litExpIterator(true) }
}

object ExpItTo {
    @JvmStatic
    fun expSeq(it: ExpIt): Sequence<Exp> = it.asSequence();
}


object Ex {


    fun expIterator(args: Array<Exp>): Iterator<Exp> = ArrayTo.iter(args)

    fun litIterator(args: Array<Exp>): Iterator<Lit> {
        return ExpArrayTo.litIter(args)
    }


}


enum class ContentModel {
    AllLits, AllComplex, Mixed, Empty, Unknown
}





enum class Structure {

    Dnnf, Disjoint, Fcc, Unknown;

    val isDnnf: Boolean get() = this == Dnnf


    val isDisjoint: Boolean get() = this == Disjoint

    val isFcc: Boolean? get() = if (this == Disjoint) true else null


}






