package com.tms.csp.ast;

/**
 *
 */
public class PairRelType implements PLConstants {

    public static final int VAR_COUNT = 0;

    public static final int PERM_COUNT = 4; //2 ^ VAR_COUNT
    public static final int EXP_COUNT = 16; //2 ^ PERM_COUNT

    public static final int MIN_PERM = 0;
    public static final int MAX_PERM = 3;  //PERM_COUNT - 1

    public static final int MIN_REL_TYPE = 0;
    public static final int MAX_REL_TYPE = 15; //EXP_COUNT - 1;

    private final static PairRelType[] relTypes = new PairRelType[EXP_COUNT];
    private final static SignSign[] ands;

    public static final PairRelType False = new PairRelType(0);

    public static final PairRelType AndTT = new PairRelType(1);
    public static final PairRelType AndTF = new PairRelType(2);
    public static final PairRelType AndTD = new PairRelType(3);
    public static final PairRelType AndFT = new PairRelType(4);
    public static final PairRelType AndDT = new PairRelType(5);
    public static final PairRelType XorTT = new PairRelType(6);
    public static final PairRelType OrTT = new PairRelType(7);
    public static final PairRelType AndFF = new PairRelType(8);
    public static final PairRelType IffTT = new PairRelType(9);
    public static final PairRelType AndDF = new PairRelType(10);

    public static final PairRelType OrTF = new PairRelType(11);
    public static final PairRelType AndFD = new PairRelType(12);
    public static final PairRelType OrFT = new PairRelType(13);
    public static final PairRelType OrFF = new PairRelType(14);

    public static final PairRelType True = new PairRelType(15);


    static {


        ands = new SignSign[]{

        };


    }

    //core state
    private int index;


    private PairRelType(int index) {
        this.index = index;
    }

    public static PairRelType mk(int relType) {
        return relTypes[relType];
    }

    public static PairRelType mk(String pppp) {
        int rel = Integer.parseInt(pppp, 2);
        return new PairRelType(rel);
    }

//    public static PairRelType mkAnd(boolean sign1, boolean sign2) {
//        int rel = Integer.parseInt(pppp, 2);
//        return new PairRelType(rel);
//    }


    public static boolean isValidExp(int exp) {
        return exp >= MIN_REL_TYPE && exp <= MAX_REL_TYPE;
    }

    public static boolean checkExp(int exp) throws IllegalArgumentException {
        if (isValidExp(exp)) {
            return true;
        } else {
            throw new IllegalArgumentException("Bad exp[" + exp + "]");
        }
    }


    public static boolean isValidPerm(int perm) {
        return perm >= MIN_PERM && perm <= MAX_PERM;
    }

    public static boolean checkPerm(int perm) throws IllegalArgumentException {
        if (isValidPerm(perm)) {
            return true;
        } else {
            throw new IllegalArgumentException("Bad bitIndex[" + perm + "]");
        }
    }

    public boolean getBit(int bitIndex) {
        checkPerm(bitIndex);
        return (index & (1 << bitIndex)) != 0;
    }


    public void clearBit(int bitIndex) {
        index &= ~(1L << bitIndex);
    }

    public void setBit(int bitIndex, boolean value) {
        if (value) {
            setBit(bitIndex);
        } else {
            clearBit(bitIndex);
        }
    }

    public void setBit(int bitIndex) {
        checkPerm(bitIndex);
        this.index |= (1 << bitIndex);
    }

//
//    public static int getBitIndex(String vv) {
//        if (!v1 && !v2) return 0;
//        if (!v1 && v2) return 1;
//        if (v1 && !v2) return 2;
//        if (v1 && v2) return 3;
//        throw new IllegalStateException();
//    }
//
//    public PairRelType formula(boolean v1, boolean v2) {
//
//    }
//
//    public PairRelType out(boolean v1, boolean v2) {
//
//    }

//
//    public SignSign asAnd() {
//
//    }

    public static void main(String[] args) {

    }

    public int getIndex() {
        return index;
    }

    @Override
    public String toString() {
        return index + "";
    }
}
