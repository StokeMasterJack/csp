package com.smartsoft.csp.fm.dnnf.cartesian;

import com.google.common.collect.ImmutableList;

import java.util.List;

import static com.google.common.base.Preconditions.checkElementIndex;

public class CartesianList {

    ImmutableList<List<Integer>> axes;
    private final int[] axesSizeProduct;

    CartesianList(ImmutableList<List<Integer>> axes) {
        this.axes = axes;
        this.axesSizeProduct = Conjunction.computeAxesSizeProduct(axes);
    }


    public Integer get(int index, int axis) {
        int axisIndex = getAxisIndexForProductIndex(index, axis);
        return axes.get(axis).get(axisIndex);
    }

    private int getAxisIndexForProductIndex(int index, int axis) {
        return (index / axesSizeProduct[axis + 1]) % axes.get(axis).size();
    }


    public int size() {
        return axesSizeProduct[0];
    }

    public ImList get(final int index) {

        class MyImList implements ImList {

            public int size() {
                return axes.size();
            }

            public int get(int axis) {
                checkElementIndex(axis, size());
                int axisIndex = getAxisIndexForProductIndex(index, axis);
                return axes.get(axis).get(axisIndex);
            }

        }

        return new MyImList();
    }


    static interface ImList {

        public int size();

        public int get(int axis);
    }


}
