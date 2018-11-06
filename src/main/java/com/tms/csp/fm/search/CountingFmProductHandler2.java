package com.tms.csp.fm.search;

import com.tms.csp.ProductHandler;
import com.tms.csp.fm.dnnf.products.Cube;

public class CountingFmProductHandler2 implements ProductHandler {

    //    private final HashSet<Product> products = new HashSet<Product>();
    private long count;

    @Override
    public void onProduct(Cube product) {
        count++;
//        products.add(product);
    }

    public long getCount() {
        return count;
//        return products.size();
    }

}
