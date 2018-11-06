package com.tms.csp.ast;

public class XorCount implements Comparable<XorCount> {

    private final Exp xor;
    private final String prefix;
    private Integer count = 0;

    public XorCount(Exp xor) {
        this.xor = xor;
        prefix = xor.getPrefix();
    }

    public void increment() {
        count++;
    }

    public int getCount() {
        return count;
    }

    @Override
    public int compareTo(XorCount that) {
        return this.count.compareTo(that.count);
    }

    public Exp getXor() {
        return xor;
    }

    public String getPrefix() {
        return prefix;
    }

    @Override
    public String toString() {
        return prefix + ":" + count;
    }
}
