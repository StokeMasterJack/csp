package com.tms.csp.pl

import com.tms.csp.ast.Csp
import com.tms.csp.ast.toCube
import com.tms.csp.ast.toCubes
import com.tms.csp.data.CspSample
import com.tms.csp.fm.dnnf.products.Cube
import com.tms.csp.util.CspBaseTest2
import junit.framework.Assert.assertEquals
import kotlin.test.Test

class CspTest : CspBaseTest2() {

//    private lateinit var space: Space
//
//    private val parser: Parser get() = space.parser;
//
//
//    @Before
//    fun setup() {
//        space = Space()
//    }


    @Test
    fun testTiny() {

        val tiny = """
            vars(a b c)
            xor(a b)
            conflict(b c)
        """


        val satCountExpected: Long = 3

        val sCubesExpected = """
            a !b c
            a !b !c
            !a b !c
        """.trim()

        val csp = Csp.parse(tiny)


        val plSatCount = csp.satCountPL()

        assertEquals(satCountExpected, plSatCount)


        val rough = csp.toDnnf()


        val smooth = rough.smooth
        val smotSatCount = smooth.satCount

        assertEquals(satCountExpected, smotSatCount)


        val cubes: Set<Cube> = smooth.cubesSmooth

        val cubesCount = cubes.size

        assertEquals(satCountExpected, cubesCount.toLong())

        assertEquals(sCubesExpected.toCubes(csp.space), cubes)
        assertEquals(satCountExpected, smooth.satCount)
        assertEquals(satCountExpected, cubes.size.toLong())


    }


    @Test
    fun testTinyWithDontCare() {

        val tiny = """
            vars(a b c d)
            xor(a b)
            conflict(b c)
            dc(d)
        """

        val satCountExpected: Long = 6

        val sCubesExpected = """
            a   !b   !c    d
            a   !b    c    d
           !a    b   !c   !d
            a   !b    c   !d
            a   !b   !c   !d
           !a    b   !c    d
        """.trim()


        val csp = Csp.parse(tiny)

        val plSatCount = csp.satCountPL()

        val d = csp.toDnnf().smooth
        val cubes = d.cubesSmooth

        assertEquals(satCountExpected, plSatCount)
        assertEquals(satCountExpected, d.satCount)
        assertEquals(satCountExpected, cubes.size.toLong())
        assertEquals(sCubesExpected.toCubes(csp), cubes)

        //Exp.printCubes(cubes);
    }


