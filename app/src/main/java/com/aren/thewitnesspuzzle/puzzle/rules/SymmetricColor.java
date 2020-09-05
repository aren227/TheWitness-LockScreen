package com.aren.thewitnesspuzzle.puzzle.rules;

import android.graphics.Color;

public enum SymmetricColor {

    CYAN, YELLOW;

    public int getRGB() {
        if (this == CYAN) {
            return Color.CYAN;
        } else if (this == YELLOW) {
            return Color.YELLOW;
        }
        return 0;
    }

}
