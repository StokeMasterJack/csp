package com.tms.csp.parse;

import com.tms.csp.ast.Exp;
import com.tms.csp.ast.PLConstants;
import com.tms.csp.ast.Ser;

import java.util.List;

public interface Writer extends PLConstants {

    void renderExp(Exp e, Ser a);

    String renderExp(Exp e);

    String renderArgList(List<Exp> e);


}
