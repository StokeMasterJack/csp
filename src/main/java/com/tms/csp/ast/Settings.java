package com.tms.csp.ast;

public class Settings {

    public static final Settings INSTANCE = new Settings();

    public boolean verbose = false;
    public boolean autoPropagate = false;
    public boolean logEvents = false;
    public int maxLines = Integer.MAX_VALUE;

    private Settings() {
    }

    public Parse parse = new Parse();

    public Ser ser = new Ser();

    public static class Parse {
        public boolean tiny = false;
    }

    public static Settings get() {
        return INSTANCE;
    }
}
