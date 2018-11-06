package com.tms.csp.pl;

import com.tms.csp.data.CspSample;
import com.tms.csp.data.TestData;
import com.tms.csp.ast.PLConstants;
import com.tms.csp.ast.Space;

public class PLTestBase implements PLConstants {

    public boolean includeSlowTests = false;
    public boolean includeSuperSlowTests = false;


    protected Space load(CspSample cspSample) throws Exception {
        return TestData.loadSpace(cspSample);
    }
}
