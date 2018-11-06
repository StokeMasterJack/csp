package com.tms.csp.solver2.minisat.constraints;

import com.tms.csp.solver2.minisat.constraints.cnf.Lits;
import com.tms.csp.solver2.minisat.core.ILits;

/**
 * @author leberre To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Generation - Code and Comments
 */
public abstract class AbstractCardinalityDataStructure extends
        AbstractDataStructureFactory {

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    @Override
    protected ILits createLits() {
        return new Lits();
    }
}
