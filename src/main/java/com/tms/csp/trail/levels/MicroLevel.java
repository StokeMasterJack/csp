package com.tms.csp.trail.levels;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

public class MicroLevel {

    private final MicroLevel previous;
    private final Level level;
    private final int index;

    public MicroLevel(MicroLevel previous) {
        checkNotNull(previous);
        this.previous = previous;
        this.level = previous.getLevel();
        this.index = previous.getIndex() + 1;
    }

    public MicroLevel(MicroLevel previous, Level newLevel) {
        checkNotNull(previous);
        checkNotNull(newLevel);
        checkState(newLevel.ordinal() > previous.getLevel().ordinal());
        this.previous = previous;
        this.level = newLevel;
        this.index = 0;
    }

    public MicroLevel(Level newLevel) {
        checkNotNull(newLevel);
        this.previous = null;
        this.level = newLevel;
        this.index = 0;
    }

    public MicroLevel getPrevious() {
        return previous;
    }

    public MicroLevel next() {
        return new MicroLevel(this);
    }

    public MicroLevel next(Level newLevel) {
        if (newLevel == null) {
            return new MicroLevel(this);
        } else {
            return new MicroLevel(this, newLevel);
        }
    }

    public MacroLevel getMacroLevel() {
        return getLevel().getMacroLevel();
    }

    public Level getLevel() {
        return level;
    }

    public int getIndex() {
        return index;
    }
}
