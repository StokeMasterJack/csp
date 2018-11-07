package com.tms.csp.ast;

import com.google.common.base.Objects;
import com.google.common.collect.Iterators;
import com.tms.csp.ExpArrayTo;
import com.tms.csp.fm.dnnf.products.AbstractCube;
import com.tms.csp.fm.dnnf.products.Cube;
import com.tms.csp.fm.dnnf.products.CubesK;
import com.tms.csp.parse.Head;
import com.tms.csp.util.Bit;
import com.tms.csp.util.DynComplex;
import com.tms.csp.util.HasVarId;
import com.tms.csp.util.XorCube;
import com.tms.csp.util.varSets.VarSet;
import com.tms.csp.util.varSets.VarSetBuilder;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * DynCube can be true|false|open
 * true: is empty
 * false: is contains conflicting assignments
 * else open
 * <p>
 * dCube is short circuiting
 * it detects the short-circuit condition sooner
 * <p>
 * <p>
 * DynFormula  can be true|false|open
 * true is empty
 * false is contains both the true and false versions of the same thing
 * otherwize open
 */
public class DynCube extends AbstractCube {

    public final Space space;

    public VarSet v;
    public VarSet t;

    public final Space _space;

    public DynCube(Space space, VarSet v, VarSet t) {
        checkNotNull(space);
        this.space = space;
        this._space = space;
        this.v = v;
        this.t = t;
    }

    public DynCube(Space space) {
        this(space, null, null);
    }

    public DynCube(Space space, Lit lit) {
        this(space);
        assign(lit);
    }


    public DynCube(Space space, Iterable<Lit> lits) throws ConflictingAssignmentException {
        this(space);
        assignLits(lits);
    }

    public DynCube(Space space, Cube cube) throws ConflictingAssignmentException {
        this(space);
        assignLits(cube);
    }

    public DynCube(Space space, Exp[] lits) throws ConflictingAssignmentException {
        this(space);
        assignLits(lits);
    }

    public static DynCube create(Space space, Exp[] lits) throws ConflictingAssignmentException {
        Iterable<Exp> iterable = ExpArrayTo.expIt(lits);
        return DynCube.create(space, iterable);
    }

    public static DynCube create(Space space, Iterable<Exp> lits) throws ConflictingAssignmentException {
        ArrayList<Lit> a = new ArrayList<Lit>();
        for (Exp exp : lits) {
            a.add(exp.asLit());
        }
        return new DynCube(space, a);
    }

    public DynCube(Space space, List<Cube> cubes) throws ConflictingAssignmentException {
        this(space);
        assignCubes(cubes);
    }

    @Override
    public Space getSpace() {
        return space;
    }

    public boolean assign(Lit lit) throws ConflictingAssignmentException {
        checkNotNull(lit);
        return assign(lit.getVarId(), lit.sign());
    }

    public boolean assign(String sLit) throws ConflictingAssignmentException {
        String varCode = Head.getVarCode(sLit);
        boolean sign = Head.getSign(sLit);
        Lit lit = space.getVar(varCode).mkLit(sign);
        return assign(lit);
    }

    public boolean assign(Var var, boolean value) throws ConflictingAssignmentException {
        return assign(var.getVarId(), value);
    }

    public boolean assign(int lit) throws ConflictingAssignmentException {
        boolean sign = Head.getSign(lit);
        int varId = Head.getVarId(lit);
        return assign(varId, sign);
    }

    public boolean assign(int varId, boolean value) throws ConflictingAssignmentException {
        if (value) {
            return assignTrue(varId);
        } else {
            return assignFalse(varId);
        }
    }

    private boolean _addVar(int varId) {
        assert space != null;
        if (v == null) v = space.varSetBuilder();
        return v.addVarId(varId);
    }

    private boolean _addTrue(int varId) {
        if (t == null) t = space.varSetBuilder();
        return t.addVarId(varId);
    }

    private boolean assignTrue(int varId) throws ConflictingAssignmentException {

        if (isFalse(varId)) {
            throw new ConflictingAssignmentException(varId, true);
        }

        if (isTrue(varId)) {
            return false;
        }

        boolean ch1 = _addVar(varId);
        boolean ch2 = _addTrue(varId);

        assert ch1 && ch2;

        return true;
    }


