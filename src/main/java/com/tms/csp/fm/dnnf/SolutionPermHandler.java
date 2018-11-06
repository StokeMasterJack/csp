package com.tms.csp.fm.dnnf;

import com.tms.csp.fm.dnnf.models.Solution;

public interface SolutionPermHandler {

    void onProduct(Solution solution, int dcPerm);
}
