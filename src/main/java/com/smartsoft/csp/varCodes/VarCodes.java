package com.smartsoft.csp.varCodes;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import com.smartsoft.csp.ast.Prefix;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class VarCodes {

    private final ImmutableSet<VarCode> varCodes;
    private ImmutableSet<VarCode> coreXorVars;
    private ImmutableSet<VarCode> invAcyVars;

    private ImmutableSet<String> prefixes;

    private ImmutableMap<String, IXor> xorMap;
    private ImmutableList<IXor> coreXors;

    public VarCodes(Collection<VarCode> varCodes) {
        this.varCodes = ImmutableSet.copyOf(varCodes);
    }

    public VarCodes(Iterable<String> varCodes) {
        this(parseVarCodes(varCodes));
    }

    public VarCodes(VarCodes fVarCodes, VarCodes iVarCodes) {
        ImmutableSet.Builder<VarCode> b = ImmutableSet.builder();
        b.addAll(fVarCodes.getVarCodes());
        if (iVarCodes != null) {
            b.addAll(iVarCodes.getVarCodes());
        }
        varCodes = b.build();
    }


    public static ImmutableList<VarCode> parseVarCodes(Iterable<String> sVarCodes) {
        ImmutableList.Builder<VarCode> b = ImmutableList.builder();
        for (String sVarCode : sVarCodes) {
            VarCode vc = new VarCode(sVarCode);
            b.add(vc);
        }
        return b.build();
    }

    public ImmutableSet<VarCode> getCoreXorVars() {
        if (coreXorVars == null) {
            ImmutableSet.Builder<VarCode> b = ImmutableSet.builder();
            for (VarCode varCode : varCodes) {
                if (varCode.isCoreXor()) {
                    b.add(varCode);
                }
            }
            coreXorVars = b.build();
        }

        return coreXorVars;
    }

    public ImmutableSet<VarCode> getInvAcyVars() {
        if (invAcyVars == null) {
            ImmutableSet.Builder<VarCode> b = ImmutableSet.builder();
            for (VarCode varCode : varCodes) {
                if (varCode.isAcy()) {
                    b.add(varCode);
                }
            }
            invAcyVars = b.build();
        }

        return invAcyVars;
    }

    public ImmutableSet<VarCode> getVarCodes() {
        return varCodes;
    }

    public ImmutableList<VarCode> getListSortedByVarCode() {
        ArrayList<VarCode> aa = new ArrayList<VarCode>(varCodes);
        Collections.sort(aa, new Comparator<VarCode>() {
            @Override
            public int compare(VarCode o1, VarCode o2) {
                String varCode1 = o1.getVarCode();
                String varCode2 = o2.getVarCode();
                return varCode1.compareTo(varCode2);
            }
        });

        return ImmutableList.copyOf(aa);
    }

    public static ImmutableList<String> toStringList(List<VarCode> varCodeList) {
        ImmutableList.Builder<String> b = ImmutableList.builder();
        for (VarCode vc : varCodeList) {
            b.add(vc.toString());
        }
        return b.build();
    }

    public ImmutableList<VarCode> toListSortedByCoreXorIndex() {
        ImmutableSortedSet<VarCode> ss = ImmutableSortedSet.copyOf(varCodes);
        return ImmutableList.copyOf(ss);
    }

    public ImmutableSet<String> getPrefixes() {
        if (prefixes == null) {
            ImmutableSet.Builder<String> b = ImmutableSet.builder();

            for (Prefix prefix : Prefix.getAll()) {
                String code = prefix.getName();
                b.add(code);
            }

            for (VarCode varCode : varCodes) {
                String prefixCode = varCode.getPrefix();
                b.add(prefixCode);
            }

            prefixes = b.build();
        }
        return prefixes;
    }

    public ImmutableSet<VarCode> getVarCodes(Prefix prefix) {
        return getVarCodes(prefix.name());
    }

    public ImmutableSet<VarCode> getVarCodes(String prefix) {
        ImmutableSet.Builder<VarCode> b = ImmutableSet.builder();
        for (VarCode varCode : varCodes) {
            if (varCode.is(prefix)) {
                b.add(varCode);
            }
        }
        return b.build();
    }

    public Map<String, IXor> getXorMap() {
        if (xorMap == null) {
            ImmutableMap.Builder<String, IXor> b = ImmutableMap.builder();
            ImmutableSet<String> prefixes = getPrefixes();
            for (String prefix : prefixes) {
                ImmutableSet<VarCode> varCodes = getVarCodes(prefix);
                IXor xor = new IXor(prefix, varCodes);
                b.put(prefix, xor);
            }
            xorMap = b.build();
        }
        return xorMap;
    }

    public IXor getXor(String prefix) {
        Map<String, IXor> xorMap = getXorMap();
        return xorMap.get(prefix);
    }

    public ImmutableList<IXor> getCoreXors() {
        if (coreXors == null) {
            ImmutableList.Builder<IXor> b = ImmutableList.builder();
            EnumSet<Prefix> coreXorPrefixes = Prefix.getCore();
            for (Prefix coreXorPrefix : coreXorPrefixes) {
                IXor xor = getXor(coreXorPrefix.name());
                if (xor != null) {
                    b.add(xor);
                }
            }
            coreXors = b.build();
        }
        return coreXors;
    }

    public void forEachCoreProduct(CoreProductHandler ph) {
        CoreProduct p = new CoreProduct();
        ImmutableList<IXor> coreXors = getCoreXors();
        for (VarCode year : coreXors.get(0).getChildVars()) {
            p.year = year;
            for (VarCode model : coreXors.get(1).getChildVars()) {
                p.model = model;
                for (VarCode xcol : coreXors.get(2).getChildVars()) {
                    p.xcol = xcol;
                    for (VarCode icol : coreXors.get(3).getChildVars()) {
                        p.icol = icol;
                        ph.onCoreProduct(p);
                    }
                }
            }
        }
    }

    public IXor getYearXor() {
        ImmutableList<IXor> coreXors = getCoreXors();
        return coreXors.get(0);
    }

    public ImmutableSet<VarCode> getModels() {
        return getVarCodes(Prefix.MDL);
    }

    public ImmutableSet<VarCode> getYears() {
        return getVarCodes(Prefix.YR);
    }

    public ImmutableSet<VarCode> getXCols() {
        return getVarCodes(Prefix.XCOL);
    }

    public ImmutableSet<VarCode> getICols() {
        return getVarCodes(Prefix.ICOL);
    }

    public int getVarCount() {
        return varCodes.size();
    }

    public Set<String> getVarCodeSet() {
        ImmutableSet.Builder<String> b = ImmutableSet.builder();
        for (VarCode varCode : varCodes) {
            b.add(varCode.getVarCode());
        }
        return b.build();
    }
}
