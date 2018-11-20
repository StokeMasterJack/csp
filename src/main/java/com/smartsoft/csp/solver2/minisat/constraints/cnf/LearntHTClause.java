package com.smartsoft.csp.solver2.minisat.constraints.cnf;

import static com.smartsoft.csp.solver2.core.LiteralsUtils.neg;

import com.smartsoft.csp.solver2.minisat.core.ILits;
import com.smartsoft.csp.solver2.specs.IVecInt;

/**
 * @author daniel
 * @since 2.1
 */
public class LearntHTClause extends HTClause {

    public LearntHTClause(IVecInt ps, ILits voc) {
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
        // looking for the literal to put formula tail
        if (this.middleLits.length > 0) {
            int maxi = 0;
            int maxlevel = this.voc.getLevel(this.middleLits[0]);
            for (int i = 1; i < this.middleLits.length; i++) {
                int level = this.voc.getLevel(this.middleLits[i]);
                if (level > maxlevel) {
                    maxi = i;
                    maxlevel = level;
                }
            }
            if (maxlevel > this.voc.getLevel(this.tail)) {
                int l = this.tail;
                this.tail = this.middleLits[maxi];
                this.middleLits[maxi] = l;
            }
        }
        // attach both head and tail literals.
        this.voc.watch(neg(this.head), this);
        this.voc.watch(neg(this.tail), this);

    }

    public boolean learnt() {
        return true;
    }

    public void setLearnt() {
        // do nothing
    }

    public void forwardActivity(double claInc) {

    }

    /**
     * @param claInc
     */
    public void incActivity(double claInc) {
        this.activity += claInc;
    }

    public void setActivity(double d) {
        this.activity = d;
    }
}
