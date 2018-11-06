package com.tms.csp.graph;

import com.tms.csp.ast.Exp;
import com.tms.csp.ast.PLConstants;

import static com.tms.csp.ssutil.Console.prindent;

/**
 * ExpProcessor is non-recursive.
 *      It will only process a single node.
 *      i.e. it will not automatically process child nodes
 *      if you need recursive: use ExpVisitor
 */
public interface Processor extends PLConstants {

    void process(Exp e, Context context);

    public class Prindent implements Processor {

        @Override
        public void process(Exp e, Context context) {
            prindent(context.depth, e.toString());

        }
    }

    Processor PRINDENT = new Prindent();


}
