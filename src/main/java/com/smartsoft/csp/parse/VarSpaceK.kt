package com.smartsoft.csp.parse

object VarSpaceK {
    /**
     * a0, a1, .. , a63, b0, b1, b63
     */
    fun generateSampleVarCodes(wordCount: Int = 3): List<String> {
        var c = 'a'
        val varCodes = mutableListOf<String>()
        repeat(wordCount) {
            for (i in 0..63) {
                val varCode = "$c$i"
                varCodes.add(varCode)
            }
            c += 1
        }
        return varCodes
    }



}