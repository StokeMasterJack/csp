package com.smartsoft.csp.api

import com.smartsoft.csp.data.CspSample
import kotlin.test.Test

class Vars {
    @Test
    fun test() {

        val vars1 = CspSample.Camry2011Dc.parseCsp().vars
        val vars2 = CspSample.TrimColorOptionsDc.parseCsp().vars

        println(vars1.minus(vars2))
        println(vars2.minus(vars1))

    }

    @Test
    fun test2() {


//
//
//        val metaVar = MetaVarParserDom4j.parseVarMetaDataFromXmlDom4j(varMetaText)
//
//
//        val varMeta = VarMetaDataUtil.reverseEngineerVarMetaForXor(vars)
    }
}