package com.tms.csp.solver2.minisat.core;

import java.io.PrintStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Contains some statistics regarding the search.
 * 
 * @author daniel
 * 
 */
public class SolverStats implements Serializable {
    private static final long serialVersionUID = 1L;

    public int starts;

    public long decisions;

    public long propagations;

    public long inspects;

    public long conflicts;

    public long learnedliterals;

    public long learnedbinaryclauses;

    public long learnedternaryclauses;

    public long learnedclauses;

    public long ignoredclauses;

    public long rootSimplifications;

    public long reducedliterals;

    public long changedreason;

    public int reduceddb;

    public int shortcuts;

    public long updateLBD;

    public int importedUnits;

    public void reset() {
        this.starts = 0;
        this.decisions = 0;
        this.propagations = 0;
        this.inspects = 0;
        this.shortcuts = 0;
        this.conflicts = 0;
        this.learnedliterals = 0;
        this.learnedclauses = 0;
        this.ignoredclauses = 0;
        this.learnedbinaryclauses = 0;
        this.learnedternaryclauses = 0;
        this.rootSimplifications = 0;
        this.reducedliterals = 0;
        this.changedreason = 0;
        this.reduceddb = 0;
        this.updateLBD = 0;
        this.importedUnits = 0;
    }

    public void printStat(PrintStream out, String prefix) {
        out.println(prefix + "starts\t\t: " + this.starts);
        out.println(prefix + "conflicts\t\t: " + this.conflicts);
        out.println(prefix + "decisions\t\t: " + this.decisions);
        out.println(prefix + "propagations\t\t: " + this.propagations);
        out.println(prefix + "inspects\t\t: " + this.inspects);
        out.println(prefix + "shortcuts\t\t: " + this.shortcuts);
        out.println(prefix + "learnt literals\t: " + this.learnedliterals);
        out.println(prefix + "learnt binary clauses\t: "
                + this.learnedbinaryclauses);
        out.println(prefix + "learnt ternary clauses\t: "
                + this.learnedternaryclauses);
        out.println(prefix + "learnt fact\t: " + this.learnedclauses);
        out.println(prefix + "ignored fact\t: " + this.ignoredclauses);
        out.println(prefix + "root simplifications\t: "
                + this.rootSimplifications);
        out.println(prefix + "removed literals (reason simplification)\t: "
                + this.reducedliterals);
        out.println(prefix + "reason swapping (by a shorter reason)\t: "
                + this.changedreason);
        out.println(prefix + "Calls to reduceDB\t: " + this.reduceddb);
        out.println(prefix + "Number of update (reduction) of LBD\t: "
                + this.updateLBD);
        out.println(prefix + "Imported unit clauses\t: "
                + this.importedUnits);
    }

    public Map<String, Number> toMap() {
        Map<String, Number> map = new HashMap<String, Number>();
//        for (Field f : this.getClass().getFields()) {
//            try {
//                map.put(f.getName(), (Number) f.get(this));
//            } catch (IllegalArgumentException e) {
//                // ignores silently
//            } catch (IllegalAccessException e) {
//                // ignores silently
//            }
//        }
        return map;
    }
}
