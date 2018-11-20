package com.smartsoft.csp.parse;


import com.smartsoft.csp.ast.Exp;
import com.smartsoft.csp.ast.Space;

/**
 * AKA Singleton
 *
 * ComboToken is both token and tokenizer
 */
public abstract class TokenTokenizer extends Tokenizer implements Token {

    public abstract boolean matchesInternal(Stream stream);

    public Token matches(Stream stream) {
        if(stream.getInput().startsWith("dont")){
            System.err.println(33333);
        }
        boolean matches = matchesInternal(stream);
        if (matches) {
            consume(stream);
            if (silentlyConsume()) {
                return null;
            } else {
                return this;
            }

        } else {
            return null;
        }
    }

    public boolean silentlyConsume() {
        return false;
    }

    public boolean isCharToken() {
        return false;
    }

    public String getString() {
        return null;
    }

    public Character getChar() {
        return null;
    }


    @Override
    public boolean isHead() {
        return !isArgSep() && !isLParen() && !isRParen();
    }


    @Override
    public boolean isConstant() {
        return isConstantFalse() || isConstantTrue();
    }

    @Override
    public Exp getPosLit(Space space) {
        throw new UnsupportedOperationException(getClass() + "");
    }

    @Override
    public PosOp getPosComplexOp() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void consume(Stream stream) {
        stream.consume(size());
    }

    @Override
    public boolean isLParen() {
        return false;
    }

    @Override
    public boolean isRParen() {
        return false;
    }

    @Override
    public boolean isArgSep() {
        return false;
    }

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

    @Override
    public boolean isVar() {
        return false;
    }

    @Override
    final public String toString() {
        return stringify();
    }

}
