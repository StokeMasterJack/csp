package com.smartsoft.csp;

public enum Level {

    ADMIN(MajorLevel.PREMISE),
    INVENTORY(MajorLevel.PREMISE),
    SESSION_INIT(MajorLevel.PREMISE),

    USER(MajorLevel.ASSUMPTION),
    PROPOSE(MajorLevel.ASSUMPTION),

    DECISION(MajorLevel.SEARCH);

    private final MajorLevel majorLevel;

    private Level(MajorLevel majorLevel) {
        this.majorLevel = majorLevel;
    }
}
