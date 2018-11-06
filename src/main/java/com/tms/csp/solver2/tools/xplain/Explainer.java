package com.tms.csp.solver2.tools.xplain;

import com.tms.csp.solver2.specs.TimeoutException;

public interface Explainer {

    int[] minimalExplanation() throws TimeoutException;

    void setMinimizationStrategy(MinimizationStrategy explainer);
}
