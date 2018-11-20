package com.smartsoft.csp.util;

public enum Bit implements Tri {

    TRUE((byte) 1),
    FALSE((byte) 0),
    OPEN((byte) -1);


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

    public boolean dup(boolean that) {
        return boolValue() == that;
    }

    public boolean is(boolean that) {
        return isAssigned() && boolValue() == that;
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

    public boolean isFalseOrOpen() {
        return isFalse() || isOpen();
    }

    @Override
    public boolean isOpen() {
        return this == OPEN;
    }

    public boolean isUnassigned() {
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


    public static Tri fromChar(String s, int i) {
        s = s.toUpperCase();
        char c = s.charAt(i);
        return fromChar(c);
    }

    public static Tri fromChar(char c) {
        if (c == TRUE_CHAR) return TRUE;
        if (c == FALSE_CHAR) return FALSE;
        if (c == OPEN_CHAR) return OPEN;
        throw new IllegalStateException();
    }

    private static final char TRUE_CHAR = 'T';
    private static final char FALSE_CHAR = 'F';
    private static final char OPEN_CHAR = '-';

    private static final char[] chars = new char[]{'-', 'F', 'T'};

    public static void printAllCombosFor3Vars() {


        for (int v0 = 0; v0 < 3; v0++) {
            for (int v1 = 0; v1 < 3; v1++) {
                for (int v2 = 0; v2 < 3; v2++) {
                    System.err.print(chars[v0] + "" + chars[v1] + "" + chars[v2]);
                    System.err.print(": ");
                    System.err.print("expected");
                    System.err.println();
                }
            }
        }
    }

    public static void printAllCombosFor2Vars() {

        for (int v0 = 0; v0 < 3; v0++) {
            for (int v1 = 0; v1 < 3; v1++) {
                System.err.print(chars[v0] + "" + chars[v1]);
                System.err.print(": ");
                System.err.print("expected");
                System.err.println();
            }
        }
    }

    public static Bit and(Bit e1, Bit e2) {
        if (e1.isFalse() || e2.isFalse()) return Bit.FALSE;
        if (e1.isTrue() && e2.isTrue()) return Bit.TRUE;
        return Bit.OPEN;
    }
}