    @Test
    fun testTrim() {


        val satCountExpected: Long = 11

        val sCubesExpected = """
            MDL_2513   !MDL_2514   !MDL_2531   !MDL_2532   !MDL_2552   !MDL_2540   !MDL_2554   !MDL_2545   !MDL_2546   !MDL_2550   !MDL_2560      ENG_L4     !ENG_V6 !ENG_Hybrid    GRD_Base     !GRD_LE     !GRD_SE    !GRD_XLE    !GRD_Hyb      TX_6MT     !TX_6AT    !TX_ECVT
           !MDL_2513    MDL_2514   !MDL_2531   !MDL_2532   !MDL_2552   !MDL_2540   !MDL_2554   !MDL_2545   !MDL_2546   !MDL_2550   !MDL_2560      ENG_L4     !ENG_V6 !ENG_Hybrid    GRD_Base     !GRD_LE     !GRD_SE    !GRD_XLE    !GRD_Hyb     !TX_6MT      TX_6AT    !TX_ECVT
           !MDL_2513   !MDL_2514    MDL_2531   !MDL_2532   !MDL_2552   !MDL_2540   !MDL_2554   !MDL_2545   !MDL_2546   !MDL_2550   !MDL_2560      ENG_L4     !ENG_V6 !ENG_Hybrid   !GRD_Base      GRD_LE     !GRD_SE    !GRD_XLE    !GRD_Hyb      TX_6MT     !TX_6AT    !TX_ECVT
           !MDL_2513   !MDL_2514   !MDL_2531    MDL_2532   !MDL_2552   !MDL_2540   !MDL_2554   !MDL_2545   !MDL_2546   !MDL_2550   !MDL_2560      ENG_L4     !ENG_V6 !ENG_Hybrid   !GRD_Base      GRD_LE     !GRD_SE    !GRD_XLE    !GRD_Hyb     !TX_6MT      TX_6AT    !TX_ECVT
           !MDL_2513   !MDL_2514   !MDL_2531   !MDL_2532    MDL_2552   !MDL_2540   !MDL_2554   !MDL_2545   !MDL_2546   !MDL_2550   !MDL_2560     !ENG_L4      ENG_V6 !ENG_Hybrid   !GRD_Base      GRD_LE     !GRD_SE    !GRD_XLE    !GRD_Hyb     !TX_6MT      TX_6AT    !TX_ECVT
           !MDL_2513   !MDL_2514   !MDL_2531   !MDL_2532   !MDL_2552    MDL_2540   !MDL_2554   !MDL_2545   !MDL_2546   !MDL_2550   !MDL_2560      ENG_L4     !ENG_V6 !ENG_Hybrid   !GRD_Base     !GRD_LE     !GRD_SE     GRD_XLE    !GRD_Hyb     !TX_6MT      TX_6AT    !TX_ECVT
           !MDL_2513   !MDL_2514   !MDL_2531   !MDL_2532   !MDL_2552   !MDL_2540    MDL_2554   !MDL_2545   !MDL_2546   !MDL_2550   !MDL_2560     !ENG_L4      ENG_V6 !ENG_Hybrid   !GRD_Base     !GRD_LE     !GRD_SE     GRD_XLE    !GRD_Hyb     !TX_6MT      TX_6AT    !TX_ECVT
           !MDL_2513   !MDL_2514   !MDL_2531   !MDL_2532   !MDL_2552   !MDL_2540   !MDL_2554    MDL_2545   !MDL_2546   !MDL_2550   !MDL_2560      ENG_L4     !ENG_V6 !ENG_Hybrid   !GRD_Base     !GRD_LE      GRD_SE    !GRD_XLE    !GRD_Hyb      TX_6MT     !TX_6AT    !TX_ECVT
           !MDL_2513   !MDL_2514   !MDL_2531   !MDL_2532   !MDL_2552   !MDL_2540   !MDL_2554   !MDL_2545    MDL_2546   !MDL_2550   !MDL_2560      ENG_L4     !ENG_V6 !ENG_Hybrid   !GRD_Base     !GRD_LE      GRD_SE    !GRD_XLE    !GRD_Hyb     !TX_6MT      TX_6AT    !TX_ECVT
           !MDL_2513   !MDL_2514   !MDL_2531   !MDL_2532   !MDL_2552   !MDL_2540   !MDL_2554   !MDL_2545   !MDL_2546    MDL_2550   !MDL_2560     !ENG_L4      ENG_V6 !ENG_Hybrid   !GRD_Base     !GRD_LE      GRD_SE    !GRD_XLE    !GRD_Hyb     !TX_6MT      TX_6AT    !TX_ECVT
           !MDL_2513   !MDL_2514   !MDL_2531   !MDL_2532   !MDL_2552   !MDL_2540   !MDL_2554   !MDL_2545   !MDL_2546   !MDL_2550    MDL_2560     !ENG_L4     !ENG_V6  ENG_Hybrid   !GRD_Base     !GRD_LE     !GRD_SE    !GRD_XLE     GRD_Hyb     !TX_6MT     !TX_6AT     TX_ECVT
        """.trim()


        val csp = Csp.parse(CspSample.Trim, tiny = true)

        val satCountPL = csp.satCountPL()

        val smooth = csp.toDnnfSmooth()

        val satCount = smooth.satCount
        val cubes = smooth.cubesSmooth

        assertEquals(satCountExpected, satCount)
        assertEquals(satCountExpected, satCountPL)
        assertEquals(satCountExpected, cubes.size.toLong())
        assertEquals(sCubesExpected.toCubes(csp.space), cubes)


    }

    /*


    start: fcc:           delta Delta: 16444
    start: bestXorSplit:  delta Delta: 6549
    start: yearXorSplit:  delta Delta: 11698
    start: mdlXorSplit:   delta Delta: 7894
    start: serXorSplit:   delta Delta: 8541
    start: xcolXorSplit:  delta Delta: huge

    start: y-s:    delta Delta: huge 6435
    start: s-y:    delta Delta: huge 6425
    start: m-y:    delta Delta: huge 5111
    start: m-s:    delta Delta: huge 6694
    start: m-s-y:  delta Delta: huge 6034
    start: y-s-m:  delta Delta: huge 4605
    start: y-m-s:  delta Delta: huge 5325

    start: y-s-m:  3894 ms  3460501125462739908
                            3460501125462739789

        load rules: 54
        parse rules: 922
        pl simplify: 133
        dnnf compile: 4460
        dnnf sat count: 15

        load rules: 50
        parse rules: 803
        pl simplify: 146
        dnnf compile: 3959
        dnnf sat count: 13

     */

