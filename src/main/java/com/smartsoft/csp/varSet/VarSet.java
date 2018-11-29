package com.smartsoft.csp.varSet;

import com.google.common.collect.*;
import com.smartsoft.csp.VarInfo;
import com.smartsoft.csp.ast.*;
import com.smartsoft.csp.dnnf.products.Cube;
import com.smartsoft.csp.dnnf.products.Cubes;
import com.smartsoft.csp.dnnf.products.VarPredicate;
import com.smartsoft.csp.dnnf.vars.IntFilter;
import com.smartsoft.csp.dnnf.vars.VarFilter;
import com.smartsoft.csp.parse.VarSpace;
import com.smartsoft.csp.ssutil.Strings;
import com.smartsoft.csp.util.BadVarIdException;
import com.smartsoft.csp.util.DynComplex;
import com.smartsoft.csp.util.ints.IntIterator;
import com.smartsoft.csp.util.it.IterTo;
import com.smartsoft.csp.util.it.VarTo;
import com.smartsoft.csp.util.it.VsSet;
import kotlin.jvm.functions.Function1;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.*;

public abstract class VarSet extends VarSets implements Set<Var>, PLConstants {

    @NotNull
    public final String getSimpleName() {
        return getClass().getSimpleName();
    }

    public static VarSet empty() {
        return EmptyVarSet.getInstance();
    }

    abstract public VarSpace getVarSpace();

    public Space getSpace() {
        return getVarSpace().getSpace();
    }

    public boolean containsVar(@NotNull Exp lit) {
        return containsVar(lit.getVr());
    }

    public boolean containsVar(@NotNull String varCode) {
        return VarSetContainsKt._containsVarCode(this, varCode);
    }

    public boolean notContainsVar(@NotNull String varCode) {
        return VarSetContainsKt._notContainsVarCode(this, varCode);
    }

    public boolean notContains(@NotNull Var vr) {
        return VarSetContainsKt._notContains(this, vr);
    }

    public boolean notContains(@NotNull VarSet vs) {
        return VarSetContainsKt._notContains(this, vs);
    }

    public int getVarCount() {
        return size();
    }

    public VarSetBuilder builder() {
        return getVarSpace().varSetBuilder();
    }


    public int hashCode() {
        return computeContentHash();
//        throw new UnsupportedOperationException();
//        return System.identityHashCode(this);
    }

    public String ser() {
        Ser a = new Ser();
        serialize(a);
        return a.toString();
    }

    @Override
    public String toString() {
        return ser();
    }

    public String toStringDetail() {
        return getSimpleName() + "  size:" + size() + "  " + ser();
    }

    public void serialize(Ser a) {
        Iterator<Var> it = varIter();
        while (it.hasNext()) {
            Var next = it.next();
            a.append(next.getVarCode());
            if (it.hasNext()) {
                a.argSep();
            }
        }
    }

    public static int computeVarCount(long[] words) {
        int varCount = 0;
        for (long word : words) {
            varCount += Long.bitCount(word);
        }
        return varCount;
    }

    public int getVarSetId() {
        throw new IllegalStateException();
    }

    public SingletonVarSet asSingleton() {
        throw new UnsupportedOperationException();
    }

    public VarPair asVarPair() {
        throw new UnsupportedOperationException();
    }


    public VarSetBuilder asVarSetBuilder() {
        throw new UnsupportedOperationException();
    }

    public VarSet asImmutable() {
        throw new UnsupportedOperationException();
    }

    @Override
    final public Object[] toArray() {
        throw new UnsupportedOperationException();
    }

    public ImmutableSet<Var> toImmutableSet() {
        ImmutableSet.Builder<Var> b = ImmutableSet.builder();
        for (Var var : this) {
            b.add(var);
        }
        return b.build();
    }

    @Override
    final public <T> T[] toArray(T[] a) {
        throw new UnsupportedOperationException();
    }

    final public Var[] toVarArray() {
        Var[] a = new Var[size()];
        int i = 0;
        for (Var var : varIt()) {
            a[i] = var;
            i++;
        }
        return a;
    }


