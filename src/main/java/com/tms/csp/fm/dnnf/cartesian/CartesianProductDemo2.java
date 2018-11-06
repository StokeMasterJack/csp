package com.tms.csp.fm.dnnf.cartesian;

import static com.google.common.base.Preconditions.checkElementIndex;

public class CartesianProductDemo2<E> {

//
//
//    List<List<E>> axes;
//    private final int[] axesSizeProduct;
//
//
//    public CartesianProductDemo2(List<List<E>> axes) {
//        this.axes = axes;
//        axesSizeProduct = new int[axes.size() + 1];
//
//        for (int i = axes.size() - 1; i >= 0; i--) {
//            axesSizeProduct[i] =
//                    IntMath.checkedMultiply(axesSizeProduct[i + 1], axes.get(i).size());
//        }
//    }
//
//    public static void main(String[] args) {
//
//
//        List<Set<Integer>> list = new ArrayList<Set<Integer>>();
//
//        list.add(ImmutableSet.of(0, 1, 2));
//        list.add(ImmutableSet.of(10, 11));
//        list.add(ImmutableSet.of(100, 200, 300));
//
//        Set<List<Integer>> cp = Sets.cartesianProduct(list);
//
//        System.err.println(cp);
//
//        System.err.println(cp.size());
//
//        assert cp.size() == 3 * 2 * 3;
//
//        for (List<Integer> ll : cp) {
//            System.err.println(ll);
//        }
//
//    }
//
//    /*
//         axes is a List<List<E>>
//
//         int[] axesSizeProduct = new int[axes.size() + 1];
//
//          axesSizeProduct[axes.size()] = 1;
//     */
//    private int getAxisIndexForProductIndex(int index, int axis) {
//        return (index / axesSizeProduct[axis + 1]) % axes.get(axis).size();
//    }
//
//    E get(int axis, int axisIndex) {
//        return null;
//    }
//
//    @Override
//    public ImmutableList<E> get(final int index) {
//        checkElementIndex(index, size());
//        return new ImmutableList<E>() {
//
//            @Override
//            public int size() {
//                return axes.size();
//            }
//
//            @Override
//            public E get(int axis) {
//                checkElementIndex(axis, size());
//                int axisIndex = getAxisIndexForProductIndex(index, axis);
//                return axes.get(axis).get(axisIndex);
//            }
//
//            @Override
//            boolean isPartialView() {
//                return true;
//            }
//        };
//    }
//
//    public int size() {
//        return axesSizeProduct[0];
//    }
//
//    public boolean contains(@Nullable Object o) {
//        if (!(o instanceof List)) {
//            return false;
//        }
//        List<?> list = (List<?>) o;
//        if (list.size() != axes.size()) {
//            return false;
//        }
//        ListIterator<?> itr = list.listIterator();
//        while (itr.hasNext()) {
//            int index = itr.nextIndex();
//            if (!axes.get(index).contains(itr.next())) {
//                return false;
//            }
//        }
//        return true;
//    }
//
//
//    @Override
//    public int indexOf(Object o) {
//        if (!(o instanceof List)) {
//            return -1;
//        }
//        List<?> l = (List<?>) o;
//        if (l.size() != axes.size()) {
//            return -1;
//        }
//        Iterator<?> lIterator = l.iterator();
//        int i = 0;
//        for (List<E> axis : axes) {
//            Object lElement = lIterator.next();
//            int axisIndex = axis.indexOf(lElement);
//            if (axisIndex == -1) {
//                return -1;
//            }
//            i = (i * axis.size()) + axisIndex;
//        }
//        return i;
//    }
//
//    @Override
//    public int lastIndexOf(Object o) {
//        if (!(o instanceof List)) {
//            return -1;
//        }
//        List<?> l = (List<?>) o;
//        if (l.size() != axes.size()) {
//            return -1;
//        }
//        Iterator<?> lIterator = l.iterator();
//        int i = 0;
//        for (List<E> axis : axes) {
//            Object lElement = lIterator.next();
//            int axisIndex = axis.lastIndexOf(lElement);
//            if (axisIndex == -1) {
//                return -1;
//            }
//            i = (i * axis.size()) + axisIndex;
//        }
//        return i;
//    }

}
