package com.tms.csp.solver2.minisat.orders;

/**
 * Utility class used to order the literals according to a specific heuristics.
 * 
 */
final class ValuedLit implements Comparable<ValuedLit> {
    final int id;

    final int count;

    ValuedLit(int id, int count) {
        this.id = id;
        this.count = count;
    }

    public int compareTo(ValuedLit t) {
        if (this.count == 0) {
            return Integer.MAX_VALUE;
        }
        if (t.count == 0) {
            return -1;
        }
        return this.count - t.count;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o instanceof ValuedLit) {
            return ((ValuedLit) o).count == this.count;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.id;
    }

    @Override
    public String toString() {
        return "" + this.id + "(" + this.count + ")"; //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
    }
}