    @Override
    final public boolean contains(Object o) {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + "  " + toString());
    }

    final public boolean containsVarId(int varId) {
        return VarSetContainsKt._containsVarId(this, varId);
    }

    final public boolean containsVar(Var vr) {
        return VarSetContainsKt._contains(this, vr);
    }

    final public boolean containsBothVars(Var vr1, Var vr2) {
        return VarSetContainsKt._containsBothVars(this, vr1, vr2);

    }

    final public boolean containsBothVarIds(int vr1, int vr2) {
        return VarSetContainsKt._containsBothVarIds(this, vr1, vr2);
    }

    final public boolean containsBoth(VarPair vs) {
        return VarSetContainsKt._containsBoth(this, vs);
    }

    final public boolean containsNeither(VarPair vs) {
        return VarSetContainsKt._containsNeither(this, vs);
    }


    final public boolean containsEitherVarId(int vr1, int vr2) {
        return VarSetContainsKt._containsEitherVarId(this, vr1, vr2);
    }

    final public boolean containsVars(VarPair pair) {
        return VarSetContainsKt._contains(this, pair);
    }

    final public boolean containsVar(Lit lit) {
        return VarSetContainsKt._containsVar(this, lit);
    }


    /**
     * Returns the smallest varId in this set.
     * <p>
     * Throws a NoSuchElementException if this set is empty.
     */
    public abstract int minVrId() throws NoSuchElementException;

    public Var getMinVar() throws NoSuchElementException {
        return getSpace().getVar(minVrId());
    }

    public Var getMaxVar() throws NoSuchElementException {
        return getSpace().getVar(maxVrId());
    }

    /**
     * Returns the largest varId in this set.
     * <p>
     * Throws a NoSuchElementException if this set is empty.
     */
    public abstract int maxVrId() throws NoSuchElementException;

    /**
     * Returns the smallest varId formula this set that
     * is greater than or equal to varId.  If this set is empty or varId is greater than this.max(),
     * NoSuchElementException is thrown.
     *
     * @return {j: this.ints | j >= i && no k: this.ints - j | k < j && k >= i}
     * @throws java.util.NoSuchElementException no this.ints || i > this.max()
     */
    public int minVrId(int varId) throws NoSuchElementException {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the largest varId formula this set that
     * is smaller than or equal to varId.  If this is empty or varId is less than this.min(),
     * NoSuchElementException is thrown.
     *
     * @throws java.util.NoSuchElementException no this.ints || i < this.min()
     */
    public int maxVrId(int varId) throws NoSuchElementException {
        throw new UnsupportedOperationException();
    }

    public abstract IntIterator intIterator();

    public Iterable<Var> varIt() {
        return new Iterable<Var>() {
            @Override
            public Iterator<Var> iterator() {
                return varIter();
            }
        };
    }

    public Iterator<Var> varIter() {
        return new VsIterator<Var>(this, varConverter());
    }

    public Iterator<Var> varIter(VarFilter filter) {
        return new FilteredVarIterator(filter);
    }

    public boolean eqVarSet(VarSet o) {
        if (this == o) return true;
        return VarSetK.eqNullSafe(this, o);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;

        VarSet that = (VarSet) o;
        assert this.getSpace() == that.getSpace();

        return eqVarSet(that);
    }


    public static int size(VarSet vars) {
        if (vars == null) return 0;
        return vars.size();
    }

    public SortedSet<String> toVarCodeSetSorted() {
        Set<String> elements = toVarCodeSet();
        return ImmutableSortedSet.copyOf(elements);
    }

    public List<String> getCodes() {
        Set<String> elements = toVarCodeSet();
        return ImmutableList.copyOf(elements);
    }

    public VarSet minus(int v1, int v2) {
        throw new UnsupportedOperationException();
    }

    public abstract boolean containsPrefix(String prefix);

    final public boolean containsMsrpVars() {
        return containsPrefix(MSRP_PREFIX);
    }

    final public boolean containsDealerVars() {
        return containsPrefix(DLR_PREFIX);
    }

    public VarSet minusInt32Vars() {
        VarSet int32Vars = getSpace().getInt32Vars();
        return minus(int32Vars);
    }

    public void println() {
        Set<String> prefixes = getPrefixes();
        for (String prefix : prefixes) {
            System.err.println("prefix: " + prefix);
            VarSet pVars = filter(prefix);
            for (Var pVar : pVars) {
                System.err.println("  " + pVar);
            }
        }
    }

    public Set<Lit> getTFLitSet() {
        ImmutableSet.Builder<Lit> b = ImmutableSet.builder();
        for (Var var : varIt()) {
            b.add(var.mkPosLit());
            b.add(var.mkNegLit());
        }
        return b.build();
    }


    public VarSet getRandomSubset(int varCount) {
        Random r = getSpace().random;
        VarSetBuilder b = getSpace().newMutableVarSet();
        for (int i = 0; i < varCount; i++) {
            int index = r.nextInt(size());
            Var var = this.get(index);
            b.add(var);
        }
        return b.build();
    }

    public void addVarCode(String varCode) {
        Var var = getSpace().getVar(varCode);
        addVar(var);
    }

    @NotNull
    public Boolean containsVarCode(@NotNull String varCode) {
        return VarSetContainsKt._containsVarCode(this, varCode);
    }

    public class FilteredVarIterator extends UnmodifiableIterator<Var> {

        private final IntIterator it;
        private Var next;
        private VarFilter filter;

        public FilteredVarIterator(VarFilter filter) {
            this.it = intIterator();
            this.filter = filter;
        }

        private Var computeNext() {
            if (next != null) {
                return next;
            }
            while (it.hasNext()) {
                int nn = it.next();
                Var var = getSpace().getVar(nn);
                if (filter.accept(var)) {
                    return var;
                }
            }
            return null;
        }

        @Override
        public boolean hasNext() {
            if (next != null) {
                return true;
            }
            next = computeNext();
            if (next != null) {
                return true;
            } else {
                return false;
            }
        }

        @Override
        public Var next() {
            assert next != null;
            Var retVal = next;
            this.next = null;
            return retVal;
        }

        public boolean isLast() {
            return !hasNext();
        }

    }

    @NotNull
    public Iterable<String> varCodeIt() {
        return new Iterable<String>() {
            @NotNull
            @Override
            public Iterator<String> iterator() {
                return varCodeIter();
            }
        };
    }


    @NotNull
    public Iterator<String> varCodeIter() {
        Iterator<Var> iter1 = varIter();
        Function1<Var, String> ff = VarTo.getVarCode();
        return IterTo.iter(iter1, ff);
    }

    public Iterator<Lit> litIterator(final VarPredicate p) {
        return new VsIterator<Lit>(this, litConverter(p));
    }

    public Iterator<Lit> litIterator(final VarSet trueVars) {
        return litIterator(Cubes.simpleVarPredicate(trueVars));
    }

    public Iterator<Lit> litIterator(final Var trueVar) {
        return litIterator(Cubes.simpleVarPredicate(trueVar));
    }

    public Iterator<Lit> litIterator(final boolean sign) {
        return litIterator(Cubes.constantVarPredicate(sign));
    }

    public Iterator<Exp> litExpIterator(VarPredicate p) {
        return new VsIterator<Exp>(this, litExpConverter(p));
    }

    public Iterable<Exp> litExpIt(VarSet trueVars) {
        return (Iterable<Exp>) () -> litExpIterator(trueVars);
    }

    public Iterator<Exp> litExpIterator(VarSet trueVars) {
        return new VsIterator<Exp>(this, litExpConverter(Cubes.simpleVarPredicate(trueVars)));
    }

    public Iterator<Exp> litExpIterator(Var trueVar) {
        return new VsIterator<Exp>(this, litExpConverter(Cubes.simpleVarPredicate(trueVar)));
    }

    public Iterator<Exp> litExpIterator(boolean sign) {
        return new VsIterator<Exp>(this, litExpConverter(Cubes.constantVarPredicate(sign)));
    }

    public Converter<Integer> varIdConverter() {
        return getSpace().getVarSpace().varIdConverter();
    }

    public Converter<Var> varConverter() {
        return getSpace().getVarSpace().varConverter();
    }

    public Converter<String> varCodeConverter() {
        return getSpace().getVarSpace().varCodeConverter();
    }

    public Converter<Lit> litConverter(VarPredicate p) {
        return getSpace().getVarSpace().litConverter(p);
    }

    public Converter<Exp> litExpConverter(VarPredicate p) {
        return getSpace().getVarSpace().litExpConverter(p);
    }


    public Set<Var> toVarSet() {
        return new VsSet<Var>(this, varConverter());
    }

    public Set<String> toVarCodeSet() {
        if (isEmpty()) return ImmutableSet.of();
        else return new VsSet<String>(this, varCodeConverter());
    }


    public Set<Integer> toVarIdSet() {
        return new VsSet<Integer>(this, varIdConverter());
    }

    public Set<Lit> toLitSet(VarPredicate p) {
        return new VsSet<Lit>(this, litConverter(p));
    }

    public Set<Exp> toLitExpSet(VarPredicate p) {
        return new VsSet<Exp>(this, litExpConverter(p));
    }

    public List<Var> varList() {
        return ImmutableList.copyOf(this);
    }

    public List<Var> sortByVarCode() {
        return varList();
//        List<Var> aa = varList();
//        Var.sortByVarCode(aa);
//        return Collections.unmodifiableList(aa);
    }

    public Iterable<Exp> expIt(boolean sign) {
        return () -> litExpIterator(sign);
    }

    @Override
    @Nonnull
    public Iterator<Var> iterator() {
        final IntIterator ii = intIterator();
        return new UnmodifiableIterator<Var>() {
            @Override
            public boolean hasNext() {
                return ii.hasNext();
            }

            @Override
            public Var next() {
                int next = ii.next();


                try {
                    return getSpace().getVar(next);
                } catch (BadVarIdException e) {
                    throw new RuntimeException(e);
                }
            }

        };
    }


    public Iterable<Integer> ids() {
        return varIdIt();
    }

    public Iterable<Integer> varIdIt() {
        return new Iterable<Integer>() {
            @Override
            public Iterator<Integer> iterator() {
                return varIdIterator();
            }
        };
    }

    public Iterator<Integer> varIdIterator() {
        return new VsIterator<Integer>(this, varIdConverter());
    }

    public IntIterator intIterator(int from, int to) {
        throw new UnsupportedOperationException();
    }

    public abstract int size();


    public abstract boolean isEmpty();

    public boolean containsAllVars(VarSet that) {
        return VarSetContainsKt._contains(this, that);
    }

    public boolean containsVarSet(VarSet that) {
        return VarSetContainsKt._contains(this, that);
    }


//    abstract public boolean containsAllBitSet(VarNSet other);

    public boolean containsVarIds(int varId1, int varId2) {
        return containsVarId(varId1) && containsVarId(varId2);
    }

    public VarSet mutableCopy() {
        return copyToVarSetBuilder();
    }

    public VarSet copy() {
        return VarSetK.copy(this);
    }

    public boolean anyVarOverlap(Cube cube) {
        return anyVarOverlap(cube.getVars());
    }

    final public boolean anyVarOverlap(Exp e) {
        if (e.isConstant()) return false;
        if (e.isLit()) {
            return containsVarId(e.getVarId());
        }
        VarSet vars = e.getVars();
        return vars.anyVarOverlap(this);
    }


//    public abstract boolean anyIntersection(VarSetBuilder that);

    final public VarSet minus(Var varToRemove) {
        return VarSetMinusKt.minus(this, varToRemove);
    }

    final public VarSet minus(int varIdToRemove) {
        return VarSetMinusKt.minus(this, varIdToRemove);
    }

    final public VarSet minus(String varCode) {
        Var vr = getSpace().getVar(varCode);
        return minus(vr);
    }

    final public VarSet minus(VarSet varsToRemove) {
        return VarSetMinusKt.minus(this, varsToRemove);
    }


    public VarSetBuilder copyToVarSetBuilder() {
        return VarSetK.copyToVarSetBuilder(this);
    }

    public VarSetBuilder copyToVarSetBuilder(VarSet vs) {
        return VarSetK.copyToVarSetBuilder(this, Adjust.add(vs));
    }

    public VarSetBuilder copyToVarSetBuilder(Var vr) {
        return VarSetK.copyToVarSetBuilder(this, Adjust.add(vr));
    }

    public VarSetBuilder copyToVarSetBuilder(Adjust adjust) {
        return VarSetK.copyToVarSetBuilder(this, adjust);
    }

//    public VarSet plus(String varCode) {
//        Var vr = getSpace().getVar(varCode);
//        return plus(vr);
//    }

    final public VarSet plus(Var vr) {
        return VarSetPlusKt.plus(this, vr);
    }

    final public VarSet plus(VarSet varSet) {
        return VarSetPlusKt.plus(this, varSet);
    }


//    public static VarSet union(VarSet... that) {
//        Space space = that[0].getSpace();
//        return plus(space, that);
//    }

    public static VarSet plus(Space space, VarSet... that) {
        return VarSetPlusKt.plusVarSets(space, that);
    }


    public int compareTo(VarSet that) {
        return VarSetComparatorFast.compare(this, that);
    }


    public int indexOf(int varId) {
        throw new UnsupportedOperationException(getClass().getName());
    }

    final public boolean anyVarOverlap(VarSet s) {
        return VarSetOverlapKt._anyOverlap(this, s);
    }

    final public VarSet overlap(VarSet that) {
        return VarSetOverlapKt._overlap(this, that);
    }

    final public boolean isVarDisjoint(VarSet s) {
        return !anyVarOverlap(s);
    }

    final public boolean isVarDisjoint(DynComplex s) {
        return !anyVarOverlap(s.getVars());
    }

    final public boolean isVarDisjoint(Cube cube) {
        return !anyVarOverlap(cube.getVars());
    }

    final public boolean isVarDisjoint(Exp e) {
        return !anyVarOverlap(e);
    }


    public VarSet immutable() {
        throw new UnsupportedOperationException(getClass().getName());
    }

    public boolean isVarPair() {
        return false;
    }

    public boolean isVarNSet() {
        return false;
    }

    public boolean isVarSetBuilder() {
        return false;
    }

    public boolean isSingleton() {
        return false;
    }


    public VarSet mkEmptyVarSet() {
        return getVarSpace().mkEmptyVarSet();
    }

    public int computeContentHash() {
        throw new UnsupportedOperationException();
    }


    public boolean checkMutable() {
        assert isMutable();
        return true;
    }

    public boolean isMutable() {
        return false;
    }

    public Set<String> getPrefixes() {
        ImmutableSet.Builder<String> b = ImmutableSet.builder();
        for (Var var : this) {
            String prefix = var.getPrefix();
            assert prefix != null;
            b.add(prefix);
            return b.build();
        }
        return b.build();
    }

    public Set<String> getXorPrefixes() {
        ImmutableSet.Builder<String> b = ImmutableSet.builder();
        for (Var var : this) {
            if (var.isXorChild()) {
                String prefix = var.getPrefix();
                assert prefix != null;
                b.add(prefix);
            }
        }
        return b.build();
    }

    public Set<String> getNonXorPrefixes() {
        ImmutableSet.Builder<String> b = ImmutableSet.builder();
        for (Var var : this) {
            if (!var.isXorChild()) {
                String prefix = var.getPrefix();
                assert prefix != null;
                b.add(prefix);
            }
        }
        return b.build();
    }

    public String getBestXorPrefix() {
        XorPrefixMap map = new XorPrefixMap(this);
        return map.getBestXor().getPrefix();
    }

    public VarSet filter(Prefix prefix) {
        return filter(prefix.getName());
    }

    public VarSet filter(String prefix) {
        VarFilter f = VarFilter.prefix(prefix);
        return filter(f);
    }

    public VarSet filterNonDerived(VarInfo varMetaInstance, List<Lit> pics) {
        VarFilter f = VarFilter.noDerived(varMetaInstance, pics);
        return filter(f);
    }

    public VarSet filter(VarFilter filter) {
        VarSetBuilder b = getSpace().newMutableVarSet();
        for (Var var : this) {
            if (filter.accept(var)) {
                b.addVar(var);
            }
        }
        return b.build();
    }

    public VarSet filter(EnumSet<Prefix> filter) {
        VarFilter f = VarFilter.prefixes(filter);
        return filter(f);
    }


    final public Var getFirstVar() {
        int varId = getFirstVarId();
        return getSpace().getVar(varId);
    }

    final public Var getSecondVar() {
        int varId = getSecondVarId();
        return getSpace().getVar(varId);
    }

    final public int getFirstVarId() {
        return minVrId();
    }

    public int getSecondVarId() {
        IntIterator it = intIterator();
        it.next();
        return it.next();
    }

    @Override
    final public boolean containsAll(Collection<?> c) {
        if (c instanceof VarSet) {
            VarSet vs = (VarSet) c;
            return containsAllVars(vs);
        } else {
            for (Object o : c) {
                Var var = (Var) o;
                if (!this.containsVar(var)) {
                    return false;
                }
            }
            return true;
        }
    }


    public abstract int getVarId(int index) throws IndexOutOfBoundsException;

    final public Var getVar(int index) throws IndexOutOfBoundsException {
        int varId = getVarId(index);
        return getSpace().getVar(varId);
    }

    final public Var get(int index) {
        return getVar(index);
    }

    final public Var getArg1() {
        return getFirstVar();
    }

    final public Var getArg2() {
        return getSecondVar();
    }

    final public Var getFirstVarForPrefix(String prefix) {
        for (Var var : this) {
            if (var.is(prefix)) return var;
        }
        return null;
    }


    //mutatations


    public boolean addVarSet(@Nonnull VarSet vs) {
        throw new UnsupportedOperationException();
    }


    public boolean addVar(Var var) {
        throw new UnsupportedOperationException();
    }

    public void addVarIdQuiet(int varId) {
        throw new UnsupportedOperationException();
    }

    public boolean addVarId(int varId) {
        throw new UnsupportedOperationException(getClass().getName());
    }

    final public boolean removeVar(String varCode) {
        Var var = getSpace().getVar(varCode);
        return removeVar(var);
    }

    public boolean removeVar(Var vr) {
        int varId = vr.getVarId();
        return removeVarId(varId);
    }

    public boolean removeVarIdDead(int varId) {
        throw new UnsupportedOperationException();
    }

    public boolean removeVarId(int varId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(Collection<? extends Var> vars) {
        throw new UnsupportedOperationException();
    }


    @Override
    public boolean add(Var var) {
        throw new UnsupportedOperationException(Strings.getSimpleName(this));
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    final public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    final public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    public boolean retainAllVars(VarSet other) {
        throw new UnsupportedOperationException();
    }

    public boolean removeAllVars(VarSet other) {
        throw new UnsupportedOperationException();
    }

    public void clear() {
        throw new UnsupportedOperationException();
    }

    public static class FilteringIntIterator implements IntIterator {

        private final IntIterator it;
        private final IntFilter filter;

        private int next;

        public FilteringIntIterator(IntIterator it, IntFilter filter) {
            this.it = it;
            this.filter = filter;
            next = computeNext();
        }

        private int computeNext() {
            while (it.hasNext()) {
                int maybeNext = it.next();
                if (filter.accept(maybeNext)) {
                    return maybeNext;
                }
            }
            return -1;
        }

        public boolean hasNext() {
            return next != -1;
        }

        public int next() {
            int tmp = next;
            next = computeNext();
            return tmp;
        }

    }


    public static class PrefixInfo {

        private final String prefix;
        private int count;

        public PrefixInfo(String prefix) {
            this.prefix = prefix;
        }

        public void increment() {
            count++;
        }

        public int getCount() {
            return count;
        }

        public PrefixInfo chooseBest(PrefixInfo currentBest) {
            if (currentBest == null || count > currentBest.count) {
                return this;
            } else {
                return currentBest;
            }
        }

        public String getPrefix() {
            return prefix;
        }
    }

    public static class XorPrefixMap {

        private PrefixInfo bestXor;
        private final Map<String, PrefixInfo> map;

        public XorPrefixMap(VarSet vars) {
            map = Maps.newHashMap();
            for (Var var : vars) {
                add(var);
            }
        }

        private void add(Var var) {
            if (!var.isXorChild()) {
                return;
            }
            String prefix = var.getPrefix();
            PrefixInfo xor = map.get(prefix);
            if (xor == null) {
                xor = new PrefixInfo(prefix);
                map.put(prefix, xor);
            }
            xor.increment();

            bestXor = xor.chooseBest(bestXor);
        }

        public PrefixInfo getBestXor() {
            return bestXor;
        }
    }

    public Var getModel() {
        return getFirstVarForPrefix(MDL_PREFIX);
    }

    public Var getYear() {
        return getFirstVarForPrefix(YR_PREFIX);
    }

    public Var getXCol() {
        return getFirstVarForPrefix(XCOL_PREFIX);
    }

    public Var getICol() {
        return getFirstVarForPrefix(ICOL_PREFIX);
    }

    public VarSet getAcyVars() {
        return filter(ACY_PREFIX);
    }

    abstract public boolean recomputeSize();

    public VarSet refreshSize() {
        recomputeSize();
        return this;
    }

    public Set<VarPair> getAllPairs() {
        return VarSetK.getAllPairs(this);
    }

    public void assertSer(String ser) {
        String ser1 = toString();
        if (!ser.equals(ser1)) {
            System.err.println("Expected: " + ser);
            System.err.println("Actual: " + ser1);
            System.err.println("Actual: " + getSimpleName());
            throw new AssertionError("Expected: " + ser + "   actual: " + ser1);
        }
    }

}
