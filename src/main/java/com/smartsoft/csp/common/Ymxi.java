package com.smartsoft.csp.common;

public class Ymxi implements Comparable<Ymxi> {

    private final YearModel yearModel;
    private final ColorCombo colorCombo;

    public Ymxi(YearModel yearModel, ColorCombo colorCombo) {
        this.yearModel = yearModel;
        this.colorCombo = colorCombo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Ymxi ymxi = (Ymxi) o;

        if (!colorCombo.equals(ymxi.colorCombo)) return false;
        if (!yearModel.equals(ymxi.yearModel)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = yearModel.hashCode();
        result = 31 * result + colorCombo.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return yearModel + " " + colorCombo;
    }

    @Override
    public int compareTo(Ymxi o) {
        int xCompare = yearModel.compareTo(o.yearModel);
        if (xCompare != 0) {
            return xCompare;
        } else {
            return colorCombo.compareTo(o.colorCombo);
        }
    }
}
