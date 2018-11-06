package com.tms.csp.util;

public interface PairCallback<T> {
    void processPair(T m1, T m2) throws StopIteratingException;
}
