package com.tms.csp.ast;

import java.util.logging.Logger;

public class CspModules {


    public final Csp core;
    public final Csp nonCore;

    public CspModules(Csp core, Csp nonCore) {
        this.core = core;
        this.nonCore = nonCore;
    }

    public void printStats() {
        log.info("CspModules stats");
        log.info("  core:     " + core.getComplexConstraintCount());
        log.info("  non-core: " + nonCore.getComplexConstraintCount());
    }

    private static Logger log = Logger.getLogger(CspModules.class.getName());


}
