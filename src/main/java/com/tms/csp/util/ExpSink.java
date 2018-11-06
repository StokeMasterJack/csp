package com.tms.csp.util;

import com.tms.csp.ast.Exp;
import com.tms.csp.ast.PLConstants;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public interface ExpSink extends PLConstants {

    void addExp(Exp exp);

    public static final class ListExpSink implements ExpSink {

        private final ArrayList<Exp> list = new ArrayList<Exp>();

        @Override
        public void addExp(Exp exp) {
            list.add(exp);
        }

        public List<Exp> getList() {
            return list;
        }
    }


}