    private boolean assignFalse(int varId) throws ConflictingAssignmentException {

        if (isTrue(varId)) {
            throw new ConflictingAssignmentException(varId, false);
        }

        if (isFalse(varId)) {
            return false;
        }

        boolean ch1 = _addVar(varId);
        assert ch1;
        return true;

    }


    public void clear() {
        v.clear();
        t.clear();
    }

    public boolean removeVar(int varId) {

        if (containsVarId(varId)) {
            v.removeVarIdDead(varId);
            if (isTrue(varId)) {
                t.removeVarIdDead(varId);
            }
            return true;
        }


        return false;
    }

    public boolean removeVar(Var var) {
        return removeVar(var.getVarId());
    }

    public boolean removeLit(Lit lit) {
        return removeLit(lit.getVarId(), lit.sign());
    }

    public boolean removeLit(int lit) {
        boolean sign = Head.getSign(lit);
        int varId = Head.getVarId(lit);
        return removeLit(varId, sign);
    }

    public boolean removeLit(int varId, boolean sign) {
        if (containsLit(varId, sign)) {
            v.removeVarIdDead(varId);
            if (sign) {
                t.removeVarIdDead(varId);
            }
            return true;
        }
        return false;
    }


    public void removeAll(Iterable<? extends HasVarId> vars) {
        for (HasVarId var : vars) {
            removeVar(var.getVarId());
        }
    }

    public void removeAll(DynCube cube) {
        for (Lit lit : cube.litIt()) {
            removeLit(lit);
        }
    }

    public void collectVars(final VarSetBuilder b) {
        for (Lit lit : this.litIt()) {
            b.addVar(lit.getVr());
        }
    }

    public void collect(final ArrayList<Exp> b) {
        for (Lit lit : this.litIt()) {
            b.add(lit);
        }
    }

    public void assignCubes(Iterable<Cube> cubes) throws ConflictingAssignmentException {
        for (Cube cube : cubes) {
            assignLits(cube);
        }
    }

    public boolean assignLits(Cube cube) throws ConflictingAssignmentException {
        return assignLits(cube.litIt());
    }

    public boolean assignLits(Iterable<Lit> lits) throws ConflictingAssignmentException {
        if (lits == null) return false;
        boolean ch = false;
        for (Exp lit : lits) {
            boolean added = assign(lit.getVarId(), lit.sign());
            if (added) {
                ch = true;
            }
        }
        return ch;
    }

    @Nullable
    public boolean assignLits(Exp[] lits) throws ConflictingAssignmentException {
        Iterable<Lit> lits1 = ExpArrayTo.litIt(lits);
        return assignLits(lits1);
    }

    @Nonnull
    public VarSet getVars() {
        if (v == null) return space.mkEmptyVarSet();
        return v.immutable();
    }

    @Nullable
    public VarSet getVarsOrNull() {
        if (v == null) return null;
        return v.immutable();
    }

    @Nonnull
    public VarSet getTrueVars() {
        if (t == null) return space.mkEmptyVarSet();
        return t.immutable();
    }

    @Nullable
    public VarSet getTrueVarsOrNull() {
        if (t == null) return null;
        return t.immutable();
    }

    @Override
    public boolean containsVar(Var var) {
        if (v == null) {
            return false;
        }
        return v.containsVar(var);
    }

    public Var getFirstTrueVar() {
        for (Var var : varIt()) {
            if (isTrue(var)) return var;
        }
        return null;
    }

    public Var getFirstFalseVar() {
        for (Var var : varIt()) {
            if (!isTrue(var)) return var;
        }
        return null;
    }


    @Nonnull
    public static DynCube union(Space space, @Nullable DynCube ass, @Nullable DynCube bb) {
        boolean assNull = ass == null || ass.isEmpty();
        boolean bbNull = bb == null || bb.isEmpty();
        if (assNull && !bbNull) {
            return bb;
        } else if (!assNull && bbNull) {
            return ass;
        } else if (assNull && bbNull) {
            return space.mkSimple();
        } else {
            assert !assNull && !bbNull;
            VarSet v = VarSet.union(space, ass.v, bb.v);
            VarSet t = VarSet.union(space, ass.t, bb.t);
            return new DynCube(space, v, t);
        }

    }

    @Override
    public boolean containsVarId(int varId) {
        return v != null && v.containsVarId(varId);
    }

    @Override
    public boolean isTrue(int varId) {
        boolean retVal = t != null && t.containsVarId(varId);
        assert !retVal || v.containsVarId(varId);
        return retVal;
    }

