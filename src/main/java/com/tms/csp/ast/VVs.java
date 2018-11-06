package com.tms.csp.ast;

import com.tms.csp.util.ints.IndexedEntry;
import com.tms.csp.util.ints.TreeSequence;

import java.util.AbstractCollection;
import java.util.Iterator;

public class VVs extends AbstractCollection<Exp> {

    Space space;
    public TreeSequence<Exp> args = new TreeSequence<Exp>();

    public VVs(Space space) {
        this.space = space;
    }

    public boolean add(Exp vv) {
        assert vv.isVv();
        int expId = vv.getExpId();
        Exp old = args.put(expId, vv);
        return old != null;
    }

    public Exp getFirst() {
        return args.get(0);
    }

    public int size() {
        return args.size();
    }

    public Iterator<Exp> iterator() {
        return argIterator();
    }

    public Iterator<Exp> argIterator() {
        final Iterator<IndexedEntry<Exp>> iter = args.iterator();

        return new Iterator<Exp>() {

            public boolean hasNext() {
                return iter.hasNext();
            }

            public Exp next() {
                return iter.next().value();
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }

        };
    }


}
