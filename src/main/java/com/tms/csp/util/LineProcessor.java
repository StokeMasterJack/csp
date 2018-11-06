package com.tms.csp.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Single line  formula text format
 */
public interface LineProcessor {
    void processLine(String line);

    public static class ListLineProcessor implements LineProcessor {

        private final ArrayList<String> list = new ArrayList<String>();

        @Override
        public void processLine(String line) {
            list.add(line);
        }

        public List<String> getList() {
            return list;
        }

    }

    



}
