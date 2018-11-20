package com.smartsoft.csp.fm.search;

import com.smartsoft.csp.ProductHandler;
import com.smartsoft.csp.fm.dnnf.products.Cube;

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
