package com.tms.csp.ast;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.tms.csp.ast.formula.Formula;
import com.tms.csp.ast.formula.KFormula;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.tms.csp.ssutil.Strings.rpad;

public class XorCounts {

    private final Map<String, XorCount> map;

    public static XorCounts count(Csp csp) {
        return count(csp.getComplexIt());
    }

    public static XorCounts count(Formula formula) {
        return count(formula.argIt());
    }

    public static XorCounts count(Iterable<Exp> constraints) {
        return new XorCounts(constraints);
    }

    public static Xor getBestXor(Iterable<Exp> constraints) {
        XorCounts count = count(constraints);
        XorCount max = count.getMax();
        if (max == null) return null;
        return max.getXor().asXor();
    }


    public static Xor getMax(KFormula formula) {
        return getBestXor(formula.argIt());
    }

    public static Xor getMax(Csp csp) {
        return getBestXor(csp.getComplexIt());
    }


    private XorCounts(Iterable<Exp> constraints) {
        List<Exp> xors = Csp.getXorConstraints(constraints);
        if (xors.isEmpty()) {
            map = ImmutableMap.of();
        } else {
            map = initMap(xors);
            countInternal(constraints);
        }
    }

    private static Map<String, XorCount> initMap(List<Exp> xors) {
        ImmutableMap.Builder<String, XorCount> b2 = ImmutableMap.builder();
        for (Exp xor : xors) {
            b2.put(xor.getPrefix(), new XorCount(xor));
        }
        return b2.build();
    }


    private void countInternal(Iterable<Exp> args) {
        for (Exp arg : args) {
            countInternal(arg);
        }
    }

    private void countInternal(Exp e) {
        if (e.isLit()) {
            String p = e.getPrefix();
            XorCount xorCount = map.get(p);
            if (xorCount != null) {
                xorCount.increment();
            }
        } else if (e.isComplex()) {
            countInternal(e.argIt());
        } else if (e.isConstant()) {
            //ignore
        } else {
            throw new IllegalStateException(e.getClass().getName());
        }
    }

    public void print() {
        for (XorCount xorCount : getCountsSorted()) {
            System.err.println(rpad(xorCount.getPrefix(), ' ', 10) + ": " + xorCount.getCount());
        }
        System.err.println("Max: " + getMax());
    }

    public List<XorCount> getCountsSorted() {
        if (map.isEmpty()) return ImmutableList.of();
        ArrayList<XorCount> a = new ArrayList<XorCount>(map.values());
        Collections.sort(a);
        return ImmutableList.copyOf(a);
    }


    public XorCount getMax() {
        if (map.isEmpty()) return null;
        return Collections.max(map.values());
    }
}
