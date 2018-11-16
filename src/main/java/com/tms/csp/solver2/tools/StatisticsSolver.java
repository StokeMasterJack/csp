package com.tms.csp.solver2.tools;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import com.tms.csp.solver2.core.LiteralsUtils;
import com.tms.csp.solver2.core.VecInt;
import com.tms.csp.solver2.minisat.core.Counter;
import com.tms.csp.solver2.specs.ContradictionException;
import com.tms.csp.solver2.specs.IConstr;
import com.tms.csp.solver2.specs.ISolver;
import com.tms.csp.solver2.specs.ISolverService;
import com.tms.csp.solver2.specs.IVec;
import com.tms.csp.solver2.specs.IVecInt;
import com.tms.csp.solver2.specs.IteratorInt;
import com.tms.csp.solver2.specs.SearchListener;
import com.tms.csp.solver2.specs.TimeoutException;
import com.tms.csp.solver2.specs.UnitClauseProvider;

public class StatisticsSolver implements ISolver {

    private static final String NOT_IMPLEMENTED_YET = "Not implemented yet!";

    private static final String THAT_SOLVER_ONLY_COMPUTE_STATISTICS = "That solver only compute statistics";

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * Number of fact formula the problem
     */
    private int expectedNumberOfConstraints;

    /**
     * Number of declared _complexVars (max vr id)
     */
    private int nbvars;

    /**
     * Size of the fact for each occurrence of each vr for each polarity
     */
    private IVecInt[] sizeoccurrences;

    private int allpositive;

    private int allnegative;

    private int horn;

    private int dualhorn;

    /**
     * Distribution of clauses size
     */
    private final Map<Integer, Counter> sizes = new HashMap<Integer, Counter>();

    public int[] model() {
        throw new UnsupportedOperationException(
                THAT_SOLVER_ONLY_COMPUTE_STATISTICS);
    }

    public boolean model(int var) {
        throw new UnsupportedOperationException(
                THAT_SOLVER_ONLY_COMPUTE_STATISTICS);
    }

    public int[] primeImplicant() {
        throw new UnsupportedOperationException(
                THAT_SOLVER_ONLY_COMPUTE_STATISTICS);
    }

    public boolean primeImplicant(int p) {
        throw new UnsupportedOperationException(
                THAT_SOLVER_ONLY_COMPUTE_STATISTICS);
    }

    public boolean isSatisfiable() throws TimeoutException {
        throw new TimeoutException(THAT_SOLVER_ONLY_COMPUTE_STATISTICS);
    }

    public boolean isSatisfiable(IVecInt assumps, boolean globalTimeout)
            throws TimeoutException {
        throw new TimeoutException(THAT_SOLVER_ONLY_COMPUTE_STATISTICS);
    }

    public boolean isSatisfiable(boolean globalTimeout) throws TimeoutException {
        throw new TimeoutException(THAT_SOLVER_ONLY_COMPUTE_STATISTICS);
    }

    public boolean isSatisfiable(IVecInt assumps) throws TimeoutException {
        throw new TimeoutException(THAT_SOLVER_ONLY_COMPUTE_STATISTICS);
    }

    public int[] findModel() throws TimeoutException {
        throw new TimeoutException(THAT_SOLVER_ONLY_COMPUTE_STATISTICS);
    }

    public int[] findModel(IVecInt assumps) throws TimeoutException {
        throw new TimeoutException(THAT_SOLVER_ONLY_COMPUTE_STATISTICS);
    }

    public int nConstraints() {
        return expectedNumberOfConstraints;
    }

    public int newVar(int howmany) {
        this.nbvars = howmany;
        sizeoccurrences = new IVecInt[(howmany + 1) << 1];
        return howmany;
    }

    public int nVars() {
        return this.nbvars;
    }

    @Deprecated
    public void printInfos(PrintStream out, String prefix) {

    }

    public void printInfos(PrintStream out) {
    }

