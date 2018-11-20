package com.smartsoft.csp.fm.search;

import com.smartsoft.csp.fm.dnnf.products.Cube;

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
