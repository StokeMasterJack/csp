package com.smartsoft.csp.parse.std;

import com.smartsoft.csp.ast.Exp;
import com.smartsoft.csp.ast.Space;
import com.smartsoft.csp.ast.Var;
import com.smartsoft.csp.parse.VarToken;

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
