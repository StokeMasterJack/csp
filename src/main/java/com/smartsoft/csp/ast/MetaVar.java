package com.smartsoft.csp.ast;

import java.util.List;

import static com.smartsoft.csp.ssutil.Console.prindent;

public class MetaVar {

    private final String code;
    private final List<MetaVar> childVars;
    private final boolean radioGroup;  //is radio group

    private MetaVar parent;

    public MetaVar(String code, List<MetaVar> childVars, boolean radioGroup) {
        this.code = code;
        this.childVars = childVars;
        this.radioGroup = radioGroup;
        for (MetaVar var : childVars) {
            var.setParent(this);
        }
    }

    public void setParent(MetaVar parent) {
        this.parent = parent;
    }

    public MetaVar getParent() {
        return parent;
    }

    public void print() {
        print(0);
    }

    public void print(int depth) {
        prindent(depth, code);
        for (MetaVar var : childVars) {
            var.print(depth + 1);
        }
    }

    public String serializeTree() {
        StringBuilder sb = new StringBuilder();
        serializeTree(0, sb);
        return sb.toString();
    }

    public void serializeTree(int depth, StringBuilder sb) {
        prindent(depth, code, sb);
        for (MetaVar var : childVars) {
            var.serializeTree(depth + 1, sb);
        }
    }

    public MetaVar getByCode(String code) {
        if (code.equals(this.code)) {
            return this;
        }

        for (MetaVar var : childVars) {
            MetaVar v = var.getByCode(code);
            if (v != null) {
                return v;
            }
        }

        return null;

    }

    public boolean isXorChild() {
        if (parent == null) return false;
        return parent.isRadioGroup();
    }

    public boolean isRadioGroup() {
        return radioGroup;
    }

    public List<MetaVar> getChildVars() {
        return childVars;
    }

    public String getCode() {
        return code;
    }
}
