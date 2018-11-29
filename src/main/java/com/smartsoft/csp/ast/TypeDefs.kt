package com.smartsoft.csp.ast

typealias LitEntry = Map.Entry<Var, Boolean>

val LitEntry.asLit: Lit get() = key.lit(value)