package com.smartsoft.inv;

import com.smartsoft.csp.ast.Var;

import java.util.ArrayList;


public class AcyVarInfo implements Comparable<AcyVarInfo> {

    private final Var acyVar;
    private ArrayList<Line> lines;

    public AcyVarInfo(Var acyVar) {
        this.acyVar = acyVar;
        lines = new ArrayList<Line>();
    }

    public void addLine(Line line) {
        lines.add(line);
    }


    public Var getAcyVar() {
        return acyVar;
    }

    @Override
    public int compareTo(AcyVarInfo o) {
        return 0;
    }

    //    public void print(int index) {
//        Exp.prindent(0, index + ": " + acyVar.getLocalName() + " lines:" + lines.size());
//    }
//
//    @Override
//    public int compareTo(@Nonnull AcyVarInfo that) {
//        Integer i2 = lines.size();
//        Integer i1 = that.lines.size();
//        return i1.compareTo(i2);
//    }


}
