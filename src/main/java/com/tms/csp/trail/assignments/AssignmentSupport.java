package com.tms.csp.trail.assignments;

import com.tms.csp.trail.AssignType;

import java.util.ArrayList;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

public class AssignmentSupport {

    private final ArrayList tCauses = new ArrayList();
    private final ArrayList fCauses = new ArrayList();

    public AssignmentSupport(boolean sign, Object cause) {
        checkNotNull(cause);
        if (sign) {
            tCauses.add(cause);
        } else {
            fCauses.add(cause);
        }
    }

    public AssignType addCause(boolean sign, Object newCause) {
        checkNotNull(newCause);

        if (isConflicted()) {
            throw new IllegalStateException("Already conflicted");
        }

        if (isTrue() && sign) {
            //dup true assignment
            boolean ch = tCauses.add(newCause);
            checkState(ch);
            return AssignType.DUP;
        }

        if (isFalse() && !sign) {
            //dup false assignment
            boolean ch = fCauses.add(newCause);
            checkState(ch);
            return AssignType.DUP;
        }


        if (isTrue() && !sign) {
            //conflicting false assignment
            boolean ch = fCauses.add(newCause);
            checkState(ch);
            return AssignType.CONFLICT;
        }

        if (isFalse() && sign) {
            //conflicting true assignment
            boolean ch = tCauses.add(newCause);
            checkState(ch);
            return AssignType.CONFLICT;
        }

        if (isOpen() && sign) {
            //new true assignment
            boolean ch = tCauses.add(newCause);
            checkState(ch);
            return AssignType.NEW;
        }


        if (isOpen() && !sign) {
            //new false assignment
            boolean ch = fCauses.add(newCause);
            checkState(ch);
            return AssignType.NEW;
        }

        throw new IllegalStateException();
    }

    public boolean isOpen() {
        return tCauses.isEmpty() && fCauses.isEmpty();
    }

    public boolean isConflicted() {
        return !tCauses.isEmpty() && !fCauses.isEmpty();
    }

    public boolean isTrue() {
        return !tCauses.isEmpty() && fCauses.isEmpty();
    }

    public boolean isFalse() {
        return tCauses.isEmpty() && !fCauses.isEmpty();
    }

    public boolean isAssigned() {
        return isTrue() || isFalse();
    }

    public ExpState2 getExpState() {
        return new ExpState2(AssignmentSupport.this);
    }

    @Override
    public String toString() {
        return "AssignmentSupport[tCon:" + tCauses + "   fCon:" + fCauses + "]";
    }
}
