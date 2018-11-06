package com.tms.csp.trail.levels;

public enum Level {

    ADMIN(MacroLevel.PREMISE),
    INVENTORY(MacroLevel.PREMISE),
    SESSION_INIT(MacroLevel.PREMISE),

    USER(MacroLevel.ASSUMPTION),
    PROPOSE(MacroLevel.ASSUMPTION),
    DECISION(MacroLevel.SEARCH);

    private final MacroLevel macroLevel;

    Level(MacroLevel macroLevel) {
        this.macroLevel = macroLevel;
    }

    public MacroLevel getMacroLevel() {
        return macroLevel;
    }

}
