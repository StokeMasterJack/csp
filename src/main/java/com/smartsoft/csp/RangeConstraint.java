package com.smartsoft.csp;

import com.google.common.collect.ImmutableList;
import com.smartsoft.csp.ast.PLConstants;
import com.smartsoft.csp.parse.VarSpace;

/**
 * Range fact are only valid at the user level
 */
public class RangeConstraint implements PLConstants {

    private final String attribute;     //msrp
    private final Integer min;
    private final Integer max;

    public RangeConstraint(String attribute, Integer min, Integer max) {
        this.attribute = attribute;
        this.min = min;
        this.max = max;
    }

    public RangeConstraint(String attribute, Integer max) {
        this(attribute, null, max);
    }

    public String getAttribute() {
        return attribute;
    }

    public String getInt32Prefix() {
        return attribute;
    }

    public Integer getMin() {
        return min;
    }

    public Integer getMax() {
        return max;
    }

    public boolean isInt32Constraint() {
        ImmutableList<String> int32VarPrefixes = VarSpace.getInt32VarPrefixes();
        return int32VarPrefixes.contains(attribute);
    }

    public boolean isEqualityConstraint() {
        return min != null && max != null && min.equals(max);
    }


    public boolean isMsrp() {
        return attribute.toUpperCase().contains("MSRP");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RangeConstraint that = (RangeConstraint) o;

        if (!attribute.equals(that.attribute)) return false;
        if (max != null ? !max.equals(that.max) : that.max != null) return false;
        if (min != null ? !min.equals(that.min) : that.min != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = attribute.hashCode();
        result = 31 * result + (min != null ? min.hashCode() : 0);
        result = 31 * result + (max != null ? max.hashCode() : 0);
        return result;
    }
}
