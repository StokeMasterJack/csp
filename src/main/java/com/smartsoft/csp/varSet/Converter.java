package com.smartsoft.csp.varSet;

public interface Converter<E> {

    E toE(int varId);

    int toVarId(E e);

}
