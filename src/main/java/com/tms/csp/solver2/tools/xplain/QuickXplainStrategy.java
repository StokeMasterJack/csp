package com.tms.csp.solver2.tools.xplain;

import java.util.Map;
import java.util.Set;

import com.tms.csp.solver2.core.VecInt;
import com.tms.csp.solver2.specs.ISolver;
import com.tms.csp.solver2.specs.IVecInt;
import com.tms.csp.solver2.specs.IteratorInt;
import com.tms.csp.solver2.specs.TimeoutException;

/**
 * An implementation of the QuickXplain algorithm as explained by Ulrich Junker
 * formula the following paper:
 * 
 * @inproceedings{DBLP:conf/aaai/Junker02, author = {Ulrich Junker}, title =
 *                                         {Preference-Based Search and
 *                                         Multi-Criteria Optimization},
 *                                         booktitle = {AAAI/IAAI}, year =
 *                                         {2002}, pages = {34-40}, bibsource =
 *                                         {DBLP, http://dblp.uni-trier.de} }
 * 
 * 
 *                                         The algorithm has been adapted to
 *                                         work properly formula a context where we
 *                                         can afford to add a selector variable
 *                                         to each clause to enable or disable
 *                                         each constraint.
 * 
 *                                         Note that for the moment, QuickXplain
 *                                         does not work properly formula an
 *                                         optimization setting.
 * 
 * 
 * @since 2.1
 */
public class QuickXplainStrategy implements MinimizationStrategy {

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
            encodingAssumptions.push(p);
        }
        int unsatcorelimit = encodingAssumptions.size() - 1;

        remainingVariables.copyTo(encodingAssumptions);
        computeExplanation(solver, constrs, encodingAssumptions,
                assumps.size(), unsatcorelimit, results);
        return results;
    }

    private void computeExplanation(ISolver solver, Map<Integer, ?> constrs,
            IVecInt encodingAssumptions, int start, int end, IVecInt result)
            throws TimeoutException {
        if (solver.isVerbose()) {
            System.out.println(solver.getLogPrefix() + "qxplain " + start + "/"
                    + end);
        }
        if (!solver.isSatisfiable(encodingAssumptions)) {
            return;
        }
        if (start == end) {
            result.push(encodingAssumptions.get(start));
            encodingAssumptions.set(start, -encodingAssumptions.get(start));
            if (solver.isVerbose()) {
                System.out.println(solver.getLogPrefix()
                        + constrs.get(-encodingAssumptions.get(start))
                        + " is mandatory ");
            }
            return;
        }
        int split = (end + start) / 2;
        if (split < end) {
            for (int j = start; j <= split; j++) {
                encodingAssumptions.set(j, -encodingAssumptions.get(j));
            }
            computeExplanation(solver, constrs, encodingAssumptions, split + 1,
                    end, result);
        }
        if (start <= split) {
            for (int j = start; j <= split; j++) {
                encodingAssumptions.set(j, -encodingAssumptions.get(j));
            }
            computeExplanation(solver, constrs, encodingAssumptions, start,
                    split, result);
        }
        if (this.computationCanceled) {
            throw new TimeoutException();
        }
    }

    @Override
    public String toString() {
        return "QuickXplain (AAAI 2004 version) minimization strategy";
    }

}
