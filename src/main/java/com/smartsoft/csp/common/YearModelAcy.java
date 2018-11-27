package com.smartsoft.csp.common;

import com.smartsoft.csp.ast.Var;
import com.smartsoft.csp.varSets.VarSet;

public class YearModelAcy implements Comparable<YearModelAcy> {

    public final YearModel yearModel;
    public final VarSet acyVars;


    public YearModelAcy(YearModel yearModel, VarSet acyVars) {
        this.yearModel = yearModel;
        this.acyVars = acyVars;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        YearModelAcy yma = (YearModelAcy) o;
        return yearModel.equals(yma.yearModel) && acyVars.equals(yma.acyVars);

    }

    @Override
    public int hashCode() {
        int result = yearModel.hashCode();
        result = 31 * result + acyVars.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return yearModel + " " + acyVars;
    }

    @Override
    public int compareTo(YearModelAcy o) {
        int xCompare = yearModel.compareTo(o.yearModel);
        if (xCompare != 0) {
            return xCompare;
        } else {
            return acyVars.compareTo(o.acyVars);
        }
    }

    public Var getYear() {
        return yearModel.getYear();
    }

    public Var getModel() {
        return yearModel.getModel();
    }

    public YearModel getYearModel() {
        return yearModel;
    }

    public VarSet getAcy() {
        return acyVars;
    }
}
