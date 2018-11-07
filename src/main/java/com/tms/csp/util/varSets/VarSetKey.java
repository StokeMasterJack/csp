package com.tms.csp.util.varSets;

public class VarSetKey {

//    private final int activeWords;
//    private final int firstVarId;
//    private final short varCount;
//    private final short lastVarId;
//    private final short subId;
//
//    public VarSetKey(int size, int firstVar, int lastVar, int subId) {
//        byte[] s = Shorts.toByteArray(Shorts.checkedCast(size));
//        byte[] fCon = Shorts.toByteArray(Shorts.checkedCast(firstVar));
//        byte[] l = Shorts.toByteArray(Shorts.checkedCast(lastVarId));
//        byte[] i = Ints.toByteArray(Shorts.checkedCast(subId));
//
//        Longs.fromBytes(s[0], s[1], fCon[0], fCon[1], l[0], l[1], i[0], i[1]);
//        this.firstVarId = firstVar;
//        this.subId = subId;
//    }
//
//
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//
//        VarSetKey varSetKey = (VarSetKey) o;
//
//        if (firstVarId != varSetKey.firstVarId) return false;
//        if (id != varSetKey.id) return false;
//        if (size != varSetKey.size) return false;
//
//        return true;
//    }
//
//    @Override
//    public int hashCode() {
//        int result = size;
//        result = 31 * result + firstVarId;
//        result = 31 * result + id;
//        return result;
//    }
//
//    public VarSet getVarSet(VarSpace space) {
//        if (size == 0) return space.mkEmptyVarSet();
//        if (size == 1) return space.getVr(firstVarId).mkSingletonVarSet();
//        if (size == 2) return space.getVr(firstVarId).mkPartner(lastVarId);
//
//    }
}
