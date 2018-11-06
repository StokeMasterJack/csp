package com.tms.csp.common;

import com.tms.csp.ast.Var;

import static com.google.common.base.Preconditions.checkNotNull;

public class YearModel implements Comparable<YearModel> {

    public final Var year;
    public final Var model;

    public YearModel(Var year, Var model) {
        checkNotNull(year);
        checkNotNull(model);
        this.year = year;
        this.model = model;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        YearModel that = (YearModel) o;
        return year == that.year && model == that.model;
    }

    @Override
    public int hashCode() {
        int result = year.hashCode();
        result = 31 * result + model.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return year + " " + model;
    }

    @Override
    public int compareTo(YearModel o) {

        int yCompare = year.compareTo(o.year);

        if (yCompare != 0) {
            return yCompare;
        } else {
            return model.compareTo(o.model);
        }
    }

    public Var getYear() {
        return year;
    }

    public Var getModel() {
        return model;
    }
}
