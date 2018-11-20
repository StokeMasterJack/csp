package com.smartsoft.csp.ast;

import com.google.common.base.Ascii;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;

public interface PLConstants {


    boolean SAT_COUNT_CACHING = true;
    boolean SMOOTH_COMPILE = true;

    boolean USE_DEALERS_STRICT = false;
    boolean USE_DEALERS_BITS = true;

    boolean USE_MSRP_STRICT = false;
    boolean USE_MSRP_BITS = true;
    boolean USE_MSRP_BUCKETS = false;

    String DLR_PREFIX = "DLR";

    String MSRP_PREFIX = "MSRP";

    String QTY_PREFIX = "QTY";
    String YR_PREFIX = "YR";
    String MDL_PREFIX = "MDL";
    String SER_PREFIX = "SER";
    String XCOL_PREFIX = "XCOL";
    String ICOL_PREFIX = "ICOL";
    String ACY_PREFIX = "ACY";

    int CR_INT = Ascii.CR;
    int LF_INT = Ascii.LF;
    int NL_INT = Ascii.NL;


    char CR_CHAR = '\r';
    char LF_CHAR = '\n';

    char LF = LF_CHAR;

    String NEW_LINE = LF + "";

    char UNDERSCORE = '_';
    char COLON = ':';
    char BANG = '!';
    char SPACE_CHAR = ' ';
    char ARG_SEP = SPACE_CHAR;
    char QUOTE = '\'';
    char EQ = '=';
    String ARG_SEP_TOKEN = " ";
    char LPAREN = '(';
    String LPAREN_TOKEN = "(";
    String RPAREN_TOKEN = ")";
    char RPAREN = ')';

    int TRUE_EXP_ID = 0;
    int FALSE_EXP_ID = 1;

    String TRUE_TOKEN = "true";
    String FALSE_TOKEN = "false";
    String NOT_TOKEN = "!";

    char TINY_OP_PREFIX = '_';
    String TINY_OP_PREFIX_STR = TINY_OP_PREFIX + "";

    public static final String OR_TOKEN = "or";
    public static final String AND_TOKEN = "and";


    public static enum ArgType {
        VarId,
        VarIds,
        VarCode,
        VarCodes,
        ExpId,
        ExpIds,
        None,
        ExpText
    }

    /*
    store types
    vr,
    lit,
    varIds,
    expIds
     */

    public static final char COMMENT_CHAR = '#';


    public static final char TRUE_CHAR = 'T';
    public static final char FALSE_CHAR = 'F';


    public static enum Value {

        OPEN,
        TRUE,
        FALSE,
        CONFLICT;

        /**
         * This is specifically designed to support NegVar and NegComplex
         */
        public Value flip() {
            switch (this) {
                case OPEN:
                    return OPEN;
                case TRUE:
                    return FALSE;
                case FALSE:
                    return TRUE;
                case CONFLICT:
                    return CONFLICT;
                default:
                    throw new IllegalStateException();
            }
        }
    }


    //Exp types

    /**
     * Type for pos type
     */
    public enum MacroType {
        CONSTANT, LITERAL, COMPLEX;

        public boolean isComplex() {
            return this.equals(COMPLEX);
        }

        public boolean isConstant() {
            return this.equals(CONSTANT);
        }

        public boolean isLiteral() {
            return this.equals(LITERAL);
        }
    }

    public static final int NARY_MAX_ARG_COUNT = 100000;


    public enum PosOp {

        TRUE(MacroType.CONSTANT, 0, 't'),
        VAR(MacroType.LITERAL, 0, 'v'),
        OR(MacroType.COMPLEX, NARY_MAX_ARG_COUNT, 'o'),
        AND(MacroType.COMPLEX, NARY_MAX_ARG_COUNT, 'a'),
        XOR(MacroType.COMPLEX, NARY_MAX_ARG_COUNT, 'x'),
        NAND(MacroType.COMPLEX, 2, 'n'),
        IMP(MacroType.COMPLEX, 2, 'i'),
        RMP(MacroType.COMPLEX, 2, 'r'),
        IFF(MacroType.COMPLEX, 2, 'b'), //bi-imp
        DC(MacroType.COMPLEX, NARY_MAX_ARG_COUNT, 'd')
//        , Vars(MacroType.COMPLEX, NARY_MAX_ARG_COUNT, 'd')

