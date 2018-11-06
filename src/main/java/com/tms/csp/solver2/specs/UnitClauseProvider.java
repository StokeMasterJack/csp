package com.tms.csp.solver2.specs;

/**
 * Interface for engines able to derive unit clauses for the current problem.
 * 
 * @author daniel
 * @since 2.3.4
 * 
 */
public interface UnitClauseProvider {

    UnitClauseProvider VOID = new UnitClauseProvider() {

        public void provideUnitClauses(UnitPropagationListener upl) {
            // do nothing
        }
    };

    void provideUnitClauses(UnitPropagationListener upl);
}
