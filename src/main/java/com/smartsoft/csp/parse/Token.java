package com.smartsoft.csp.parse;

import com.smartsoft.csp.ast.Exp;
import com.smartsoft.csp.ast.PLConstants;
import com.smartsoft.csp.ast.Space;

public interface Token extends PLConstants {

    //static char tokens
    boolean isLParen();

    boolean isRParen();

    boolean isArgSep();

    boolean isNot();

    ////static string tokens

    boolean isPosComplex();

    boolean isConstantTrue();

    boolean isConstantFalse();

    boolean isConstant();

    boolean isVar();

    Exp getPosLit(Space space);

    PosOp getPosComplexOp();

    int size();

    void consume(Stream stream);

    boolean isHead();

    Character getChar();

    String stringify();
}
