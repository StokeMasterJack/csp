package com.tms.csp.ast;

import com.google.common.collect.ImmutableSet;
import com.tms.csp.util.varSets.VarSet;

public class Mod extends Mods {

    public final String name;
    public final Mod superMod;
    public final ImmutableSet<String> localPrefixes;
    public final ImmutableSet<String> allPrefixes;

    public Mod(String name, Mod superMod, ImmutableSet<String> prefixes) {
        this.name = name;
        this.superMod = superMod;

        this.localPrefixes = prefixes;
        if (superMod == null) {
            allPrefixes = prefixes;
        } else {
            ImmutableSet.Builder<String> b = ImmutableSet.builder();
            b.addAll(superMod.getAllPrefixes());
            b.addAll(prefixes);
            this.allPrefixes = b.build();
        }
    }

    public String getName() {
        return name;
    }

    public Mod getSuperMod() {
        return superMod;
    }

    public ImmutableSet<String> getAllPrefixes() {
        return allPrefixes;
    }

    public ImmutableSet<String> getLocalPrefixes() {
        return localPrefixes;
    }


    public boolean isLocal(Exp e) {
        return is(e) && (superMod == null || !superMod.is(e));
    }

    public boolean is(Exp e) {
        if (e.isLit()) {
            String prefix = e.getVr().getPrefix();
            return localPrefixes.contains(prefix);
        } else if (e.isConstant()) {
            return false;
        } else {
            //complex
            assert e.isComplex();
            VarSet careVars = e.getVars();
            for (int careVar : careVars.varIdIt()) {
                Space space = e.getSpace();
                Var var = space.getVar(careVar);

                if (!is(var.pLit())) {
                    return false;
                }


            }
            return true;
        }
    }


}
