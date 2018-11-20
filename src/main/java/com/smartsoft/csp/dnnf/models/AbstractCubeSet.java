package com.smartsoft.csp.dnnf.models;

import com.smartsoft.csp.ast.Space;
import com.smartsoft.csp.dnnf.products.Cube;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public abstract class AbstractCubeSet implements Set<Cube> {

    private final Space space;

    AbstractCubeSet(Space space) {
        this.space = space;
    }

    public Space getSpace() {
        return space;
    }

    @Override
    abstract public int size();

    @Override
    abstract public boolean isEmpty();

    @Override
    abstract public boolean contains(Object o);

    @NotNull
    @Override
    abstract public Iterator<Cube> iterator();

    @NotNull
    @Override
    public Object[] toArray() {
        int L = size();
        Object[] a = new Object[L];
        int i = 0;
        for (Cube cube : this) {
            a[i] = cube;
            i++;
        }
        return a;
    }

    @NotNull
    @Override
    public <T> T[] toArray(T[] a) {
        int L = size();
        assert a.length == L;
        int i = 0;
        for (Cube cube : this) {
            //noinspection unchecked
            a[i] = (T) cube;
            i++;
        }
        return a;
    }

    @Override
    public boolean add(Cube cube) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    abstract public boolean containsAll(@NotNull Collection<?> cubes);

    @Override
    public boolean addAll(@NotNull Collection<? extends Cube> cubes) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> cubes) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> cubes) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

//    public String toString() {
//        Iterator<Cube> i = iterator();
//        if (!i.hasNext())
//            return "[]";
//
//        StringBuilder sb = new StringBuilder();
//        sb.append('[');
//        for (; ; ) {
//            Cube e = i.next();
//            sb.append(e == this ? "(this Collection)" : e);
//            if (!i.hasNext())
//                return sb.append(']').toString();
//            sb.append(", ");
//        }
//    }


}
