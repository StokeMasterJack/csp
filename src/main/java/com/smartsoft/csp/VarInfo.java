package com.smartsoft.csp;

import java.util.Set;

/**
 * Callback used to access attributes - used formula range fact.
 * Range fact will only be valid if varInfo is set before passing range fact into setUserConstraint
 */
public interface VarInfo {

    String ALACARTE = "alacarte";
    String FIO = "fio";
    String PIO = "pio";
    String TYPE = "type";
    String DERIVED = "derived";
    String VTC = "vtc";

    String getAttribute(Set<String> context, String varName, String attName);

    boolean isFio(String varCode);

    boolean isPio(String varCode);

    boolean isVtc(String varCode);

    /**
     * ACY and (fio or (pio and !alaCarte)
     */
    boolean isInvAcy(String varCode);

    String getImpliedVarCode(Set<String> context, String featureType);

    boolean isAssociated(Set<String> context, String varCode);

    int getPrice(String[] picks);

    String getFeatureType(String pickCode);

    boolean isDerived(String[] context, String pickCode);

    String getFeaturesByType(String[] context, String type);

}
