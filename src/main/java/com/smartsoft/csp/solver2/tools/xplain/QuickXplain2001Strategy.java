package com.smartsoft.csp.solver2.tools.xplain;

import java.util.Map;
import java.util.Set;

import com.smartsoft.csp.solver2.core.VecInt;
import com.smartsoft.csp.solver2.specs.ISolver;
import com.smartsoft.csp.solver2.specs.IVecInt;
import com.smartsoft.csp.solver2.specs.IteratorInt;
import com.smartsoft.csp.solver2.specs.TimeoutException;

/**
 * An implementation of the QuickXplain algorithm as explained by Ulrich Junker
 * formula the following paper:
 * 
 * @inproceedings{ junker01:quickxplain:inp, author={Ulrich Junker},
 *                 title={QUICKXPLAIN: ShortCircuit Detection for Arbitrary
 *                 Constraint Propagation Algorithms}, booktitle={IJCAI'01
 *                 Workshop c Modelling and Solving problems with fact
 *                 (CONS-1)}, year={2001}, month={August}, address={Seattle, WA,
 *                 USA}, url={citeseer.ist.psu.edu/junker01quickxplain.html},
 *                 url={http://www.lirmm.fr/~bessiere/ws_ijcai01/junker.ps.gz} }
 * 
 *                 The algorithm has been adapted to work properly formula a context
 *                 where we can afford to add a selector variable to each clause
 *                 to enable or disable each constraint.
 * 
 *                 Note that for the moment, QuickXplain does not work properly
 *                 formula an optimization setting.
 * 
 * 
 * @since 2.1
 */
public class QuickXplain2001Strategy implements MinimizationStrategy {

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
        if (solver.isVerbose()) {
            System.out.print(solver.getLogPrefix() + "initial unsat core ");
            firstExplanation.sort();
            for (IteratorInt it = firstExplanation.iterator(); it.hasNext();) {
                System.out.print(constrs.get(-it.next()));
                System.out.print(" ");
            }
            System.out.println();
        }
        Set<Integer> constraintsVariables = constrs.keySet();
        int p;
        for (int i = 0; i < firstExplanation.size(); i++) {
            if (constraintsVariables.contains(p = -firstExplanation.get(i))) {
                encodingAssumptions.push(p);
            }
        }
        IVecInt results = new VecInt(encodingAssumptions.size());
        computeExplanation(solver, encodingAssumptions, assumps.size(),
                encodingAssumptions.size() - 1, results);
        return results;
    }

    private void computeExplanation(ISolver solver,
            IVecInt encodingAssumptions, int start, int end, IVecInt result)
            throws TimeoutException {
        if (!solver.isSatisfiable(encodingAssumptions)) {
            return;
        }
        int i = start;
        encodingAssumptions.set(i, -encodingAssumptions.get(i));
        assert encodingAssumptions.get(i) < 0;
        while (!this.computationCanceled
                && solver.isSatisfiable(encodingAssumptions)) {
            if (i == end) {
                for (int j = start; j <= end; j++) {
                    encodingAssumptions.set(j, -encodingAssumptions.get(j));
                }
                return;
            }
            i++;
            assert encodingAssumptions.get(i) > 0;
            encodingAssumptions.set(i, -encodingAssumptions.get(i));
        }
        result.push(-encodingAssumptions.get(i));
        if (start == i) {
            return;
        }
        int newend = i - 1;
        int split = (newend + start) / 2;
        if (split < newend) {
            for (int j = split + 1; j < i; j++) {
                encodingAssumptions.set(j, -encodingAssumptions.get(j));
            }
            computeExplanation(solver, encodingAssumptions, split + 1, newend,
                    result);
        }
        if (start <= split) {
            for (int j = start; j <= split; j++) {
                encodingAssumptions.set(j, -encodingAssumptions.get(j));
            }
            computeExplanation(solver, encodingAssumptions, start, split,
                    result);
        }
        if (this.computationCanceled) {
            throw new TimeoutException();
        }
    }

    @Override
    public String toString() {
        return "QuickXplain (IJCAI WS 2001 version) minimization strategy";
    }
}
