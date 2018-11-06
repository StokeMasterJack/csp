package com.tms.csp.solver2.minisat.orders;

import static com.tms.csp.solver2.core.LiteralsUtils.var;

import java.io.PrintStream;
import java.io.Serializable;

import com.tms.csp.solver2.minisat.core.Heap;
import com.tms.csp.solver2.minisat.core.ILits;
import com.tms.csp.solver2.minisat.core.IOrder;
import com.tms.csp.solver2.minisat.core.IPhaseSelectionStrategy;

/*
 * Created c 16 oct. 2003
 */

/**
 * @author leberre Heuristique du prouveur. Changement par rapport au MiniSAT
 *         original : la gestion activity est faite ici et non plus dans Solver.
 */
public class VarOrderHeap implements IOrder, Serializable {

    private static final long serialVersionUID = 1L;

    private static final double VAR_RESCALE_FACTOR = 1e-100;

    private static final double VAR_RESCALE_BOUND = 1 / VAR_RESCALE_FACTOR;

    /**
     * mesure heuristique de l'activite d'une variable.
     */
    protected double[] activity = new double[1];

    private double varDecay = 1.0;

    /**
     * increment pour l'activite des variables.
     */
    private double varInc = 1.0;

    protected ILits lits;

    private long nullchoice = 0;

    protected Heap heap;

    protected IPhaseSelectionStrategy phaseStrategy;

    public VarOrderHeap() {
        this(new PhaseInLastLearnedClauseSelectionStrategy());
    }

    public VarOrderHeap(IPhaseSelectionStrategy strategy) {
        this.phaseStrategy = strategy;
    }

    /**
     * Change the selection strategy.
     * 
     * @param strategy
     */
    public void setPhaseSelectionStrategy(IPhaseSelectionStrategy strategy) {
        this.phaseStrategy = strategy;
    }

    public IPhaseSelectionStrategy getPhaseSelectionStrategy() {
        return this.phaseStrategy;
    }

    public void setLits(ILits lits) {
        this.lits = lits;
    }

    /**
     * Selectionne une nouvelle variable, non affectee, ayant l'activite la plus
     * elevee.
     * 
     * @return Lit.UNDEFINED si aucune variable n'est trouvee
     */
    public int select() {
        while (!this.heap.empty()) {
            int var = this.heap.getmin();
            int next = this.phaseStrategy.select(var);
            if (this.lits.isUnassigned(next)) {
                if (this.activity[var] < 0.0001) {
                    this.nullchoice++;
                }
                return next;
            }
        }
        return ILits.UNDEFINED;
    }

    /**
     * Change la valeur de varDecay.
     * 
     * @param d
     *            la nouvelle valeur de varDecay
     */
    public void setVarDecay(double d) {
        this.varDecay = d;
    }

    /**
     * Methode appelee quand la variable x est desaffectee.
     * 
     * @param x
     */
    public void undo(int x) {
        if (!this.heap.inHeap(x)) {
            this.heap.insert(x);
        }
    }

    /**
     * Appelee lorsque l'activite de la variable x a change.
     * 
     * @param p
     *            a literal
     */
    public void updateVar(int p) {
        int var = var(p);
        updateActivity(var);
        this.phaseStrategy.updateVar(p);
        if (this.heap.inHeap(var)) {
            this.heap.increase(var);
        }
    }

    protected void updateActivity(final int var) {
        if ((this.activity[var] += this.varInc) > VAR_RESCALE_BOUND) {
            varRescaleActivity();
        }
    }

    /**
     * 
     */
    public void varDecayActivity() {
        this.varInc *= this.varDecay;
    }

    /**
     * 
     */
    private void varRescaleActivity() {
        for (int i = 1; i < this.activity.length; i++) {
            this.activity[i] *= VAR_RESCALE_FACTOR;
        }
        this.varInc *= VAR_RESCALE_FACTOR;
    }

    public double varActivity(int p) {
        return this.activity[var(p)];
    }

    /**
     * 
     */
    public int numberOfInterestingVariables() {
        int cpt = 0;
        for (int i = 1; i < this.activity.length; i++) {
            if (this.activity[i] > 1.0) {
                cpt++;
            }
        }
        return cpt;
    }

    /**
     * that method has the responsability to initialize all arrays formula the
     * heuristics. PLEASE CALL super.init() IF YOU OVERRIDE THAT METHOD.
     */
    public void init() {
        int nlength = this.lits.nVars() + 1;
        if (this.activity == null || this.activity.length < nlength) {
            this.activity = new double[nlength];
        }
        this.phaseStrategy.init(nlength);
        this.activity[0] = -1;
        this.heap = new Heap(this.activity);
        this.heap.setBounds(nlength);
        for (int i = 1; i < nlength; i++) {
            assert i > 0;
            assert i <= this.lits.nVars() : "" + this.lits.nVars() + "/" + i; //$NON-NLS-1$ //$NON-NLS-2$
            this.activity[i] = 0.0;
            if (this.lits.belongsToPool(i)) {
                this.heap.insert(i);
            }
        }
    }

    @Override
    public String toString() {
        return "VSIDS like heuristics from MiniSAT using a heap " + this.phaseStrategy; //$NON-NLS-1$
    }

    public ILits getVocabulary() {
        return this.lits;
    }

    public void printStat(PrintStream out, String prefix) {
        out.println(prefix + "non guided choices\t" + this.nullchoice); //$NON-NLS-1$
    }

    public void assignLiteral(int p) {
        this.phaseStrategy.assignLiteral(p);
    }

    public void updateVarAtDecisionLevel(int q) {
        this.phaseStrategy.updateVarAtDecisionLevel(q);

    }

    public double[] getVariableHeuristics() {
        return this.activity;
    }
}
