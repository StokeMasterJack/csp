package com.tms.csp.util.varSets;

public interface Converter<E> {

    E toE(int varId);

    int toVarId(E e);

}
