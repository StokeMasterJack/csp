package com.tms.csp.util.listPlusOne;

import com.google.common.hash.Funnel;
import com.google.common.hash.PrimitiveSink;

public class ListPlusOneFunnel<E extends Comparable<E>> implements Funnel<PlusOne<E>> {

    private final Funnel<E> elementFunnel;

    public ListPlusOneFunnel(Funnel<E> elementFunnel) {
        this.elementFunnel = elementFunnel;
    }

    @Override
    public void funnel(PlusOne<E> from, PrimitiveSink into) {
        if (from == null) return;
        E element = from.getElement();
        elementFunnel.funnel(element, into);
        PlusOne<E> parent = from.getParent();
        funnel(parent, into);
    }
}


