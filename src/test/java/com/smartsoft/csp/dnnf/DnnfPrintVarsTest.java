package com.smartsoft.csp.dnnf;

import com.smartsoft.csp.util.CspBaseTest2;
import com.smartsoft.csp.fm.dnnf.Dnnf;
import org.junit.Test;

public class DnnfPrintVarsTest extends CspBaseTest2 {

    @Test
    public void tiny() throws Exception {
        Dnnf csp = loadCspTiny();
        csp.printVars();
    }

    @Test
    public void trim() throws Exception {
        Dnnf csp = loadCspTrim();
        csp.printVars();
    }

    @Test
    public void trimColor() throws Exception {
        Dnnf csp = loadCspTrimColor();
        csp.printVars();
    }

    @Test
    public void trimColorOptions() throws Exception {
        Dnnf csp = loadCspTrimColorOptions();
        csp.printVars();
    }

    @Test
    public void camry() throws Exception {
        Dnnf csp = loadCspCamry();
        csp.printVars();
    }


}