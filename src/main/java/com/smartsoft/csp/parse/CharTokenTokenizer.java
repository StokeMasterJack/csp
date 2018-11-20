package com.smartsoft.csp.parse;

public class CharTokenTokenizer extends TokenTokenizer {

    protected final char c;

    public CharTokenTokenizer(char c) {
        this.c = c;
    }

    public boolean matchesInternal(Stream stream) {
        char peek = stream.peek();
        return (c == peek);
    }

    @Override
    public boolean isCharToken() {
        return true;
    }

    public Character getChar() {
        return c;
    }

    @Override
    public int size() {
        return 1;
    }



    public String stringify(){
        return c + "";
    }

}
