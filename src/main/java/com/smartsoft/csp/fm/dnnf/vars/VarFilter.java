package com.smartsoft.csp.fm.dnnf.vars;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.smartsoft.csp.VarInfo;
import com.smartsoft.csp.ast.Lit;
import com.smartsoft.csp.ast.Prefix;
import com.smartsoft.csp.ast.Space;
import com.smartsoft.csp.ast.Var;
import com.smartsoft.csp.ast.VarConstants;
import com.smartsoft.csp.ast.VarMeta;
import com.smartsoft.csp.fm.dnnf.products.Cube;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class VarFilter implements Predicate<Var>, VarConstants {


    public VarFilter(VarInfo varMeta) {
        // TODO Auto-generated constructor stub
    }

    public VarFilter() {
        // TODO Auto-generated constructor stub
    }

    abstract public boolean accept(Var var);

    @Override
    public boolean apply(@Nullable Var var) {
        return accept(var);
    }

    public final static VarFilter ALWAYS_TRUE = new VarFilter() {
        @Override
        public boolean accept(Var var) {
            return true;
        }

    };

    public static VarFilter prefixes(final EnumSet<Prefix> prefixes) {
        return new VarFilter() {
            @Override
            public boolean accept(Var var) {
                return var.is(prefixes);
            }
        };
    }

    public static VarFilter prefixes(final String... prefixes) {
        return new VarFilter() {
            @Override
            public boolean accept(Var var) {
                return var.isAny(prefixes);
            }
        };
    }

    public static VarFilter prefixes(final Iterable<String> prefixes) {
        return new VarFilter() {
            @Override
            public boolean accept(Var var) {
                return var.isAny(prefixes);
            }
        };
    }

    public static VarFilter prefix(final Prefix prefix) {
        return new VarFilter() {
            @Override
            public boolean accept(Var var) {
                return var.is(prefix.name());
            }
        };
    }

    public static VarFilter prefix(final String prefix) {
        return new VarFilter() {
            @Override
            public boolean accept(Var var) {
                return var.is(prefix);
            }
        };
    }

    public static VarFilter notIn(final Collection<Var> vars) {
        return new NotInFilter(vars);
    }

    public static VarFilter in(final Collection<Var> vars) {
        return new InFilter(vars);
    }

    public void print() {

    }

    public static class InFilter extends VarFilter {

        private final Collection<Var> vars;

        public InFilter(Collection<Var> outVars) {
            this.vars = outVars;
        }

        @Override
        public boolean accept(Var var) {
            return vars.contains(var);
        }

        @Override
        public String toString() {
            return "[InFilter]" + vars.toString();
        }
    }

    public static class NotInFilter extends VarFilter {

        private final Collection<Var> vars;

        public NotInFilter(Collection<Var> outVars) {
            this.vars = outVars;
        }

        @Override
        public boolean accept(Var var) {
            return !vars.contains(var);
        }

        @Override
        public String toString() {
            return "[NotInFilter]" + vars.toString();
        }
    }


    public static VarFilter alwaysTrue() {
        return ALWAYS_TRUE;
    }

    public Set<Var> filterVarSet(Set<Var> varsIn) {
        return Sets.filter(varsIn, this);
    }

    public List<Var> filterVarList(Iterable<Var> varsIn) {
        ImmutableList.Builder<Var> b = ImmutableList.builder();
        for (Var var : varsIn) {
            if (accept(var)) {
                b.add(var);
            }
        }
        return b.build();
    }

    public Set<Lit> filterLitSet(Iterable<Lit> litsIn) {
        ImmutableSet.Builder<Lit> b = ImmutableSet.builder();
        for (Lit lit : litsIn) {
            if (accept(lit.getVr())) {
                b.add(lit);
            }
        }
        return b.build();
    }

    public static Set<Var> trueVars(Iterable<Lit> litsIn, VarFilter filter) {
        ImmutableSet.Builder<Var> b = ImmutableSet.builder();
        for (Lit lit : litsIn) {
            if (lit.isPos() && filter.accept(lit.getVr())) {
                b.add(lit.getVr());
            }
        }
        return b.build();
    }

    public static Set<Var> falseVars(Iterable<Lit> litsIn, VarFilter filter) {
        ImmutableSet.Builder<Var> b = ImmutableSet.builder();
        for (Lit lit : litsIn) {
            if (lit.isNeg() && filter.accept(lit.getVr())) {
                b.add(lit.getVr());
            }
        }
        return b.build();
    }

    public List<Lit> filterLitList(Iterable<Lit> litsIn) {
        ImmutableList.Builder<Lit> b = ImmutableList.builder();
        for (Lit lit : litsIn) {
            if (accept(lit.getVr())) {
                b.add(lit);
            }
        }
        return b.build();
    }


    public static VarFilter and(final VarFilter filter1, final VarFilter filter2) {
        return new VarFilter() {
            @Override
            public boolean accept(Var var) {
                return filter1.accept(var) && filter2.accept(var);
            }
        };
    }

    public VarFilter and(VarFilter that) {
        return VarFilter.and(this, that);
    }

//    public Function<Cube, Cube> cubeToFilteredCubeMappingFunction() {
//        return new Function<Cube, Cube>() {
//            @Nullable
//            @Override
//            public Cube apply(@Nullable Cube input) {
//                return new FilteredCube(input, VarFilter.this);
//            }
//        };
//
//    }

    public Function<Var, Lit> varToLitMappingFunction(final Cube cube) {
        return new Function<Var, Lit>() {
            @Nullable
            @Override
            public Lit apply(@Nullable Var input) {
                boolean sign = cube.isTrue(input);
                return input.mkLit(sign);
            }
        };

    }


    public static VarFilter year() {
        return prefix(YR);
    }

    public static VarFilter model() {
        return prefix(MDL);
    }

    public static VarFilter xCol() {
        return prefix(XCOL);
    }

    public static VarFilter iCol() {
        return prefix(ICOL);
    }

    public static VarFilter yearModel() {
        return prefixes(YR, MDL);
    }

    public static VarFilter colors() {
        return prefixes(XCOL, ICOL);
    }

    public static VarFilter core() {
        return prefixes(Prefix.core);
    }

    public static VarFilter inv() {
        return prefixes(Prefix.inv);
    }

    public static VarFilter acy() {
        return prefix(ACY);
    }

    public static VarFilter fio(final Space space) {
        return new VarFilter() {
            @Override
            public boolean accept(Var var) {
                VarMeta varMeta = space.getVarMeta();
                varMeta.checkVarInfo();
                return var.is(ACY) && varMeta.isFio(var.getVarCode());
            }
        };
    }

    public static Predicate<Lit> litFilter(final VarFilter varFilter) {
        return new Predicate<Lit>() {
            @Override
            public boolean apply(@Nullable Lit input) {
                return varFilter.accept(input.getVr());
            }
        };
    }

    public Predicate<Lit> litFilter() {
        return litFilter(this);
    }


    public static VarFilter noDerived(final VarInfo varMetaInstance, final List<Lit> pics) {
        return new VarFilter(varMetaInstance) {
            VarInfo varMeta = varMetaInstance;

            @Override
            public boolean accept(Var var) {
                Set<String> context = new HashSet<String>();
                String tt = "";
                for (Lit s : pics) {
                    context.add(s.getCode());
                    tt += s.getCode() + " ";
                }
                return !this.varMeta.getAttribute(context, var.getVarCode(), "derived").equals("true");
            }
        };
    }

}