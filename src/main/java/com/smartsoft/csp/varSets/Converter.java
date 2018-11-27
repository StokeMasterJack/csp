package com.smartsoft.csp.varSets;

public interface Converter<E> {

    E toE(int varId);

    int toVarId(E e);

}
