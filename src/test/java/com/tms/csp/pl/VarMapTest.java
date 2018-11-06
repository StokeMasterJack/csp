package com.tms.csp.pl;

import com.tms.csp.ast.Exp;
import com.tms.csp.ast.Parser;
import com.tms.csp.ast.Space;
import org.junit.Test;

public class VarMapTest extends PLTestBase {

    String varMap = "vars(d a b c)";


    /*
    xor(a b)
    imp(b c)
     */


    @Test
    public void testParseStd() throws Exception {

        Space space = Space.withVars(varMap);

        Parser p = space.parser;
        Exp exp = p.parseExp("a");
//        p.parseExp("d(d)");
//        p.parseExp("xor(a b)");
//        p.parseExp("imp(b c)");


    }


}


