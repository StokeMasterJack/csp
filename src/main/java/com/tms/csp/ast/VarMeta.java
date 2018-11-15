package com.tms.csp.ast;

import com.tms.csp.VarInfo;

import java.util.Set;

public class VarMeta implements VarInfo, VarConstants {

    private final static String FIO_SUFFIX = ".type=FIO";


    public VarInfo varInfo;

    public VarMeta(String varInfoClob) {
        this(VarInf.parse(varInfoClob));
    }

    public VarMeta(VarInfo varInfo) {
        this.varInfo = varInfo;
    }

    public VarMeta() {
        this((VarInfo) null);
    }

    @Override
    public boolean isFio(String varCode) {
        checkVarInfo();
        return varInfo.isFio(varCode);
    }

    @Override
    public boolean isPio(String varCode) {
        checkVarInfo();
        return varInfo.isPio(varCode);
    }

    @Override
    public boolean isVtc(String varCode) {
        checkVarInfo();
        return varInfo.isVtc(varCode);
    }

    @Override
    public boolean isInvAcy(String varCode) {
        return varInfo.isInvAcy(varCode);
    }

    public boolean isFio(Var var) {
        checkVarInfo();
        return varInfo.isFio(var.getVarCode());
    }

    public void checkVarInfo() {
        if (varInfo == null) {
            throw new UnsupportedOperationException(THIS_OPERATION_REQUIRES_A_VAR_INFO);
        }
    }

    @Override
    public String getAttribute(Set<String> context, String varName, String attName) {
        checkVarInfo();
        return varInfo.getAttribute(context, varName, attName);
    }

    public boolean isInvVar(String varCode) {
        return isCoreXorVar(varCode) || isInvAcyVar(varCode);
    }


    public static boolean isYearVar(String varCode) {
        return varCode.startsWith(YR_);
    }

    public static boolean isModelVar(String varCode) {
        return varCode.startsWith(MDL_);
    }

    public static boolean isXColVar(String varCode) {
        return varCode.startsWith(XCOL_);
    }

    public static boolean isIColVar(String varCode) {
        return varCode.startsWith(ICOL_);
    }

    public static boolean isCoreXorVar(String varCode) {
        return isYearVar(varCode) || isModelVar(varCode) || isXColVar(varCode) || isIColVar(varCode);
    }

    public boolean isCoreAcyVar(String varCode) {
        return varInfo.isInvAcy(varCode);
    }

    public boolean isCoreVar(String varCode) {
        return isCoreXorVar(varCode) || isCoreAcyVar(varCode);
    }

    public boolean isInvAcyVar(String varCode) {
        return varInfo.isInvAcy(varCode);
    }

    public boolean isInvAcyVar(Var var) {
        return isInvAcyVar(var.getVarCode());
    }

    public boolean isAcyVar(String varCode) {
        return varCode.startsWith(ACY_);
    }

    public void setVarInfo(VarInfo varInfo) {
        this.varInfo = varInfo;
    }


    public static boolean isAcyPrefix(String prefix) {
        if (prefix == null) return false;
        return prefix.equals(ACY);
    }

    public static boolean isIColPrefix(String prefix) {
        if (prefix == null) return false;
        return prefix.equals(ICOL);
    }

    public static boolean isXColPrefix(String prefix) {
        if (prefix == null) return false;
        return prefix.equals(XCOL);
    }

    public static boolean isModelPrefix(String prefix) {
        if (prefix == null) return false;
        return prefix.equals(MDL);
    }

    public static boolean isYearPrefix(String prefix) {
        if (prefix == null) return false;
        return prefix.equals(YR);
    }

    public static boolean isSeriesPrefix(String prefix) {
        if (prefix == null) return false;
        return prefix.equals(SER);
    }

    public static boolean isCoreInvPrefix(String prefix) {
        if (prefix == null) return false;
        return isYearOrModelPrefix(prefix) || isColorPrefix(prefix);
    }

    public static boolean isYearOrModelPrefix(String prefix) {
        if (prefix == null) return false;
        return isYearPrefix(prefix) || isModelPrefix(prefix);
    }

    public static boolean isColorPrefix(String prefix) {
        if (prefix == null) return false;
        return isXColPrefix(prefix) || isIColPrefix(prefix);
    }

    @Override
    public String getImpliedVarCode(Set<String> context, String featureType) {
        checkVarInfo();
        return varInfo.getImpliedVarCode(context, featureType);
    }

    @Override
    public boolean isAssociated(Set<String> context, String varCode) {
        checkVarInfo();
        return varInfo.isAssociated(context, varCode);
    }


    @Override
    public int getPrice(String[] picks) {
        checkVarInfo();
        return varInfo.getPrice(picks);
    }

    @Override
    public String getFeatureType(String pickCode) {
        checkVarInfo();
        return varInfo.getFeatureType(pickCode);
    }

    @Override
    public boolean isDerived(String[] context, String pickCode) {
        checkVarInfo();
        return varInfo.isDerived(context, pickCode);
    }

    @Override
    public String getFeaturesByType(String[] context, String type) {
        checkVarInfo();
        return varInfo.getFeaturesByType(context, type);
    }


}
