package com.smartsoft.csp.solver2.tools;

import java.io.PrintStream;
import java.util.Map;

import com.smartsoft.csp.solver2.specs.ContradictionException;
import com.smartsoft.csp.solver2.specs.IConstr;
import com.smartsoft.csp.solver2.specs.ISolver;
import com.smartsoft.csp.solver2.specs.ISolverService;
import com.smartsoft.csp.solver2.specs.IVec;
import com.smartsoft.csp.solver2.specs.IVecInt;
import com.smartsoft.csp.solver2.specs.SearchListener;
import com.smartsoft.csp.solver2.specs.TimeoutException;
import com.smartsoft.csp.solver2.specs.UnitClauseProvider;

public abstract class AbstractOutputSolver implements ISolver {

    protected int nbvars;

    protected int nbclauses;

    protected boolean fixedNbClauses = false;

    protected boolean firstConstr = true;

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    public boolean removeConstr(IConstr c) {
        throw new UnsupportedOperationException();
    }

    public void addAllClauses(IVec<IVecInt> clauses)
            throws ContradictionException {
        throw new UnsupportedOperationException();
    }

    public void setTimeout(int t) {
        // TODO Auto-generated method stub

    }

    public void setTimeoutMs(long t) {
        // TODO Auto-generated method stub
    }

    public int getTimeout() {
        return 0;
    }

    /**
     * @since 2.1
     */
    public long getTimeoutMs() {
        return 0L;
    }

    public void expireTimeout() {
        // TODO Auto-generated method stub

    }

    public boolean isSatisfiable(IVecInt assumps, boolean global)
            throws TimeoutException {
        throw new TimeoutException("There is no real solver behind!");
    }

    public boolean isSatisfiable(boolean global) throws TimeoutException {
        throw new TimeoutException("There is no real solver behind!");
    }

    public void printInfos(PrintStream output, String prefix) {
    }

    public void setTimeoutOnConflicts(int count) {

    }

    public boolean isDBSimplificationAllowed() {
        return false;
    }

    public void setDBSimplificationAllowed(boolean status) {

    }

    public void printStat(PrintStream output, String prefix) {
        // TODO Auto-generated method stub
    }

    public Map<String, Number> getStat() {
        // TODO Auto-generated method stub
        return null;
    }

    public void clearLearntClauses() {
        // TODO Auto-generated method stub

    }

    public int[] model() {
        throw new UnsupportedOperationException();
    }

    public boolean model(int var) {
        throw new UnsupportedOperationException();
    }

    public boolean isSatisfiable() throws TimeoutException {
        throw new TimeoutException("There is no real solver behind!");
    }

    public boolean isSatisfiable(IVecInt assumps) throws TimeoutException {
        throw new TimeoutException("There is no real solver behind!");
    }

    public int[] findModel() throws TimeoutException {
        throw new UnsupportedOperationException();
    }

    public int[] findModel(IVecInt assumps) throws TimeoutException {
        throw new UnsupportedOperationException();
    }

    /**
     * @since 2.1
     */
    public boolean removeSubsumedConstr(IConstr c) {
        return false;
    }

    /**
     * @since 2.1
     */
    public IConstr addBlockingClause(IVecInt literals)
            throws ContradictionException {
        throw new UnsupportedOperationException();
    }

    /**
     * @since 2.2
     */
    public <S extends ISolverService> SearchListener<S> getSearchListener() {
        throw new UnsupportedOperationException();
    }

    /**
     * @since 2.1
     */
    public <S extends ISolverService> void setSearchListener(
            SearchListener<S> sl) {
    }

    /**
     * @since 2.2
     */
    public boolean isVerbose() {
        return true;
    }

    /**
     * @since 2.2
     */
    public void setVerbose(boolean value) {
        // do nothing
    }

    /**
     * @since 2.2
     */
    public void setLogPrefix(String prefix) {
        // do nothing

    }

    /**
     * @since 2.2
     */
    public String getLogPrefix() {
        return "";
    }

    /**
     * @since 2.2
     */
    public IVecInt unsatExplanation() {
        throw new UnsupportedOperationException();
    }

    public int[] primeImplicant() {
        throw new UnsupportedOperationException();
    }

    public int nConstraints() {
        // TODO Auto-generated method stub
        return 0;
    }

    public int newVar(int howmany) {
        // TODO Auto-generated method stub
        return 0;
    }

    public int nVars() {
        // TODO Auto-generated method stub
        return 0;
    }

    public boolean isSolverKeptHot() {
        return false;
    }

    public void setKeepSolverHot(boolean value) {
    }

    public ISolver getSolvingEngine() {
        throw new UnsupportedOperationException();
    }

    public void setUnitClauseProvider(UnitClauseProvider upl) {
        throw new UnsupportedOperationException();
    }
}
