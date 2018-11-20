package com.smartsoft.csp.parse;


import com.smartsoft.csp.ast.Exp;
import com.smartsoft.csp.ast.Space;

public abstract class TokenBase implements Token {

    @Override
    public boolean isNot() {
        return false;
    }

    @Override
    public boolean isPosComplex() {
        return false;
    }

    @Override
    public boolean isConstantTrue() {
        return false;
    }

    @Override
    public boolean isConstantFalse() {
        return false;
    }

    public boolean isLParen() {
        return false;
    }

    public boolean isRParen() {
        return false;
    }

    public boolean isArgSep() {
        return false;
    }

    public boolean isCharToken() {
        return false;
    }

    public boolean isStringToken() {
        return false;
    }


    @Override
    public boolean isConstant() {
        return isConstantFalse() || isConstantTrue();
    }

    @Override
    public boolean isHead() {
        return !isArgSep() && !isLParen() && !isRParen();
    }


    public PosOp getOp() {
        throw new UnsupportedOperationException(getClass() + "");
    }

    public boolean isVar() {
        return false;
    }

    public abstract int size();


    public void consume(Stream stream) {
        stream.consume(size());
    }

    public Character getChar() {
        throw new UnsupportedOperationException(getClass() + "");
    }

    @Override
    public PosOp getPosComplexOp() {
        throw new UnsupportedOperationException(getClass() + "");
    }

    @Override
    public Exp getPosLit(Space space) {
        throw new UnsupportedOperationException(getClass() + "");
    }

    @Override
    final public String toString() {
        return stringify();
    }

}
