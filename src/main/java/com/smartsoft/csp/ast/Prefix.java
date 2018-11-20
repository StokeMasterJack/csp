package com.smartsoft.csp.ast;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.smartsoft.csp.VarInfo;
import com.smartsoft.csp.dnnf.vars.VarGrp;
import com.smartsoft.csp.varCodes.IVar;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

public enum Prefix implements VarConstants {

    YR, MDL, XCOL, ICOL,
    SER,
    ACY,
    GRD, TX, ENG, TRAN, DRV, CAB, BED,
    DISC,
    NULL;


    public static final EnumSet<Prefix> yearModel = of(YR, MDL);
    public static final EnumSet<Prefix> color = of(XCOL, ICOL);
    public static final EnumSet<Prefix> core = of(yearModel, color);
    public static final EnumSet<Prefix> inv = of(core, ACY);
    public static final EnumSet<Prefix> atts = of(GRD, TX, ENG, TRAN, DRV, DISC, BED, CAB);
    public static final EnumSet<Prefix> xors = of(core, atts);

    public static Map<VarGrp, EnumSet<Prefix>> prefixGroups;

    public static final char UNDERSCORE = '_';

    public String getTiny() {
        return name().toLowerCase().charAt(0) + "";
    }

    public static Prefix getPrefix(int index) {
        return values()[index];
    }

    public static int indexOf(String prefix) {
        Prefix[] values = values();
        for (int i = 0; i < values.length; i++) {
            Prefix p = values[i];
            if (p.name().equals(prefix)) {
                return i;
            }
        }
        return -1;
    }

    public boolean is(Prefix prefix) {
        return this.equals(prefix);
    }

    public boolean is(String prefix) {
        return this.name().equals(prefix);
    }

    public int getIndex() {
        return this.ordinal();
    }

    public boolean isYr() {
        return is(YR);
    }

    public boolean isMdl() {
        return is(MDL);
    }

    public boolean isXCol() {
        return is(XCOL);
    }

    public boolean isICol() {
        return is(ICOL);
    }

    public boolean isAcy() {
        return is(ACY);
    }


    public String getName() {
        return name();
    }

    public static ImmutableList<Prefix> getAll() {
        return ImmutableList.copyOf(values());
    }

    public static EnumSet<Prefix> getCore() {
        return core;
    }

    public static ImmutableList<Prefix> getInv() {
        return ImmutableList.copyOf(inv);
    }

    public static Prefix get(String sPrefix) {
        for (Prefix prefix : values()) {
            if (prefix.is(sPrefix)) {
                return prefix;
            }
        }
        return null;
    }

    public static boolean contains(String prefix) {
        return get(prefix) != null;
    }

    public static Prefix get(int index) {
        return values()[index];
    }

    public static Prefix get(IVar iVar) {
        return get(iVar.getPrefix());
    }

    public boolean isCore() {
        return core.contains(this);
    }

    public boolean isInv() {
        return inv.contains(this);
    }


    public boolean isXor() {
        return xors.contains(this);
    }

    public boolean isAtt() {
        return atts.contains(this);
    }

    public static String getVarAttribute(VarInfo varInfo, String varName, String attName) {
        if (varInfo == null) return null;
        String attVal = varInfo.getAttribute(null, varName, attName);
        if (attVal != null) {
            attVal = attVal.trim();
            if (attVal.isEmpty()) {
                attVal = null;
            }
        }
        return attVal;
    }

    public static Boolean getBooleanVarAttribute(VarInfo varInfo, String varName, String attName) {
        String v = getVarAttribute(varInfo, varName, attName);
        if (v == null) return null;
        return Boolean.parseBoolean(v);
    }

    public static Boolean isXor(String prefix) {
        return is(xors, prefix);
    }

    public static Prefix getFromList(List<Prefix> list, String prefixString) {
        for (Prefix p : list) {
            if (p.is(prefixString)) {
                return p;
            }
        }
        return null;
    }


    public static boolean isInv(String prefix) {
        return is(inv, prefix);
    }

    public static boolean isInvXor(String prefixString) {
        Prefix p = Prefix.get(prefixString);
        return p != null && p.isCore();
    }

    public String getVarCode(String localName) {
        return name() + "_" + localName;
    }

    public static Prefix getByTiny(String tiny) {
        Prefix p = get(tiny);
        if (p != null) {
            return p;
        }
        for (Prefix prefix : values()) {
            String t = prefix.getTiny();
            if (t != null && t.equals(tiny)) {
                return prefix;
            }
        }
        return null;
    }

