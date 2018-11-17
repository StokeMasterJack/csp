package com.tms.csp.varCodes;

import com.tms.csp.ast.Mods;
import com.tms.csp.ast.Prefix;
import com.tms.csp.ast.Space;
import com.tms.csp.ast.VarMeta;
import com.tms.csp.ssutil.Strings;

import javax.annotation.Nonnull;
import java.util.EnumSet;

import static com.tms.csp.ssutil.Strings.checkNotEmpty;
import static com.tms.csp.ssutil.Strings.isEmpty;

public class VarCode extends Mods implements Comparable<VarCode>, IVar {

    @Nonnull
    public final String prefix;    //no prefix _complexVars use empty string

    @Nonnull
    public final String localName;

    public VarCode(String raw) {
        raw = raw.trim();
        int split = split(raw);
        if (split == -1) {
            this.prefix = "";
            this.localName = raw.trim();
        } else {
            this.prefix = fixTinyPrefix(raw.substring(0, split));
            this.localName = raw.substring(split + 1);
        }


    }

    @Nonnull
    public static String getPrefix(String varCode) {
        int i = varCode.indexOf('_');
        if (i == -1) return "";
        return varCode.substring(0, i);
    }

    public VarCode(String prefix, int localIndex) {
        checkNotEmpty(prefix);
        this.prefix = prefix.trim();
        this.localName = Strings.lpad(localIndex + "", '0', 2);
    }

    public VarCode(String prefix, String localName) {
        checkNotEmpty(prefix);
        this.prefix = prefix.trim();
        this.localName = localName;
    }

    public static String fixTinyPrefix(String shortPrefix) {
        String p = Prefix.expandTiny(shortPrefix);
        if (p == null) return "";
        return p.trim();
    }

    public static String tiny(String largePrefix) {
        Prefix p = Prefix.largeToTiny(largePrefix);
        if (p == null) return "";
        return p.getTiny();
    }

    public static Boolean initXor(String prefix) {
        return Prefix.isXor(prefix);
    }

    public static int split(String raw) {
        int split = raw.indexOf(UNDERSCORE);
        if (split == -1) {
            split = raw.indexOf(COLON);
        }
        return split;
    }

    public static String fixPrefix(String prefix) throws IllegalArgumentException {
        if (prefix == null) {
            throw new IllegalArgumentException("Null prefix");
        }
        prefix = prefix.trim();


        if (prefix.isEmpty()) {
            throw new IllegalArgumentException("Empty prefix");
        }

        return prefix;
    }

    public static String fixSuffix(String suffix) throws IllegalArgumentException {
        if (suffix == null) {
            throw new IllegalArgumentException("Null suffix");
        }
        suffix = suffix.trim();

        if (suffix.isEmpty()) {
            throw new IllegalArgumentException("Empty suffix");
        }

        return suffix;
    }

//    public Prefix getPref() {
//        return prefix;
//    }

    @Nonnull
    @Override
    public String getPrefix() {
        assert prefix != null;
        return prefix;
    }

    public String getLocalName() {
        return localName;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VarCode that = (VarCode) o;
        return prefix.equals(that.prefix) && localName.equals(that.localName);
    }

    @Override
    public int hashCode() {
        int result = prefix.hashCode();
        result = 31 * result + localName.hashCode();
        return result;
    }

    @Override
    public int compareTo(@Nonnull VarCode that) {

        String p1 = prefix;
        String p2 = that.prefix;

        int pc = p1.compareTo(p2);

        if (pc == 0) {
            return localName.compareTo(that.localName);
        } else {
            return pc;
        }

    }

    @Override
    public String toString() {
        return getVarCode();
    }

    public boolean isAny(String... prefixes) {
        if (prefixes == null && isEmpty(this.prefix)) {
            return true;
        }
        for (String prefix : prefixes) {
            if (is(prefix)) {
                return true;
            }
        }
        return false;
    }

    public boolean isAny(Iterable<String> prefixes) {
        if (prefixes == null && isEmpty(this.prefix)) {
            return true;
        }
        for (String prefix : prefixes) {
            if (is(prefix)) {
                return true;
            }
        }
        return false;
    }

    public boolean is(EnumSet<Prefix> enumSet) {
        String prefix = getPrefix();
        return Prefix.is(enumSet, prefix);
    }

    public boolean is(Prefix prefix) {
        if (prefix == null) return false;
        return is(prefix.name());
    }

    public boolean is(String prefix) {
        if (isEmpty(prefix)) {
            return isEmpty(this.prefix);
        } else {
            return prefix.equalsIgnoreCase(this.prefix);
        }
    }

    @Override
    public boolean isYear() {
        return isYr();
    }

    public boolean isSeries() {
        return VarMeta.isSeriesPrefix(prefix);
    }

    public boolean isYr() {
        return VarMeta.isYearPrefix(prefix);
    }

    public boolean isMdl() {
        return VarMeta.isModelPrefix(prefix);
    }


    public boolean isXCol() {
        return VarMeta.isXColPrefix(prefix);
    }

    public boolean isICol() {
        return VarMeta.isIColPrefix(prefix);
    }

    public boolean isAcy() {
        return VarMeta.isAcyPrefix(prefix);
    }

    @Override
    public boolean isCoreXor() {
        return Prefix.isCore(prefix);
    }

    @Override
    public String getVarCode() {
        if (Prefix.isEmpty(prefix)) {
            return getLocalName();
        }
        String retVal = prefix + "_" + getLocalName();
        if (retVal.startsWith("_")) {
            throw new IllegalStateException();
        }
        return retVal;
    }

    public String getVarCodeTiny() {
        if (Prefix.isEmpty(prefix)) {
            return getLocalName();
        }

        String tinyPrefix = tiny(prefix);
        String retVal = tinyPrefix + ":" + getLocalName();
        if (retVal.startsWith(":")) {
            throw new IllegalStateException();
        }
        return retVal;
    }

    @Override
    public boolean isInv() {
        return Prefix.isInv(prefix);
    }

    @Override
    public boolean isXorChild() {
        return Prefix.isXor(prefix);
    }

    @Override
    public Prefix getPrefix2() {
        return Prefix.get(prefix);
    }


    @Override
    public VarCode toVarCode() {
        return this;
    }


    public static boolean hasUnderscore(String token) {
        return token.indexOf(UNDERSCORE) != -1;
    }

    public static boolean noPrefix(String token) {
        return !hasUnderscore(token);
    }

    public boolean isInt32() {
        return Space.isInt32(prefix);
    }

    public boolean isQty() {
        return prefix.equals(QTY_PREFIX);
    }

    public boolean isDlr() {
        return prefix.equals(DLR_PREFIX);
    }

    public boolean isMsrp() {
        return prefix.equals(MSRP_PREFIX);
    }

    public boolean hasPrefix() {
        return !prefix.equals("");
    }
}
