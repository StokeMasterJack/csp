package com.smartsoft.csp.util;

import com.smartsoft.csp.ast.Var;

public class EvalContexts {

    public static EvalContext fromAssignmentString(final String values) {

        return new AbstractEvalContext() {
            @Override
            public Tri getValue(Var var) {
                String code = var.getVarCode();
                int i = Integer.parseInt(code);
                return Bit.fromChar(values, i);
            }
        };
    }


    public static EvalContext allOpen() {
        return new AbstractEvalContext() {
            @Override
            public Tri getValue(Var var) {
                return Bit.OPEN;
            }
        };
    }

}