    @Test
    fun testEfcSimplifyAtVarsYes() {

        val t0 = System.currentTimeMillis()
        val clob = CspSample.EfcOriginal.loadText()
        val t1 = System.currentTimeMillis()
        println("load rules: " + (t1 - t0))


        val csp = Csp.parse(clob)
        val t2 = System.currentTimeMillis()
        println("parse rules: " + (t2 - t1))


        csp.simplifyAlwaysTrueVars()
        val t3 = System.currentTimeMillis()
        println("simplifyAlwaysTrueVars: " + (t3 - t2))


        val rough = csp.toDnnf()
        val t4 = System.currentTimeMillis()
        println("dnnf rough: " + (t4 - t3))

        val dNode = rough.smooth
        val t5 = System.currentTimeMillis()
        println("dnnf smooth: " + (t5 - t4))


        val count = dNode.satCount
        val t6 = System.currentTimeMillis()
        System.err.println("dnnf sat count: " + (t6 - t5))

        assertEquals(3460501125462739789L, count)
    }

    /*
    load rules: 52
    parse rules: 820
    dnnf compile: 3770
    dnnf sat count: 22

    load rules: 52
    parse rules: 773
    dnnf compile: 3841
    dnnf sat count: 18
     */

    @Test
    fun testEfcSimplifyAtVarsNo() {

        val t0 = System.currentTimeMillis()
        val clob = CspSample.EfcOriginal.loadText()
        val t1 = System.currentTimeMillis()

        val csp = Csp.parse(clob)  //parse rules: 1446, 1410,1600

        val t2 = System.currentTimeMillis()

        val rough = csp.toDnnf()   //11014
        val t3 = System.currentTimeMillis()
        val dNode = rough.smooth
        val t4 = System.currentTimeMillis()

        val count = dNode.satCount
        val t5 = System.currentTimeMillis()


        System.err.println("load rules: " + (t1 - t0))
        System.err.println("parse rules: " + (t2 - t1))
        System.err.println("dnnf rough: " + (t3 - t2))
        System.err.println("dnnf smooth: " + (t4 - t3))
        System.err.println("dnnf sat count: " + (t5 - t4))

        assertEquals(3460501125462739908L, count)


    }


