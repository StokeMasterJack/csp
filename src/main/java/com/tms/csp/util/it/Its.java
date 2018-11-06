package com.tms.csp.util.it;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Iterators;
import com.tms.csp.util.HasCode;
import kotlin.jvm.functions.Function1;

import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;

public class Its {

    public static <T> Set<T> toSet(Iterator<T> iterator) {
        return ImmutableSet.copyOf(iterator);
    }

    public static Set<String> toStringSet(Iterator iterator) {
        return ImmutableSet.copyOf(mapToString(iterator));
    }

    public static SortedSet<String> toSortedStringSet(Iterator iterator) {
        return ImmutableSortedSet.copyOf(mapToString(iterator));
    }

    public static SortedSet<String> toSortedCodeSet(Iterator<? extends HasCode> it) {
        return ImmutableSortedSet.copyOf(mapToCodes(it));
    }

    public static SortedSet<String> toSortedCodeSet(Iterable<? extends HasCode> it) {
        return ImmutableSortedSet.copyOf(mapToCodes(it));
    }

    public static <F, T> Iterator<T> mapTo(final Iterator<? extends F> iterator, final java.util.function.Function<F, T> converter) {
        return new MappingIterator<F, T>(iterator, converter);
    }

    public static <F, T> Iterator<T> mapTo2(final Iterator<? extends F> iterator, final java.util.function.Function<F, T> converter) {
        return new MappingIterator<F, T>(iterator, converter);
    }

    public static Iterator<String> mapToString(final Iterator iterator) {
        return new ToStringIterator(iterator);
    }

    public static Iterator<String> mapToCodes(final Iterator<? extends HasCode> iterator) {
        return new ToCodeIterator(iterator);
    }

    public static Iterable<String> mapToCodes(final Iterable<? extends HasCode> it) {
        return new Iterable<String>() {
            @Override
            public Iterator<String> iterator() {
                return mapToCodes(it.iterator());
            }
        };
    }

    public static Iterable<String> mapToStringIt(final Iterable it) {
        return new Iterable<String>() {
            @Override
            public Iterator<String> iterator() {
                return mapToString(it.iterator());
            }
        };
    }

//    public static Iterable<String> itForArray(String[] a) {
//        return new Iterable<String>() {
//            @Override
//            public Iterator<String> iterator() {
//                return It.forArray(a);
//            }
//        };
//    }

    public static <T> Iterable<T> itForArray(final T[] a) {
        return new Iterable<T>() {
            @Override
            public Iterator<T> iterator() {
                return Iterators.forArray(a);
            }
        };
    }

}
