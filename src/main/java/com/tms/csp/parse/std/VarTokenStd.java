package com.tms.csp.parse.std;

import com.tms.csp.ast.Exp;
import com.tms.csp.ast.Space;
import com.tms.csp.ast.Var;
import com.tms.csp.parse.VarToken;

public class VarTokenStd extends VarToken {

    public VarTokenStd(String text) {
        super(text);
    }

    public String getCode() {
        return text;
    }

    @Override
    public Exp getPosLit(Space space) {
        Var var = space.mkVar(text);
        return var.pLit();
    }

}
