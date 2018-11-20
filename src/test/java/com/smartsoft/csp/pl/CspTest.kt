package com.smartsoft.csp.pl

import com.smartsoft.csp.ast.Csp
import com.smartsoft.csp.ast.toCube
import com.smartsoft.csp.ast.toCubes
import com.smartsoft.csp.data.CspSample
import com.smartsoft.csp.dnnf.products.Cube
import com.smartsoft.csp.util.CspBaseTest2
import java.math.BigInteger
import kotlin.test.Test
import kotlin.test.assertEquals

class CspTest : CspBaseTest2() {



    @Test
    fun testTiny() {

        val tiny = """
            vars(a b c)
            xor(a b)
            conflict(b c)
        """


        val satCountExpected: BigInteger = 3.toBigInteger()

        val sCubesExpected = """
            a !b c
            a !b !c
            !a b !c
        """.trim()

        val csp = Csp.parse(tiny)

        val plSatCount = csp.satCountPL()

        assertEquals(satCountExpected, plSatCount.toBigInteger())


        val rough = csp.toDnnf()


        val smooth = rough.smooth
        val smotSatCount = smooth.satCount

        assertEquals(satCountExpected, smotSatCount)


        val cubes: Set<Cube> = smooth.cubesSmooth

        val cubesCount = cubes.size

        assertEquals(satCountExpected, cubesCount.toBigInteger())

        assertEquals(sCubesExpected.toCubes(csp.space), cubes)
        assertEquals(satCountExpected, smooth.satCount)
        assertEquals(satCountExpected, cubes.size.toBigInteger())


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
        assertEquals(satCountExpected, d.satCount.toLong())
        assertEquals(satCountExpected, cubes.size.toLong())
        assertEquals(sCubesExpected.toCubes(csp), cubes)

        //Exp.printCubes(cubes);
    }


    @Test
    fun testTrim() {


        val satCountExpected: Long = 11

        val csp = Csp.parse(CspSample.TrimNoDc)

        val sCubesExpected = """
            2513   !2514   !2531   !2532   !2552   !2540   !2554   !2545   !2546   !2550   !2560      L4     !V6 !Hybrid    Base     !LE     !SE    !XLE    !Hyb      6MT     !6AT    !ECVT
           !2513    2514   !2531   !2532   !2552   !2540   !2554   !2545   !2546   !2550   !2560      L4     !V6 !Hybrid    Base     !LE     !SE    !XLE    !Hyb     !6MT      6AT    !ECVT
           !2513   !2514    2531   !2532   !2552   !2540   !2554   !2545   !2546   !2550   !2560      L4     !V6 !Hybrid   !Base      LE     !SE    !XLE    !Hyb      6MT     !6AT    !ECVT
           !2513   !2514   !2531    2532   !2552   !2540   !2554   !2545   !2546   !2550   !2560      L4     !V6 !Hybrid   !Base      LE     !SE    !XLE    !Hyb     !6MT      6AT    !ECVT
           !2513   !2514   !2531   !2532    2552   !2540   !2554   !2545   !2546   !2550   !2560     !L4      V6 !Hybrid   !Base      LE     !SE    !XLE    !Hyb     !6MT      6AT    !ECVT
           !2513   !2514   !2531   !2532   !2552    2540   !2554   !2545   !2546   !2550   !2560      L4     !V6 !Hybrid   !Base     !LE     !SE     XLE    !Hyb     !6MT      6AT    !ECVT
           !2513   !2514   !2531   !2532   !2552   !2540    2554   !2545   !2546   !2550   !2560     !L4      V6 !Hybrid   !Base     !LE     !SE     XLE    !Hyb     !6MT      6AT    !ECVT
           !2513   !2514   !2531   !2532   !2552   !2540   !2554    2545   !2546   !2550   !2560      L4     !V6 !Hybrid   !Base     !LE      SE    !XLE    !Hyb      6MT     !6AT    !ECVT
           !2513   !2514   !2531   !2532   !2552   !2540   !2554   !2545    2546   !2550   !2560      L4     !V6 !Hybrid   !Base     !LE      SE    !XLE    !Hyb     !6MT      6AT    !ECVT
           !2513   !2514   !2531   !2532   !2552   !2540   !2554   !2545   !2546    2550   !2560     !L4      V6 !Hybrid   !Base     !LE      SE    !XLE    !Hyb     !6MT      6AT    !ECVT
           !2513   !2514   !2531   !2532   !2552   !2540   !2554   !2545   !2546   !2550    2560     !L4     !V6  Hybrid   !Base     !LE     !SE    !XLE     Hyb     !6MT     !6AT     ECVT
        """.trim()




        val satCountPL = csp.satCountPL()

        val smooth = csp.toDnnfSmooth()

        val satCount = smooth.satCount
        val cubes = smooth.cubesSmooth

        assertEquals(satCountExpected, satCount.toLong())
        assertEquals(satCountExpected, satCountPL)
        assertEquals(satCountExpected, cubes.size.toLong())
        assertEquals(sCubesExpected.toCubes(csp.space), cubes)


    }



