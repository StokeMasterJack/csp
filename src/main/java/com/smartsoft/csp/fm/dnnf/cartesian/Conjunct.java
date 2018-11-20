package com.smartsoft.csp.fm.dnnf.cartesian;

public class Conjunct {

    private final int cubeCount;


    String name;

    public Conjunct(int cubeCount) {
        this.cubeCount = cubeCount;
    }

    public int getCubeCount() {
        return cubeCount;
    }

    public String getCube(int cubeIndex) {
        return null;
    }
}
