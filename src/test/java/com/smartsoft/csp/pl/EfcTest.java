package com.smartsoft.csp.pl;

import com.smartsoft.csp.ast.Csp;
import com.smartsoft.csp.data.CspSample;
import com.smartsoft.csp.transforms.Transformer;
import com.smartsoft.csp.util.CspBaseTest;
import org.junit.Test;

public class EfcTest extends CspBaseTest {

    @Test
    public void loadCamryAsPL() throws Exception {
        Csp csp = Csp.parse(CspSample.Camry2011Dc);
        csp.print();
    }


    /*
        5s
    */
    @Test
    public void loadEfcAsPL() throws Exception {
        Csp csp = Csp.parse(CspSample.EfcOriginal);
    }


    /*
    4s

    reachableComplex[46679]
    frontierComplex[14559]
    careComplex[32120]
    deadComplex[54349]
    spaceComplex[101028]

    reachableVars[784]
    frontierVars[3]
    careVars[781]
    deadVars[0]
    spaceVars[784]
     */
    @Test
    public void loadToNnf() throws Exception {

        Csp csp = Csp.parse(CspSample.EfcOriginal);
        csp.transform(Transformer.Companion.getNNF());
    }

    @Test
    public void loadToNnf2() throws Exception {
        Csp csp = Csp.parse(CspSample.EfcOriginal);
        csp.toBnf();
        csp.bnfToNnf();
//        writeText(cspDir, "efc.nnf.csp.txt", space);
    }


}
