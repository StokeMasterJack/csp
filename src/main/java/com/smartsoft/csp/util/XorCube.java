package com.smartsoft.csp.util;

import com.smartsoft.csp.ast.*;
import com.smartsoft.csp.dnnf.products.AbstractCube;
import com.smartsoft.csp.util.varSets.VarSet;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public class XorCube extends AbstractCube {

    private final Xor xor;
    private final Var trueVar;

    public XorCube(Xor xor, Var trueVar) {
        this.xor = xor;
        this.trueVar = trueVar;
    }


    @NotNull
    @Override
    public Space getSpace() {
        return trueVar.getSpace();
    }

    @Override
    public boolean isTrue(int varId) {
        return this.trueVar.getVarId() == varId;
    }

    @Override
    public boolean containsVarId(int varId) {
        return xor.getVars().containsVarId(varId);
    }

    @Override
    public boolean containsVar(Var var) {
        return xor.getVars().containsVar(var);
    }

    public int getVarId() {
        return trueVar.getVarId();
    }

    public String getPrefix() {
        return xor.getPrefix();
    }

    @NotNull
    public VarSet getVars() {
        return xor.getVars();
    }

    @Override
    public boolean containsLit(int varId, boolean sign) {
        return containsVarId(varId) && isTrue(varId);
    }

    @NotNull
    @Override
    public Iterator<Lit> litIterator() {
        return new LitIterator(varIterator(), trueVar);
    }

    @Override
    public int getTrueVarCount() {
        return 1;
    }

    @NotNull
    @Override
    public Iterator<Var> varIterator() {
        return xor.getVars().varIter();
    }

    @NotNull
    @Override
    public VarSet getTrueVars() {
        return trueVar.mkSingletonVarSet();
    }

//    @Override
//    public void print() {
//        System.err.println("  XorChildEvalContext");
//        System.err.println("    _complexVars: " + xor.get_complexVars());
//        System.err.println("    tVar: " + trueVar);
//    }


    public Xor getXor() {
        return xor;
    }

    public Var getTrueVar() {
        return trueVar;
    }

    @Override
    public boolean anyVarOverlap(@NotNull Exp exp) {
        VarSet otherVars = exp.getVars();
        return otherVars.anyVarOverlap(xor.getVars());
    }


}


