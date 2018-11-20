package com.smartsoft.csp.common;

import com.smartsoft.csp.ast.DynCube;
import com.smartsoft.csp.ast.Space;
import com.smartsoft.csp.ast.Var;
import com.smartsoft.csp.fm.dnnf.products.Cube;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class SeriesYear implements Comparable<SeriesYear> {

    private final Var series;
    private final Var year;

    public SeriesYear(Var series, Var year) {
        checkNotNull(series);
        checkNotNull(year);
        checkArgument(series.isSeries());
        checkArgument(year.isYear());
        this.series = series;
        this.year = year;
    }

    public Var getSeries() {
        return series;
    }

    public Var getYear() {
        return year;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SeriesYear that = (SeriesYear) o;

        if (!series.equals(that.series)) return false;
        return year.equals(that.year);

    }

    @Override
    public int hashCode() {
        int result = series.hashCode();
        result = 31 * result + year.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return series + " " + year;
    }

    @Override
    public int compareTo(SeriesYear o) {
        int xCompare = series.compareTo(o.series);
        if (xCompare != 0) {
            return xCompare;
        } else {
            return year.compareTo(o.year);
        }
    }

    public Cube toCube() {
        Space space = series.getSpace();
        DynCube cube = new DynCube(space);
        cube.assign(series.mkPosLit());
        cube.assign(year.mkPosLit());
        return cube;
    }
}
