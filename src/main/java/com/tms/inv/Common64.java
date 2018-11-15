package com.tms.inv;

import com.google.common.collect.ImmutableList;
import com.tms.csp.ast.Var;
import com.tms.csp.varCodes.IVar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Common64 {

    private final ImmutableList<Var> vars;
    private final ImmutableList<String> suffixes;

    public Common64(ArrayList<Var> vars) {
        Collections.sort(vars, new Comparator<IVar>() {
            @Override
            public int compare(IVar o1, IVar o2) {
                return o1.getLocalName().compareTo(o2.getLocalName());
            }
        });


        this.vars = ImmutableList.copyOf(vars);

        ArrayList<String> ss = new ArrayList<String>();

        for (IVar var : vars) {
            ss.add(var.getLocalName());
        }

        suffixes = ImmutableList.copyOf(ss);
    }

    public int indexOf(String suffix) {
        return suffix.indexOf(suffix);
    }

    public int indexOf(IVar var) {
        return suffixes.indexOf(var.getLocalName());
    }

    public String getSuffix(int index) {
        return suffixes.get(index);
    }

    public IVar getVar(int index) {
        return vars.get(index);
    }

    public long serialize(Line line) {

        long elements = 0;

        for (int i = 0; i < 64; i++) {
            Var var = vars.get(i);
            if (line.containsVar(var)) {
                elements = add(elements, i);
            }
        }

        return elements;


    }

    public long add(long elements, int varIndex) {
        elements |= (1L << varIndex);
        return elements;
    }

}
