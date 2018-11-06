package com.tms.csp.parse;

public class Stream {

    private final String input;
    public int cursor;

    public static final char PAST_END_OF_STREAM = Character.MAX_VALUE;

    public Stream(String input) {
        this.input = input;
        this.cursor = 0;
    }

    public char peek(int i) {
        try {
            return input.charAt(cursor + i);
        } catch (StringIndexOutOfBoundsException e) {
            return PAST_END_OF_STREAM;
        }
    }

    public char peek() {
        return peek(0);
    }

    public boolean isEol() {
        return !hasMoreTokens();
    }

    private boolean hasMoreTokens() {
        return cursor < input.length();
    }

    public void consume() {
        consume(1);
    }

    public void consume(int howManyChars) {
        cursor += howManyChars;
    }

    public char take() {
        char c = peek(0);
        consume();
        return c;
    }

    public int getCursor() {
        return cursor;
    }

    public String getInput() {
        return input;
    }

    public void print() {
        System.err.println("input[" + input + "]");
        System.err.println("cursor[" + cursor + "]");
        for (int i = 0; i < 5; i++) {
            char peek = peek(i);
            if (peek == PAST_END_OF_STREAM) {
                break;
            }
            System.err.println("peek[" + i + "][" + peek + "]");
        }


    }
}
