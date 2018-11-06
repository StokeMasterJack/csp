package com.tms.csp.solver2.minisat.constraints.cnf;

import com.tms.csp.solver2.minisat.core.ILits;
import com.tms.csp.solver2.specs.IVecInt;

public final class LearntWLClause extends WLClause {

    public LearntWLClause(IVecInt ps, ILits voc) {
        super(ps, voc);
    }

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /*
     * (non-Javadoc)
     * 
     * @see org.sat4j.minisat.fact.cnf.WLClause#register()
     */
    public void register() {
        assert this.lits.length > 1;
        // prendre un deuxieme litt???ral ??? surveiller
        int maxi = 1;
        int maxlevel = this.voc.getLevel(this.lits[1]);
        for (int i = 2; i < this.lits.length; i++) {
            int level = this.voc.getLevel(this.lits[i]);
            if (level > maxlevel) {
                maxi = i;
                maxlevel = level;
            }
        }
        int l = this.lits[1];
        this.lits[1] = this.lits[maxi];
        this.lits[maxi] = l;

        // ajoute la clause a la liste des clauses control???es.
        this.voc.watch(this.lits[0] ^ 1, this);
        this.voc.watch(this.lits[1] ^ 1, this);

    }

    public boolean learnt() {
        return true;
    }

    public void setLearnt() {
        // do nothing
    }

    /**
     * @since 2.1
     */
    public void forwardActivity(double claInc) {

    }

    /**
     * @param claInc
     */
    public void incActivity(double claInc) {
        this.activity += claInc;
    }
}
