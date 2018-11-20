package com.smartsoft.csp.varCodes;

public class CountingCoreProductHandler implements CoreProductHandler {

    int count;

    @Override
    public void onCoreProduct(CoreProduct p) {
        count++;
    }

    public int getCount() {
        return count;
    }
}
