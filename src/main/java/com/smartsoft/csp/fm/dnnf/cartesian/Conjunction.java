package com.smartsoft.csp.fm.dnnf.cartesian;

import com.google.common.collect.ImmutableList;

import java.util.List;

public abstract class Conjunction<T> {

    abstract public int getConjunctCount();

    abstract public int getCubeCountForConjunct(int conjunctIndex);

    abstract public T getCube(int conjunctIndex, int cubeIndex);

    public int computeCartesianProductCount() {

        int conjunctCount = getConjunctCount();

        int cpCount = 1;

        for (int conjunct = 0; conjunct < conjunctCount; conjunct++) {
            int cubeCount = getCubeCountForConjunct(conjunct);
            cpCount = cpCount * cubeCount;
        }

        return cpCount;

    }


    public static interface ProductHandler<T> {
        void onProduct(T product);
    }

    public void forEach(ProductHandler<T> h) {

        int conjunctCount = getConjunctCount();

        for (int conjunctIndex = 0; conjunctIndex < conjunctCount; conjunctIndex++) {

            int cubeCount = getCubeCountForConjunct(conjunctIndex);

            for (int cubeIndex = 0; cubeIndex < cubeCount; cubeIndex++) {
                T cube = getCube(conjunctIndex, cubeIndex);
                h.onProduct(cube);
            }

        }


    }


    public static int[] computeAxesSizeProduct(ImmutableList<List<Integer>> axes) {
        int[] axesSizeProduct = new int[axes.size() + 1];
        axesSizeProduct[axes.size()] = 1;
        for (int i = axes.size() - 1; i >= 0; i--) {
            axesSizeProduct[i] = axesSizeProduct[i + 1] * axes.get(i).size();
        }
        return axesSizeProduct;
    }


}
