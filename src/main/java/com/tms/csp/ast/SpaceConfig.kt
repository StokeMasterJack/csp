package com.tms.csp.ast

data class SpaceConfig(
        val logTransforms: Boolean = false,
        val logCondition: Boolean = false,
        val logCondition2: Boolean = false,
        val logVvSimplified: Boolean = false,
        val includeDontCaresInDnnf: Boolean = true,
        val checkForSimpleOverlapWhenAddingComplexConstraint: Boolean = false

) {
    var tmp: String? = null


}
