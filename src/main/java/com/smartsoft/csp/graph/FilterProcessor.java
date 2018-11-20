package com.smartsoft.csp.graph;

import com.smartsoft.csp.ast.Exp;

public class FilterProcessor implements Filter, Processor {

    public Processor processor = PRINDENT;
    public Filter filter;

    public FilterProcessor(Processor processor, Filter filter) {
        this.processor = processor;
        this.filter = filter;
    }

    public FilterProcessor(Filter filter) {
        this.filter = filter;
    }

    public FilterProcessor() {
    }

    @Override
    public void process(Exp e, Context c) {
        if (processor != null) {
            processor.process(e, c);
        }
    }

    @Override
    public boolean accept(ExpDb db) {
        if (filter == null) {
            return true;
        } else {
            return filter.accept(db);
        }

    }

    @Override
    public boolean accept(Exp e) {
        return filter == null || filter.accept(e);
    }
}
