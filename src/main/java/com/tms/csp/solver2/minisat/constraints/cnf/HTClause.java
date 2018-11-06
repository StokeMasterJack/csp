package com.tms.csp.solver2.minisat.constraints.cnf;

import static com.tms.csp.solver2.core.LiteralsUtils.neg;

import java.io.Serializable;

import com.tms.csp.solver2.minisat.core.Constr;
import com.tms.csp.solver2.minisat.core.ILits;
import com.tms.csp.solver2.minisat.core.Propagatable;
import com.tms.csp.solver2.specs.IVecInt;
import com.tms.csp.solver2.specs.UnitPropagationListener;

/**
 * Lazy data structure for clause using the Head Tail data structure from SATO,
 * The original scheme is improved by avoiding moving pointers to literals but
 * moving the literals themselves.
 * 
 * We suppose here that the clause contains at least 3 literals. Use the
 * BinaryClause or UnaryClause clause data structures to deal with binary and
 * unit clauses.
 * 
 * @author leberre
 * @see BinaryClause
 * @see UnitClause
 * @since 2.1
 */
public abstract class HTClause implements Propagatable, Constr, Serializable {

    private static final long serialVersionUID = 1L;

    protected double activity;

    protected final int[] middleLits;

    protected final ILits voc;

    protected int head;

    protected int tail;

    /**
     * Creates a new basic clause
     * 
     * @param voc
     *            the vocabulary of the formula
     * @param ps
     *            A VecInt that WILL BE EMPTY after calling that method.
     */
    public HTClause(IVecInt ps, ILits voc) {
        assert ps.size() > 1;
        this.head = ps.get(0);
        this.tail = ps.last();
        final int size = ps.size() - 2;
        assert size > 0;
        this.middleLits = new int[size];
        System.arraycopy(ps.toArray(), 1, this.middleLits, 0, size);
        ps.clear();
        assert ps.size() == 0;
        this.voc = voc;
        this.activity = 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see Constr#calcReason(Solver, Lit, Vec)
     */
    public void calcReason(int p, IVecInt outReason) {
        if (this.voc.isFalsified(this.head)) {
            outReason.push(neg(this.head));
        }
        final int[] mylits = this.middleLits;
        for (int mylit : mylits) {
            if (this.voc.isFalsified(mylit)) {
                outReason.push(neg(mylit));
            }
        }
        if (this.voc.isFalsified(this.tail)) {
            outReason.push(neg(this.tail));
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see Constr#remove(Solver)
     */
    public void remove(UnitPropagationListener upl) {
        this.voc.watches(neg(this.head)).remove(this);
        this.voc.watches(neg(this.tail)).remove(this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see Constr#simplify(Solver)
     */
    public boolean simplify() {
        if (this.voc.isSatisfied(this.head) || this.voc.isSatisfied(this.tail)) {
            return true;
        }
        for (int middleLit : this.middleLits) {
            if (this.voc.isSatisfied(middleLit)) {
                return true;
            }
        }
        return false;
    }

    public boolean propagate(UnitPropagationListener s, int p) {

        if (this.head == neg(p)) {
            final int[] mylits = this.middleLits;
            int temphead = 0;
            // moving head c the right
            while (temphead < mylits.length
                    && this.voc.isFalsified(mylits[temphead])) {
                temphead++;
            }
            assert temphead <= mylits.length;
            if (temphead == mylits.length) {
                this.voc.watch(p, this);
                return s.enqueue(this.tail, this);
            }
            this.head = mylits[temphead];
            mylits[temphead] = neg(p);
            this.voc.watch(neg(this.head), this);
            return true;
        }
        assert this.tail == neg(p);
        final int[] mylits = this.middleLits;
        int temptail = mylits.length - 1;
        // moving tail c the left
        while (temptail >= 0 && this.voc.isFalsified(mylits[temptail])) {
            temptail--;
        }
        assert -1 <= temptail;
        if (-1 == temptail) {
            this.voc.watch(p, this);
            return s.enqueue(this.head, this);
        }
        this.tail = mylits[temptail];
        mylits[temptail] = neg(p);
        this.voc.watch(neg(this.tail), this);
        return true;
    }

    /*
     * For learnt clauses only @author leberre
     */
    public boolean locked() {
        return this.voc.getReason(this.head) == this
                || this.voc.getReason(this.tail) == this;
    }

    /**
     * @return the activity of the clause
     */
    public double getActivity() {
        return this.activity;
    }

    @Override
    public String toString() {
        StringBuffer stb = new StringBuffer();
        stb.append(Lits.toString(this.head));
        stb.append("["); //$NON-NLS-1$
        stb.append(this.voc.valueToString(this.head));
        stb.append("]"); //$NON-NLS-1$
        stb.append(" "); //$NON-NLS-1$
        for (int middleLit : this.middleLits) {
            stb.append(Lits.toString(middleLit));
            stb.append("["); //$NON-NLS-1$
            stb.append(this.voc.valueToString(middleLit));
            stb.append("]"); //$NON-NLS-1$
            stb.append(" "); //$NON-NLS-1$
        }
        stb.append(Lits.toString(this.tail));
        stb.append("["); //$NON-NLS-1$
        stb.append(this.voc.valueToString(this.tail));
        stb.append("]"); //$NON-NLS-1$
        return stb.toString();
    }

    /**
     * Return the ith literal of the clause. Note that the order of the literals
     * does change during the search...
     * 
     * @param i
     *            the index of the literal
     * @return the literal
     */
    public int get(int i) {
        if (i == 0) {
            return this.head;
        }
        if (i == this.middleLits.length + 1) {
            return this.tail;
        }
        return this.middleLits[i - 1];
    }

    /**
     * @param d
     */
    public void rescaleBy(double d) {
        this.activity *= d;
    }

    public int size() {
        return this.middleLits.length + 2;
    }

    public void assertConstraint(UnitPropagationListener s) {
        assert this.voc.isUnassigned(this.head);
        boolean ret = s.enqueue(this.head, this);
        assert ret;
    }

    public void assertConstraintIfNeeded(UnitPropagationListener s) {
        if (voc.isFalsified(this.tail)) {
            boolean ret = s.enqueue(this.head, this);
            assert ret;
        }
    }

    public ILits getVocabulary() {
        return this.voc;
    }

    public int[] getLits() {
        int[] tmp = new int[size()];
        System.arraycopy(this.middleLits, 0, tmp, 1, this.middleLits.length);
        tmp[0] = this.head;
        tmp[tmp.length - 1] = this.tail;
        return tmp;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        try {
            HTClause wcl = (HTClause) obj;
            if (wcl.head != this.head || wcl.tail != this.tail) {
                return false;
            }
            if (this.middleLits.length != wcl.middleLits.length) {
                return false;
            }
            boolean ok;
            for (int lit : this.middleLits) {
                ok = false;
                for (int lit2 : wcl.middleLits) {
                    if (lit == lit2) {
                        ok = true;
                        break;
                    }
                }
                if (!ok) {
                    return false;
                }
            }
            return true;
        } catch (ClassCastException e) {
            return false;
        }
    }

    @Override
    public int hashCode() {
        long sum = this.head + this.tail;
        for (int p : this.middleLits) {
            sum += p;
        }
        return (int) sum / this.middleLits.length;
    }

    public boolean canBePropagatedMultipleTimes() {
        return false;
    }

    public Constr toConstraint() {
        return this;
    }

    public void calcReasonOnTheFly(int p, IVecInt trail, IVecInt outReason) {
        calcReason(p, outReason);
    }
}
