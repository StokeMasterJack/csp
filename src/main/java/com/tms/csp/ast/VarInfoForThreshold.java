package com.tms.csp.ast;

import com.tms.csp.VarInfo;

import java.util.Set;

public class VarInfoForThreshold implements VarInfo {


    @Override
    public String getAttribute(Set<String> context, String varCode, String attName) {
        //String th = space.getAttribute(ctx, ym.model.getVarCode(), "modelthreshold");
        //colorthreshold
        if ("modelthreshold".equals(attName) || "colorthreshold".equals(attName) || "accessorythreshold".equals(attName)) {
            return "0";
        }
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isFio(String varCode) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isPio(String varCode) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isVtc(String varCode) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isInvAcy(String varCode) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getImpliedVarCode(Set<String> context, String featureType) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isAssociated(Set<String> context, String varCode) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getPrice(String[] picks) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getFeatureType(String pickCode) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isDerived(String[] context, String pickCode) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getFeaturesByType(String[] context, String type) {
        throw new UnsupportedOperationException();
    }
}
