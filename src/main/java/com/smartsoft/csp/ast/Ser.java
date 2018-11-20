package com.smartsoft.csp.ast;

import com.smartsoft.csp.ssutil.Path;
import com.smartsoft.csp.ssutil.Strings;
import com.smartsoft.csp.transforms.Transformer;
import com.smartsoft.csp.util.CodeResolver;

import java.io.IOException;

public class Ser implements PLConstants {

    public Closer closer;

    public Transformer transformer;
    public boolean dc = true;
    public boolean tiny = false;

    public Appendable appendable;
    public String name;
    public Path dir;
    public String ext;
    public Path outFile;
    public boolean renderImpishOrAsImp = false;
    public Tokens tokens = new Tokens();
    public CodeResolver resolver;

    public boolean expIds = true;

    public Ser(Appendable appendable) {
        this.appendable = appendable;
    }


    public Ser(CodeResolver resolver) {
        this();
        this.resolver = resolver;
    }

    public Ser() {
        this.appendable = new StringBuilder();
    }

    public static Ser forToString() {
        Ser ser = new Ser();
        ser.dc = false;
        ser.appendable = new StringBuffer();
        ser.tiny = false;
        return ser;
    }

    public String getFormat() {
        return tiny ? "tiny" : "std";
    }

    public String getFileName() {
        return name + "-" + getFormat() + "-" + transformer.getName() + "." + ext;
    }

    public Path getFilePath() {
        return this.dir.append(getFileName());
    }

    public Ser notTiny() {
        tiny = false;
        return this;
    }


    public Appendable getAppendable() {
        return appendable;
    }

    public Ser bang() {
        return append(BANG);
    }

    public Ser ap(char c) {
        return append(c);
    }

    public Ser ap(Object c) {
        return append(c);
    }

    public Ser ap(int c) {
        return append(c);
    }

    public Ser lit(int lit) {
        return append("L" + lit);
    }

    public Ser rpad(String s, int len) {
        String ss = Strings.rpad(s, ' ', len);
        return append(ss);
    }

    public Ser rpad(int s, int len) {
        String ss = Strings.rpad(s + "", ' ', len);
        return append(ss);
    }

    public Ser lpad(String s, int len) {
        String ss = Strings.lpad(s, ' ', len);
        return append(ss);
    }

    public Ser lpad(int s, int len) {
        String ss = Strings.lpad(s + "", ' ', len);
        return append(ss);
    }

    public Ser ap(CharSequence csq) {
        return append(csq);
    }

    public Ser println(Object csq) {
        append(csq.toString());
        return newLine();
    }

    public Ser println(CharSequence csq) {
        append(csq);
        return newLine();
    }


    public Ser append(Object o) {
        return append(o + "");
    }

    public Ser append(int i) {
        return append(i + "");
    }

    public Ser append(CharSequence csq) {
        try {
            appendable.append(csq);
            return this;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Ser append(CharSequence csq, int start, int end) {
        try {
            appendable.append(csq, start, end);
            return this;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public Ser append(char c) {
        try {
            appendable.append(c);
            return this;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        return appendable.toString();
    }

    public Ser prindent(int depth, String text) {
        indent(depth);
        append(text);
        append(NEW_LINE);
        return this;
    }

    public Ser newLine() {
        append(NEW_LINE);
        return this;
    }

    public Ser indent2(int depth) {
        for (int i = 0; i < depth; i++) {
            ap('\t');
        }
        return this;
    }

    public Ser indent(int depth) {
        for (int i = 0; i < depth; i++) {
            ap(' ');
            ap(' ');
        }
        return this;
    }

    public void argSep() {
        ap(tokens.argSep);
    }

    public void constantFalse() {
        ap(tokens.constantFalse);
    }

    public void constantTrue() {
        ap(tokens.constantTrue);
    }

    public void negation() {
        ap(tokens.negationVar.token);
    }

    public void lparen() {
        ap(tokens.lparen);
    }

    public void rparen() {
        ap(tokens.rparen);
    }

    public static Ser sbsat() {
        Ser a = new Ser();
        a.initForSbSat();
        return a;
    }

    public static Ser tiny() {
        Ser a = new Ser();
        a.initForTiny();
        return a;
    }

    public Ser var(int i) {
        ap("V" + i);
        return this;

    }

    public CodeResolver getResolver() {
        return resolver;
    }

    public void deleteTrailingNewLine() {
        deleteTrailingChar('\n');
    }

    public void deleteTrailingSpace() {
        deleteTrailingChar(' ');
    }

    public void deleteTrailingComma() {
        deleteTrailingChar(',');
    }

    public void deleteTrailingChar(char c) {
        if (appendable instanceof StringBuilder) {
            StringBuilder bb = (StringBuilder) appendable;
            if (bb.length() == 0) {
                return;
            }
            int indexOfLastChar = bb.length() - 1;
            if (bb.charAt(indexOfLastChar) == c) {
                bb.deleteCharAt(indexOfLastChar);
            }
        }
    }


    public static interface Closer {
        void closeQuietly();

    }

    public void close() {
        if (closer != null) {
            closer.closeQuietly();
        } else if (appendable instanceof Closer) {
            Closer closer = (Closer) appendable;
            closer.closeQuietly();
        }
    }

    public static class SerNegation {
        public String token = NOT_TOKEN;
        public boolean parens = false;
    }

    public static class Tokens {
        public String lparen = LPAREN_TOKEN;
        public String rparen = RPAREN_TOKEN;
        public String constantTrue = TRUE_TOKEN;
        public String constantFalse = FALSE_TOKEN;
        public String argSep = ARG_SEP_TOKEN;
        public SerNegation negationVar = new SerNegation();
        public SerNegation negationComplex = new SerNegation();
        public String and = "and";

        public String[] varCodes;

    }

    public void initForSbSat() {
        tokens.argSep = ", ";
        tokens.negationVar.token = "-";
        tokens.negationComplex.token = "not";
        tokens.negationComplex.parens = true;
    }

    public void initForTiny() {
        tokens.constantTrue = "_t";
        tokens.constantFalse = "_f";
        tiny = true;
    }


}
