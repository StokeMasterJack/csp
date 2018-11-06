package com.tms.csp.fm.dnnf.products;


import com.tms.csp.ast.Exp;
import com.tms.csp.ast.Space;
import com.tms.csp.ast.Var;
import com.tms.csp.fm.dnnf.models.Solution;
import com.tms.csp.util.varSets.VarSet;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public class DcPermCube extends AbstractCube {

    private final Solution solution;
    private final int dcPerm;

    public DcPermCube(Solution solution, int dcPerm) {
        this.solution = solution;
        this.dcPerm = dcPerm;
    }

    @Override
    public Space getSpace() {
        return solution.getSpace();
    }

    public boolean isTrue(int varId) {
        return solution.isTrue(varId, dcPerm);
    }

    @Override
    public int getSize() {
        return solution.getVarCount();
    }

    @Override
    public boolean containsVarId(int varId) {
        return solution.containsVar(varId);
    }

    @Override
    public boolean anyVarOverlap(@NotNull Cube cube) {
        return solution.anyVarOverlap(cube);
    }

    @Override
    public boolean isVarDisjoint(@NotNull Exp exp) {
        return solution.getVars().isVarDisjoint(exp);
    }

    @Override
    public boolean anyVarOverlap(@NotNull VarSet vs) {
        return solution.getVars().anyVarOverlap(vs);
    }

    @Override
    public boolean containsLit(int varId, boolean sign) {
        if (!containsVarId(varId)) return false;
        if (sign) {
            isTrue(varId);
        }
        return true;
    }

    @Override
    public Iterator<Var> varIterator() {
        return solution.varIterator();
    }

    @Override
    public VarSet getTrueVars() {
        return solution.getTrueVars(dcPerm);
    }

    public Solution getSolution() {
        return solution;
    }

    @Override
    public VarSet getVars() {
        return solution.getVars();
    }


    public String info() {
        return getClass() + " " + solution.getSolutionCube().getClass();
    }


    @Override
    public boolean containsVar(Var var) {
        return solution.containsVar(var);
    }

    @Override
    public boolean isTrue(Var vr) {
        return solution.isTrue(vr.getVarId(), dcPerm);
    }


    public static boolean useGoodEqualsAndHashCode = true;
}
