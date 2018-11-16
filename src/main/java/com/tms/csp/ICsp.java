package com.tms.csp;

import com.tms.csp.fm.dnnf.products.Cube;
import com.tms.csp.util.Bit;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * This api depends c the csp string format: changed d to _complexVars
 */
public interface ICsp {

    /**
     * Callback used to access attributes - used formula range fact.
     * Range fact will only be valid if varInfo is set before passing range fact into setUserConstraint
     */
    void setVarInfo(VarInfo varInfo);

    /**
     * Adds a constraint to this csp at the *current* level
     * <p>
     * Examples of valid fact:
     * or(a b c)
     * and(or(x y z) or(p d q) or(h d e))
     * xor(q w e tCon)
     * imp(a b)
     * iff(a b)
     * x
     * !y
     *
     * @Deprecated renamed to setUserConstraint
     */
    @Deprecated
    void addConstraint(String constraint);

    void setUserConstraint(String constraint);

    /**
     * @param constraint user pics, ordered by weight
     */
    void setUserConstraint(String constraint, RangeConstraint[] rangeConstraints);

    void setUserConstraint(String constraint, RangeConstraint[] rangeConstraints, OrConstraint[] orConstraints);

    void setUserConstraint(String constraint, RangeConstraint[] intAttributeConstraints, RangeConstraint[] intCspConstraints, OrConstraint[] orConstraints);


    /**
     * Sets the current the assignment level for this csp
     * Any new fact added to the csp will be associated with this level.
     * <p>
     * Initially defaults to level: Level.ADMIN
     * <p>
     * A constraint's level is used by retract
     */
    void setLevel(Level level);

    Level getLevel();

    /**
     * @return true is satCount >= 0
     */
    boolean isSat();

    /**
     * Same as:
     * <p>
     * csp.setLevel(Level.
     *
     * @param constraint
     * @return
     */
    boolean propose(String constraint); //constraint will typically be a single vr

    long forEach(@Nullable ProductHandler ph, @Nullable String... outVars);

    long forEachSatCount();

    /**
     * @return # of valid vehicles (aka products) produced by the csp
     */
    long satCount();

    /**
     * @return # of valid vehicles (aka products) produced by the csp (based only c outVars)
     */
    long satCount(@Nullable String... outVars);

    /**
     * Undo all fact level and higher
     */
    void retract(Level level);

//    void dump();

    /**
     * Attempts to optimize/minimize the boolean formula
     * to one that is equivalent but more efficient.
     */
    void optimize();

    void serialize(Appendable a);

    /**
     * explain why vr is formula the current state that its formula
     * i.e. was it user assigned, inferred, etc.
     */
    String explain(String var);

    /**
     * explain why the csp is formula the current state that its formula
     * primary use-case: after calling isSat and it returns false (i.e. csp is formula an invalid state)
     * you can call csp.explain() to get an explanation
     */
    String explain();

    Bit getValue(String var);

    boolean isTrue(String var);

    boolean isFalse(String var);

    boolean isOpen(String var);

    void printVars();

    List<String> getTrueVarsCodes();

    List<String> getOpenVarsCodes();

    List<String> getFalseVarsCodes();

    List<String> getAllVarsCodes();

    int getVarId(String varCode);




    /**
     * return value of 0 would be the same as getValue() returning false
     *
     * @param varCode
     * @return the number of valid unique configurations if this were to be picked
     */
    long getFacetCount(String varCode);

    /**
     * Same as satCount with outVars = invClob _complexVars
     */
    long getFacetCount();


    Collection<Cube> computeComplete();

    List<String> getInventoryAccyOutVars();

    List<String> getInventoryOutVars();

    void clearSessionState();

    long getSatCount();

    int[] getCubeCountBatch(String[] var);

    int getCubeCount(String var);

    int getCubeCount();

    List<String> getDontCareVars(String[] outVars);

    void createAndSetHardPics(Set<String> hardPicks);

}
