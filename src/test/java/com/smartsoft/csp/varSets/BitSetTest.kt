package com.smartsoft.csp.varSets

import com.smartsoft.csp.ssutil.BitSet
import com.smartsoft.csp.ssutil.bitSetOf
import com.smartsoft.csp.ssutil.mutableBitSetOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class BitSetTest {

    @Test
    fun testAddAll() {
        val s1 = mutableBitSetOf("15 16 17")
        s1.assertSer("15 16 17")

        s1.add(18)
        s1.assertSer("15 16 17 18")

        s1.add(3)
        s1.assertSer("3 15 16 17 18")

        s1.addAll("1 20 22")
        s1.assertSer("1 3 15 16 17 18 20 22")

        s1.remove(3)
        s1.assertSer("1 15 16 17 18 20 22")

        s1.removeAll("15 20")
        s1.assertSer("1 16 17 18 22")

        s1.removeAll("16 18")
        s1.assertSer("1 17 22")

        s1.addAll("2 4 6  28 29")
        s1.assertSer("1 2 4 6 17 22 28 29")

        s1.removeAll("2 6 28")
        s1.assertSer("1 4 17 22 29")

    }

    @Test
    fun testBadIndex() {
        val x1 = mutableBitSetOf()
        assertFailsWith<IndexOutOfBoundsException> { x1.set(-1) }
        assertFailsWith<IndexOutOfBoundsException> { x1.set(32) }
    }


    @Test
    fun testSizeEmptySer() {

        val x1 = mutableBitSetOf()

        assertEquals(0, x1.size)
        assert(x1.isEmpty)
        x1.assertSer("")
        x1.assertClear(0)
        x1.assertClear(31)

        x1.add(0)
        x1.add(31)

        assertEquals(2, x1.size)
        assert(x1.isNotEmpty)
        x1.assertSer("0 31")
        x1.assertSet(0)
        x1.assertSet(31)

        val x2 = mutableBitSetOf("0 15 31")
        assertEquals(3, x2.size)
        assert(x2.isNotEmpty)
        x2.assertSer("0 15 31")
        x2.assertSet(0)
        x2.assertSet(15)
        x2.assertSet(31)


    }

    @Test
    fun testUnion() {

        val s1 = bitSetOf(15, 16, 17)
        s1.assertSer("15 16 17")

        val s2 = bitSetOf(1, 15, 17)
        s2.assertSer("1 15 17")

        val s3 = BitSet.union(s1, s2)
        s3.assertSer("1 15 16 17")

        val s4 = BitSet.union(s1, s2, s3)
        s4.assertSer("1 15 16 17")

    }
}