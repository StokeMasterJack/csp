package com.tms.csp.varCodes;

import com.tms.csp.ast.Prefix;

import javax.annotation.Nonnull;
import java.util.EnumSet;

public interface IVar {

    String getVarCode();

    @Nonnull
    String getPrefix();

    String getLocalName();


    boolean isAny(String... prefixes);

    boolean is(EnumSet<Prefix> prefixes);

    boolean is(String prefix);

    boolean is(Prefix prefix);

    boolean isAcy();

    boolean isCoreXor();

    boolean isInv();

    boolean isXorChild();

    Prefix getPrefix2();

    VarCode toVarCode();

    boolean isYear();
}
