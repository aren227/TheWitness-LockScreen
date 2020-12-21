package com.aren.thewitnesspuzzle.puzzle.base.rules;

import android.graphics.Color;

public enum SymmetryColor {

    NONE, CYAN, YELLOW, CYAN2, YELLOW2;

    public int getRGB() {
        if (this == CYAN) {
            return Color.CYAN;
        } else if (this == YELLOW) {
            return Color.YELLOW;
        } else if (this == CYAN2) {
            return Color.parseColor("#60fccb");
        } else if (this == YELLOW2) {
            return Color.parseColor("#f0d41d");
        }
        return 0;
    }

    public static SymmetryColor fromString(String str) {
        for (SymmetryColor color : SymmetryColor.values()) {
            if (color.toString().equalsIgnoreCase(str)) {
                return color;
            }
        }
        return null;
    }
}
