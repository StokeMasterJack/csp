package com.smartsoft.csp.varSet

import com.smartsoft.csp.ast.Parser
import kotlin.test.Test

class VarsLineVsExtractedVars{


    @Test
    fun test() {

        val clob = """
            vars(a b c d e)
            xor(a b)
            conflict(b c)
            conflict(x y)
        """

        Parser.compareVarsLineToExtract(clob)
    }
}