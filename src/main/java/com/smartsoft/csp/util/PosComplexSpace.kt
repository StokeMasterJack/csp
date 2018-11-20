package com.smartsoft.csp.util

import com.smartsoft.csp.argBuilder.IArgBuilder
import com.smartsoft.csp.ast.*
import com.smartsoft.csp.ast.Fcc
import com.smartsoft.csp.ast.Formula
import com.smartsoft.csp.dnnf.DAnd
import com.smartsoft.csp.dnnf.DOr

class PosComplexSpace(val space: Space) {

    private val ROW_COUNT = 30000

    private val nodeCount: Int get() = space.nodeCount

    private val table: Array<Exp?> = arrayOfNulls(ROW_COUNT)

    fun mkExp(b: IArgBuilder): Exp {
        val contentHash = computeContentHash(b)
        val htHash = computeHtHash(contentHash)
        val bucket = computeBucketIndex(htHash)
        return mkExp(b, bucket)
    }

    private fun createPosComplex(b: IArgBuilder, expId: Int, args: Array<Exp>): PosComplexMultiVar {
        val op: Op = b.op

        val retVal = when (op) {
            Op.Cube -> CubeExp(space, expId, args)
            Op.DAnd -> DAnd(space, expId, args)
            Op.Formula -> Formula(space, expId, args, b.fcc)
            Op.Fcc -> {
                Formula(space, expId, args, Fcc())
            }
            Op.And -> And(space, expId, args)
            Op.DOr -> DOr(space, expId, args)
            Op.Xor -> Xor(space, expId, args)
            Op.Or -> Or(space, expId, args)
            Op.Iff -> Iff(space, expId, args)
            else -> throw IllegalArgumentException(op.toString())
        }



        return retVal
    }


//    private fun computeContentHash(b: IArgBuilder): Int {
//        return hashCode(b)
//    }


    private fun addNode(exp: Exp) {
        space.addNode(exp)
    }


    private fun mkExp(b: IArgBuilder, bucket: Int): Exp {
        val prev = table[bucket]
        return mkExp(b, bucket, prev)
    }

    private fun mkExp(b: IArgBuilder, bucket: Int, prev: Exp?): Exp {
        if (prev == null) {
            val newExpId = nodeCount

            val newExp = create(b, newExpId)
            assert(newExp.isNew)

            assert(newExpId == nodeCount)

            table[bucket] = newExp

            addNode(newExp)

            assert(space.nodeCount == newExpId + 1)

            return newExp
        }

        if (eq(b, prev)) {
            prev.notNew();
            assert(!prev.isNew)
            return prev
        }

        val next = prev.next

        if (next == null) {
            val newExpId = nodeCount
            val newExp = create(b, newExpId)
            assert(newExp.isNew)
            prev.putNext(newExp)
            addNode(newExp)
            return newExp
        }

        return mkExp(b, bucket, next)

    }

    fun eqSize(b: IArgBuilder, e: Exp): Boolean = b.size == e.argCount

    fun eqOp(b: IArgBuilder, e: Exp): Boolean = b.op == e.op

    fun eqArgs(b: IArgBuilder, e: Exp): Boolean {
        val it1 = b.argIt.iterator()
        for (i in 0 until e.size) {

            if (!it1.hasNext()) {
                println(b::class)
                for (exp in b.argIt) {
                    print(exp)
                    print(" ")
                }
                println()
            }

            val e1 = it1.next()
            val e2 = e[i]
            if (e1.expId != e2.expId) return false;
        }
        assert(!it1.hasNext())
        return true
    }

    private fun eq(b: IArgBuilder, e: Exp): Boolean {


//        fun eqArgs(b: IArgBuilder, e: Exp): Boolean = Iterables.elementsEqual(b.argIt, e.argIt())


        if (!eqSize(b, e)) {
            return false
        }

        if (!eqOp(b, e)) {
            return false
        }

        if (!eqArgs(b, e)) {
            return false
        }

        return true
    }


    private fun create(b: IArgBuilder, expId: Int): Exp {
        assert(expId == space.nodeCount)
        val aa: Array<Exp> = b.createExpArray()
        assert(checkArgs(aa))
        assert(expId == space.nodeCount)
        val retVal = createPosComplex(b, expId, aa)
        assert(expId == space.nodeCount) { b.op }
        return retVal
    }