    @Test
    fun testReduceAndBb() {

        val expected = object {
            val bb0 = ""
            val bb1 = "!MDL_4431 !MDL_4433 !MDL_4441 !MDL_4443 !MDL_4451 !MDL_4453 !MDL_5336 !MDL_5346 !YR_2011 exteriorcolor interiorcolor model series"
            val bb2 = "!4431 !4433 !4441 !4443 !4451 !4453 !5336 !5346 !YR_2011"
            val bb3 = "!ACY_EN !ICOL_FB01 !ICOL_FB20 !ICOL_FB41 !ICOL_FB44 !ICOL_FC44 !ICOL_FD22 !ICOL_FD42 !ICOL_FD80 !ICOL_LA44 !ICOL_LB44 !MDL_4431 !MDL_4433 !MDL_4441 !MDL_4443 !MDL_4451 !MDL_4453 !MDL_5336 !MDL_5346 !XCOL_01F9 !XCOL_04V8 !XCOL_06W3 !XCOL_08W7 !XCOL_09AF !YR_2011 !YR_2014"
            val bb4 = "!ACY_1M !ACY_2U !ACY_30 !ACY_31 !ACY_33 !ACY_34 !ACY_3S !ACY_3T !ACY_3V !ACY_45 !ACY_4P !ACY_4Q !ACY_5A !ACY_5F !ACY_5K !ACY_5L !ACY_5Y !ACY_61 !ACY_62 !ACY_67 !ACY_69 !ACY_6B !ACY_6D !ACY_6H !ACY_6I !ACY_6J !ACY_6K !ACY_6L !ACY_6N !ACY_95 !ACY_9D !ACY_9E !ACY_9G !ACY_9H !ACY_9S !ACY_9T !ACY_9V !ACY_A2 !ACY_A6 !ACY_AD !ACY_AT !ACY_AY !ACY_B1 !ACY_B7 !ACY_BD !ACY_BM !ACY_BT !ACY_BU !ACY_C1 !ACY_C3 !ACY_C6 !ACY_C7 !ACY_CA !ACY_CD !ACY_CK !ACY_CL !ACY_CQ !ACY_CR !ACY_CV !ACY_CW !ACY_CX !ACY_D1 !ACY_D5 !ACY_D8 !ACY_DB !ACY_DD !ACY_DL !ACY_DR !ACY_E1 !ACY_E4 !ACY_EE !ACY_EF !ACY_EG !ACY_EJ !ACY_EK !ACY_EL !ACY_EN !ACY_EX !ACY_FL !ACY_G0 !ACY_G1 !ACY_G2 !ACY_G4 !ACY_GE !ACY_H3 !ACY_H4 !ACY_H9 !ACY_IZ !ACY_K7 !ACY_KC !ACY_KD !ACY_L3 !ACY_L5 !ACY_L7 !ACY_LA !ACY_LE !ACY_LM !ACY_LP !ACY_LT !ACY_M2 !ACY_M4 !ACY_MC !ACY_MF !ACY_MR !ACY_MU !ACY_N1 !ACY_N2 !ACY_N5 !ACY_NW !ACY_NY !ACY_OC !ACY_P3 !ACY_PC !ACY_PD !ACY_PE !ACY_PJ !ACY_PM !ACY_PQ !ACY_PV !ACY_PY !ACY_Q1 !ACY_Q7 !ACY_Q8 !ACY_QA !ACY_QN !ACY_QR !ACY_R4 !ACY_R5 !ACY_R6 !ACY_R7 !ACY_RB !ACY_RF !ACY_RR !ACY_S1 !ACY_S2 !ACY_S5 !ACY_S8 !ACY_SE !ACY_SS !ACY_SW !ACY_SX !ACY_SY !ACY_T1 !ACY_T2 !ACY_TC !ACY_TE !ACY_TG !ACY_TH !ACY_TI !ACY_TP !ACY_TR !ACY_TS !ACY_TT !ACY_TW !ACY_UI !ACY_UR !ACY_WB !ACY_WX !ACY_WZ !ACY_XL !ACY_Z2 !ACY_Z3 !ACY_Z4 !ACY_Z5 !CAB_AccessCab !DRV_4WDi !DRV_4x2PreRunner !DRV_4x2XRunner !DRV_4x4MT !DRV_AWD !DRV_FWD !4X2AT !4X4AT !4X4MT !Highlander !HighlanderPlus !Hybrid !HybridLE !HybridLimited !HybridXLE !L !LE !LE7Passenger !LandCruiser !Limited7Passenger !PriusTwo !PriusVFive !PriusVThree !PriusVTwo !RAV4 !S !SE !SE8PassengerV6 !SR5 !SiennaL7Passenger !Sport !Trail !XLE !XLE7Passenger !XLE8Passenger !ICOL_EA10 !ICOL_EA20 !ICOL_EA30 !ICOL_EA40 !ICOL_FA00 !ICOL_FA01 !ICOL_FA10 !ICOL_FA11 !ICOL_FA12 !ICOL_FA13 !ICOL_FA14 !ICOL_FA16 !ICOL_FA18 !ICOL_FA28 !ICOL_FA40 !ICOL_FA42 !ICOL_FA47 !ICOL_FA66 !ICOL_FB00 !ICOL_FB01 !ICOL_FB10 !ICOL_FB13 !ICOL_FB16 !ICOL_FB18 !ICOL_FB20 !ICOL_FB40 !ICOL_FB41 !ICOL_FB44 !ICOL_FB60 !ICOL_FC00 !ICOL_FC11 !ICOL_FC13 !ICOL_FC14 !ICOL_FC42 !ICOL_FC43 !ICOL_FC44 !ICOL_FD00 !ICOL_FD10 !ICOL_FD11 !ICOL_FD13 !ICOL_FD17 !ICOL_FD19 !ICOL_FD22 !ICOL_FD42 !ICOL_FD43 !ICOL_FD80 !ICOL_FE00 !ICOL_FE11 !ICOL_FE13 !ICOL_FE15 !ICOL_FE28 !ICOL_FF15 !ICOL_FF28 !ICOL_FG10 !ICOL_FG13 !ICOL_FG44 !ICOL_FJ42 !ICOL_FK13 !ICOL_FK42 !ICOL_FQ43 !ICOL_FR43 !ICOL_LA00 !ICOL_LA01 !ICOL_LA10 !ICOL_LA12 !ICOL_LA14 !ICOL_LA18 !ICOL_LA20 !ICOL_LA28 !ICOL_LA40 !ICOL_LA42 !ICOL_LA44 !ICOL_LA47 !ICOL_LA66 !ICOL_LB00 !ICOL_LB10 !ICOL_LB11 !ICOL_LB14 !ICOL_LB20 !ICOL_LB21 !ICOL_LB22 !ICOL_LB23 !ICOL_LB28 !ICOL_LB40 !ICOL_LB41 !ICOL_LB42 !ICOL_LB44 !ICOL_LB60 !ICOL_LC03 !ICOL_LC15 !ICOL_LC20 !ICOL_LC23 !ICOL_LC25 !ICOL_LD03 !ICOL_LD17 !ICOL_LD20 !ICOL_LE03 !ICOL_LE17 !ICOL_LE20 !ICOL_LF03 !ICOL_LF17 !ICOL_LF20 !ICOL_LF22 !ICOL_LF42 !ICOL_LG44 !ICOL_LH21 !ICOL_LH44 !ICOL_LM03 !ICOL_LM17 !ICOL_LM20 !ICOL_LT03 !ICOL_LT17 !ICOL_LT20 !ICOL_YJ13 !1201 !1203 !1205 !1207 !1223 !1225 !1227 !1228 !1229 !1235 !1239 !1243 !1245 !1249 !1421 !1422 !1424 !1462 !1463 !1464 !1466 !1831 !1832 !1833 !1834 !1838 !1844 !1848 !1931 !1932 !1933 !1934 !1938 !2514 !2532 !2540 !2546 !2550 !2554 !2559 !2560 !2810 !2812 !2820 !2822 !2830 !2832 !2834 !2836 !2842 !2846 !3506 !3508 !3514 !3544 !3546 !3548 !3554 !4430 !4431 !4432 !4433 !4440 !4441 !4442 !4443 !4450 !4451 !4452 !4453 !4480 !4702 !4703 !4704 !5302 !5328 !5335 !5336 !5338 !5342 !5345 !5346 !5348 !5356 !5366 !5376 !5386 !6156 !6942 !6944 !6946 !6947 !6948 !6949 !6951 !6953 !6954 !6956 !6964 !6966 !7103 !7104 !7113 !7114 !7153 !7162 !MDL_7164 !MDL_7182 !MDL_7186 !MDL_7188 !MDL_7190 !MDL_7503 !MDL_7504 !MDL_7513 !MDL_7514 !MDL_7553 !MDL_7554 !MDL_7593 !MDL_7594 !MDL_7596 !MDL_7917 !MDL_7919 !MDL_7927 !MDL_7929 !MDL_7931 !MDL_7933 !MDL_7940 !MDL_7942 !MDL_7944 !MDL_8642 !MDL_8648 !MDL_8664 !MDL_8668 !MDL_8670 !SER_4runner !SER_avalon !SER_camry !SER_corolla !SER_fjcruiser !SER_highlander !SER_landcruiser !SER_matrix !SER_prius !SER_priusc !SER_priusplugin !SER_priusv !SER_rav4 !SER_rav4ev !SER_sequoia !SER_sienna !SER_tacoma !SER_venza !SER_yaris !TRAN_4AT !TRAN_5MT !TRAN_6AT4Cyl !TRAN_6ATV6 !TRAN_6MT !TRAN_ECVT !XCOL_0058 !XCOL_0070 !XCOL_0082 !XCOL_01E7 !XCOL_01F7 !XCOL_01F9 !XCOL_01H1 !XCOL_01H2 !XCOL_01H5 !XCOL_0209 !XCOL_0218 !XCOL_02KB !XCOL_02KC !XCOL_02KD !XCOL_02KP !XCOL_02KQ !XCOL_02LF !XCOL_03P0 !XCOL_03P2 !XCOL_03Q3 !XCOL_03R0 !XCOL_03T0 !XCOL_04R3 !XCOL_04R8 !XCOL_04T8 !XCOL_04U2 !XCOL_04U3 !XCOL_04V7 !XCOL_04V8 !XCOL_05B2 !XCOL_06T7 !XCOL_06V2 !XCOL_06W3 !XCOL_0774 !XCOL_0781 !XCOL_0785 !XCOL_0787 !XCOL_0788 !XCOL_08S7 !XCOL_08T0 !XCOL_08T5 !XCOL_08T7 !XCOL_08U6 !XCOL_08V1 !XCOL_08V5 !XCOL_08W1 !XCOL_08W7 !XCOL_09AF !XCOL_09AH !YR_2011 !YR_2014 bed cab drive grade transmission"
        }


        val clob = CspSample.EfcOriginal.loadText()
        val csp = Csp.parse(clob)
        csp.simplifyAlwaysTrueVars()
        val d = csp.toDnnf()
        val bb = d.bb
        println("bb: $bb")

        if(true) return


//        val bb2Expected = expected.bb2.toCube(csp2)
//        assertEquals("${bb2.size}",bb2Expected, bb2)

        val d3 = d.condition("YR_2013")
        val bb3 = d3.bb
//        println("bb3: $bb3")

        assertEquals(expected.bb3.toCube(csp), bb3,"${bb3.size}")

        val d4 = d3.condition("SER_tundra")
        val bb4 = d4.bb
        println("bb4: $bb4")
        assertEquals(expected.bb4.toCube(csp), bb4,"${bb3.size}")


    }

