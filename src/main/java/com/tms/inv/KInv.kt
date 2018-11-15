package com.tms.inv

import com.google.common.collect.ImmutableSet
import com.tms.csp.ast.PLConstants
import com.tms.csp.ast.Parser

object KInv {


    @JvmStatic
    fun extractVarCodes(invClob: String): Set<String> {
        val b = mutableSetOf<String>()
        val sLines: Sequence<String> = Parser.clobToLines(invClob)

        for (sLine in sLines) {
            val lineVarCodes = Line.extractVarCodes(sLine)
            b.addAll(lineVarCodes)
        }

        return b

    }


    fun extractDealerVarCodes(invClob: String?): Set<String> {
        if (invClob == null || !Inv.hasDealers(invClob)) {
            return ImmutableSet.of()
        }

        val b = ImmutableSet.builder<String>()

        val sLines = Parser.clobToLines(invClob)

        for (sLine in sLines) {
            val lineVarCodes = Line.extractDealerVarCodes(sLine)
            b.addAll(lineVarCodes)
        }

        return b.build()

    }

    fun extractMsrpBucketVarCodes(invClob: String): ImmutableSet<String> {

        if (!Inv.hasMsrps(invClob)) {
            return ImmutableSet.of()
        }

        val b = ImmutableSet.builder<String>()

        val sLines = Parser.clobToLines(invClob)

        for (sLine in sLines) {
            val msrpFloorVarCode = Line.extractMsrpVarCode(sLine)
            b.add(msrpFloorVarCode)
        }

        return b.build()

    }

    fun extractMsrpStrictVarCodes(invClob: String): ImmutableSet<String> {

        if (!Inv.hasMsrps(invClob)) {
            return ImmutableSet.of()
        }

        val b = ImmutableSet.builder<String>()

        val sLines = Parser.clobToLines(invClob)

        for (sLine in sLines) {
            val msrpVarCode = Line.extractMsrpVarCode(sLine)
            b.add(msrpVarCode)
        }

        return b.build()

    }

    /**
     * This only exists because Space requires all vars be predefined - before adding fact.
     * Eventually, I think this requirement should be dropped.
     *
     *
     * Examples of Extra vars:
     *
     *
     * dealer vars: extracted from invClob file
     * msrp strict vars: extracted from invClob file
     * msrp bucket vars
     * msrp bit vars
     */
    @JvmStatic
    fun buildExtraVars(clobInv: String): Set<String> {
        val b = ImmutableSet.builder<String>()

        if (PLConstants.USE_DEALERS_STRICT) {
            b.addAll(KInv.extractDealerVarCodes(clobInv))
        }

        if (PLConstants.USE_DEALERS_BITS) {
            b.addAll(Inv.createDealerBitVarCodes())
        }

        if (PLConstants.USE_MSRP_BUCKETS) {
            b.addAll(extractMsrpBucketVarCodes(clobInv))
        }

        if (PLConstants.USE_MSRP_STRICT) {
            b.addAll(extractMsrpStrictVarCodes(clobInv))
        }

        if (PLConstants.USE_MSRP_BITS) {
            b.addAll(Inv.createMsrpBitVarCodes())
        }

        return b.build()
    }
}