package com.smartsoft.csp.ast;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.smartsoft.csp.VarInfo;
import com.smartsoft.csp.varCodes.VarCode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.smartsoft.csp.ssutil.Strings.notEmpty;

public class VarInf implements VarInfo {

    private final ImmutableMap<String, String> map;
    private String series;

    public VarInf(String varInfoClob) {
        this(parseToMap(varInfoClob));
    }

    public VarInf(ImmutableMap<String, String> map) {
        this.map = map;
    }


    @Override
    public boolean isFio(String varCode) {
        String type = getType(varCode);
        return type != null && type.equalsIgnoreCase(FIO);
    }

    @Override
    public boolean isPio(String varCode) {
        String type = getType(varCode);
        return type != null && type.equalsIgnoreCase(PIO);
    }

    @Override
    public boolean isVtc(String varCode) {
        throw new UnsupportedOperationException();
    }

    public boolean isAlaCarte(String varCode) {
        return is(null, varCode, ALACARTE);
    }

    public boolean is(Set<String> context, String varCode, String attributeName   /* boolean attribute */) {
        if (context == null) {
            context = ImmutableSet.of();
        }
        String value = getAttribute(context, varCode, attributeName);
        return value != null && value.equalsIgnoreCase("true");
    }

    @Override
    public String getAttribute(Set<String> context, String varCode, String attName) {
//        System.err.println("VarInf.getAttribute[" + varCode + "-" + attName + "]");
        Context ctx = Context.parse(context);
        return getAttribute(ctx, varCode, attName);
    }

    public String getAttribute(Context context, String varName, String attName) {
        if (attName.equalsIgnoreCase("vtc")) return "true";
        ArrayList<String> copy = new ArrayList<String>(context.getOrderedList());
        while (true) {
            String key = Context.listToKey(varName, copy, attName);
            String value = map.get(key);
            if (notEmpty(value)) {
                return value;
            }
            if (copy.isEmpty()) {
                return null;
            } else {
                copy.remove(copy.size() - 1);
            }
        }
    }

    public String getType(String varCode) {
        Set<String> context = ImmutableSet.of();
        return getAttribute(context, varCode, TYPE);
    }

    public static class Context {

        private final String year;
        private final String series;
        private final String model;

        private final ImmutableList<String> orderedList;

        public Context(String year, String series, String model) {
            this.year = year;
            this.series = series;
            this.model = model;

            ImmutableList.Builder<String> b = ImmutableList.builder();
            if (notEmpty(year)) b.add(year);
            if (notEmpty(series)) b.add(series);
            if (notEmpty(model)) b.add(model);
            orderedList = b.build();
        }

        public static Context parse(Set<String> context) {
            String year = null;
            String series = null;
            String model = null;

            if (context != null) {

                for (String varCode : context) {
                    VarCode vc = new VarCode(varCode);
                    if (vc.isYear()) year = varCode;
                    if (vc.isSeries()) series = varCode;
                    if (vc.isMdl()) model = varCode;
                }
            }

            return new Context(year, series, model);
        }

        public String getYear() {
            return year;
        }

        public String getSeries() {
            return series;
        }

        public String getModel() {
            return model;
        }

        public ImmutableList<String> getOrderedList() {
            return orderedList;
        }

        public static String listToKey(String prefix, List<String> context, String attName) {
            ArrayList<String> a = new ArrayList<String>();
            a.add(prefix);
            a.addAll(context);
            a.add(attName);
            Joiner joiner = Joiner.on('.');
            return joiner.join(a);
        }

    }

    public static VarInfo parse(String varInfoClob) {
        return parse(varInfoClob, null);
    }

    public static VarInfo parse(String varInfoClob, String series) {
        if (varInfoClob == null) {
            return null;
        }
        VarInf varInf = new VarInf(varInfoClob);
        varInf.series = series;
        return varInf;
    }


    public boolean isLio(String varCode) {
        return false;
    }

    @Override
    public boolean isInvAcy(String varCode) {
        return isFio(varCode);
    }

    @Override
    public String getImpliedVarCode(Set<String> context, String featureType) {
        return series;
    }

    @Override
    public boolean isAssociated(Set<String> context, String varCode) {
        return true;
    }


    public static ImmutableMap<String, String> parseToMap(String varInfoClob) {
        checkNotNull(varInfoClob);
        varInfoClob = varInfoClob.trim();
        String[] lineArray = varInfoClob.split("\n");

        HashMap<String, String> b = new HashMap<String, String>();
        for (String line : lineArray) {
            int i = line.indexOf('=');
            String name = line.substring(0, i);
            String value = line.substring(i + 1);
            b.put(name, value);
        }
        return ImmutableMap.copyOf(b);
    }

    @Override
    public int getPrice(String[] picks) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getFeatureType(String pickCode) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isDerived(String[] context, String pickCode) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getFeaturesByType(String[] context, String type) {
        throw new UnsupportedOperationException();
    }
}
