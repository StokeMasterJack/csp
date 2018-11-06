package com.tms.csp.util;

import com.tms.csp.ast.Space;

import static com.google.common.base.Preconditions.checkNotNull;

public class SpaceBase implements SpaceAware {

    public final Space space;

    public SpaceBase(Space space) {
        checkNotNull(space);

        this.space = space;
    }

    @Override
    public Space getSpace() {
        return space;
    }
}
