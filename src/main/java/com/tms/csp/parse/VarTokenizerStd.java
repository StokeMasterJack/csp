package com.tms.csp.parse;

import com.tms.csp.parse.std.VarTokenStd;

public class VarTokenizerStd extends VarTokenizer {

    public boolean isValidVarFirstChar(char c) {
        return Character.isLetterOrDigit(c);
    }

    public boolean isValidVarChar(char c) {
        return Character.isLetterOrDigit(c) || c == UNDERSCORE || c == '|' || c == '.' || c == ':';
    }


    @Override
    public Token matches(Stream stream) {
        if (!matchesInternal(stream)) return null;
        StringBuilder sb = new StringBuilder();

        while (keepReading(stream)) {
            sb.append(stream.take());
        }
        return new VarTokenStd(sb.toString());
    }


    public boolean matchesInternal(Stream stream) {
        char c = stream.peek();
        boolean b = isValidVarFirstChar(c);
        if (!b) {
            System.err.println("badFirstChar: " + c + " at cursor[" + stream.getCursor() +"] formula string: " + stream.getInput() );
        }
        return b;
    }

    public boolean keepReading(Stream stream) {
        if (stream.isEol()) {
            return false;
        }
        char c = stream.peek();

        boolean validVarChar = isValidVarChar(c);

        if (!validVarChar) {
//            System.err.println("invalidVarChar[" + c + "]");
        }

        return validVarChar;
    }


    public boolean isValidVarFirstChar(Stream stream) {
        return isValidVarFirstChar(stream.peek());
    }

}
