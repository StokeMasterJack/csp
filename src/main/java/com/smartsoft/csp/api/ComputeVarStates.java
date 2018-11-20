package com.smartsoft.csp.api;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.smartsoft.csp.ast.DynCube;
import com.smartsoft.csp.ast.Exp;
import com.smartsoft.csp.ast.Space;
import com.smartsoft.csp.ast.Var;
import com.smartsoft.csp.fm.dnnf.vars.VarFilter;
import com.smartsoft.csp.util.Bit;
import com.smartsoft.csp.util.varSets.VarSet;

import java.util.HashMap;
import java.util.logging.Logger;

/**
 * This is the primary purpose of the configurator library.
 * <p>
 * Given a CSP (formula the form of a DNNF Exp) and a set of user picks (assumed valid), compute what simple changes are and are not allowed.
 * <p>
 * Open checkboxes: Is it confirmed-open | inferred-true | inferred-false
 * True checkboxes: can it be deselected
 */
public class ComputeVarStates {

    private final Exp baseConstraint;
    private final VarSet pics_;

    //computed
    private final Exp conditioned;
    private final boolean sat;

    public ComputeVarStates(Exp baseConstraint, VarSet pics) {
        this.baseConstraint = baseConstraint;

        if (pics == null || pics.isEmpty()) {
            pics_ = baseConstraint.getSpace().mkEmptyVarSet();
            this.conditioned = baseConstraint;
        } else {
            this.pics_ = pics;
            //pre-compute cached values
            this.conditioned = baseConstraint.condition(pics);
        }

        this.sat = this.conditioned.isSat();

    }

    private static ImmutableMap<String, Var> computeRadioTruePicks(VarSet pics) {
        HashMap<String, Var> b = new HashMap<String, Var>();
        for (Var pic : pics) {
            if (pic.isRadioVar()) {
                Var old = b.put(pic.getPrefix(), pic);
                if (old != null) {
                    throw new IllegalStateException("[" + pic + "] prefix used twice");
                }
            }
        }
        return ImmutableMap.copyOf(b);
    }

    public VarStates computeVarStates() {
        VarStates varStates = new VarStates();
        varStates.setSat(sat);
        if (!sat) return null;

        if (sat) {
            VarSet vars = baseConstraint.getSpace().getVars();
            for (Var var : vars) {
                VarState varState = computeVarState2(var);
                varStates.addVarState(varState);
            }
        }

        return varStates;
    }

    public boolean isUserOpen(Var var) {
        return !pics_.containsVar(var);
    }

    public boolean isUserTrue(Var var) {
        return pics_.containsVar(var);
    }

    public VarState computeVarState2(Var proposedVar) {
        Space space = baseConstraint.getSpace();
        VarSet localPics = pics_;

        Bit currentValue = localPics.contains(proposedVar) ? Bit.TRUE : Bit.OPEN;

        Preconditions.checkState(sat);
        VarSet picsMinusProposed;
        if (proposedVar.isCheckboxVar()) {
            picsMinusProposed = localPics.minus(proposedVar);
        } else {
            //radio
            picsMinusProposed = localPics.minus(proposedVar.getAllXorSiblings());
        }


        DynCube pics = new DynCube(space, picsMinusProposed, picsMinusProposed);

        DynCube pPics = pics.copy();
        pPics.assign(proposedVar.pLit());

        DynCube nPics = pics.copy();
        nPics.assign(proposedVar.nLit());


        boolean pSat = baseConstraint.computeSat(pPics);
        boolean nSat = baseConstraint.computeSat(nPics);

        if (pSat && nSat) return new VarState(proposedVar, currentValue, Bit.OPEN);
        else if (pSat && !nSat) return new VarState(proposedVar, currentValue, Bit.TRUE);
        else if (!pSat && nSat) return new VarState(proposedVar, currentValue, Bit.FALSE);
        else if (!pSat && !nSat) throw new IllegalStateException(); //
        else throw new IllegalStateException();

    }


    public VarSet getUserOpenCheckboxVars() {
        VarSet vars = baseConstraint.getVars();
        return vars.minus(pics_).filter(new VarFilter() {
            @Override
            public boolean accept(Var var) {
                return !var.isXorChild();
            }
        });
    }


    public VarSet getRadioVars() {
        VarSet vars = baseConstraint.getVars();
        return vars.filter(new VarFilter() {
            @Override
            public boolean accept(Var var) {
                return var.isRadioVar();
            }
        });
    }

    public VarSet getCheckboxVars() {
        VarSet vars = baseConstraint.getVars();
        return vars.filter(new VarFilter() {
            @Override
            public boolean accept(Var var) {
                return var.isCheckboxVar();
            }
        });
    }


    public void printVarStates() {
        System.err.println("CheckboxVars:" + getCheckboxVars().size());
        System.err.println("RadioVars:   " + getRadioVars().size());

    }

    public Space getSpace() {
        return baseConstraint.getSpace();
    }

    public Exp getBaseConstraint() {
        return baseConstraint;
    }

    public boolean isSat() {
        return sat;
    }

    public Var getVar(String varCode) {
        return getSpace().getVar(varCode);
    }

    public void dumpPicks() {
        log.info("pics dump: " + pics_.toString());

    }

    public VarSet getPics() {
        return pics_;
    }

    private static Logger log = Logger.getLogger(ComputeVarStates.class.getName());
}
