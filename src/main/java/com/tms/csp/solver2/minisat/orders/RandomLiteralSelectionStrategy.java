package com.tms.csp.solver2.minisat.orders;

import static com.tms.csp.solver2.core.LiteralsUtils.negLit;
import static com.tms.csp.solver2.core.LiteralsUtils.posLit;

import java.util.Random;

import com.tms.csp.solver2.minisat.core.IPhaseSelectionStrategy;

/**
 * The variable selection strategy randomly picks one phase, either positive or
 * negative.
 * 
 * @author leberre
 * 
 */
public final class RandomLiteralSelectionStrategy implements
        IPhaseSelectionStrategy {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * @since 2.2
     */
    public static final Random RAND = new Random(123456789);

    public void assignLiteral(int p) {
    }

    public void init(int nlength) {
    }

    public void init(int var, int p) {
    }

    public int select(int var) {
        if (RAND.nextBoolean()) {
            return posLit(var);
        }
        return negLit(var);
    }

    public void updateVar(int p) {
    }

    public void updateVarAtDecisionLevel(int q) {
    }

    @Override
    public String toString() {
        return "random phase selection";
    }
}
