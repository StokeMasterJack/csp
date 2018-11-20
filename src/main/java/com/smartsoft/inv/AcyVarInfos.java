package com.smartsoft.inv;

import com.google.common.collect.ImmutableSet;
import com.smartsoft.csp.ast.Space;
import com.smartsoft.csp.ast.Var;
import com.smartsoft.csp.util.varSets.VarSet;
import com.smartsoft.csp.varCodes.IVar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Line
 *      core
 *          yr     1 bytes
 *          xcol   2
 *          icol   2
 *          mdl    2
 *      commonAccessories
 *          64 bits
 *      otherAccessories
 *          259 - 64 = 194
 *          1 byte per accessory
 *      rareAccessories
 *          suffix
 *
 *
 */
public class AcyVarInfos {

    Inv inv;
    public HashMap<String, AcyVarInfo> map = new HashMap<String, AcyVarInfo>();
    ArrayList<AcyVarInfo> aa = new ArrayList<AcyVarInfo>();


    public AcyVarInfos(Inv inv) {
        this.inv = inv;
        VarSet acyVars = inv.getAcyVars();

        Space space = inv.getSpace();

        for (Var acyInvVar : acyVars.varIt()) {
            AcyVarInfo info = new AcyVarInfo(acyInvVar);
            String varCode = acyInvVar.getVarCode();
            map.put(varCode, info);
            aa.add(info);
        }

        for (Line line : inv.getLines()) {
            for (Var var : line.getVars().varIt()) {
                if (var.isAcy()) {
                    AcyVarInfo info = get(var);
                    info.addLine(line);
                }
            }
        }
    }

    public Var getBestVar() {
        sort();
        return aa.get(0).getAcyVar();
    }

    public void partition() {
        Common64 common64;
    }

    AcyVarInfo get(IVar var) {
        return map.get(var.getVarCode());
    }

    public void sort() {
        Collections.sort(aa);
    }

//    public void print() {
//        sort();
//
//        for (int i = 0; i < aa.size(); i++) {
//            AcyVarInfo info = aa.get(i);
//            info.print(i);
//
//        }
//    }

    public void getTop64() {
        sort();
        List<AcyVarInfo> top64 = aa.subList(0, 64);

        ImmutableSet.Builder<Var> b = ImmutableSet.builder();
        for (AcyVarInfo varInfo : top64) {
            Var var = varInfo.getAcyVar();
            b.add(var);
        }

        ImmutableSet<Var> vars = b.build();

        ArrayList<Var> aaa = new ArrayList<Var>(vars);


        Common64 common64 = new Common64(aaa);


    }
}
