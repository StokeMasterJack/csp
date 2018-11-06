package com.tms.csp.util;

public interface CodeResolver {

    String idToCode(int id);

    int codeToId(String code);
}
