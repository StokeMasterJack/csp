package com.tms.csp.parse;

import com.tms.csp.ast.PLConstants;

public abstract class TextToken extends TokenBase implements PLConstants {

    protected final String text;

    protected TextToken(String text) {
        this.text = text;
    }

    @Override
    public boolean isStringToken() {
        return true;
    }

    public String getText() {
        return text;
    }

    @Override
    abstract public boolean isHead();

    @Override
    public String stringify() {
        return text;
    }


    @Override
    public int size() {
        return text.length();
    }

    @Override
    public void consume(Stream stream) {
        stream.consume(size());
    }

}