    @Test
    fun testReduceAndBb() {

        val expected = object {
            val bb0 = ""
            val bb1 = "!MDL_4431 !MDL_4433 !MDL_4441 !MDL_4443 !MDL_4451 !MDL_4453 !MDL_5336 !MDL_5346 Root !YR_2011 exteriorcolor interiorcolor model series"
            val bb2 = "!MDL_4431 !MDL_4433 !MDL_4441 !MDL_4443 !MDL_4451 !MDL_4453 !MDL_5336 !MDL_5346 !YR_2011"
            val bb3 = "!ACY_EN !ICOL_FB01 !ICOL_FB20 !ICOL_FB41 !ICOL_FB44 !ICOL_FC44 !ICOL_FD22 !ICOL_FD42 !ICOL_FD80 !ICOL_LA44 !ICOL_LB44 !MDL_4431 !MDL_4433 !MDL_4441 !MDL_4443 !MDL_4451 !MDL_4453 !MDL_5336 !MDL_5346 !XCOL_01F9 !XCOL_04V8 !XCOL_06W3 !XCOL_08W7 !XCOL_09AF !YR_2011 !YR_2014"
            val bb4 = "!ACY_1M !ACY_2U !ACY_30 !ACY_31 !ACY_33 !ACY_34 !ACY_3S !ACY_3T !ACY_3V !ACY_45 !ACY_4P !ACY_4Q !ACY_5A !ACY_5F !ACY_5K !ACY_5L !ACY_5Y !ACY_61 !ACY_62 !ACY_67 !ACY_69 !ACY_6B !ACY_6D !ACY_6H !ACY_6I !ACY_6J !ACY_6K !ACY_6L !ACY_6N !ACY_95 !ACY_9D !ACY_9E !ACY_9G !ACY_9H !ACY_9S !ACY_9T !ACY_9V !ACY_A2 !ACY_A6 !ACY_AD !ACY_AT !ACY_AY !ACY_B1 !ACY_B7 !ACY_BD !ACY_BM !ACY_BT !ACY_BU !ACY_C1 !ACY_C3 !ACY_C6 !ACY_C7 !ACY_CA !ACY_CD !ACY_CK !ACY_CL !ACY_CQ !ACY_CR !ACY_CV !ACY_CW !ACY_CX !ACY_D1 !ACY_D5 !ACY_D8 !ACY_DB !ACY_DD !ACY_DL !ACY_DR !ACY_E1 !ACY_E4 !ACY_EE !ACY_EF !ACY_EG !ACY_EJ !ACY_EK !ACY_EL !ACY_EN !ACY_EX !ACY_FL !ACY_G0 !ACY_G1 !ACY_G2 !ACY_G4 !ACY_GE !ACY_H3 !ACY_H4 !ACY_H9 !ACY_IZ !ACY_K7 !ACY_KC !ACY_KD !ACY_L3 !ACY_L5 !ACY_L7 !ACY_LA !ACY_LE !ACY_LM !ACY_LP !ACY_LT !ACY_M2 !ACY_M4 !ACY_MC !ACY_MF !ACY_MR !ACY_MU !ACY_N1 !ACY_N2 !ACY_N5 !ACY_NW !ACY_NY !ACY_OC !ACY_P3 !ACY_PC !ACY_PD !ACY_PE !ACY_PJ !ACY_PM !ACY_PQ !ACY_PV !ACY_PY !ACY_Q1 !ACY_Q7 !ACY_Q8 !ACY_QA !ACY_QN !ACY_QR !ACY_R4 !ACY_R5 !ACY_R6 !ACY_R7 !ACY_RB !ACY_RF !ACY_RR !ACY_S1 !ACY_S2 !ACY_S5 !ACY_S8 !ACY_SE !ACY_SS !ACY_SW !ACY_SX !ACY_SY !ACY_T1 !ACY_T2 !ACY_TC !ACY_TE !ACY_TG !ACY_TH !ACY_TI !ACY_TP !ACY_TR !ACY_TS !ACY_TT !ACY_TW !ACY_UI !ACY_UR !ACY_WB !ACY_WX !ACY_WZ !ACY_XL !ACY_Z2 !ACY_Z3 !ACY_Z4 !ACY_Z5 !CAB_AccessCab !DRV_4WDi !DRV_4x2PreRunner !DRV_4x2XRunner !DRV_4x4MT !DRV_AWD !DRV_FWD !GRD_4X2AT !GRD_4X4AT !GRD_4X4MT !GRD_Highlander !GRD_HighlanderPlus !GRD_Hybrid !GRD_HybridLE !GRD_HybridLimited !GRD_HybridXLE !GRD_L !GRD_LE !GRD_LE7Passenger !GRD_LandCruiser !GRD_Limited7Passenger !GRD_PriusTwo !GRD_PriusVFive !GRD_PriusVThree !GRD_PriusVTwo !GRD_RAV4 !GRD_S !GRD_SE !GRD_SE8PassengerV6 !GRD_SR5 !GRD_SiennaL7Passenger !GRD_Sport !GRD_Trail !GRD_XLE !GRD_XLE7Passenger !GRD_XLE8Passenger !ICOL_EA10 !ICOL_EA20 !ICOL_EA30 !ICOL_EA40 !ICOL_FA00 !ICOL_FA01 !ICOL_FA10 !ICOL_FA11 !ICOL_FA12 !ICOL_FA13 !ICOL_FA14 !ICOL_FA16 !ICOL_FA18 !ICOL_FA28 !ICOL_FA40 !ICOL_FA42 !ICOL_FA47 !ICOL_FA66 !ICOL_FB00 !ICOL_FB01 !ICOL_FB10 !ICOL_FB13 !ICOL_FB16 !ICOL_FB18 !ICOL_FB20 !ICOL_FB40 !ICOL_FB41 !ICOL_FB44 !ICOL_FB60 !ICOL_FC00 !ICOL_FC11 !ICOL_FC13 !ICOL_FC14 !ICOL_FC42 !ICOL_FC43 !ICOL_FC44 !ICOL_FD00 !ICOL_FD10 !ICOL_FD11 !ICOL_FD13 !ICOL_FD17 !ICOL_FD19 !ICOL_FD22 !ICOL_FD42 !ICOL_FD43 !ICOL_FD80 !ICOL_FE00 !ICOL_FE11 !ICOL_FE13 !ICOL_FE15 !ICOL_FE28 !ICOL_FF15 !ICOL_FF28 !ICOL_FG10 !ICOL_FG13 !ICOL_FG44 !ICOL_FJ42 !ICOL_FK13 !ICOL_FK42 !ICOL_FQ43 !ICOL_FR43 !ICOL_LA00 !ICOL_LA01 !ICOL_LA10 !ICOL_LA12 !ICOL_LA14 !ICOL_LA18 !ICOL_LA20 !ICOL_LA28 !ICOL_LA40 !ICOL_LA42 !ICOL_LA44 !ICOL_LA47 !ICOL_LA66 !ICOL_LB00 !ICOL_LB10 !ICOL_LB11 !ICOL_LB14 !ICOL_LB20 !ICOL_LB21 !ICOL_LB22 !ICOL_LB23 !ICOL_LB28 !ICOL_LB40 !ICOL_LB41 !ICOL_LB42 !ICOL_LB44 !ICOL_LB60 !ICOL_LC03 !ICOL_LC15 !ICOL_LC20 !ICOL_LC23 !ICOL_LC25 !ICOL_LD03 !ICOL_LD17 !ICOL_LD20 !ICOL_LE03 !ICOL_LE17 !ICOL_LE20 !ICOL_LF03 !ICOL_LF17 !ICOL_LF20 !ICOL_LF22 !ICOL_LF42 !ICOL_LG44 !ICOL_LH21 !ICOL_LH44 !ICOL_LM03 !ICOL_LM17 !ICOL_LM20 !ICOL_LT03 !ICOL_LT17 !ICOL_LT20 !ICOL_YJ13 !MDL_1201 !MDL_1203 !MDL_1205 !MDL_1207 !MDL_1223 !MDL_1225 !MDL_1227 !MDL_1228 !MDL_1229 !MDL_1235 !MDL_1239 !MDL_1243 !MDL_1245 !MDL_1249 !MDL_1421 !MDL_1422 !MDL_1424 !MDL_1462 !MDL_1463 !MDL_1464 !MDL_1466 !MDL_1831 !MDL_1832 !MDL_1833 !MDL_1834 !MDL_1838 !MDL_1844 !MDL_1848 !MDL_1931 !MDL_1932 !MDL_1933 !MDL_1934 !MDL_1938 !MDL_2514 !MDL_2532 !MDL_2540 !MDL_2546 !MDL_2550 !MDL_2554 !MDL_2559 !MDL_2560 !MDL_2810 !MDL_2812 !MDL_2820 !MDL_2822 !MDL_2830 !MDL_2832 !MDL_2834 !MDL_2836 !MDL_2842 !MDL_2846 !MDL_3506 !MDL_3508 !MDL_3514 !MDL_3544 !MDL_3546 !MDL_3548 !MDL_3554 !MDL_4430 !MDL_4431 !MDL_4432 !MDL_4433 !MDL_4440 !MDL_4441 !MDL_4442 !MDL_4443 !MDL_4450 !MDL_4451 !MDL_4452 !MDL_4453 !MDL_4480 !MDL_4702 !MDL_4703 !MDL_4704 !MDL_5302 !MDL_5328 !MDL_5335 !MDL_5336 !MDL_5338 !MDL_5342 !MDL_5345 !MDL_5346 !MDL_5348 !MDL_5356 !MDL_5366 !MDL_5376 !MDL_5386 !MDL_6156 !MDL_6942 !MDL_6944 !MDL_6946 !MDL_6947 !MDL_6948 !MDL_6949 !MDL_6951 !MDL_6953 !MDL_6954 !MDL_6956 !MDL_6964 !MDL_6966 !MDL_7103 !MDL_7104 !MDL_7113 !MDL_7114 !MDL_7153 !MDL_7162 !MDL_7164 !MDL_7182 !MDL_7186 !MDL_7188 !MDL_7190 !MDL_7503 !MDL_7504 !MDL_7513 !MDL_7514 !MDL_7553 !MDL_7554 !MDL_7593 !MDL_7594 !MDL_7596 !MDL_7917 !MDL_7919 !MDL_7927 !MDL_7929 !MDL_7931 !MDL_7933 !MDL_7940 !MDL_7942 !MDL_7944 !MDL_8642 !MDL_8648 !MDL_8664 !MDL_8668 !MDL_8670 !SER_4runner !SER_avalon !SER_camry !SER_corolla !SER_fjcruiser !SER_highlander !SER_landcruiser !SER_matrix !SER_prius !SER_priusc !SER_priusplugin !SER_priusv !SER_rav4 !SER_rav4ev !SER_sequoia !SER_sienna !SER_tacoma !SER_venza !SER_yaris !TRAN_4AT !TRAN_5MT !TRAN_6AT4Cyl !TRAN_6ATV6 !TRAN_6MT !TRAN_ECVT !XCOL_0058 !XCOL_0070 !XCOL_0082 !XCOL_01E7 !XCOL_01F7 !XCOL_01F9 !XCOL_01H1 !XCOL_01H2 !XCOL_01H5 !XCOL_0209 !XCOL_0218 !XCOL_02KB !XCOL_02KC !XCOL_02KD !XCOL_02KP !XCOL_02KQ !XCOL_02LF !XCOL_03P0 !XCOL_03P2 !XCOL_03Q3 !XCOL_03R0 !XCOL_03T0 !XCOL_04R3 !XCOL_04R8 !XCOL_04T8 !XCOL_04U2 !XCOL_04U3 !XCOL_04V7 !XCOL_04V8 !XCOL_05B2 !XCOL_06T7 !XCOL_06V2 !XCOL_06W3 !XCOL_0774 !XCOL_0781 !XCOL_0785 !XCOL_0787 !XCOL_0788 !XCOL_08S7 !XCOL_08T0 !XCOL_08T5 !XCOL_08T7 !XCOL_08U6 !XCOL_08V1 !XCOL_08V5 !XCOL_08W1 !XCOL_08W7 !XCOL_09AF !XCOL_09AH !YR_2011 !YR_2014 bed cab drive grade transmission"
        }


        val clob = CspSample.EfcOriginal.loadText()
        val csp = Csp.parse(clob)
        val d = csp.toDnnf()
        val bb0 = d.bb
        val bb0Expected = expected.bb0.toCube(csp)
        assertEquals(bb0Expected, bb0)

        val csp1 = csp.atRefine()
        val d1 = csp1.toDnnf()
        val bb1 = d1.bb
        val bb1Expected = expected.bb1.toCube(csp)
        assertEquals(bb1Expected, bb1)


        val csp2: Csp = csp1.reduce();
        val d2 = csp2.toDnnf()
        val bb2 = d2.bb
        val bb2Expected = expected.bb2.toCube(csp2)
        assertEquals(bb2Expected, bb2)

        val d3 = d2.condition("YR_2013")
        val bb3 = d3.bb
        assertEquals(expected.bb3.toCube(csp2), bb3)

        val d4 = d3.condition("SER_tundra")
        val bb4 = d4.bb
        assertEquals(expected.bb4.toCube(csp2), bb4)


    }

