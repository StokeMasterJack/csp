package com.tms.csp.graph;


import com.tms.csp.ast.Exp;
import com.tms.csp.ast.PLConstants;

public interface Filter extends PLConstants {

    boolean accept(ExpDb db);

    boolean accept(Exp e);


    class VarFilter implements Filter {

        @Override
        public boolean accept(ExpDb db) {
            return db == ExpDb.VAR;
        }

        @Override
        public boolean accept(Exp var) {
            return var.isOpen();
        }
    }

    class OpenVars extends VarFilter {

        @Override
        public boolean accept(Exp var) {
            return var.isOpen();
        }
    }

    class TrueVars extends VarFilter {
        @Override
        public boolean accept(Exp var) {
            return var.isTrue();
        }
    }

    class FalseVars extends VarFilter {
        @Override
        public boolean accept(Exp var) {
            return var.isFalse();
        }
    }


    Filter CLAUSE_FILTER = new Filter() {
        @Override
        public boolean accept(ExpDb db) {
            return true;
        }

        @Override
        public boolean accept(Exp e) {
            return e.isClause();
        }
    };


    Filter NON_CLAUSE_FILTER = new Filter() {
        @Override
        public boolean accept(ExpDb db) {
            return true;
        }

        @Override
        public boolean accept(Exp e) {
            return !e.isClause();
        }
    };

    Filter PAIR_NARY = new Filter() {

        @Override
        public boolean accept(ExpDb db) {
            return db != ExpDb.VAR;
        }

        @Override
        public boolean accept(Exp e) {
            return true;
        }

    };

    Filter PAIR = new Filter() {

        @Override
        public boolean accept(ExpDb db) {
            return db == ExpDb.PAIR;
        }

        @Override
        public boolean accept(Exp e) {
            return true;
        }

    };

    Filter NARY = new Filter() {

        @Override
        public boolean accept(ExpDb db) {
            return db == ExpDb.NARY;
        }

        @Override
        public boolean accept(Exp e) {
            return true;
        }

    };


}
