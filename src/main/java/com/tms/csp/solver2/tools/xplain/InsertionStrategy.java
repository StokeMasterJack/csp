package com.tms.csp.solver2.tools.xplain;

import java.util.Map;
import java.util.Set;

import com.tms.csp.solver2.core.VecInt;
import com.tms.csp.solver2.specs.ISolver;
import com.tms.csp.solver2.specs.IVecInt;
import com.tms.csp.solver2.specs.IteratorInt;
import com.tms.csp.solver2.specs.TimeoutException;

/**
 * An implementation of the ReplayXplain algorithm as explained by Ulrich Junker
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
 * @author daniel
 * @since 2.1
 */
public class InsertionStrategy implements MinimizationStrategy {

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    private boolean computationCanceled;

    /**
     * @since 2.1
     */
    public void cancelExplanationComputation() {
        this.computationCanceled = true;
    }

    /**
     * @since 2.1
     */
    public IVecInt explain(ISolver solver, Map<Integer, ?> constrs,
            IVecInt assumps) throws TimeoutException {
        this.computationCanceled = false;
        IVecInt encodingAssumptions = new VecInt(constrs.size()
                + assumps.size());
        assumps.copyTo(encodingAssumptions);
        IVecInt firstExplanation = solver.unsatExplanation();
        if (firstExplanation.size() == 1) {
            IVecInt results = new VecInt();
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
        remainingVariables.copyTo(encodingAssumptions);
        boolean shouldContinue;
        int startingPoint = assumps.size();
        do {
            shouldContinue = false;
            int i = startingPoint;
            encodingAssumptions.set(i, -encodingAssumptions.get(i));
            assert encodingAssumptions.get(i) < 0;
            while (!this.computationCanceled
                    && solver.isSatisfiable(encodingAssumptions)) {
                i++;
                assert encodingAssumptions.get(i) > 0;
                encodingAssumptions.set(i, -encodingAssumptions.get(i));
            }
            if (!this.computationCanceled && i > startingPoint) {
                assert !solver.isSatisfiable(encodingAssumptions);
                if (i < encodingAssumptions.size()) {
                    // latest constraint is for sure responsible for the
                    // inconsistency.
                    int tmp = encodingAssumptions.get(i);
                    for (int j = i; j > startingPoint; j--) {
                        encodingAssumptions.set(j,
                                -encodingAssumptions.get(j - 1));
                    }
                    encodingAssumptions.set(startingPoint, tmp);
                    if (solver.isVerbose()) {
                        System.out.println(solver.getLogPrefix()
                                + constrs.get(tmp) + " is mandatory ");
                    }
                }
                shouldContinue = true;
            }
            startingPoint++;
        } while (!this.computationCanceled && shouldContinue
                && solver.isSatisfiable(encodingAssumptions));
        if (this.computationCanceled) {
            throw new TimeoutException();
        }
        IVecInt constrsKeys = new VecInt(startingPoint);
        for (int i = assumps.size(); i < startingPoint; i++) {
            constrsKeys.push(-encodingAssumptions.get(i));
        }
        return constrsKeys;
    }

    @Override
    public String toString() {
        return "Replay (Insertion-based) minimization strategy";
    }
}
