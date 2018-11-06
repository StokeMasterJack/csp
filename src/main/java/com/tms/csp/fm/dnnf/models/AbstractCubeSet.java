package com.tms.csp.fm.dnnf.models;

import com.tms.csp.ast.Space;
import com.tms.csp.fm.dnnf.products.Cube;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public abstract class AbstractCubeSet implements Set<Cube> {

    private final Space space;

    protected AbstractCubeSet(Space space) {
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

    @Override
    abstract public Iterator<Cube> iterator();

    @Override
    public Object[] toArray() {
        int L = size();
        Object[] a = new Object[L];
        assert a.length == L;
        int i = 0;
        for (Cube cube : this) {
            a[i] = cube;
            i++;
        }
        return a;
    }

    @Override
    public <T> T[] toArray(T[] a) {
        int L = size();
        assert a.length == L;
        int i = 0;
        for (Cube cube : this) {
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
    abstract public boolean containsAll(Collection<?> cubes);

    @Override
    public boolean addAll(Collection<? extends Cube> cubes) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> cubes) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> cubes) {
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
