package com.tms.csp.ast

data class SpaceConfig(
        val logTranforms: Boolean = false,
        val logCondition: Boolean = false,
        val checkForSimpleOverlapWhenAddingComplexConstraint: Boolean = true

)
