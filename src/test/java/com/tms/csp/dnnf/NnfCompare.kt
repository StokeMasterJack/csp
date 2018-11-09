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
    fun efc() {
        nnfCompare(CspSample.Efc, true)
    }

    @Test
    fun efcOriginal() {
        nnfCompare(CspSample.EfcOriginal, true)
    }

    @Test
    fun efcProdFactoryRules() {
        nnfCompare(CspSample.EfcProdFactoryRules, true)
    }

    @Test
    fun efcComboFactoryPlusInv() {
        nnfCompare(CspSample.ComboFactoryPlusInv, true)
    }

    fun nnfCompare(sample: CspSample, keepXors: Boolean = false) {
        return nnfCompare(sample.loadText(), keepXors)
    }

    fun nnfCompare(clob: String, keepXors: Boolean = false) {

        val csp = Csp.parse(clob)

        val nnf: Csp = csp.copy().apply { toNnf(keepXors) }

        println("csp.toDnnf")
        val cspDnnf = csp.toDnnfSmooth()

        println("nnf1.toDnnf")
        val nnfDnnf = nnf.toDnnfSmooth()


        val cspSatCount = cspDnnf.satCount
        val nnfSatCount = nnfDnnf.satCount

        assertEquals(cspSatCount, nnfSatCount)


    }

}