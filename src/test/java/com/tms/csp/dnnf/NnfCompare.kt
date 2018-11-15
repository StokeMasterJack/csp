package com.tms.csp.dnnf

import com.tms.csp.ast.Csp
import com.tms.csp.data.CspSample
import kotlin.test.Test
import kotlin.test.assertEquals

class NnfCompare {

    @Test
    fun camry() {
        nnfCompare(CspSample.Camry2011NoDc, false)
        nnfCompare(CspSample.Camry2011NoDc, true)
    }

    @Test
    fun tundra() {
        nnfCompare(CspSample.Tundra, false)
        nnfCompare(CspSample.Tundra, true)
    }


    @Test
    fun efcOriginal() {
        nnfCompare(CspSample.EfcOriginal, true)
    }

    @Test
    fun efcProdFactoryRules() {
        nnfCompare(CspSample.EfcProdFactoryRules, true)
    }


    fun nnfCompare(sample: CspSample, keepXors: Boolean = false) {

        val csp = Csp.parse(sample)

        val nnf: Csp = csp.copy().apply { toNnf(keepXors) }

        val cspDnnf = csp.toDnnfSmooth()

        val nnfDnnf = nnf.toDnnfSmooth()


        val cspSatCount = cspDnnf.satCount
        val nnfSatCount = nnfDnnf.satCount

        assertEquals(cspSatCount, nnfSatCount)


    }

}