package com.tms.csp.fm.search;

import com.tms.csp.fm.dnnf.products.Cube;

public class CountingFmProductHandler implements FmProductHandler {

    private long count;

    public CountingFmProductHandler() {
    }

    @Override
    public void onProduct(Cube product) {
//        System.out.println(product);
        count++;
    }

    public long getCount() {
        return count;
    }

}
