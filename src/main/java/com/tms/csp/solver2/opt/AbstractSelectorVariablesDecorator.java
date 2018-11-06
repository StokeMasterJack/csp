package com.tms.csp.solver2.opt;

import com.tms.csp.solver2.core.VecInt;
import com.tms.csp.solver2.specs.IOptimizationProblem;
import com.tms.csp.solver2.specs.ISolver;
import com.tms.csp.solver2.specs.IVecInt;
import com.tms.csp.solver2.specs.TimeoutException;
import com.tms.csp.solver2.tools.SolverDecorator;

/**
 * Abstract class which adds a new "selector" variable for each clause entered
 * formula the solver.
 * 
 * As a consequence, an original problem with n variables and m clauses will end
 * up with n+m variables.
 * 
 * @author daniel
 * 
 */
public abstract class AbstractSelectorVariablesDecorator extends
        SolverDecorator<ISolver> implements IOptimizationProblem {

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    private int nbexpectedclauses;

    private int[] prevfullmodel;

    /**
     * @since 2.1
     */
    private int[] prevmodel;
    /**
     * @since 2.1
     */
    private boolean[] prevboolmodel;

    private boolean isSolutionOptimal;

    public AbstractSelectorVariablesDecorator(ISolver solver) {
        super(solver);
    }

    @Override
    public void setExpectedNumberOfClauses(int nb) {
        this.nbexpectedclauses = nb;
    }

    public int getExpectedNumberOfClauses() {
        return this.nbexpectedclauses;
    }

    public boolean admitABetterSolution() throws TimeoutException {
        return admitABetterSolution(VecInt.EMPTY);
    }

    /**
     * @since 2.1
     */
    public boolean admitABetterSolution(IVecInt assumps)
            throws TimeoutException {
        this.isSolutionOptimal = false;
        boolean result = super.isSatisfiable(assumps, true);
        if (result) {
            this.prevboolmodel = new boolean[nVars()];
            for (int i = 0; i < nVars(); i++) {
                this.prevboolmodel[i] = decorated().model(i + 1);
            }
            this.prevfullmodel = super.modelWithInternalVariables();
            this.prevmodel = super.model();
            calculateObjectiveValue();
        } else {
            this.isSolutionOptimal = true;
        }
        return result;
    }

    abstract void calculateObjectiveValue();

    @Override
    public int[] model() {
        return this.prevmodel;
    }

    @Override
    public boolean model(int var) {
        return this.prevboolmodel[var - 1];
    }

    public boolean isOptimal() {
        return this.isSolutionOptimal;
    }

    public int getNbexpectedclauses() {
        return nbexpectedclauses;
    }

    public void setNbexpectedclauses(int nbexpectedclauses) {
        this.nbexpectedclauses = nbexpectedclauses;
    }

    public int[] getPrevfullmodel() {
        return prevfullmodel;
    }

    public void setPrevfullmodel(int[] prevfullmodel) {
        this.prevfullmodel = prevfullmodel.clone();
    }

    public int[] getPrevmodel() {
        return prevmodel;
    }

    public void setPrevmodel(int[] prevmodel) {
        this.prevmodel = prevmodel.clone();
    }

    public boolean[] getPrevboolmodel() {
        return prevboolmodel;
    }

    public void setPrevboolmodel(boolean[] prevboolmodel) {
        this.prevboolmodel = prevboolmodel.clone();
    }

    public boolean isSolutionOptimal() {
        return isSolutionOptimal;
    }

    public void setSolutionOptimal(boolean isSolutionOptimal) {
        this.isSolutionOptimal = isSolutionOptimal;
    }
}
