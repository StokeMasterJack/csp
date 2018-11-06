package com.tms.csp.parse;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkState;

public class TokenStream {

    private final GlobalTokenizer tokenizer;

    private final Stream stream;

    Token buffer;

    public TokenStream(GlobalTokenizer tokenizer, Stream stream) {
        this.tokenizer = tokenizer;
        this.stream = stream;
    }

    public TokenStream(GlobalTokenizer tokenizer, String inputText) {
        this(tokenizer, new Stream(inputText));
    }

    public List<Token> readAllTokens() {
        ArrayList<Token> a = new ArrayList<Token>();
        while (true) {
            if (stream.isEol()) {
                break;
            }
            Token token = take();
            a.add(token);
        }
        return a;

    }

    public void printTokens() {
        Token t = take();
        while (t != null) {
            System.err.println(t);
        }

    }

    public Token peek() {
        if (this.buffer == null) {
            this.buffer = tokenizer.takeNextToken(stream);
        }
        return this.buffer;
    }

//    public Token take() {
//        if (tokens.isEmpty()) {
//            return null;
//        } else {
//            return tokens.remove(0);
//        }
//    }

//    public Token takeNextTokenFromStream() {
//        return tokenizer.takeNextToken(stream);
//    }

    public Token take() {
        if (this.buffer != null) {
            Token tmp = buffer;
            this.buffer = null;
            return tmp;
        } else {
            return tokenizer.takeNextToken(stream);
        }
    }

    public GlobalTokenizer getTokenizer() {
        return tokenizer;
    }

    public Stream getStream() {
        return stream;
    }


    public void consumeAndCheck(char c) {
        Token t = consume();
        char cc = t.getChar();
        checkState(cc == c, "check failed - expected[" + c + "]   got[" + cc + "]");
    }

    public Token consume() {
        return take();
    }

    public boolean isEmpty() {
        return stream.isEol() && buffer == null;
    }


}