    fun checkArgs(args: Array<Exp>): Boolean {
        for (i in args.indices) {

            val a2 = args[i]

            if (i != 0) {
                val a1 = args[i - 1]

                val expId1 = a1.expId
                val expId2 = a2.expId

                if (expId1 == expId2) {
                    throw IllegalStateException(expId1.toString() + " " + expId2)
                } else if (expId1 > expId2) {
                    System.err.println("e1: " + a1.expId + ": " + a1)
                    System.err.println("e2: " + a2.expId + ": " + a2)
                    throw IllegalStateException(expId1.toString() + " " + expId2)
                }

            }

        }
        return true
    }
//
//    internal fun logCacheHits(prev: Exp) {
//        val prefix: String
//        if (prev.isClause) {
//            prefix = "Clause"
//        } else if (prev.isCube) {
//            prefix = "Cube"
//        } else if (prev.isFormula) {
//            prefix = "Formula"
//        } else if (prev.isFcc) {
//            prefix = "Fcc"
//        } else if (prev.isAnd) {
//            prefix = "Mixed"
//        } else if (prev.isXor) {
//            prefix = "Xor"
//        } else if (prev.isOr) {
//            prefix = "ComplexOr"
//        } else {
//            prefix = "Other"
//        }
//
//        if (prefix.startsWith("Formula")) {
//            System.err.println("Cache hit: $prefix: $prev")
//        }
//
//    }


    class OpCounter {
        /**
         * maps op1.ordinal -> count
         * index: op1.ordinal
         * value: count
         */
        var counts = IntArray(Op1.values().size)

        fun count(op2: Op) {
            counts[op2.ordinal]++
        }

        fun printReport() {
            for (opIndex in counts.indices) {
                val count = counts[opIndex]
                val op = Op1.values()[opIndex]
                System.err.println(op.toString() + ": " + count)
            }
        }
    }


    private fun computeBucketIndex(contentHash: Int): Int {
        val tableLength = table.size
        assert(tableLength != 0)
        return (contentHash and 0x7FFFFFFF) % tableLength
    }

    companion object {

        @JvmStatic
        public fun createExpArray(b: IArgBuilder): Array<Exp> {
            val argCount = b.size
            val aa: Array<Exp?> = arrayOfNulls(argCount)


            for ((i, arg) in b.argIt.withIndex()) {
                aa[i] = arg
            }

            return aa.requireNoNulls()
        }


        @JvmStatic

        fun computeHtHash(contentHashIn: Int): Int {
            var contentHash = contentHashIn

            // This function ensures that hashCodes that differ only by
            // constant multiples at each bit position have a bounded
            // number of collisions (approximately 8 at default load factor).
            contentHash = contentHash xor (contentHash.ushr(20) xor contentHash.ushr(12))
            return contentHash xor contentHash.ushr(7) xor contentHash.ushr(4)
        }

        @JvmStatic
        fun hashCode(b: IArgBuilder): Int {
            var result = 1
            var prev: Int = -1
            for (arg in b.argIt) {
                assert(arg.expId > prev)
                result = 31 * result + arg.expId
                prev = arg.expId
            }
            return result
        }

        @JvmStatic
        fun computeContentHash(b: IArgBuilder): Int {
            return hashCode(b);
        }

        @JvmStatic
        fun checkArgItOrder(argIt: Iterable<Exp>): Boolean {
            var i = 0
            var prev: Int = -1
            for (arg in argIt) {
                if (arg.expId <= prev) {
                    System.err.println("arg: $arg  ${arg.expId}")
                    System.err.println("prev: $prev")
                    System.err.println("index: $i")
                    System.err.flush()
                    return false
                }
                prev = arg.expId
                i++
            }
            return true
        }


    }


    fun printPosComplexTableReport() {

        var minDepth = Integer.MAX_VALUE
        var maxDepth = 0
        var totalDepth = 0

        var nullCount = 0
        for (i in 0 until table.size) {
            if (table[i] == null) {
                nullCount++
            } else {
                var depth = 0
                var pos = table[i]
                while (pos != null) {
                    depth++
                    pos = pos.next
                }

                if (depth < minDepth) minDepth = depth
                if (depth > maxDepth) maxDepth = depth
                totalDepth += depth

            }
        }

        val nonNullCount = ROW_COUNT - nullCount

        val avgDepth = (totalDepth / nonNullCount).toDouble()

        assert(table.size == ROW_COUNT)

        System.err.println("PosComplexTableReport")
        System.err.println("  rowCount[" + table.size + "]")
        System.err.println("  nullCount[$nullCount]")
        System.err.println("  nonNullCount[$nonNullCount]")
        System.err.println("  avgDepth[$avgDepth]")
        System.err.println("  minDepth[$minDepth]")
        System.err.println("  maxDepth[$maxDepth]")
    }


}





