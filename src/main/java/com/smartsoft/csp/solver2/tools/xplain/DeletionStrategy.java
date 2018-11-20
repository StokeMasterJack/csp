package com.smartsoft.csp.solver2.tools.xplain;

import java.util.Map;
import java.util.Set;

import com.smartsoft.csp.solver2.core.VecInt;
import com.smartsoft.csp.solver2.specs.ISolver;
import com.smartsoft.csp.solver2.specs.IVecInt;
import com.smartsoft.csp.solver2.specs.IteratorInt;
import com.smartsoft.csp.solver2.specs.TimeoutException;

/**
 * An implementation of the deletion based minimization.
 * 
 * 
 * @since 2.1
 */
public class DeletionStrategy implements MinimizationStrategy {

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    private boolean computationCanceled;

    public void cancelExplanationComputation() {
        this.computationCanceled = true;
    }

    public IVecInt explain(ISolver solver, Map<Integer, ?> constrs,
            IVecInt assumps) throws TimeoutException {
        this.computationCanceled = false;
        IVecInt encodingAssumptions = new VecInt(constrs.size()
                + assumps.size());
        assumps.copyTo(encodingAssumptions);
        IVecInt firstExplanation = solver.unsatExplanation();
        IVecInt results = new VecInt(firstExplanation.size());
        if (firstExplanation.size() == 1) {
            results.push(-firstExplanation.get(0));
            return results;
        }
        if (solver.isVerbose()) {
            System.out.print(solver.getLogPrefix() + "initial unsat core ");
            firstExplanation.sort();
            for (IteratorInt it = firstExplanation.iterator(); it.hasNext();) {
                System.out.print(constrs.get(-it.next()));
                System.out.print(" ");
            }
            System.out.println();
            solver.printStat(System.out, "c ");
        }
        for (int i = 0; i < firstExplanation.size();) {
            if (assumps.contains(firstExplanation.get(i))) {
                firstExplanation.delete(i);
            } else {
                i++;
            }
        }
        Set<Integer> constraintsVariables = constrs.keySet();
        IVecInt remainingVariables = new VecInt(constraintsVariables.size());
        for (Integer v : constraintsVariables) {
            remainingVariables.push(v);
        }
        int p;
        for (IteratorInt it = firstExplanation.iterator(); it.hasNext();) {
            p = it.next();
            if (p < 0) {
                p = -p;
            }
            remainingVariables.remove(p);
        }

        remainingVariables.copyTo(encodingAssumptions);
        int unsatcorebegin = encodingAssumptions.size();
        firstExplanation.copyTo(encodingAssumptions);
        assert !solver.isSatisfiable(encodingAssumptions);
        int unsatcorelimit = encodingAssumptions.size() - 1;
        for (int i = unsatcorebegin; i < unsatcorelimit; i++) {
            if (this.computationCanceled) {
                throw new TimeoutException();
            }
            encodingAssumptions.set(i, -encodingAssumptions.get(i));
            if (solver.isVerbose()) {
                System.out.println(solver.getLogPrefix() + "checking "
                        + constrs.get(encodingAssumptions.get(i)) + " ...");
            }
            if (solver.isSatisfiable(encodingAssumptions)) {
                encodingAssumptions.set(i, -encodingAssumptions.get(i));
                results.push(-encodingAssumptions.get(i));
                if (solver.isVerbose()) {
                    System.out.println(solver.getLogPrefix() + "mandatory.");
                }
            } else {
                if (solver.isVerbose()) {
                    System.out.println(solver.getLogPrefix() + "not needed.");
                }
            }
        }
        if (results.size() == 0) {
            // the last group must be the cause of the inconsistency
            results.push(-encodingAssumptions.get(unsatcorelimit));
            if (solver.isVerbose()) {
                System.out.println(solver.getLogPrefix()
                        + "skipping last test,the remaining element "
                        + constrs.get(encodingAssumptions.get(unsatcorelimit))
                        + " is causing the inconsistency!");
            }
        } else {
            encodingAssumptions.set(unsatcorelimit,
                    -encodingAssumptions.get(unsatcorelimit));
            if (solver.isVerbose()) {
                System.out.println(solver.getLogPrefix() + "checking "
                        + constrs.get(encodingAssumptions.get(unsatcorelimit))
                        + " ...");
            }
            if (solver.isSatisfiable(encodingAssumptions)) {
                encodingAssumptions.set(unsatcorelimit,
                        -encodingAssumptions.get(unsatcorelimit));
                results.push(-encodingAssumptions.get(unsatcorelimit));
                if (solver.isVerbose()) {
                    System.out.println(solver.getLogPrefix() + "mandatory.");
                }
            } else {
                if (solver.isVerbose()) {
                    System.out.println(solver.getLogPrefix() + "not needed.");
                }
            }
        }
        return results;
    }

    @Override
    public String toString() {
        return "Deletion based minimization strategy";
    }
}
