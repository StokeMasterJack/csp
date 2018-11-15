package com.tms.inv;

import java.util.ArrayList;

//todo Need to finish this
public class RejectedInventoryReport {

    public ArrayList<UnknownVarReject> unknownVarRejects = new ArrayList<UnknownVarReject>();
    public ArrayList<ViolatedFactoryConstraintReject> violatedFactoryConstraintRejects = new ArrayList<ViolatedFactoryConstraintReject>();

    public static enum Reason {INVALID_VAR, VIOLATED_FACTORY_CONSTRAINT}

    public static class UnknownVarReject {
        Line line;
        String badVar;
    }

    public static class ViolatedFactoryConstraintReject {
        Line line;
        String factoryConstraint;
    }


}