    @Test
    fun testSatCountEqAllSat1() {

        val expectedSatCount = 6L

        val clob = """
            imp(series xor(SER_rav4ev SER_avalon))
        """.trimIndent()


        val csp: Csp = Csp.parse(clob)
        val satCountPL = csp.satCountPL()
        val smooth = csp.toDnnfSmooth()
        val cubes = smooth.cubesSmooth

        assertEquals(expectedSatCount, satCountPL)
        assertEquals(expectedSatCount, smooth.satCount)

        assertEquals(expectedSatCount, cubes.size.toLong())


    }

    @Test
    fun testSatCountEqAllSat() {

        val expectedSatCount = 524307L

        val clob = """
            imp(series xor(SER_rav4ev SER_avalon SER_highlander SER_rav4 SER_tundra SER_landcruiser SER_venza SER_corolla SER_priusv SER_yaris SER_sequoia SER_camry SER_tacoma SER_priusplugin SER_prius SER_fjcruiser SER_priusc SER_4runner SER_sienna))
        """.trimIndent()

        val csp: Csp = Csp.parse(clob)

        val satCountPL = csp.satCountPL()

        val smooth = csp.toDnnfSmooth()

        val smoothCubesSmooth = smooth.cubesSmooth

        assertEquals(expectedSatCount, smooth.satCount)
        assertEquals(expectedSatCount, smoothCubesSmooth.size.toLong())
        assertEquals(expectedSatCount, satCountPL)


    }
}