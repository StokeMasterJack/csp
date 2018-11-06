package com.tms.csp.util;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterators;
import com.tms.csp.ExpItTo;
import com.tms.csp.argBuilder.IArgBuilder;
import com.tms.csp.ast.*;
import com.tms.csp.fm.dnnf.products.Cube;
import com.tms.csp.util.ints.IndexedEntry;
import com.tms.csp.util.ints.TreeSequence;
import com.tms.csp.util.it.ExpFilterIterator;
import com.tms.csp.util.varSets.VarSet;
import kotlin.sequences.Sequence;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class DynComplex implements IArgBuilder, PLConstants, Iterable<Exp> {

    private final Space sp;

    //For And
    //    null:   constantFalse
    //    empty:  constantTrue
    public TreeSequence<Exp> args = new TreeSequence<Exp>();
    private VarSet vars;

    public Boolean fcc;


    public DynComplex(Space sp, VarSet vars) {
        this.sp = sp;
        this.vars = vars;
    }

    public DynComplex(Space sp) {
        this(sp, (VarSet) null);
    }


    public DynComplex(Space sp, Iterable<Exp> args) {
        this(sp);
        addAll(args);
    }

    public DynComplex(Space sp, Exp[] args) {
        this(sp);
        addAll(args);
    }

    public DynComplex(DynComplex that) {
        this(that.sp);
        this.args = that.args.copy();
        vars = that.vars.copy();
        fcc = that.fcc;
    }

    public DynComplex copy() {
        return new DynComplex(this);
    }


    public void addAll(Exp[] args) {
        for (Exp arg : args) {
            add(arg);
        }
    }

    public void addAll(Iterable<Exp> args) {
        for (Exp arg : args) {
            add(arg);
        }
    }

    public boolean assertVars() {
        return assertVars("");
    }

    public String assertVarsMsg(VarSet vars1, VarSet vars2, String constraintJustAdded) {
        return "\n  vars[" + vars1 + "]\n  computed[" + vars2 + "]\n  constraint[" + constraintJustAdded + "]";
    }

    private boolean assertVars(String constraintJustAdded) {
        VarSet vars1 = vars;
        VarSet vars2 = _computeVars();

        if (vars1 == null) {
            throw new IllegalStateException();
        }

        boolean chk = vars1.equals(vars2);

        assert chk : assertVarsMsg(vars1, vars2, constraintJustAdded);
        return true;
    }

    public boolean add(Exp e) {
        if (e.isAnd()) throw new IllegalArgumentException("Ands should be added at the csp level");
        Exp old = args.put(e.getExpId(), e);
        boolean ch = (old == null);
        boolean retVal;
        if (!ch) {
            assert old.expId == e.expId;
//            System.err.println("Put Error:");
//            System.err.println("  new: " + e.getExpId() + ": " + e);
//            System.err.println("  old: " + old.getExpId() + ": " + old);
//            boolean sameSpace1 = e.getSpace() == old.getSpace();
//            boolean sameSpace2 = getSpace() == old.getSpace();
//            System.err.println("  sameSpace1[" + sameSpace1 + "]");
//            System.err.println("  sameSpace2[" + sameSpace2 + "]");
//            getSpace().check();

//            System.err.println("Dup complex constraint: " + old);
            retVal = false;
        } else {
            addVars(e);
            retVal = true;
        }

//        if (retVal) {
//            assertVars(e.toString());
//        }
        return retVal;


    }

    private void addVars(Exp e) {
        if (vars == null) {
            vars = getSpace().varSetBuilder();
        }
        this.vars.addVars(e.getVars());
    }


    public int size() {
        return args.size();
    }

    public boolean isEmpty() {
        return args.isEmpty();
    }


    /**
     * @return an iterator over this.args, formula the increasing order of ids.
     */
    public Iterator<Exp> iterator() {
        return argIterator();
    }

    public Iterator<Exp> argIterator() {
        final Iterator<IndexedEntry<Exp>> iter = args.iterator();
        return new Iterator<Exp>() {
            public boolean hasNext() {
                return iter.hasNext();
            }

            public Exp next() {
                return iter.next().value();
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    public int computeHash() {
        int sum = 0;
        for (Exp arg : this) {
            sum += arg.hashCode();
        }
        return sum;
    }

    public boolean remove(Exp arg) {
        int expId = arg.getExpId();
        Exp removed = args.remove(expId);
        boolean ch = removed != null;
        if (ch) {
            vars = sp.mkEmptyVarSet();
        }
        return ch;
    }

    public boolean removeAll(Iterable<Exp> complex) {
        boolean ch = false;
        for (Exp exp : complex) {
            if (remove(exp)) {
                ch = true;
            }
        }
        return ch;
    }


    public Exp[] toArray() {
        Exp[] a = new Exp[size()];
        int index = 0;
        for (Exp arg : this) {
            a[index] = arg;
            index++;
        }
        return a;
    }

    public List<Exp> toList() {
        ArrayList<Exp> a = new ArrayList<Exp>(size());
        for (Exp arg : this) {
            a.add(arg);
        }
        return a;
    }

    public int[] toIntArray() {
        int[] a = new int[size()];
        int i = 0;
        for (Exp exp : this) {
            a[i] = exp.getExpId();
        }
        return a;
    }

    public Exp first() {
        return iterator().next();
    }

    public Space getSpace() {
        return first().getSpace();
    }

    public boolean eq(DynComplex that) {
        return args.equals(that.args);
    }

    public boolean contains(Exp e) {
        return args.containsIndex(e.expId);
    }

    public Exp get(int expId) {
        if (args == null) {
            throw new UnsupportedOperationException();
        }
        return args.get(expId);
    }

    private VarSet _computeVars() {
        if (isEmpty()) {
            return sp.mkEmptyVarSet();
        } else {
            VarSet vrs = getSpace().newMutableVarSet();
            for (Exp constraint : this) {
                vrs.addVars(constraint.getVars());
            }
            return vrs;
        }
    }

    public VarSet vars() {
        assert vars != null;
        return vars;
    }


    public boolean remove(Object o) {
        return remove((Exp) o);
    }

    public void clear() {
        vars = sp.mkEmptyVarSet();
        args.clear();
    }

    public boolean isConstantTrue() {
        return args.isEmpty();
    }

    public boolean isConstantFalse() {
        return args == null;
    }

    public int getConstraintCount() {
        return size();
    }


    public Exp toDnnf() {
        if (isEmpty()) return sp.mkTrue();
        Exp exp = mkExp();
        Exp dnnf = exp.toDnnf();

        assert dnnf.isDnnf();
//        assert dnnf.checkDnnf();
        return dnnf;
    }

    @NotNull
    @Override
    public Exp mk() {
        return mkExp();
    }

    public Exp mkExp() {
        if (isConstantFalse()) {
            return sp.mkConstantFalse();
        } else if (isConstantTrue()) {
            return sp.mkConstantTrue();
        } else if (size() == 1) {
            Exp arg = getFirstArg();
            assert arg.getSpace() == sp;
            return arg;
        } else {
            assert size() > 1;
            return sp.mkPosComplex(this);
//            return new ExpSet(space, this).mkExp();
        }
    }

    public Exp getFirstArg() {
        return iterator().next();
    }


    @Override
    public Boolean isFcc() {
        return fcc;
    }

    public Iterable<Exp> vvIt() {
        return new Iterable<Exp>() {
            @Override
            public Iterator<Exp> iterator() {
                return vvIterator();
            }
        };
    }

    public Iterator<Exp> vvIterator() {
        if (isEmpty()) return Iterators.emptyIterator();

        return new ExpFilterIterator(argIterator()) {
            @Override
            public boolean accept(Exp e) {
                return e.isVv();
            }
        };
    }

    public Iterable<Exp> vvpIt() {
        return new Iterable<Exp>() {
            @Override
            public Iterator<Exp> iterator() {
                return vvpIterator();
            }
        };
    }

    public Iterator<Exp> vvpIterator() {
        return new ExpFilterIterator(argIterator()) {
            @Override
            public boolean accept(Exp e) {
                return e.isVVPlus();
            }
        };
    }

    public Exp mkFormula() {
        return mkExp();
    }

    @Override
    public String toString() {
        ImmutableList<Exp> args = ImmutableList.copyOf(argIterator());
        Ser a = new Ser();
        Exp.serializeArgList(a, args);
        return a.toString();
    }


    @Override
    public int getSize() {
        return size();
    }

    @NotNull
    @Override
    public Op getOp() {
        return Op.Formula;
    }

    @NotNull
    @Override
    public Op1 getOp1() {
        return getOp().getOp1();
    }

    @NotNull
    @Override
    public Iterable<Exp> getArgIt() {
        return this::argIterator;
    }


    public boolean containsVar(Var var) {
        return vars.containsVar(var);
    }

    public boolean anyVarOverlap(Lit lit) {
        return vars.anyVarOverlap(lit);
    }

    public boolean anyVarOverlap(Cube cube) {
        return vars.anyVarOverlap(cube);
    }

    public boolean isVarDisjoint(DynCube simple) {
        return vars().isVarDisjoint(simple);
    }

    @NotNull
    public VarSet intersection(@NotNull DynCube simple) {
        return vars().intersection(simple.getVars());
    }

    public Sequence<Exp> asSeq() {
        return ExpItTo.expSeq(getArgIt());
    }

    @NotNull
    @Override
    public Exp[] createExpArray() {
        return ExpFactory.createExpArray(size(), getArgIt());
    }

}
