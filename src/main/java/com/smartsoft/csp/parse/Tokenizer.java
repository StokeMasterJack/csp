package com.smartsoft.csp.parse;

import com.smartsoft.csp.ast.PLConstants;

/**
 * AKA Singleton
 */
public abstract class Tokenizer implements PLConstants {

    abstract public Token matches(Stream stream);

    public  boolean isDcOr(){
        return false;
    }


}
