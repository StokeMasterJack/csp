package com.tms.csp.ast

data class SpaceConfig(
        val logTranforms: Boolean = false,
        val logCondition: Boolean = false,
        val logCondition2: Boolean = true,
        val logVvSimplified: Boolean = false,
        val includeDontCaresInDnnf: Boolean = true,
        val checkForSimpleOverlapWhenAddingComplexConstraint: Boolean = true

) {


}
