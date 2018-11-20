package com.smartsoft.csp.parse;


public abstract class VarToken extends TextToken {

    protected VarToken(String text) {
        super(text);
    }

    public boolean isVar() {
        return true;
    }

    @Override
    public boolean isHead() {
        return true;
    }

    public PosOp getOp() {
        return PosOp.VAR;
    }



}
