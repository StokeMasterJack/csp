
package com.smartsoft.csp.solver2.tools.encoding;

import java.io.Serializable;

import com.smartsoft.csp.solver2.core.VecInt;
import com.smartsoft.csp.solver2.specs.ContradictionException;
import com.smartsoft.csp.solver2.specs.IConstr;
import com.smartsoft.csp.solver2.specs.ISolver;
import com.smartsoft.csp.solver2.specs.IVecInt;

/**
 * The aim of this class is to use different encodings for specific fact.
 * The class is abstract because it does not makes sense to use it "as is".
 * 
 * @author sroussel
 * @since 2.3.1
 */
public abstract class EncodingStrategyAdapter implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public IConstr addAtLeast(ISolver solver, IVecInt literals, int degree)
            throws ContradictionException {
        final int n = literals.size();
        IVecInt newLiterals = new VecInt(n);
        for (int i = 0; i < n; i++) {
            newLiterals.push(-literals.get(i));
        }
        return this.addAtMost(solver, newLiterals, n - degree);
    }

    public IConstr addAtLeastOne(ISolver solver, IVecInt literals)
            throws ContradictionException {
        return solver.addClause(literals);
    }

    public IConstr addAtMost(ISolver solver, IVecInt literals, int degree)
            throws ContradictionException {
        return solver.addAtMost(literals, degree);
    }

    public IConstr addAtMostOne(ISolver solver, IVecInt literals)
            throws ContradictionException {
        return this.addAtMost(solver, literals, 1);
    }

    public IConstr addExactly(ISolver solver, IVecInt literals, int degree)
            throws ContradictionException {
        return solver.addExactly(literals, degree);
    }

    public IConstr addExactlyOne(ISolver solver, IVecInt literals)
            throws ContradictionException {
        return this.addExactly(solver, literals, 1);
    }

    @Override
    public String toString() {
        return this.getClass().getName();
    }

}
