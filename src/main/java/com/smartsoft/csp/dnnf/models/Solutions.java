package com.smartsoft.csp.dnnf.models;

import com.smartsoft.csp.ast.Exp;
import com.smartsoft.csp.ast.Space;
import com.smartsoft.csp.dnnf.products.Cube;
import com.smartsoft.csp.dnnf.products.Cubes;
import com.smartsoft.csp.util.varSets.VarSet;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

/**
 * Union
 */
public class Solutions extends AbstractCubeSet {

    private final Set<Cube> solutionCubes;
    private final VarSet dcVars;
    private final Cube prefix;
    private final int size;

    public Solutions(Space space, Set<Cube> solutionCubes, VarSet dcVars) {
        this(space, solutionCubes, dcVars, null);
    }

    public Solutions(Space space, Set<Cube> solutionCubes, VarSet dcVars, Cube prefix) {
        super(space);
        this.solutionCubes = solutionCubes;
        this.dcVars = dcVars;
        this.prefix = prefix;

        int solutionCubesSize = solutionCubes.size();
        assert solutionCubesSize >= 0;

        int dcPermCount = Exp.Companion.computeDcPermCount(dcVars.size());
        assert dcPermCount >= 0;

        this.size = solutionCubesSize * dcPermCount;
        if (this.size < 0) {
            throw new IllegalStateException();
        }
        assert this.size >= 0;
    }

    @Override
    public int size() {
        return size;
    }


    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public boolean contains(Object o) {
        throw new UnsupportedOperationException();
    }

    @NotNull
    @Override
    public Iterator<Cube> iterator() {
        return new SolutionsIterator(this);
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> cubes) {
        throw new UnsupportedOperationException();
    }


    public static class SolutionsIterator implements Iterator<Cube> {

        private final Solutions solutions;
        private Iterator<Cube> it1;

        private Solution solution;
        private Iterator<Cube> it2;

        SolutionsIterator(Solutions solutions) {
            this.solutions = solutions;
        }

        @Override
        public boolean hasNext() {
            if (it1 == null) {
                return true;
            }

            if (it1.hasNext()) {
                return true;
            }

            if (it2 == null) {
                return true;
            }

            if (it2.hasNext()) {
                return true;
            }

            return false;
        }

        @Override
        public Cube next() {
            assert hasNext();

            if (it2 == null || !it2.hasNext()) {
                if (it1 == null) {
                    it1 = solutions.solutionCubes.iterator();
                }
                Cube cube = it1.next();

                Space space = solutions.getSpace();

                solution = new Solution(space, cube, solutions.dcVars, solutions.prefix);
                it2 = solution.iterator();
            }

            return it2.next();
        }

        @Override
        public void remove() {
            throw new IllegalStateException();
        }
    }

    @Override
    public boolean equals(Object obj) {
        return Cubes.setsEquals(this, obj);
    }
}
