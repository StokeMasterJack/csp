package com.tms.inv;

import java.util.Set;

public interface Product {

    boolean isTrue(String varCode);

    Set<String> getTrueVars();

    Inv getInv();

    Line getLine();

}
