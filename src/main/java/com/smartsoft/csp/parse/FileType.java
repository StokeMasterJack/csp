package com.smartsoft.csp.parse;

public enum FileType {
    PL, DNNF, fileType;


    public boolean isPL() {
        return this == PL;
    }

    public boolean isDnnf() {
        return this == DNNF;
    }
}
