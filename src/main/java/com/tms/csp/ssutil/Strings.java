package com.tms.csp.ssutil;


import com.google.common.base.Preconditions;

import java.util.Collection;

public class Strings {


    public static String unCamalize(String name) {
        StringBuffer newString = new StringBuffer();
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if (Character.isUpperCase(c)) {
                newString.append(" ");
            }
            newString.append(c);
        }
        String s = newString.toString().trim();
        return capFirstLetter(s);
    }

    public static String capFirstLetter(String s) {
        if (isEmpty(s)) return null;
        String firstChar = s.substring(0, 1);
        String rest = s.substring(1);
        return firstChar.toUpperCase() + rest;
    }

    public static String uncapFirstLetter(String s) {
        if (isEmpty(s)) return null;
        String firstChar = s.substring(0, 1);
        String rest = s.substring(1);
        return firstChar.toLowerCase() + rest;
    }

    public static boolean notEmpty(String s1, String s2) {
        return !isEmpty(s1) && !isEmpty(s2);
    }

    public static boolean notEmpty(String s) {
        return !isEmpty(s);
    }

    public static boolean notEmpty(Object s) {
        return !isEmpty(s);
    }

    public static void checkNotEmpty(Object s) throws IllegalStateException {
        Preconditions.checkState(notEmpty(s));
    }

    public static void checkNotEmpty(Object s, String msg) throws IllegalStateException {
        Preconditions.checkState(notEmpty(s), msg);
    }

    public static boolean isEmpty(Object s) {
        return s == null || isEmpty(s.toString());
    }

    public static boolean isEmpty(String s) {
        if (s == null || s.length() == 0) {
            return true;
        } else {
            return false;
        }
    }

    public static void indent1(int tabCount, StringBuilder sb) {
        for (int i = 0; i < tabCount; i++) {
            sb.append("\t");
        }
    }

    public static String indent(int tabCount) {
        final StringBuilder sb = new StringBuilder();
        indent1(tabCount, sb);
        return sb.toString();
    }

    public static void indent(int tabCount, Object thingToPrint) {
        System.out.println(indent(tabCount) + thingToPrint);
    }

    public static String lpad(String unpaddedString, char padChar, int desiredFinalLength) {
        int padCount = desiredFinalLength - unpaddedString.length();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < padCount; i++) {
            sb.append(padChar);
        }
        sb.append(unpaddedString);
        return sb.toString();
    }

    public static String rpad(String unpaddedString, char padChar, int desiredFinalLength) {
        int padCount = desiredFinalLength - unpaddedString.length();
        StringBuilder sb = new StringBuilder();
        sb.append(unpaddedString);
        for (int i = 0; i < padCount; i++) {
            sb.append(padChar);
        }
        return sb.toString();
    }

    public static String pad(int spaceCount) {
        if (spaceCount == 0) return "";
        final StringBuffer sb = new StringBuffer();
        for (int i = 0; i < spaceCount; i++) {
            sb.append(" ");
        }
        return sb.toString();
    }

    public static String tab(int tabCount) {
        final StringBuffer sb = new StringBuffer();
        for (int i = 0; i < tabCount; i++) {
            sb.append("\t");
        }
        return sb.toString();
    }


    public static String nullNormalize(String s) {
        if (s == null) return null;
        s = s.trim();
        if (s.equals("")) return null;
        return s;
    }

    public static boolean containsWhitespace(String s) {
        return s.matches(".*\\s+.*");
    }

    public static boolean containsNonWordChar(String s) {
        return s.matches(".*\\W+.*");
    }

    public static boolean isInt(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static String crLf() {
        return "\r\n";
    }

    /**
     * Replace reserved XML characters with their XML entity equivalents.
     *
     * @param plainString input string
     * @return An escaped string
     */
    public static String escapeXml(String plainString) {
        if (plainString == null) return null;
        StringBuffer stringBuild = new StringBuffer();
        for (int i = 0; i < plainString.length(); i++) {
            char testChar = plainString.charAt(i);
            if (testChar == '<') stringBuild.append("&lt;");
            else if (testChar == '>') stringBuild.append("&gt;");
            else if (testChar == '&') stringBuild.append("&amp;");
            else if (testChar == '"') stringBuild.append("&quot;");
            else if (testChar == '\'') stringBuild.append("&apos;");
            else stringBuild.append(testChar);
        }
        return stringBuild.toString();
    }


    public static String[] toStringArray(Collection collection) {
        int i = 0;
        String[] a = new String[collection.size()];
        for (Object o : collection) {
            a[i] = o.toString();
            i++;
        }
        return a;
    }


    public static String getSimpleName(Class cls) {
        return cls.getSimpleName();
    }

    public static String getSimpleName(Object o) {
        return o.getClass().getSimpleName();
    }


    public static int containsHowMany(String outer, String inner) {
        int count = 0;
        int start = 0;
        while (true) {
            int i = outer.indexOf(inner, start);
            if (i == -1) {
                break;
            } else {
                count++;
                start = i + 1;
            }
        }
        return count;
    }

    public static boolean endsWith(StringBuffer sb, String s) {
        int sbL = sb.length();
        int sL = s.length();
        if (sbL < sL) return false;
        for (int i = 0; i < sL; i++) {
            int sIndex = i;
            int sbIndex = (sbL - sL) + sIndex;
            char sbChar = sb.charAt(sbIndex);
            char sChar = s.charAt(sIndex);
            if (sbChar != sChar) return false;
        }
        return true;
    }


}

