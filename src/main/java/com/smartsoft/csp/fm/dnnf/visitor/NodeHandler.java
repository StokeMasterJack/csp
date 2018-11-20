package com.smartsoft.csp.fm.dnnf.visitor;

import com.smartsoft.csp.ast.Exp;

public abstract class NodeHandler extends Traversal {

    public abstract void onHead(Exp n);

}
