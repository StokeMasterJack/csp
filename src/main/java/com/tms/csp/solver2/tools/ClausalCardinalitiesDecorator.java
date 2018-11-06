package com.tms.csp.solver2.tools;

import com.tms.csp.solver2.specs.ContradictionException;
import com.tms.csp.solver2.specs.IConstr;
import com.tms.csp.solver2.specs.ISolver;
import com.tms.csp.solver2.specs.IVecInt;
import com.tms.csp.solver2.tools.encoding.EncodingStrategyAdapter;
import com.tms.csp.solver2.tools.encoding.Policy;

/**
 * 
 * A decorator for clausal cardinalities fact.
 * 
 * @author stephanieroussel
 * @since 2.3.1
 * @param <T>
 */
public class ClausalCardinalitiesDecorator<T extends ISolver> extends
        SolverDecorator<T> {

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    private final EncodingStrategyAdapter encodingAdapter;

    public ClausalCardinalitiesDecorator(T solver) {
        super(solver);
        this.encodingAdapter = new Policy();
    }

    public ClausalCardinalitiesDecorator(T solver,
            EncodingStrategyAdapter encodingAd) {
        super(solver);
        this.encodingAdapter = encodingAd;
    }

    @Override
    public IConstr addAtLeast(IVecInt literals, int k)
            throws ContradictionException {
        if (k == 1) {
            return this.encodingAdapter.addAtLeastOne(decorated(), literals);
        } else {
            return this.encodingAdapter.addAtLeast(decorated(), literals, k);
        }
    }

    @Override
    public IConstr addAtMost(IVecInt literals, int k)
            throws ContradictionException {
        if (k == 1) {
            return this.encodingAdapter.addAtMostOne(decorated(), literals);
        } else {
            return this.encodingAdapter.addAtMost(decorated(), literals, k);
        }
    }

    @Override
    public IConstr addExactly(IVecInt literals, int k)
            throws ContradictionException {

        if (k == 1) {
            return this.encodingAdapter.addExactlyOne(decorated(), literals);
        } else {
            return this.encodingAdapter.addExactly(decorated(), literals, k);
        }
    }

    @Override
    public String toString() {
        return toString("");
    }

    @Override
    public String toString(String prefix) {
        return super.toString(prefix) + "\n"
                + "Cardinality to SAT encoding: \n" + "Encoding: "
                + this.encodingAdapter + "\n";
    }

}
