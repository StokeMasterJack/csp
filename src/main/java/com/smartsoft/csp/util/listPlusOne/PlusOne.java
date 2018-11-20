package com.smartsoft.csp.util.listPlusOne;

import com.google.common.collect.ImmutableList;
import com.google.common.hash.Funnel;
import com.smartsoft.csp.parse.ParseUtil;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Not allowed:
 *      empty elements
 *      empty list
 *      no dups allowed
 */
public class PlusOne<E extends Comparable<E>> implements SortedSet<E> {

    protected final PlusOne<E> parent;
    protected final E element;

    protected PlusOne(PlusOne<E> parent, E element) {
        checkNotNull(element);
        if (parent != null) {
            checkSort(parent.element, element);
        }
        this.element = element;
        this.parent = parent;
    }

    protected PlusOne(E element) {
        this(null, element);
    }

    public static <T extends Comparable<T>> boolean checkSort(T parentElement, T childElement) {
        int i = childElement.compareTo(parentElement);
        if (i <= 0) {
            throw new IllegalArgumentException();
        }
        return true;
    }

    public int size() {
        if (parent == null) return 1;
        return parent.size() + 1;
    }

    public PlusOne<E> getParent() {
        return parent;
    }

    @Nonnull
    public Iterator<E> iterator() {
        return new ListPlusOneIterator<E>(this);
    }

    public boolean isEmpty() {
        return false;
    }


    public boolean isRoot() {
        return parent == null;
    }

    public boolean contains(Object o) {
        if (element.equals(o)) return true;
        if (parent == null) return false;
        return parent.contains(o);
    }


    public PlusOne<E> append(E e) {
        checkNotNull(e);
        return new PlusOne<E>(this, e);
    }


    public static <E extends Comparable<E>> PlusOne<E> create(E firstElement) {
        return new PlusOne<E>(null, firstElement);
    }

    public static <E extends Comparable<E>> PlusOne<E> create(PlusOne<E> init, List<E> list, boolean reverseList) {
        if (list == null || list.isEmpty()) {
            return init;
        }

        PlusOne<E> current = init;
        if (reverseList) {
            for (int i = list.size() - 1; i >= 0; i--) {
                E e = list.get(i);
                if (current == null) {
                    current = create(e);
                } else {
                    current = current.append(e);
                }
            }
        } else {
            for (int i = 0; i < list.size(); i++) {
                E e = list.get(i);
                if (current == null) {
                    current = create(e);
                } else {
                    current = current.append(e);
                }
            }
        }


        return current;
    }

    @Override
    public Object[] toArray() {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean add(E e) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }


    public static <E extends Comparable<E>> ListPlusOneFunnel<E> createFunnel(Funnel<E> elementFunnel) {
        return new ListPlusOneFunnel<E>(elementFunnel);
    }

    public E getElement() {
        return element;
    }

    public PlusOne<E> getRoot() {
        if (parent == null) return this;
        return parent.getRoot();
    }

    public E first() {
        if (parent == null) return element;
        return parent.first();
    }

    public E last() {
        return element;
    }

    public E getPrevious() {
        if (parent == null) return null;
        return parent.element;
    }

    @Override
    public Comparator<? super E> comparator() {
        return new Comparator<E>() {
            @Override
            public int compare(E o1, E o2) {
                return o1.compareTo(o2);
            }
        };
    }

    @Nonnull
    @Override
    public SortedSet<E> headSet(E toElement) {
        throw new UnsupportedOperationException();
    }


    @Nonnull
    @Override
    public SortedSet<E> subSet(E fromElement, E toElement) {
        throw new UnsupportedOperationException();
    }

    @Nonnull
    @Override
    public SortedSet<E> tailSet(E fromElement) {
        throw new UnsupportedOperationException();
    }

    public PlusOne<E> findElement(E e) {
        return findElement(e, null);

    }

    public PlusOne<E> findElement(E e, List<E> above) {

        PlusOne<E> current = this;
        while (true) {
            if (current == null) {
                return null;
            }

            int i = current.element.compareTo(e);

            if (i < 0) {
                return null;
            } else if (i == 0) {
                return current;
            } else {
                assert i > 0;
                if (above != null) {
                    above.add(current.element);
                }
                current = current.parent;
            }


        }

    }

    public PlusOne<E> removeElement(E e) {
        ArrayList<E> tail = new ArrayList<E>();
        PlusOne<E> head = findElement(e, tail);

        if (head == null) {
            return null;
        }


        return create(head.parent, tail, true);
    }


    public PlusOne<E> appendAll(E... list) {
        ImmutableList<E> x = ImmutableList.copyOf(list);
        return appendAll(x);
    }

    public PlusOne<E> appendAll(List<E> list) {
        return PlusOne.create(this, list, false);
    }


    public static boolean ass(PlusOne p, String s) {
        String ss = p.toString();
        assert ss.equals(s);
        return true;
    }

    public static boolean ass2(Collection p, String s) {
        String ss = ParseUtil.Companion.serializeCodes(p);
        assert ss.equals(s);
        return true;
    }

    public String toString() {
        StringBuilder a = new StringBuilder();
        PlusOne current = this;
        while (current != null) {
            a.append(current.element.toString());
            a.append(' ');
            current = current.parent;
        }
        return a.toString().trim();
    }


    public static void main(String[] args) {
        PlusOne<String> a = new PlusOne<String>("a");
        PlusOne<String> b = a.append("b");

        ass(a, "a");
        ass(b, "b a");

        PlusOne<String> c = b.append("c");
        ass(c, "c b a");


        PlusOne<String> f = c.appendAll("d", "e", "f");

        ass(f, "f e d c b a");


        ArrayList<String> addBack = new ArrayList<String>();
        PlusOne<String> cHead = f.findElement("c", addBack);
        ass(cHead, "c b a");


        ass2(addBack, "f e d");

        PlusOne<String> frc = f.removeElement("c");
        ass(frc, "f e d b a");


    }
}
