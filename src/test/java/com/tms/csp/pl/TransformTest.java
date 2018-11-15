package com.tms.csp.pl;

import com.tms.csp.Vars;
import com.tms.csp.ast.Parser;
import com.tms.csp.data.CspSample;
import com.tms.csp.data.TestData;
import com.tms.csp.ast.Exp;
import com.tms.csp.ast.Space;
import com.tms.csp.transforms.AndsToBinary;
import com.tms.csp.transforms.ConflictsToCnf;
import com.tms.csp.transforms.FlattenOrs;
import com.tms.csp.transforms.FlattenTopLevelAnds;
import com.tms.csp.transforms.IffToCnf;
import com.tms.csp.transforms.ImpsToCnf;
import com.tms.csp.transforms.OrToAnd;
import com.tms.csp.transforms.PushAndsOut;
import com.tms.csp.transforms.PushNotsIn;
import com.tms.csp.transforms.Transformer;
import com.tms.csp.transforms.Transforms;
import com.tms.csp.transforms.XorsToCnf;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TransformTest extends Transforms {

    Parser f;

    //
    @Before
    public void setUp() throws Exception {
        Iterable<String> vars = Vars.parseVarList("a b c d e fCon g x y z ee rr s 6MT 6AT ECVT SE LE L4 6AT 2546 QA AW LA Ash 4T8 776 Bisque 1F7");
        Space space = new Space(vars);
        f = space.parser;
    }

    @Test
    public void testOrToAnd() throws Exception {
        OrToAnd tt1 = new OrToAnd();
        testTransform(tt1, "or(a b)", "!and(!a !b)");


        OrToAnd tt2 = new OrToAnd();
        testTransform(tt2, "or(a b c)", "!and(!a !b !c)");
    }

    @Test
    public void testAndToBinary() throws Exception {
        Exp e1, e2;

        e1 = f.parseExp("and(a b c)");
        System.err.println("e1[" + e1 + "]");

        e2 = andToBinary(e1);
        System.err.println("e2[" + e2 + "]");


        e1 = flattenAnd(e2);
        System.err.println("e1[" + e1 + "]");

        System.err.println();

        e1 = f.parseExp("and(a b c d e fCon g)");
        System.err.println("e1[" + e1 + "]");

        e2 = andToBinary(e1);
        System.err.println("e2[" + e2 + "]");


        e1 = flattenAnd(e2);
        System.err.println("e1[" + e1 + "]");


//        testTransform(t1, e1, "and(and(a b) c)");
    }


    @Test
    public void testXorToCnf1() throws Exception {
        Transformer t = new XorsToCnf();
        String in = "xor(a b)";
        String expected = "and(or(a b) or(!a !b))";
        testTransform(t, in, expected);
    }

    @Test
    public void testXorToCnf() throws Exception {
        Transformer t = new XorsToCnf();
        String in = "xor(6MT 6AT ECVT)";
        String expected = "and(or(6MT 6AT ECVT) or(!6MT !6AT) or(!6MT !ECVT) or(!6AT !ECVT))";
        testTransform(t, in, expected);
    }

    @Test
    public void testRemoveConflicts1() throws Exception {
        Transformer t = new ConflictsToCnf();
        String in = "nand(6MT 6AT)";
        String expected = "or(!6MT !6AT)";
        testTransform(t, in, expected);
    }

    @Test
    public void testFlattenImps() throws Exception {
//        Transformer tCon = new FlattenImps();
//        String formula = "imp(x and(a b c))";
//        String expected = "and(imp(x a) imp(x b) imp(x c))";
//        testTransform(tCon, formula, expected);
    }

    @Test
    public void testFlattenOrs() throws Exception {
        Transformer t = new FlattenOrs();
        String in = "or(x y or(a b c) ee rr or(s d))";
        String expected = "or(x y a b c ee rr s d)";
        testTransform(t, in, expected);
    }

    @Test
    public void testFlattenAnds() throws Exception {
        Transformer t = new FlattenTopLevelAnds();
        String in = "and(x y and(a b c) ee rr and(s d))";
        String expected = "and(x y a b c ee rr s d)";
        testTransform(t, in, expected);
    }

    //a or (b and c and d) = (a or b) and (a or c) and (a or d)
    @Test
    public void testPushAndsOut() throws Exception {
        Transformer t = new PushAndsOut();
        String in = "or(a and(b c d))";
        String expected = "and(or(a b) or(a c) or(a d))";
        testTransform(t, in, expected);
    }


    @Test
    public void testAndsToBinary() throws Exception {
        Transformer t = new AndsToBinary();
        String in = "and(a b c d)";
        String expected = "and(a and(b and(c d)))";
        testTransform(t, in, expected);
    }

    @Test
    public void testPushNotsIn1() throws Exception {
        Transformer t = new PushNotsIn();
        String in = "!or(a b)";
        String expected = "and(!a !b)";
        testTransform(t, in, expected);
    }

    @Test
    public void testPushNotsIn2() throws Exception {
        Transformer t = new PushNotsIn();
        String in = "!and(a b)";
        String expected = "or(!a !b)";
        testTransform(t, in, expected);
    }

    @Test
    public void testPushNotsIn3() throws Exception {
        Transformer t = new PushNotsIn();
        String in = "or(!and(SE L4 6AT) 2546)";
        String expected = "or(or(!SE !L4 !6AT) 2546)";
        testTransform(t, in, expected);
    }

    @Test
    public void testPushNotsIn4() throws Exception {
        Transformer t = new PushNotsIn();
        String in = "and(or(!2546 SE) or(!2546 L4) or(!2546 6AT) or(!and(SE L4 6AT) 2546))";
        String expected = "and(or(!2546 SE) or(!2546 L4) or(!2546 6AT) or(or(!SE !L4 !6AT) 2546))";
        testTransform(t, in, expected);
    }


    //iff(a b) with an and(imp(a b) imp(b a))
    @Test
    public void testIffToCnf() throws Exception {
        Transformer t = new IffToCnf();
        String in = "iff(a b)";
        String expected = "and(or(!a b) or(!b a))";
        testTransform(t, in, expected);
    }

    @Test
    public void testIffToCnf2() throws Exception {
        Transformer t = new IffToCnf();
        String in = "iff(x and(a b c))";
        String expected = "and(or(!x and(a b c)) or(!and(a b c) x))";
        testTransform(t, in, expected);
    }

    //"nand(LA or(and(Ash 4T8) and(Ash 776) and(Bisque 1F7)))"


    @Test
    public void testIffToCnf3() throws Exception {
        Transformer t = new IffToCnf();
        String in = "iff(QA and(AW LE))";
        String expected = "and(or(!QA and(AW LE)) or(!and(AW LE) QA))";
        testTransform(t, in, expected);
    }

    //formula:   imp(a b)
    //out:  or(!a b)
    @Test
    public void testRemoveImps() throws Exception {
        Transformer t = new ImpsToCnf();
        String in = "imp(a b)";
        String expected = "or(!a b)";
        testTransform(t, in, expected);
    }


    @Test
    public void testMany() throws Exception {
        Exp e;
        e = f.parseExp("xor(6MT 6AT ECVT)");
        System.err.println(e);

        e = xorToCnf(e);
        System.err.println(e);

        e = removeConflicts(e);
        System.err.println(e);

        e = removeIffs(e);
        System.err.println(e);

        e = removeImps(e);
        System.err.println(e);

        e = pushUpAnds(e);
        System.err.println(e);
    }

    @Test
    public void testMany2() throws Exception {
        Exp e;
        e = f.parseExp("nand(LA or(and(Ash 4T8) and(Ash 776) and(Bisque 1F7)))");
        System.err.println(e);

        e = removeConflicts(e);
        System.err.println(e);

        e = pushNotsIn(e);
        System.err.println(e);

        e = pushUpAnds(e);
        System.err.println(e);

        e = flattenOrs(e);
        System.err.println(e);
    }


    @Test
    public void testBaseNormalForm() throws Exception {
        Exp e;
        Transformer t;

        e = f.parseExp("nand(LA or(and(Ash 4T8) and(Ash 776) and(Bisque 1F7)))");
        System.err.println(e.toString());


        t = Transforms.bnf();
        e = t.transform(e);
        System.err.println(e);

        t = Transforms.pushNotsIn();
        e = t.transform(e);
        System.err.println(e);


    }

    @Test
    public void testNegationNormalForm() throws Exception {
        Exp e;
        Transformer t;

        e = f.parseExp("nand(LA or(and(Ash 4T8) and(Ash 776) and(Bisque 1F7)))");
        System.err.println(e);


        t = Transforms.nnf();
        e = t.transform(e);
        System.err.println(e);

    }

    @Test
    public void testNegationNormalSuperLarge() throws Exception {
        Space space = TestData.loadSpaceNnf(CspSample.EfcOriginal);
//        space.fact.printSummary();
    }

    public void testTransform(Transformer t, String inExp, String expectedOutExp) throws Exception {
        Exp before = f.parseExp(inExp);
        Exp expected = f.parseExp(expectedOutExp);
        System.err.println(expected.toString());
        Exp after = before.transform(t);
//        assertEquals(expected, after);
        assertEquals(expectedOutExp, after.toString());
    }


}
