package com.smartsoft.csp.parse;

import java.util.HashMap;
import java.util.Map;

public class ParseCounter {
    private final Map<String, Integer> prefixCounts = new HashMap<String, Integer>();

    public void countPrefix(String prefix) {
        Integer count = prefixCounts.get(prefix);
        if (count == null) count = 0;
        count++;
        prefixCounts.put(prefix, count);
    }

    public void print() {
        for (String prefix : prefixCounts.keySet()) {
            System.err.println(prefix + ": " + prefixCounts.get(prefix));
        }
    }

    public Map<String, Integer> getPrefixCounts() {
        return prefixCounts;
    }
}
