package com.tms.csp.fm.dnnf.models;

import com.google.common.collect.Iterators;
import com.tms.csp.ast.Exp;
import com.tms.csp.ast.Space;
import com.tms.csp.ast.Var;
import com.tms.csp.fm.dnnf.products.Cube;
import com.tms.csp.fm.dnnf.products.DcPermCube;
import com.tms.csp.util.varSets.VarSet;
import com.tms.csp.util.varSets.VarSetBuilder;

import java.util.Collection;
import java.util.Iterator;

public class Solution extends AbstractCubeSet {

    private final Cube solutionCube;
    private final VarSet dcVars;
    private final Cube prefix;

    public Solution(Space space, Cube solutionCube, VarSet dcVars) {
        this(space, solutionCube, dcVars, null);
    }

    public Solution(Space space, Cube solutionCube, VarSet dcVars, Cube prefix) {
        super(space);
        this.solutionCube = solutionCube;
        this.dcVars = dcVars.immutable();
        this.prefix = prefix;
    }

    public Iterator<Var> varIterator() {
        if (prefix == null) return Iterators.concat(solutionCube.varIterator(), dcVars.varIter());
        return Iterators.concat(solutionCube.varIterator(), dcVars.varIter(), prefix.varIterator());
    }

    public boolean containsVar(Var var) {
        return containsVar(var.getVarId());
    }

    public boolean containsVar(int varId) {
        return solutionCube.containsVarId(varId) || dcVars.containsVarId(varId) || (prefix == null || prefix.containsVarId(varId));
    }

    public boolean anyVarOverlap(Cube other) {
        if (other == null || other.isEmpty()) return false;

        if (solutionCube.anyVarOverlap(other)) return true;
        if (other.anyVarOverlap(dcVars)) return true;

        if (prefix == null) return false;
        return prefix.anyVarOverlap(other);

    }

    public VarSet getVars() {
        VarSetBuilder b = getSpace().newMutableVarSet();
        b.addVarSet(solutionCube.getVars());
        b.addVarSet(dcVars);
        if (prefix != null) b.addVarSet(prefix.getVars());
        return b.build();
    }


    public VarSet getTrueVars(int dcPerm) {
        VarSetBuilder b = getSpace().newMutableVarSet();

        b.addVarSet(solutionCube.getTrueVars());
        if (prefix != null) {
            b.addVarSet(prefix.getTrueVars());
        }
        collectDcTrueVars(b, dcPerm);
        return b.build();
    }

    public void collectDcTrueVars(VarSetBuilder tVars, int dcPerm) {
        for (int varId : dcVars.varIdIt()) {
            if (isTrue(varId, dcPerm)) {
                tVars.addVarId(varId);
            }
        }
    }

    public Cube getSolutionCube() {
        return solutionCube;
    }

    public int computeBitIndex(int varId) {
        return dcVars.indexOf(varId);
    }

    public void forEach(CubeHandler ph) {
        int dcCount = dcVars.size();
        long dcPermCount = Exp.Companion.computeDcPermCount(dcCount);
        for (int dcPerm = 0; dcPerm < dcPermCount; dcPerm++) {
            DcPermCube p = new DcPermCube(this, dcPerm);
            ph.onCube(p);
        }
    }

    public static int twoToThePowerOf(int power) {
        return (int) Math.pow(2, power);
    }

    public static int computeDcPermCount(int dcCount) {
        return twoToThePowerOf(dcCount);
    }

    public int getDcPermCount() {
        int dcCount = getDcCount();
        return computeDcPermCount(dcCount);
    }

    public int getDcCount() {
        return dcVars.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Solution solution = (Solution) o;

        if (!dcVars.equals(solution.dcVars)) return false;
        if (prefix != null ? !prefix.equals(solution.prefix) : solution.prefix != null) return false;
        if (!solutionCube.equals(solution.solutionCube)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = solutionCube.hashCode();
        result = 31 * result + dcVars.hashCode();
        result = 31 * result + (prefix != null ? prefix.hashCode() : 0);
        return result;
    }

    public VarSet getDcVars() {
        return dcVars;
    }

    @Override
    public int size() {
        return computeDcPermCount(dcVars.size());
    }

    public int getVarCount() {
        return solutionCube.getSize() + dcVars.size() + (prefix == null ? 0 : prefix.getSize());
    }


    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public boolean contains(Object cube) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<Cube> iterator() {
        return new DcPermProductIterator(this);
    }

    @Override
    public boolean containsAll(Collection<?> cubes) {
        throw new UnsupportedOperationException();
    }


    public boolean isTrue(int varId, int dcPerm) {
        return solutionCube.isTrue(varId) || (prefix != null && prefix.isTrue(varId)) || isBitSet(varId, dcPerm);
    }

    public int getPrefixSize() {
        if (prefix == null) return 0;
        return prefix.getSize();
    }

    public Cube getPrefix() {
        return prefix;
    }


    public boolean isBitSet(int varId, int dcPerm) {
        int bitIndex = dcVars.indexOf(varId);
        if (bitIndex == -1) return false;
        return (dcPerm & (1 << bitIndex)) != 0;
    }
}



