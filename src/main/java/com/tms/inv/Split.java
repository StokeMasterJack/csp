package com.tms.inv;

public interface Split {

    void printTree(int depth);

    void onNode(ProductHandler ph);

    int getSatCount();

}
