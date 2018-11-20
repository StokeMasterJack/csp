package com.smartsoft.csp.parse;

import com.smartsoft.csp.ast.Exp;
import com.smartsoft.csp.ast.PLConstants;
import com.smartsoft.csp.ast.Ser;

import java.util.List;

public interface Writer extends PLConstants {

    void renderExp(Exp e, Ser a);

    String renderExp(Exp e);

    String renderArgList(List<Exp> e);


}