    public static String expandTiny(String tiny) {
        Prefix p = getByTiny(tiny);
        if (p == null) {
            return tiny;
        }
        return p.name();
    }

    public static Prefix largeToTiny(String large) {
        for (Prefix prefix : values()) {
            String n = prefix.getName();
            if (n != null && n.equals(large)) {
                return prefix;
            }
        }
        return null;
    }

    public static boolean isEmpty(String prefix) {
        return prefix == null || prefix.trim().equals("");
    }

    public static boolean is(EnumSet<Prefix> set, String prefix) {
        if (isEmpty(prefix)) {
            return false;
        }
        for (Prefix p : set) {
            if (p.name().equals(prefix)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isCore(String prefix) {
        return is(core, prefix);
    }

    public static boolean isNull(String prefix) {
        return prefix == null || prefix.trim().equals("");
    }

    public static boolean isYr(String prefix) {
        return prefix.equals(YR.name());
    }

    public static boolean isMdl(String prefix) {
        return prefix.equals(MDL.name());
    }

    public static boolean isAcy(String prefix) {
        return prefix.equals(ACY.name());
    }

    public static boolean isXCol(String prefix) {
        return prefix.equals(XCOL.name());
    }

    public static boolean isICol(String prefix) {
        return prefix.equals(ICOL.name());
    }

    public static ImmutableSet<String> getCoreXorPrefixes() {
        ImmutableSet.Builder<String> b = ImmutableSet.builder();
        for (Prefix prefix : Prefix.core) {
            b.add(prefix.getName());
        }
        return b.build();
    }

    public static ImmutableSet<String> getInvPrefixes() {
        ImmutableSet.Builder<String> b = ImmutableSet.builder();
        for (Prefix prefix : Prefix.inv) {
            b.add(prefix.getName());
        }
        return b.build();
    }

    public static EnumSet<Prefix> of(EnumSet<Prefix> set1, EnumSet<Prefix> set2) {
        Class<Prefix> elementType = Prefix.class;
        EnumSet<Prefix> result = EnumSet.noneOf(elementType);
        for (Prefix prefix : set1) {
            result.add(prefix);
        }
        for (Prefix prefix : set2) {
            result.add(prefix);
        }
        return result;
    }

    public static EnumSet<Prefix> of(EnumSet<Prefix> set, Prefix... prefixes) {
        Class<Prefix> elementType = Prefix.class;
        EnumSet<Prefix> result = EnumSet.noneOf(elementType);
        result.addAll(set);
        Collections.addAll(result, prefixes);
        return result;
    }

    public static EnumSet<Prefix> of(Prefix... prefixes) {
        Class<Prefix> elementType = Prefix.class;
        EnumSet<Prefix> result = EnumSet.noneOf(elementType);
        Collections.addAll(result, prefixes);
        return result;
    }

    public static int builtinCount() {
        return values().length;
    }

    public static EnumSet<Prefix> noneOf() {
        Class<Prefix> elementType = Prefix.class;
        EnumSet<Prefix> result = EnumSet.noneOf(elementType);
        return result;
    }

    public static EnumSet<Prefix> getGroup(VarGrp q) {
        if (Prefix.prefixGroups == null) {
            Prefix.prefixGroups = initGroups();
        }
        return prefixGroups.get(q);
    }

    public static Prefix getPrefix(VarGrp key) {
        for (Prefix p : values()) {
            String pName = p.name();
            String kName = key.name();
            if (pName.equalsIgnoreCase(kName)) {
                return p;
            }
        }
        return null;
    }

    private static ImmutableMap<VarGrp, EnumSet<Prefix>> initGroups() {


        ImmutableMap.Builder<VarGrp, EnumSet<Prefix>> b = ImmutableMap.builder();
        b.put(VarGrp.YEAR_MODEL, yearModel);
        b.put(VarGrp.XCOL_ICOL, color);
        b.put(VarGrp.CORE, core);
        b.put(VarGrp.INV, inv);
        b.put(VarGrp.ATT, atts);
        b.put(VarGrp.XOR, xors);

        return b.build();
    }


    public int getScore() {
        if (isMdl()) return 10 * 5;
        if (isICol()) return 10 * 4;
        if (isXCol()) return 10 * 3;
        if (isYr()) return 10 * 2;
        return 0;
    }

    public int getCoreXorLevel() {
        if (isMdl()) return 1;
        if (isICol()) return 2;
        if (isXCol()) return 3;
        if (isYr()) return 4;
        throw new IllegalStateException();
    }
}
