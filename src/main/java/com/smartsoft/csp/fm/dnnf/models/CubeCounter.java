package com.smartsoft.csp.fm.dnnf.models;

import com.smartsoft.csp.fm.dnnf.products.Cube;
import com.smartsoft.csp.util.CodeResolver;

import java.util.HashSet;

public class CubeCounter implements CubeHandler {

    private long count;
    HashSet set = new HashSet();

    CodeResolver resolver;

    public CubeCounter(CodeResolver resolver) {
        this.resolver = resolver;
    }

    public CubeCounter() {
    }

    @Override
    public void onCube(Cube cube) {
        count(cube);
        log(cube);
        collect(cube);
    }

    public void log(Cube cube) {
//        System.err.println(s);
        System.err.println("\t " + cube);
    }

    public void count(Cube cube) {
        count++;
    }

    public void collect(Cube cube) {
        set.add(cube);
    }

    public long getCount() {
        return count;
    }

    public long getCount2() {
        return set.size();
    }


}
