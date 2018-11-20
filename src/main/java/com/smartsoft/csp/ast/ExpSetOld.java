package com.smartsoft.csp.ast;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedSet;

import java.util.*;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.smartsoft.csp.ssutil.Console.prindent;

public class ExpSetOld extends AbstractList<Exp> implements PLConstants {

    public static final ExpSetOld EMPTY = new ExpSetOld();
//    public static final Comparator<Exp> SORT = Exp.COMPARATOR_BY_EXP_ID;

    public final Exp[] args;


    public Exp headExpression;

    private ExpSetOld() {
        this.args = new Exp[0];
    }


    public Space getSpace() {
        return args[0].getSpace();
    }

    public Var getFirstVar() {
        for (Exp arg : args) {
            Var var = arg.getFirstVar();
            if (var != null) {
                return var;
            }
        }
        return null;
    }

//    public VarSet getCareVarsOld() {
//        if (careVars == null) {
//            careVars = Exp.extractCareVars(this, getSpace());
//        }
//        return careVars;
//    }


    public boolean isPair() {
        return args.length == 2;
    }

    public boolean isNary() {
        return args.length > 2;
    }

//    public List<Exp> getRest() {
//        throw new UnsupportedOperationException();
//    }

    public Exp getArg() {
        return get(0);
    }

    public Exp getArg1() {
        return get(0);
    }

    public Exp getArg2() {
        return get(1);
    }


    @Override
    final public String toString() {
        Ser a = Ser.forToString();
        serialize(a);
        return a.toString();
    }

    public void toString(Ser a) {
        serialize(a);
    }


    @Override
    public Exp get(int index) {
        return args[index];
    }

    @Override
    public int size() {
        return args.length;
    }


    /**
     * Discouraged - use fixArgs2(Exp[] instead)
     */
    public static Exp[] fixArgs(Collection<Exp> argsCollection) throws IllegalArgumentException {
        checkNotNull(argsCollection);
        ArrayList<Exp> aa = new ArrayList<Exp>(argsCollection);
        Exp[] newArray = new Exp[aa.size()];
        newArray = aa.toArray(newArray);
        return fixArgs(newArray);
    }

    public static ImmutableList<Exp> fixArgs2(Collection<Exp> args) {
        Exp[] exps = fixArgs(args);
        return ImmutableList.copyOf(exps);
    }

    public static Exp[] fixArgs(Exp[] args) {
        checkNotNull(args);
//        Arrays.sort(args, Exp.COMPARATOR_BY_EXP_ID);

        //any null array element to be removed
        //any dup array element to be set to null

        int lenOfFinalArray = args.length;
        for (int i = 0; i < args.length - 1; i++) {
            if (args[i] == null) {
                lenOfFinalArray--;
            } else if (i != 0 && (args[i] == args[i - 1])) {
                args[i] = null;
                lenOfFinalArray--;
            }
        }

        if (lenOfFinalArray == args.length) {
            return args;
        } else {
            Exp[] a = new Exp[lenOfFinalArray];
            int i = 0;
            for (Exp arg : args) {
                if (arg == null) continue;
                a[i] = arg;
                i++;
            }
            return a;
        }
    }

    public static Exp[] checkForEmptyElements(Exp[] args) throws IllegalArgumentException {
        for (Exp arg : args) {
            if (arg == null) throw new IllegalArgumentException();
        }
        return args;
    }

    public static List<Exp> checkForEmptyElements(List<Exp> args) throws IllegalArgumentException {
        for (Exp arg : args) {
            if (arg == null) throw new IllegalArgumentException();
        }
        return args;
    }


//    public static ExpSet create(Exp... args) {
//        if (args == null || args.length == 0) return EMPTY;
//        checkNotNull(args);
//        return new ExpSet(args);
//    }

//    public static ExpSet create(List<Exp> args) {
//        if (args == null) return EMPTY;
//        if (args instanceof ExpSet) {
//            throw new IllegalArgumentException();
//        } else {
//            if (args.size() == 0) return EMPTY;
//            return new ExpSet(args);
//        }
//    }
//
//    public static ExpSet create(Collection<Exp> args) {
//        if (args == null) return EMPTY;
//        if (args instanceof ExpSet) {
//            return (ExpSet) args;
////            throw new IllegalArgumentException();
//        } else {
//            if (args.size() == 0) return EMPTY;
//            return new ExpSet(args);
//        }
//    }


