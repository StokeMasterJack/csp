package com.tms.csp.parse;

import com.tms.csp.ast.PLConstants;

/**
 * AKA Singleton
 */
public abstract class Tokenizer implements PLConstants {

    abstract public Token matches(Stream stream);

    public  boolean isDcOr(){
        return false;
    }


}
