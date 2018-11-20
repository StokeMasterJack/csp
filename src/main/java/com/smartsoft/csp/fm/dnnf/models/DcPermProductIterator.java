package com.smartsoft.csp.fm.dnnf.models;

import com.smartsoft.csp.fm.dnnf.products.Cube;
import com.smartsoft.csp.fm.dnnf.products.DcPermCube;

import java.util.Iterator;

public class DcPermProductIterator implements Iterator<Cube> {

    private final Solution solution;

    private int dcPerm = 0; //dcPerm

    DcPermProductIterator(Solution solution) {
        this.solution = solution;
    }

    @Override
    public boolean hasNext() {
        return dcPerm < solution.size();
    }

    @Override
    public Cube next() {
        assert hasNext();

        assert dcPerm >= 0;
        assert dcPerm < solution.size();

        Cube retVal = new DcPermCube(solution, dcPerm);

        dcPerm++;

        return retVal;
    }

    @Override
    public void remove() {
        throw new IllegalStateException();
    }

    public static void main(String[] args) {

    }
}
