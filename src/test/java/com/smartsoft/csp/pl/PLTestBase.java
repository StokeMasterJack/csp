package com.smartsoft.csp.pl;

import com.smartsoft.csp.data.CspSample;
import com.smartsoft.csp.data.TestData;
import com.smartsoft.csp.ast.PLConstants;
import com.smartsoft.csp.ast.Space;

public class PLTestBase implements PLConstants {

    public boolean includeSlowTests = false;
    public boolean includeSuperSlowTests = false;


    protected Space load(CspSample cspSample) throws Exception {
        return TestData.loadSpace(cspSample);
    }
}
