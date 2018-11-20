package com.smartsoft.csp.ast

data class CfgAddConstraint(
        val notOr_to_and: Boolean = true,
        val notClause_to_cube: Boolean = true,
        val checkComplexForSimpleOverlap: Boolean = false,
        val litImps: Boolean = false
)

data class CfgDnnfCompile(
        val includeDcs: Boolean = true
)

data class CfgLog(
        val transform: Boolean = false,
        val condition: Boolean = false,
        val vv: Boolean = false,
        val complexDups: Boolean = false,
        val litImps: Boolean = false
)

data class SpaceConfig(
        val addConstraint: CfgAddConstraint = CfgAddConstraint(),
        val log: CfgLog = CfgLog(),
        val dnnfCompile: CfgDnnfCompile = CfgDnnfCompile()

)
