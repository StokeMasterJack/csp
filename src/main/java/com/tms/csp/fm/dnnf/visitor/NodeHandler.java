package com.tms.csp.fm.dnnf.visitor;

import com.tms.csp.ast.Exp;

public abstract class NodeHandler extends Traversal {

    public abstract void onHead(Exp n);

}
