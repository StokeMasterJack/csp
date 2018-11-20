
package com.smartsoft.csp.solver2.tools.encoding;

/**
 * This Enum describes the different encodings that can be used for the
 * "at most one", "at most k", "exactly one", "exactly k" fact.
 * 
 * @author stephanieroussel
 * 
 */
public enum EncodingStrategy {
    BINARY("Binary"), BINOMIAL("Binomial"), COMMANDER("Commander"), LADDER(
            "Ladder"), PRODUCT("Product"), SEQUENTIAL("Sequential"), NATIVE(
            "Native");

    private String name;

    EncodingStrategy(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
