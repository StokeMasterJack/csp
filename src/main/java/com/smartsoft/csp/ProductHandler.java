package com.smartsoft.csp;

import com.smartsoft.csp.dnnf.products.Cube;

import java.util.Collection;

public interface ProductHandler {

    public static class CountingProductHandler implements ProductHandler {

        protected long count;

        @Override
        public void onProduct(Cube product) {
            count++;
//            System.err.println(product);
        }


        public long getCount() {
            return count;
        }
    }

    public static class LoggingProductHandler extends CountingProductHandler {

        @Override
        public void onProduct(Cube product) {
            super.onProduct(product);
            System.err.println(product);
        }

    }

    public static class LoggingProductHandler2 extends CountingProductHandler {

        public Collection<String> xx;

        @Override
        public void onProduct(Cube product) {
            super.onProduct(product);
            System.err.println(product);
        }


    }

    void onProduct(Cube product);


}
