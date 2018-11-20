package com.smartsoft.csp.util;

public abstract class TriDelegate implements Tri {

    abstract public Bit getValue();

    @Override
    public boolean is(boolean that) {
        return getValue().is(that);
    }

    @Override
    public boolean dup(boolean that) {
        return getValue().dup(that);
    }

    @Override
    public boolean isTrue() {
        return getValue().isTrue();
    }

    @Override
    public boolean isFalse() {
        return getValue().isFalse();
    }

    @Override
    public boolean isOpen() {
        return getValue().isOpen();
    }

    @Override
    public boolean isUnassigned() {
        return getValue().isUnassigned();
    }

    @Override
    public boolean isConstant() {
        return getValue().isConstant();
    }

    @Override
    public boolean isAssigned() {
        return getValue().isAssigned();
    }

    @Override
    public boolean boolValue() {
        return getValue().boolValue();
    }

    @Override
    public boolean isNonConstant() {
        return getValue().isNonConstant();
    }
}