    @Override
    final public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null) return false;


        Cube that = (Cube) o;

        assert this.space == that.getSpace();

        if (getVarCount() != that.getVarCount()) return false;
        if (getTrueVarCount() != that.getTrueVarCount()) return false;

        VarSet thisVars = getVars();
        VarSet thatVars = that.getVars();

        if (thisVars == null && thatVars != null) return false;
        if (thisVars != null && thatVars == null) return false;
        if (thisVars == null && thatVars == null) return true;

        assert thisVars != null && thatVars != null;

        if (!thisVars.equals(thatVars)) {
            return false;
        }

        if (!Objects.equal(thisVars, that.getVars())) return false;


        if (that instanceof DynCube) {
            return Objects.equal(getTrueVars(), that.getTrueVars());
        }

        if (that instanceof XorCube) {
            Lit firstLit = that.getFirstLit();
            return containsLit(firstLit);
        }

        throw new UnsupportedOperationException();
    }


    @NotNull
    @Override
    public Iterable<Lit> litIt() {
        return litIt(this);
    }

    public static Iterator<Lit> litIterator(DynCube ass) {
        if (ass == null) return Iterators.emptyIterator();
        return ass.litIterator();
    }


    public static Iterable<Lit> litIt(final DynCube ass) {
        return () -> litIterator(ass);
    }

    @NotNull
    @Override
    public Iterator<Var> varIterator() {
        if (v == null) return Iterators.emptyIterator();
        return v.varIter();
    }

    public static boolean isTrue(VarSet vars, VarSet t, Var var) {
        if (t == null) return false;
        return t.containsVar(var);
    }

    public static int getTrueVarCount(VarSet t) {
        if (t == null) return 0;
        return t.size();
    }

//    @Nullable
//    public Cube intersection(DynComplex complex) {
//        if (complex == null) return null;
//        return intersection(complex.vars);
//    }
//
//    @Nullable
//    public Cube intersection(Exp exp) {
//        if (exp == null) return null;
//        return intersection(exp.getVars());
//    }
//
//    @Nullable
//    public Cube intersection(VarSet vars) {
//        if (v == null || v.isEmpty()) {
//            return null;
//        }
//        if (vars == null || vars.isEmpty()) {
//            return null;
//        }
//
//        VarSet vi = vars.overlap(v);
//
//        if (tCon == null) {
//            return new DynCube(space, vi, null);
//        } else {
//            VarSet ti = tCon.overlap(vars);
//            return new DynCube(space, vi, ti);
//        }
//
//
//    }

    public void assignSafe(Lit lit) {
        try {
            boolean added = assign(lit);
            if (!added) throw new IllegalStateException();
        } catch (ConflictingAssignmentException e) {
            throw new IllegalStateException();
        }
    }

    public void assignSafe(Cube cube) {
        try {
            boolean added = assignLits(cube);
            if (!added) throw new IllegalStateException();
        } catch (ConflictingAssignmentException e) {
            throw new IllegalStateException();
        }
    }


    public Exp toDnnf() {
        return mkExp();
    }


    public boolean isConstantTrue() {
        return isEmpty();
    }

    public boolean isAssignedFlip(Lit lit) {
        if (v == null) {
            return false;
        }
        Bit value = getValue(lit.vr);
        if (value.isOpen()) {
            return false;
        } else if (value.is(lit.sign())) {
            return false;
        } else {
            assert value.is(!lit.sign());
            return true;
        }
    }

    public DynCube copy() {
        return new DynCube(space, v.mutableCopy(), t.mutableCopy());
    }

    @NotNull
    public DynCube copyImmutable() {
        return new DynCube(space, v.immutable(), t.immutable());
    }

    @NotNull
    public Set<String> getTrueVarCodes() {
        return t.toVarCodeSet();
    }

    @NotNull
    public Set<String> getFalseVarCodes() {
        return getFalseVars().toVarCodeSet();
    }

    public boolean containsAll(Cube cube) {
        for (Lit lit : cube.litIt()) {
            boolean contains = containsLit(lit);
            if (!contains) return false;
        }
        return true;
    }


    /**
     * always returns a dnnf:
     *
     * @return constantTrue | constantFalse | Lit | Cube(all lits)
     */
    public Exp mkExp() {
        return mk();
    }

    public Exp mkCubeExp() {
        return mk();
    }

    @NotNull
    public Exp mk() {
        return CubesK.mk(this);
    }


}



