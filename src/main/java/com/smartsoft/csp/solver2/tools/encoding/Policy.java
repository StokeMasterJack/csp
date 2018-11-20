
package com.smartsoft.csp.solver2.tools.encoding;

import com.smartsoft.csp.solver2.specs.ContradictionException;
import com.smartsoft.csp.solver2.specs.IConstr;
import com.smartsoft.csp.solver2.specs.ISolver;
import com.smartsoft.csp.solver2.specs.IVecInt;

/**
 * This class allows the use of different encodings for different cardinality
 * fact.
 * 
 * @author stephanieroussel
 * @since 2.3.1
 */
public class Policy extends EncodingStrategyAdapter {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private final Sequential seq = new Sequential();
    private final Binary binary = new Binary();
    private final Product product = new Product();
    private final Commander commander = new Commander();
    private final Binomial binomial = new Binomial();
    private final Ladder ladder = new Ladder();

    private EncodingStrategyAdapter atMostOneEncoding = null;
    private EncodingStrategyAdapter atMostKEncoding = null;
    private EncodingStrategyAdapter exactlyOneEncoding = null;
    private EncodingStrategyAdapter exactlyKEncoding = null;
    private EncodingStrategyAdapter atLeastOneEncoding = null;
    private EncodingStrategyAdapter atLeastKEncoding = null;

    private EncodingStrategyAdapter getAdapterFromEncodingName(
            EncodingStrategy encodingName) {
        switch (encodingName) {
        case BINARY:
            return this.binary;
        case BINOMIAL:
            return this.binomial;
        case COMMANDER:
            return this.commander;
        case LADDER:
            return this.ladder;
        case PRODUCT:
            return this.product;
        case SEQUENTIAL:
            return this.seq;
        case NATIVE:
        default:
            return null;
        }
    }

    private EncodingStrategy getEncodingTypeFromAdapter(
            EncodingStrategyAdapter adapter) {
        if (adapter instanceof Binary) {
            return EncodingStrategy.BINARY;
        } else if (adapter instanceof Binomial) {
            return EncodingStrategy.BINOMIAL;
        } else if (adapter instanceof Commander) {
            return EncodingStrategy.COMMANDER;
        } else if (adapter instanceof Ladder) {
            return EncodingStrategy.LADDER;
        } else if (adapter instanceof Product) {
            return EncodingStrategy.PRODUCT;
        } else if (adapter instanceof Sequential) {
            return EncodingStrategy.SEQUENTIAL;
        } else {
            return EncodingStrategy.NATIVE;
        }

    }

    public EncodingStrategyAdapter getAtMostOneEncoding() {
        return this.atMostOneEncoding;
    }

    public void setAtMostOneEncoding(EncodingStrategyAdapter atMostOneEncoding) {
        this.atMostOneEncoding = atMostOneEncoding;
    }

    public void setAtMostOneEncoding(EncodingStrategy atMostOneEncoding) {
        this.atMostOneEncoding = getAdapterFromEncodingName(atMostOneEncoding);
    }

    public EncodingStrategyAdapter getAtMostKEncoding() {
        return this.atMostKEncoding;
    }

    public void setAtMostKEncoding(EncodingStrategyAdapter atMostKEncoding) {
        this.atMostKEncoding = atMostKEncoding;
    }

    public void setAtMostKEncoding(EncodingStrategy atMostKEncoding) {
        this.atMostKEncoding = getAdapterFromEncodingName(atMostKEncoding);
    }

    public EncodingStrategyAdapter getExactlyOneEncoding() {
        return this.exactlyOneEncoding;
    }

    public void setExactlyOneEncoding(EncodingStrategyAdapter exactlyOneEncoding) {
        this.exactlyOneEncoding = exactlyOneEncoding;
    }

    public void setExactlyOneEncoding(EncodingStrategy exactlyOneEncoding) {
        this.exactlyOneEncoding = getAdapterFromEncodingName(exactlyOneEncoding);
    }

    public EncodingStrategyAdapter getExactlyKEncoding() {
        return this.exactlyKEncoding;
    }

    public void setExactlyKEncoding(EncodingStrategyAdapter exactlyKEncoding) {
        this.exactlyKEncoding = exactlyKEncoding;
    }

    public void setExactlyKEncoding(EncodingStrategy exactlyKEncoding) {
        this.exactlyKEncoding = getAdapterFromEncodingName(exactlyKEncoding);
    }

    public EncodingStrategyAdapter getAtLeastOneEncoding() {
        return this.atLeastOneEncoding;
    }

    public void setAtLeastOneEncoding(EncodingStrategyAdapter atLeastOneEncoding) {
        this.atLeastOneEncoding = atLeastOneEncoding;
    }

    public void setAtLeastOneEncoding(EncodingStrategy atLeastOneEncoding) {
        this.atLeastOneEncoding = getAdapterFromEncodingName(atLeastOneEncoding);
    }

    public EncodingStrategyAdapter getAtLeastKEncoding() {
        return this.atLeastKEncoding;
    }

    public void setAtLeastKEncoding(EncodingStrategyAdapter atLeastKEncoding) {
        this.atLeastKEncoding = atLeastKEncoding;
    }

    public void setAtLeastKEncoding(EncodingStrategy atLeastKEncoding) {
        this.atLeastKEncoding = getAdapterFromEncodingName(atLeastKEncoding);
    }

    @Override
    public IConstr addAtMost(ISolver solver, IVecInt literals, int k)
            throws ContradictionException {

        if (k == 0 || literals.size() == 1) {
            // will propagate unit literals
            return super.addAtMost(solver, literals, k);
        }
        if (literals.size() <= 1) {
            throw new UnsupportedOperationException(
                    "requires at least 2 literals");
        }
        if (k == 1 && this.atMostOneEncoding != null) {
            return this.atMostOneEncoding.addAtMostOne(solver, literals);
        }
        if (this.atMostKEncoding != null) {
            if (k == 1) {
                return this.atMostKEncoding.addAtMostOne(solver, literals);
            } else {
                return this.atMostKEncoding.addAtMost(solver, literals, k);
            }
        }
        return super.addAtMost(solver, literals, k);
    }

    @Override
    public IConstr addExactly(ISolver solver, IVecInt literals, int n)
            throws ContradictionException {
        if (n == 1 && this.exactlyOneEncoding != null) {
            return this.exactlyOneEncoding.addExactlyOne(solver, literals);
        } else if (this.exactlyKEncoding != null) {
            if (n == 1) {
                return this.exactlyKEncoding.addExactlyOne(solver, literals);
            } else {
                return this.exactlyKEncoding.addExactly(solver, literals, n);
            }
        }

        return super.addExactly(solver, literals, n);
    }

    @Override
    public IConstr addAtLeast(ISolver solver, IVecInt literals, int n)
            throws ContradictionException {
        if (n == 1) {
            if (this.atLeastOneEncoding != null) {
                return this.atLeastOneEncoding.addAtLeastOne(solver, literals);
            }
        } else if (this.atLeastKEncoding != null) {
            return this.atLeastKEncoding.addAtLeast(solver, literals, n);
        }

        return super.addAtLeast(solver, literals, n);

    }

    @Override
    public String toString() {
        String s = "";
        s += "Policy = [At most K: "
                + getEncodingTypeFromAdapter(getAtMostKEncoding())
                + ", at most 1: "
                + getEncodingTypeFromAdapter(getAtMostOneEncoding())
                + ", exactly K: "
                + getEncodingTypeFromAdapter(getExactlyKEncoding())
                + ", exactly 1: "
                + getEncodingTypeFromAdapter(getExactlyOneEncoding()) + "]";

        return s;
    }

}