    final public boolean equals(Object o) {
        if (this == o) return true;
        if (o.getClass() != getClass()) {
            return false;
        }
        ExpSetOld that = (ExpSetOld) o;
        return Arrays.equals(args, that.args);
    }

    @Override
    final public int hashCode() {
        return Arrays.hashCode(args);
    }

    public static Collection<Exp> checkExpList(Collection<Exp> args) throws IllegalArgumentException {
        checkNotNull(args, "Null args");
        checkArgument(!args.isEmpty(), "Empty args");
        checkArgument(!args.contains(null), "Contains null elements");
        return args;
    }

    private static ImmutableList<Exp> createExpList(Collection<Exp> args) {
        ImmutableSortedSet<Exp> set = ImmutableSortedSet.copyOf(args);
        return ImmutableList.copyOf(set);
    }

    private static ImmutableList<Exp> createExpList(Exp... args) {
        ImmutableSortedSet<Exp> set = ImmutableSortedSet.copyOf(args);
        return ImmutableList.copyOf(set);
    }

    public Exp getFirstAnd() {
        return getFirstAnd(args);
    }

    public static Exp getFirstAnd(List<Exp> args) {
        for (Exp e : args) {
            if (e.isAnd()) return e;
        }
        return null;
    }

    public static Exp getFirstAnd(Exp[] args) {
        for (Exp e : args) {
            if (e.isAnd()) return e;
        }
        return null;
    }

    public List<Exp> toList() {
        return this;
    }

    public void print() {
        print(0);
    }


    public void print(int depth) {
        for (Exp e : this) {
            prindent(depth, e.toString());
        }
    }


    public boolean isCnf() {
        for (Exp element : args) {
            if (!element.isCnf()) {
                return false;
            }
        }
        return true;
    }


    public void serialize(Ser a) {
        serializeArgList(a, this);
    }

    public static void serializeArgs(Ser a, List<Exp> args) {
        Exp.Companion.serializeArgs(a, args);
    }

    public static void serializeArgList(Ser a, List<Exp> args) {
        Exp.Companion.serializeArgList(a, args);
    }


    public Exp[] copyOfArgs() {
        Exp[] argsCopy = new Exp[args.length];
        System.arraycopy(args, 0, argsCopy, 0, argsCopy.length);
        return argsCopy;
    }

    public boolean isAllVars() {
        for (Exp arg : args) {
            if (!arg.isPosLit()) {
                return false;
            }
        }
        return true;
    }

    public boolean isAllLits() {
        for (Exp arg : args) {
            if (!arg.isLit()) {
                return false;
            }
        }
        return true;
    }

    public boolean isYrSerMdl() {

        if (!isAllVars()) return false;
        if (args.length != 3) return false;


        int yrCount = 0;
        int serCount = 0;
        int mdlCount = 0;

        for (Exp arg : args) {
            String prefix = arg.asVar().getPrefixCode();
            if (prefix.equals("YR")) yrCount++;
            if (prefix.equals("SER")) serCount++;
            if (prefix.equals("MDL")) mdlCount++;
        }

        return yrCount == 1 && serCount == 1 && mdlCount == 1;


    }

    public boolean isEveryArgAnAndYrSerMdl() {
        for (Exp arg : args) {
            if (!arg.isAndYrSerMdl()) {
                return false;
            }
        }
        return true;
    }

    public Exp getFirstVarOfType(String prefix) {
        for (Exp arg : args) {
            if (arg.isPosLitOfType(prefix)) {
                return arg;
            }
        }
        return null;
    }

    public Exp getFirstVarOfTypeYr() {
        return getFirstVarOfType("YR");
    }

    public Exp getFirstVarOfTypeSer() {
        return getFirstVarOfType("SER");
    }

    public Exp getFirstVarOfTypeMdl() {
        return getFirstVarOfType("MDL");
    }

    public boolean isNnf() {
        for (Exp arg : args) {
            if (!arg.isNnf()) {
                return false;
            }
        }
        return true;
    }

    public boolean isBnf() {
        for (Exp arg : args) {
            if (!arg.isBnf()) {
                return false;
            }
        }
        return true;
    }


    public int getMinVar() {
        return 0;
    }

    public int getMaxVar() {
        return 0;
    }


}
