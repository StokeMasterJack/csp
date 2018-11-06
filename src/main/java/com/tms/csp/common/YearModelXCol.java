package com.tms.csp.common;

import com.tms.csp.ast.Var;

public class YearModelXCol implements Comparable<YearModelXCol> {

    public final YearModel yearModel;
    public final Var xCol;

    public YearModelXCol(YearModel yearModel, Var xCol) {
        this.yearModel = yearModel;
        this.xCol = xCol;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        YearModelXCol that = (YearModelXCol) o;

        if (!xCol.equals(that.xCol)) return false;
        if (!yearModel.equals(that.yearModel)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = yearModel.hashCode();
        result = 31 * result + xCol.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return yearModel + " " + xCol;
    }

    @Override
    public int compareTo(YearModelXCol o) {
        int xCompare = yearModel.compareTo(o.yearModel);
        if (xCompare != 0) {
            return xCompare;
        } else {
            return xCol.compareTo(o.xCol);
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

    public Var getXCol() {
        return xCol;
    }
}
