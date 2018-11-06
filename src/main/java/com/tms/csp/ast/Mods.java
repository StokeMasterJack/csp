package com.tms.csp.ast;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.tms.csp.varCodes.VarCode;
import com.tms.csp.varCodes.IVar;

import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

public class Mods implements PLConstants {


    public static final String PREFIX_YR = "YR";
    public static final String PREFIX_SER = "SER";
    public static final String PREFIX_MDL = "MDL";
    public static final String PREFIX_ACY = "ACY";
    public static final String PREFIX_ICOL = "ICOL";
    public static final String PREFIX_XCOL = "XCOL";

    public final static ImmutableSet<String> MOD_CORE_PREFIXES = ImmutableSet.of("", PREFIX_YR, PREFIX_SER, PREFIX_MDL);
    public final static ImmutableSet<String> MOD_TRIM_PREFIXES = ImmutableSet.of("GRD", "TRAN", "DRV", "CAB", "BED");
    public final static ImmutableSet<String> MOD_COLOR_PREFIXES = ImmutableSet.of("ICOL", "XCOL");
    public final static ImmutableSet<String> MOD_ACCESSORY_PREFIXES = ImmutableSet.of("ACY");

    public final static ImmutableList<String> INV_CORE_XORS = ImmutableList.of(PREFIX_YR, PREFIX_MDL, PREFIX_XCOL, PREFIX_ICOL);

    public static final Mod CORE = new Mod("core", null, MOD_CORE_PREFIXES);
    public static final Mod TRIM = new Mod("trim", CORE, MOD_TRIM_PREFIXES);
    public static final Mod COLOR = new Mod("color", TRIM, MOD_COLOR_PREFIXES);
    public static final Mod ACCESSORY = new Mod("accessory", TRIM, MOD_ACCESSORY_PREFIXES);

    public static boolean isInvCareVar(IVar var) {
        return var.isInv();
    }

    public static boolean isInvAcy(VarCode invVar) {
        if (invVar.isAcy()) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isInvCoreXor(String prefix) {
        return INV_CORE_XORS.contains(prefix);
    }

    public static int getInvCoreIndex(String prefix) {
        return INV_CORE_XORS.indexOf(prefix);
    }


    public static String getBestOpenCoreInvXor(Set<String> xorPrefixes) {
        if (xorPrefixes == null) {
            return null;
        }
        int bestIndex = Integer.MAX_VALUE;
        String bestXorPrefix = null;
        for (String xor : xorPrefixes) {
            bestXorPrefix = chooseBestXor(bestXorPrefix, xor);
        }
        return bestXorPrefix;
    }

    public static String chooseBestXor(String xor1Prefix, String xor2Prefix) {
        int i1 = getInvCoreIndex(xor1Prefix);
        int i2 = getInvCoreIndex(xor2Prefix);

        if (i1 < i2) {
            return xor1Prefix;
        } else if (i2 < i1) {
            return xor2Prefix;
        } else {
            if (i1 < 0) {
                return null;
            } else {
                return xor1Prefix;
            }
        }


    }

    public static int getCoreIndex(IVar prefix) {
        return INV_CORE_XORS.indexOf(prefix.getPrefix());
    }

    public static int compareVarsByCoreIndex(IVar var1, IVar var2) {
        Integer i1 = getCoreIndex(var1);
        Integer i2 = getCoreIndex(var2);
        return i1.compareTo(i2);
    }

    public static int compareCoreXorPrefixes(String prefix1, String prefix2) {
        Integer i1 = getInvCoreIndex(prefix1);
        Integer i2 = getInvCoreIndex(prefix2);
        return i1.compareTo(i2);
    }

    public static boolean isXor(VarCode xorVar) {
        return false;
    }
}