        ;

        public final MacroType macroType;
        public final int maxArgCount;
        public final char tinyChar;
        public final int tinyId;
        public final String tokenTiny;
        public final String token;

        private static List<PosOp> complexOps1;
        private static EnumSet<PosOp> complexOps2;
        private static EnumSet<PosOp> naryOps2;
        private static ImmutableMap<String, PosOp> posComplexOpMap;

        PosOp(MacroType macroType, int maxArgCount, char tinyChar) {
            this.macroType = macroType;
            this.maxArgCount = maxArgCount;


            this.token = name().toLowerCase();


            this.tinyChar = tinyChar;
            this.tinyId = ordinal();
            this.tokenTiny = Integer.toString(ordinal(), Character.MAX_RADIX);
        }

        public static List<PosOp> getComplexOps1() {
            if (complexOps1 == null) complexOps1 = posComplexList();
            return complexOps1;
        }

        public static Map<String, PosOp> getPosComplexOpMap() {
            ImmutableMap.Builder<String, PosOp> b = ImmutableMap.builder();
            if (posComplexOpMap == null) {
                List<PosOp> ops = posComplexList();
                for (PosOp op : ops) {
                    b.put(op.name().toLowerCase(), op);
                }
                posComplexOpMap = b.build();
            }

            return posComplexOpMap;
        }

        private static List<PosOp> posComplexList() {
            ImmutableList.Builder<PosOp> b = ImmutableList.builder();
            for (PosOp posOp : PosOp.values()) {
                if (posOp.isComplex()) {
                    b.add(posOp);
                }
            }
            return b.build();
        }

        public static EnumSet<PosOp> getComplexOps2() {
            if (complexOps2 == null) complexOps2 = createComplexOps2();
            return complexOps2;
        }

        public static EnumSet<PosOp> getNaryOps2() {
            if (naryOps2 == null) naryOps2 = createNaryComplexOps2();
            return complexOps2;
        }

        private static EnumSet<PosOp> createComplexOps2() {
            EnumSet<PosOp> tmp = EnumSet.noneOf(PosOp.class);
            for (PosOp posOp : values()) {
                if (posOp.isComplex()) {
                    tmp.add(posOp);
                }
            }
            return tmp;
        }

        private static EnumSet<PosOp> createNaryComplexOps2() {
            EnumSet<PosOp> tmp = EnumSet.noneOf(PosOp.class);
            for (PosOp posOp : values()) {
                if (posOp.isComplex() && posOp.isNaryCapable()) {
                    tmp.add(posOp);
                }
            }
            return tmp;
        }

        public boolean isNaryCapable() {
            return getMaxArgCount() > 2;
        }


        public MacroType getMacroType() {
            return macroType;
        }

        public char getTinyChar() {
            return tinyChar;
        }

        public boolean isComplex() {
            return macroType.isComplex();
        }

        public boolean isConstant() {
            return macroType.isConstant();
        }

        public boolean isLiteral() {
            return macroType.isLiteral();
        }

        public boolean isNand() {
            return this.equals(NAND);
        }

        public boolean isConflict() {
            return this.equals(NAND);
        }

        public boolean isIff() {
            return this.equals(IFF);
        }

        public boolean isImp() {
            return this.equals(IMP);
        }

        public boolean isRmp() {
            return this.equals(RMP);
        }

        public boolean isXor() {
            return this.equals(XOR);
        }

        public boolean isAnd() {
            return this.equals(AND);
        }

        public boolean isOr() {
            return this.equals(OR);
        }

        public boolean isDontCare() {
            return this.equals(DC);
        }

        public boolean isTrue() {
            return this.equals(TRUE);
        }

        public int getMaxArgCount() {
            return maxArgCount;
        }

        public String getComplexOpTokenTiny() {
            assert isComplex();
            return TINY_OP_PREFIX + "" + getTinyChar();
        }

        public String getComplexOpToken() {
            assert isComplex();
            return token;
        }

        public String getComplexOpToken(Ser a) {
            assert isComplex();
            if (a.tiny) {
                return getComplexOpTokenTiny();
            } else {
                return getComplexOpToken();
            }
        }




    }

    public static class ExpType {

        public final boolean sign;
        public final PosOp expType;

        public ExpType(boolean sign, PosOp expType) {
            this.sign = sign;
            this.expType = expType;
        }
    }

}


