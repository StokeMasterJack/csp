package com.tms.csp.util;

import com.tms.csp.ast.PLConstants;

public class SpaceUtil implements PLConstants {

    public static boolean isEven(int x) {
        return (x & 1) == 0;
    }

    public static boolean isOdd(int x) {
        return !isEven(x);
    }

    public static int parseIntBase36(String base36) {
        return Integer.parseInt(base36, 36);
    }
}
