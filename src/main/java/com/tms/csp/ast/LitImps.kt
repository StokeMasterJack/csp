package com.tms.csp.ast

import com.tms.csp.fm.dnnf.products.Cube

interface LitImps {

    fun imp(lit1: Lit, lit2: Lit)
    fun imp(lit: Lit, cube: Cube)

}
