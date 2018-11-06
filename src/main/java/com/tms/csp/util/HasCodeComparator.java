package com.tms.csp.util;

import com.google.common.collect.ImmutableSortedSet;
import com.tms.csp.ast.Exp;
import com.tms.csp.ast.Var;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class HasCodeComparator<T extends HasCode> implements Comparator<T> {

    private static final String[] EMPTY_SET_KEY = new String[0];

    public static final HasCodeComparator INSTANCE = new HasCodeComparator();

    @Override
    public int compare(HasCode o1, HasCode o2) {
        String i1 = o1.getCode();
        String i2 = o2.getCode();
        return i1.compareTo(i2);
    }

    public static HasCodeComparator<Var> varComparator() {
        return (HasCodeComparator<Var>) INSTANCE;
    }

    public static HasCodeComparator<Exp> expComparator() {
        return (HasCodeComparator<Exp>) INSTANCE;
    }

    public static <T extends HasCode> void sortArray(T[] array) {
        if (array == null || array.length == 0) return;
        Arrays.sort(array, INSTANCE);
    }

    public static <T extends HasCode> void sortList(List<T> list) {
        Collections.sort(list, INSTANCE);
    }

    public static <T extends HasCode> String[] createKey(T... elements) {
        if (elements == null || elements.length == 0) return EMPTY_SET_KEY;
        ImmutableSortedSet.Builder<T> builder = ImmutableSortedSet.orderedBy(INSTANCE);
        for (T element : elements) {
            builder.add(element);
        }

        ImmutableSortedSet<T> ts = builder.build();
        return createKey2(ts);
    }

    public static <T extends HasCode> String[] createKey(Collection<T> elements) {
        if (elements == null || elements.size() == 0) return EMPTY_SET_KEY;
        ImmutableSortedSet<T> ts = ImmutableSortedSet.copyOf(INSTANCE, elements);
        return createKey2(ts);
    }

    public static <T extends HasCode> String[] createKey2(ImmutableSortedSet<T> elements) {
        if (elements == null || elements.isEmpty()) return EMPTY_SET_KEY;
        String[] a = new String[elements.size()];
        int i = 0;
        for (T t : elements) {
            a[i] = t.getCode();
            i++;
        }
        return a;
    }


}