    @Deprecated
    public int newVar() {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED_YET);
    }

    public int nextFreeVarId(boolean reserve) {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED_YET);
    }

    public void registerLiteral(int p) {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED_YET);
    }

    public void setExpectedNumberOfClauses(int nb) {
        this.expectedNumberOfConstraints = nb;
    }

    public IConstr addClause(IVecInt literals) throws ContradictionException {
        int size = literals.size();
        Counter counter = sizes.get(size);
        if (counter == null) {
            counter = new Counter(0);
            sizes.put(size, counter);
        }
        counter.inc();
        IVecInt list;
        int x, p;
        int pos = 0, neg = 0;
        for (IteratorInt it = literals.iterator(); it.hasNext();) {
            x = it.next();
            if (x > 0) {
                pos++;
            } else {
                neg++;
            }
            p = LiteralsUtils.toInternal(x);
            list = sizeoccurrences[p];
            if (list == null) {
                list = new VecInt();
                sizeoccurrences[p] = list;
            }
            list.push(size);
        }
        if (neg == 0) {
            allpositive++;
        } else if (pos == 0) {
            allnegative++;
        } else if (pos == 1) {
            horn++;
        } else if (neg == 1) {
            dualhorn++;
        }
        return null;
    }

    public IConstr addBlockingClause(IVecInt literals)
            throws ContradictionException {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED_YET);
    }

    public boolean removeConstr(IConstr c) {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED_YET);
    }

    public boolean removeSubsumedConstr(IConstr c) {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED_YET);
    }

    public void addAllClauses(IVec<IVecInt> clauses)
            throws ContradictionException {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED_YET);
    }

    public IConstr addAtMost(IVecInt literals, int degree)
            throws ContradictionException {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED_YET);
    }

    public IConstr addAtLeast(IVecInt literals, int degree)
            throws ContradictionException {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED_YET);
    }

    public IConstr addExactly(IVecInt literals, int n)
            throws ContradictionException {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED_YET);
    }

    public void setTimeout(int t) {
    }

    public void setTimeoutOnConflicts(int count) {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED_YET);
    }

    public void setTimeoutMs(long t) {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED_YET);
    }

    public int getTimeout() {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED_YET);
    }

    public long getTimeoutMs() {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED_YET);
    }

    public void expireTimeout() {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED_YET);
    }

    public void reset() {
    }

    public void printStat(PrintStream out, String prefix) {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED_YET);
    }

    public void printStat(PrintStream out) {
        int realNumberOfVariables = 0;
        int realNumberOfLiterals = 0;
        int pureLiterals = 0;
        int minOccV = Integer.MAX_VALUE;
        int maxOccV = Integer.MIN_VALUE;
        int sumV = 0;
        int sizeL, sizeV;
        int minOccL = Integer.MAX_VALUE;
        int maxOccL = Integer.MIN_VALUE;
        int sumL = 0;
        IVecInt list;
        boolean oneNull;
        if (sizeoccurrences == null) {
            return;
        }
        int max = sizeoccurrences.length - 1;
        for (int i = 1; i < max; i += 2) {
            sizeV = 0;
            oneNull = false;
            for (int k = 0; k < 2; k++) {
                list = sizeoccurrences[i + k];
                if (list == null) {
                    oneNull = true;
                } else {
                    realNumberOfLiterals++;
                    sizeL = list.size();
                    sizeV += sizeL;
                    if (minOccL > sizeL) {
                        minOccL = sizeL;
                    }
                    if (sizeL > maxOccL) {
                        maxOccL = sizeL;
                    }
                    sumL += sizeL;
                }
            }

            if (sizeV > 0) {
                if (oneNull) {
                    pureLiterals++;
                }
                realNumberOfVariables++;
                if (minOccV > sizeV) {
                    minOccV = sizeV;
                }
                if (sizeV > maxOccV) {
                    maxOccV = sizeV;
                }
                sumV += sizeV;
            }

        }
        System.out.println("c Distribution of fact size:");
        int nbclauses = 0;
        for (Map.Entry<Integer, Counter> entry : sizes.entrySet()) {
//            System.out.printf("c %d => %d%n", entry.getKey(), entry.getValue().getValue());
            nbclauses += entry.getValue().getValue();
        }

//        System.out
//                .printf("c Real number of variables, literals, number of clauses, #pureliterals, ");
//        System.out.printf("variable occurrences (min/max/avg) ");
//        System.out.printf("literals occurrences (min/max/avg) ");
//        System.out
//                .println("Specific clauses: #positive  #negative #horn  #dualhorn #remaining");
//
//        System.out.printf("%d %d %d %d %d %d %d %d %d %d ",
//                realNumberOfVariables, realNumberOfLiterals, nbclauses,
//                pureLiterals, minOccV, maxOccV, sumV / realNumberOfVariables,
//                minOccL, maxOccL, sumL / realNumberOfLiterals);
//        System.out.printf("%d %d %d %d %d%n", allpositive, allnegative, horn,
//                dualhorn, nbclauses - allpositive - allnegative - horn
//                        - dualhorn);
    }

    public Map<String, Number> getStat() {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED_YET);
    }

    public String toString(String prefix) {
        return prefix + "Statistics about the benchmarks";
    }

    public void clearLearntClauses() {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED_YET);
    }

    public void setDBSimplificationAllowed(boolean status) {
    }

    public boolean isDBSimplificationAllowed() {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED_YET);
    }

    public <S extends ISolverService> void setSearchListener(
            SearchListener<S> sl) {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED_YET);
    }

    public <S extends ISolverService> SearchListener<S> getSearchListener() {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED_YET);
    }

    public boolean isVerbose() {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED_YET);
    }

    public void setVerbose(boolean value) {
    }

    public void setLogPrefix(String prefix) {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED_YET);
    }

    public String getLogPrefix() {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED_YET);
    }

    public IVecInt unsatExplanation() {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED_YET);
    }

    public int[] modelWithInternalVariables() {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED_YET);
    }

    public int realNumberOfVariables() {
        return nbvars;
    }

    public boolean isSolverKeptHot() {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED_YET);
    }

    public void setKeepSolverHot(boolean keepHot) {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED_YET);
    }

    public ISolver getSolvingEngine() {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED_YET);
    }

    public void setUnitClauseProvider(UnitClauseProvider ucp) {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED_YET);
    }
}
