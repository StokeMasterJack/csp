package com.tms.inv.tundra;

import com.google.common.collect.ImmutableSet;
import com.tms.csp.ast.*;
import com.tms.csp.util.CspBaseTest2;
import com.tms.csp.VarInfo;
import com.tms.inv.ComboCsp;
import com.tms.inv.Line;
import com.tms.csp.ssutil.TT;
import org.junit.Test;

import java.math.BigInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TundraCfgTest extends CspBaseTest2 {

    private String vPath = "varInfo.txt";
    private String fPath = "factory.pl.txt";
    private String iPath = "inventory.cfg.txt";

    //    4/29/2014    toDnnf delta: 1498      toDnnf delta(no -ea): 1050   toDnnf delta(no -ea & csp.at): 703ms
    @Test
    public void compileFactory() throws Exception {



        TT tt = new TT();
        String clob = loadResource(this, fPath);
        tt.t("load text");

        Parser.compareVarsLineToExtract(clob);

        Csp csp = Csp.parse(clob);
        tt.t("parse text");

        csp.conditionOutAtVars();   //at makes series-level compile faster
        tt.t("atVars");

        Exp nRough = csp.toDnnf();
        tt.t("toDnnf");

        Exp nSmooth = nRough.getSmooth();
        tt.t("getSmooth");

        long satCount = nSmooth.getSatCountLong();
        tt.t("satCountSmooth");

        assertEquals(2635956486144L, satCount);

        Exp nn = nSmooth.copyToOtherSpace();
        String tinyDnnf = nn.getSpace().serializeTinyDnnf();

        int dnnfSize = tinyDnnf.length();
        System.err.println("dnnfSize[" + dnnfSize + "]");

        Exp nnn = Exp.Companion.parseTinyDnnf(tinyDnnf);
        assertEquals(2635956486144L, nnn.getSatCountLong());


    }


    //2m 6s
    @Test
    public void compileFactoryPlusInventory() throws Exception {

        String fClob = loadResource(this, fPath);
        String iClob = loadResource(this, iPath);

        VarInfo varInfo = new VarInfoForThreshold();

        ComboCsp combo = new ComboCsp(fClob, iClob, varInfo);

        combo.processInventory();

        String tinyDnnf = combo.getFactoryInvTinyDnnf();

        writeText("tundra/fact-invClob.cfg.dnnf.txt", tinyDnnf);

        Exp nnn = Exp.Companion.parseTinyDnnf(tinyDnnf);
        BigInteger satCount = nnn.getSatCount();
        System.err.println("satCount[" + satCount + "]");
//        assertEquals(23114496L, satCount);

    }


    @Test
    public void testExtractMsrp() throws Exception {
        String sLine = "MDL_8363 YR_2015 XCOL_01G3 ICOL_FC20 ACY_SP ACY_G_S7 ACY_RE ACY_EE ACY_G_P4 ACY_G_BT ACY_G_BM ACY_G_TX ACY_G_CK 1 $53992 DLR_42276";
        Integer msrp = Line.extractMsrp(sLine);
        System.err.println("msrp[" + msrp + "]");
    }

    @Test
    public void testVarInfo() throws Exception {

        String vClob = loadResource(this, vPath);

        VarInfo varInfo = VarInf.parse(vClob);

        //ACY_5T.YR_2015.SER_tundra.MDL_8241.vtc=true
        ImmutableSet<String> set = ImmutableSet.of("YR_2015", "SER_tundra", "MDL_8241");
        String value = varInfo.getAttribute(set, "ACY_5T", "vtc");
        assertEquals("true", value);
        System.err.println("ACY_5T.YR_2015.SER_tundra.MDL_8241.vtc[" + value + "]");

        //ACY_5F.vtc=true
        set = null;
        value = varInfo.getAttribute(set, "ACY_5F", "vtc");
        assertEquals("true", value);
        System.err.println("ACY_5F.vtc[" + value + "]");

        //DISC_jbl.derived=false
        //DISC_jbl.YR_2015.SER_tundra.MDL_8241.derived=false
        set = ImmutableSet.of("YR_2015", "SER_tundra", "MDL_8241");
        value = varInfo.getAttribute(set, "DISC_jbl", "derived");
        assertEquals("false", value);
        System.err.println("DISC_jbl.derived[" + value + "]");


        //        ACY_H1.type=FIO
        boolean v = varInfo.isFio("ACY_H1");
        assertTrue(v);
        System.err.println("ACY_H1.isFio()[" + value + "]");
    }


}
