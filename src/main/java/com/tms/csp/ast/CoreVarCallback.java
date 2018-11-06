package com.tms.csp.ast;

import com.google.common.collect.ImmutableSet;
import com.tms.csp.varCodes.VarCode;

/**
 *
 * A CoreXorVar is an MDL, YR, XCOL or ICOL or a CoreAccessoryVar
 *
 * A CoreAccessoryVar is an ACY vr formula the *fact* rules that is an FIO or a non-alaCarte PIO.
 *
 * A CoreVar is a CoreXorVar or CoreAccessoryVar
 *
 * The determination or Core/NonCore is solely based c fact rules. It does not involve the inventory table.
 *
 * The purpose is to determine outOfStock _vars when formula Inventory Mode.
 * So when formula Inventory Mode: any Var that is Core but *not* formula the inventory table should be set to false.
 *
 */
public abstract class CoreVarCallback implements PLConstants {

    public static final ImmutableSet CORE_XOR_PREFIXES = ImmutableSet.of(MDL_PREFIX, YR_PREFIX, XCOL_PREFIX, ICOL_PREFIX);

    /**
     * To be implemented c Hitachi's side
     */
    public abstract boolean isCoreAccessoryVar(String varCode);

    public boolean isCoreXorVar(String varCode) {
        String prefix = VarCode.getPrefix(varCode);
        return CORE_XOR_PREFIXES.contains(prefix);
    }

    public boolean isCoreVar(String varCode) {
        return isCoreXorVar(varCode) || isCoreAccessoryVar(varCode);
    }

}
