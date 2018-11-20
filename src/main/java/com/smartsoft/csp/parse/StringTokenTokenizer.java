package com.smartsoft.csp.parse;

public class StringTokenTokenizer extends TokenTokenizer {

    private final String token;

    protected StringTokenTokenizer(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    @Override
    public boolean matchesInternal(Stream stream) {
        int size = size();
        for (int i = 0; i < size && !stream.isEol(); i++) {
            char c = stream.peek(i);
            if (!isCharValid(i, c)) {
                return false;
            }
        }
        return true;
    }

    public boolean isCharValid(int i, char c) {
        return c == token.charAt(i);
    }

    @Override
    public int size() {
        return token.length();
    }


    @Override
    public String stringify() {
        return getToken();
    }
}
