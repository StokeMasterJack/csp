package com.tms.csp.ssutil;


import java.io.IOException;

import static com.tms.csp.ssutil.Strings.indent;

public class Console {

    public static void prindent(int depth, Object msg) {
        System.err.println(Strings.indent(depth) + msg);
    }

    public static void prindent(int depth, String msg) {
        System.err.println(Strings.indent(depth) + msg);
    }

    public static void prindent(int depth, int msg) {
        System.err.println(Strings.indent(depth) + msg);
    }

    public static void prindent() {
        System.err.println();
    }

    public static Appendable prindent(int depth, Object msg, Appendable sb) {
        return prindent(depth, msg.toString(), sb);
    }

    public static Appendable prindent(int depth, String msg, Appendable sb) {
        append(sb, Strings.indent(depth));
        append(sb, msg);
        append(sb, '\n');
        return sb;
    }

    public static Appendable prindent(int depth, int msg, Appendable sb) {
        return prindent(depth, msg + "", sb);
    }

    public static Appendable prindent(Appendable sb) {
        return append(sb, '\n');
    }


    public static Appendable append(Appendable appendable, CharSequence csq) {
        try {
            return appendable.append(csq);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Appendable append(Appendable appendable, CharSequence csq, int start, int end) {
        try {
            return appendable.append(csq, start, end);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Appendable append(Appendable appendable, char c) {
        try {
            return appendable.append(c);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}

