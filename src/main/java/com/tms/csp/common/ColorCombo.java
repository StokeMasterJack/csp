package com.tms.csp.common;

import com.tms.csp.ast.Var;

import static com.google.common.base.Preconditions.checkNotNull;

public class ColorCombo implements Comparable<ColorCombo> {

    public final Var xcol;
    public final Var icol;

    public ColorCombo(Var xcol, Var icol) {
        checkNotNull(xcol);
        checkNotNull(icol);
        this.xcol = xcol;
        this.icol = icol;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ColorCombo combo = (ColorCombo) o;
        return xcol.equals(combo.xcol) && icol.equals(combo.icol);
    }

    @Override
    public int hashCode() {
        int result = xcol.hashCode();
        result = 31 * result + icol.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return xcol + " " + icol;
    }

    @Override
    public int compareTo(ColorCombo o) {
        int xCompare = xcol.compareTo(o.xcol);
        if (xCompare != 0) {
            return xCompare;
        } else {
            return icol.compareTo(o.icol);
        }
    }

    public Var getXCol() {
        return xcol;
    }

    public Var getICol() {
        return icol;
    }
}