    @Test
    fun testSatCountEqAllSat1() {

        val expectedSatCount = 6L

        val clob = """
            vars(series SER_rav4ev SER_avalon)
            imp(series xor(SER_rav4ev SER_avalon))
        """.trimIndent()


        val csp: Csp = Csp.parse(clob)
        val satCountPL = csp.satCountPL()
        val smooth = csp.toDnnfSmooth()
        val cubes = smooth.cubesSmooth

        assertEquals(expectedSatCount, satCountPL)
        assertEquals(expectedSatCount, smooth.satCount.toLong())

        assertEquals(expectedSatCount.toLong(), cubes.size.toLong())


    }

    @Test
    fun testSatCountEqAllSat() {

        val expectedSatCount = 524307L

        val clob = """
            vars(SER_rav4ev SER_avalon SER_highlander SER_rav4 SER_tundra SER_landcruiser SER_venza SER_corolla SER_priusv SER_yaris SER_sequoia SER_camry SER_tacoma SER_priusplugin SER_prius SER_fjcruiser SER_priusc SER_4runner SER_sienna series)
            imp(series xor(SER_rav4ev SER_avalon SER_highlander SER_rav4 SER_tundra SER_landcruiser SER_venza SER_corolla SER_priusv SER_yaris SER_sequoia SER_camry SER_tacoma SER_priusplugin SER_prius SER_fjcruiser SER_priusc SER_4runner SER_sienna))
        """.trimIndent()

        val csp: Csp = Csp.parse(clob)

        val satCountPL = csp.satCountPL()

        val smooth = csp.toDnnfSmooth()

        val smoothCubesSmooth = smooth.cubesSmooth

        assertEquals(expectedSatCount.toLong(), smooth.satCount.toLong())
        assertEquals(expectedSatCount.toLong(), smoothCubesSmooth.size.toLong())
        assertEquals(expectedSatCount.toLong(), satCountPL.toLong())


    }
}