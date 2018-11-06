package com.tms.csp.fm;

import com.tms.csp.fm.dnnf.Value;

public enum Bit implements Tri {

    TRUE((byte) -2),
    FALSE((byte) -1),
    OPEN((byte) 0);

    public static final byte VAR_STATE_TRUE = -2;
    public static final byte VAR_STATE_FALSE = -1;
    public static final byte VAR_STATE_OPEN = 0;     //or more

    private final byte value;

    private Bit(byte value) {
        this.value = value;
    }

    public byte value() {
        return value;
    }

    public char toChar() {
        switch (this) {
            case TRUE:
                return '1';
            case FALSE:
                return '0';
            case OPEN:
                return '-';
            default:
                throw new IllegalStateException();
        }
    }

    public static Bit fromInt(int bit) {
        return fromByte((byte) bit);
    }

    public static Bit fromByte(byte bit) {
        for (Bit b : values()) {
            if (b.value == bit) return b;
        }
        throw new IllegalArgumentException("Bad bit value: [" + bit + "]");
    }

    public boolean matches(byte bit) {
        return bit == value;
    }

    public static Bit fromBool(boolean value) {
        return value ? TRUE : FALSE;
    }

    public static boolean isUnassigned(byte triState) {
        return triState == OPEN.value;
    }

    public static boolean isTrue(byte triState) {
        return triState == TRUE.value;
    }

    @Override
    public boolean isTrue() {
        return this == TRUE;
    }

    @Override
    public boolean isFalse() {
        return this == FALSE;
    }

    @Override
    public boolean isOpen() {
        return this == OPEN;
    }

    @Override
    public boolean isNonConstant() {
        return isOpen();
    }

    @Override
    public boolean isConstant() {
        return !isOpen();
    }

    public boolean isAssigned() {
        return !isOpen();
    }


    public boolean boolValue() {
        if (isTrue()) return true;
        if (isFalse()) return false;
        throw new IllegalStateException();
    }

    public Value toValue() {
        if (isTrue()) return Value.TRUE;
        else if (isFalse()) return Value.FALSE;
        else if (isOpen()) {
            return Value.NULL;
        } else {
            throw new IllegalStateException();
        }
    }

}
