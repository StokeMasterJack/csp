package com.tms.csp.parse;

import com.tms.csp.ast.PLConstants;

public class Head implements PLConstants {


    public static boolean isNegated(int lit) {
        return lit < 1;
    }

    public static int getVarId(int lit) {
        return Math.abs(lit);
    }

    public static boolean getSign(int lit) {
        return lit >= 0;
    }

    public static boolean getSign(String head) {
        return !isNegated(head);
    }

    public static String getCode(int head) {
        return Math.abs(head) + "";
    }

    public static boolean isNegated(String head) {
        return head.charAt(0) == BANG;
    }

    public static String getVarCode(String head) {
        if (isNegated(head)) {
            return head.substring(1);
        } else {
            return head;
        }
    }

    public static boolean isConstantTrue(String head) {
        return head.equals(TRUE_TOKEN);
    }

    public static boolean isConstantFalse(String head) {
        return head.equals(FALSE_TOKEN);
    }


}
