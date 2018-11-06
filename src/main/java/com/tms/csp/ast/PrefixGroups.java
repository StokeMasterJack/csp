package com.tms.csp.ast;

import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableSet;
import com.tms.csp.util.varSets.VarSet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

public class PrefixGroups implements Iterable<PrefixGroup> {

    private final Space space;
    private ImmutableBiMap<String, PrefixGroup> _prefixMap;

    public PrefixGroups(Space space) {
        this.space = space;
    }

    public Space getSpace() {
        return space;
    }

    @Override
    public Iterator<PrefixGroup> iterator() {
        return getPrefixMap().values().iterator();
    }

    public Set<String> getPrefixes() {
        return getPrefixMap().keySet();
    }

    private ImmutableBiMap<String, PrefixGroup> getPrefixMap() {
        if (_prefixMap == null) {
            HashMap<String, PrefixGroup> map = new HashMap<String, PrefixGroup>();

            VarSet vars = space.getVars();
            Set<String> prefixes = vars.getPrefixes();

            for (String prefix : prefixes) {
                PrefixGroup value = new PrefixGroup(space, prefix);
                map.put(prefix, value);
            }

            _prefixMap = ImmutableBiMap.copyOf(map);

        }
        return _prefixMap;
    }

    public Set<PrefixGroup> getGroups() {
        ImmutableBiMap<String, PrefixGroup> prefixMap = getPrefixMap();  //Book
        ImmutableSet<PrefixGroup> values = prefixMap.values();
        Set<PrefixGroup> retVal = Collections.unmodifiableSet(values);
        return retVal;
    }

    public Set<PrefixGroup> getXorGroups() {
        HashSet<PrefixGroup> aa = new HashSet<PrefixGroup>();
        for (PrefixGroup g : getGroups()) {
            if (g.isXor()) {
                aa.add(g);
            }
        }
        return Collections.unmodifiableSet(aa);
    }

    public Set<PrefixGroup> getNonXorGroups() {
        HashSet<PrefixGroup> aa = new HashSet<PrefixGroup>();
        for (PrefixGroup g : getGroups()) {
            if (!g.isXor()) {
                aa.add(g);
            }
        }
        return Collections.unmodifiableSet(aa);
    }

    public List<PrefixGroup> getGroupsByVarCount() {
        return getGroupsByVarCount(false);
    }

    public List<PrefixGroup> getXorGroupsByVarCount() {
        return getXorGroupsByVarCount(false);
    }

    public List<PrefixGroup> getXorGroupsByVarCount(boolean desc) {
        ArrayList<PrefixGroup> aa = new ArrayList<PrefixGroup>(getXorGroups());
        sortByVarCount(aa, desc);
        return Collections.unmodifiableList(aa);
    }

    public List<PrefixGroup> getGroupsByVarCount(boolean desc) {
        ArrayList<PrefixGroup> aa = new ArrayList<PrefixGroup>(getPrefixMap().values());
        sortByVarCount(aa, desc);
        return Collections.unmodifiableList(aa);
    }

    public PrefixGroup get(Prefix prefix) {
        return getPrefixMap().get(prefix.getName());
    }

    public PrefixGroup get(String prefix) {
        return getPrefixMap().get(prefix);
    }

    public PrefixGroup getModels() {
        return get(Prefix.MDL);
    }

    public PrefixGroup getYears() {
        return get(Prefix.YR);
    }

    public PrefixGroup getXCols() {
        return get(Prefix.XCOL);
    }

    public PrefixGroup getICols() {
        return get(Prefix.ICOL);
    }

    public static void sortByVarCount(List<PrefixGroup> list, final boolean desc) {
        Collections.sort(list, new Comparator<PrefixGroup>() {
            @Override
            public int compare(PrefixGroup o1, PrefixGroup o2) {
                Integer vc1 = o1.getVarCount();
                Integer vc2 = o2.getVarCount();
                if (desc) {
                    return vc2.compareTo(vc1);
                } else {
                    return vc1.compareTo(vc2);
                }

            }
        });
    }

    public PrefixGroup getBestXorSplit() {
        List<PrefixGroup> xors = getXorGroupsByVarCount();
        if (xors.isEmpty()) return null;
        return xors.get(0);
    }


    private static Logger log = Logger.getLogger(PrefixGroups.class.getName());

}
