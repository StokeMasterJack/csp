package com.tms.csp.solver2.minisat.core;

import java.io.Serializable;
import java.util.Iterator;

import com.tms.csp.solver2.core.Vec;
import com.tms.csp.solver2.specs.IVec;

/**
 * Agregator for conflict timers (composite design pattern).
 * 
 * @author daniel
 * 
 */
public class ConflictTimerContainer implements Serializable, ConflictTimer {

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    private final IVec<ConflictTimer> timers = new Vec<ConflictTimer>();

    ConflictTimerContainer add(ConflictTimer timer) {
        this.timers.push(timer);
        return this;
    }

    ConflictTimerContainer remove(ConflictTimer timer) {
        this.timers.remove(timer);
        return this;
    }

    public void reset() {
        Iterator<ConflictTimer> it = this.timers.iterator();
        while (it.hasNext()) {
            it.next().reset();
        }
    }

    public void newConflict() {
        Iterator<ConflictTimer> it = this.timers.iterator();
        while (it.hasNext()) {
            it.next().newConflict();
        }
    }
}