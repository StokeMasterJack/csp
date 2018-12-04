package com.smartsoft.csp.varSet

import com.smartsoft.csp.ast.Ser
import com.smartsoft.csp.ast.Space
import com.smartsoft.csp.parse.VarSpace
import com.smartsoft.csp.util.ints.IntIterator
import java.util.*

object EmptyVarSet : VarSet() {

    override fun getVarSpace(): VarSpace = throw UnsupportedOperationException()

    override fun minVrId(): Int = throw NoSuchElementException()

    override fun maxVrId(): Int = throw NoSuchElementException()

    override fun intIterator(): IntIterator = IntIterator.EMPTY;

    override fun containsPrefix(prefix: String?): Boolean = false

    override fun isEmpty(): Boolean = true

    override fun getVarId(index: Int): Int = throw IndexOutOfBoundsException()


    override fun indexOf(varId: Int): Int = -1

    override fun getSpace(): Space = throw UnsupportedOperationException()

    override fun serialize(a: Ser) {}

    override val size: Int get() = 0

    override fun immutable(): VarSet = this

    override fun computeContentHash(): Int = 0

    @JvmStatic
    fun getInstance(): EmptyVarSet {
        return this
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        return other === EmptyVarSet || size == 0
    }
}