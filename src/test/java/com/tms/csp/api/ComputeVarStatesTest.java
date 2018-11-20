package com.tms.csp.api;

import com.tms.csp.ast.*;
import com.tms.csp.data.CspSample;
import com.tms.csp.ssutil.TT;
import com.tms.csp.util.CspBaseTest2;
import com.tms.csp.util.MetaVarParserDom4j;
import com.tms.csp.util.VarMetaDataUtil;
import com.tms.csp.util.varSets.VarSet;
import com.tms.csp.util.varSets.VarSetBuilder;
import org.dom4j.Element;
import org.junit.Test;

import java.math.BigInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ComputeVarStatesTest extends CspBaseTest2 {




    @Test
    public void testComputeVarMetaCamry() throws Exception {

        Csp csp = Csp.parse(CspSample.Camry2011NoDc);


        VarSet vars = csp.getVars();

        Element varMeta = VarMetaDataUtil.reverseEngineerVarMetaForXor(vars);
        System.err.println(varMeta.asXML());

    }


    @Test
    public void testCompileCamryToDnnfAndSerialize() throws Exception {


        Csp csp = Csp.parse(CspSample.Camry2011NoDc);
        Exp root = csp.toDnnf().getSmooth();

        String dnnf = root.gc().getSpace().serializeTinyDnnf();
        System.err.println(dnnf);


    }

    @Test
    public void testComputeVarStatesCamryUsingVarMeta() throws Exception {

        CspSample.EfcDnnf.loadText();
        CspSample.CamryDnnf.loadText();
        CspSample.EfcOriginalDnnf.loadText();
        CspSample.EfcProdFactoryRules.loadText();
        CspSample.ComboFactoryPlusInvDnnf.loadText();


        if (true) return;


        Exp d1 = Csp.parse(CspSample.CamryDnnf).toDnnf();
        d1.getSmooth();
        System.err.println(d1.getSatCount());


        String camryDnnfText = loadResource(this, "camry.dnnf.txt");
        String varMetaText = loadResource(this, "camry-2011-var-meta.xml");

        Exp root = Exp.Companion.parseTinyDnnf(camryDnnfText);
        MetaVar metaVar = MetaVarParserDom4j.parseVarMetaDataFromXmlDom4j(varMetaText);

        Space space = root.getSpace();
        space.setMetaVar(metaVar);

        VarSet pics1 = space.newMutableVarSet();
        pics1.addVarCode("040");

        ComputeVarStates cvs = new ComputeVarStates(root, pics1);

        VarStates varStates = cvs.computeVarStates();

        varStates.print();

    }

    @Test
    public void testParseVarMeta() throws Exception {
        String varMetaText = loadResource(this, "camry-2011-var-meta.xml");
        MetaVar metaVar = MetaVarParserDom4j.parseVarMetaDataFromXmlDom4j(varMetaText);
        System.err.println(metaVar.serializeTree());
    }


    @Test
    public void testBBCamry() throws Exception {
//        String text = CspSample.Camry2011NoDc.loadText();
//        Space space = new Space(text);
        Csp csp = CspSample.Camry2011NoDc.parseCsp();
        Exp root = csp.toDnnf().getSmooth();
        Exp exp = root.condition("FC14");

//        Exp root = space.getCsp().toDnnf().getSmooth();
        BigInteger satCount1 = root.getSatCount();
        System.err.println("root.satCount1[" + satCount1 + "]");

        BigInteger satCount2 = exp.getSmooth().getSatCount();
        System.err.println("exp.satCount2[" + satCount2 + "]");

        boolean sat1 = root.isSat();
        boolean sat2 = exp.isSat();

        System.err.println("root.sat1[" + sat1 + "]");
        System.err.println(" exp.sat2[" + sat2 + "]");


        DynCube bb1 = root.getBb();
        System.err.println("root.bb: " + bb1);
        DynCube bb2 = exp.getBb();

        System.err.println("  exp.bb: " + bb2);

//        for (Lit lit : bb.litIt()) {
//            System.err.println(lit);
//        }

    }


    @Test
    public void compareComputeSatToCondition() throws Exception {

        Csp csp = Csp.parse(CspSample.Camry2011NoDc);
        Space space = csp.getSpace();
        Exp root = csp.toDnnf().getSmooth();

        VarSet vars = root.getVars();

        for (int i = 0; i < 10000; i++) {

            VarSet pics1 = vars.getRandomSubset(3);
            DynCube pics2 = new DynCube(space, pics1, pics1);

            Exp conditioned = root.condition(pics2);
            boolean isSat1 = conditioned.isSat();

            boolean isSat2 = root.computeSat(pics2);

            assertEquals(isSat1, isSat2);

        }


    }


    @Test
    public void compareComputeSatToConditionPerformance() throws Exception {

        Csp csp = Csp.parse(CspSample.Camry2011NoDc);
        Space space = csp.getSpace();
        Exp root = csp.toDnnf().getSmooth();

        VarSet vars = root.getVars();

        TT tt = new TT();

        for (int i = 0; i < 10000; i++) {
            VarSet pics1 = vars.getRandomSubset(3);

            DynCube pics2 = new DynCube(space, pics1, pics1);
            boolean isSat2 = root.computeSat(pics1);
        }

        tt.t("condition c _complexVars");


        for (int i = 0; i < 10000; i++) {
            VarSet pics1 = vars.getRandomSubset(3);
            DynCube pics2 = new DynCube(space, pics1, pics1);
            Exp conditioned = root.condition(pics2);
            boolean isSat1 = conditioned.isSat();
        }
        tt.t("condition c lits");


    }

    //    @Test
    public void testGetUserCheckboxVars() throws Exception {

        Csp csp = Csp.parse(CspSample.Camry2011NoDc);
        Space space = csp.getSpace();
        Exp root = csp.toDnnf().getSmooth();

        VarSetBuilder pics = space.newMutableVarSet();
        pics.addVar("V6");
        pics.addVar("Ash");

        ComputeVarStates c = new ComputeVarStates(root, pics);

        c.printVarStates();

        VarSet openCheckboxVars = c.getUserOpenCheckboxVars();
        for (Var openCheckboxVar : openCheckboxVars) {
            System.err.println(openCheckboxVar);
            assertTrue(openCheckboxVar.isCheckboxVar());
            assertTrue(!pics.containsVar(openCheckboxVar));
        }


    }
}
