package com.tms.csp.ssutil;

import java.util.logging.Logger;

public class TT {

    private final boolean enabled;
    private long t = System.currentTimeMillis();


    public TT(boolean enabled) {
        this.enabled = enabled;
    }

    public TT() {
        this(true);
    }

    public long t(String lbl) {
        long delta = System.currentTimeMillis() - t;
        if (enabled) {
            log.info(lbl + " delta: " + delta);
        }
        t = System.currentTimeMillis();
        return delta;
    }

    private static Logger log = Logger.getLogger(TT.class.getName());

}
