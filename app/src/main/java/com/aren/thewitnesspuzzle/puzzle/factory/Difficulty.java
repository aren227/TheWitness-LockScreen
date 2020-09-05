package com.aren.thewitnesspuzzle.puzzle.factory;

public enum Difficulty {

    CUSTOM_PATTERN, ALWAYS_SOLVABLE, VERY_EASY, EASY, NORMAL, HARD, VERY_HARD;

    public int getColor() {
        if (this == CUSTOM_PATTERN) return android.graphics.Color.parseColor("#dbd1ff");
        else if (this == ALWAYS_SOLVABLE) return android.graphics.Color.parseColor("#fafafa");
        else if (this == VERY_EASY) return android.graphics.Color.parseColor("#bdfdff");
        else if (this == EASY) return android.graphics.Color.parseColor("#c0ffbd");
        else if (this == NORMAL) return android.graphics.Color.parseColor("#fffdbd");
        else if (this == HARD) return android.graphics.Color.parseColor("#ffe1bd");
        else return android.graphics.Color.parseColor("#ffc9bd");
    }

    public String toUserFriendlyString() {
        if (this == CUSTOM_PATTERN) return "Custom Pattern";
        else if (this == ALWAYS_SOLVABLE) return "Always Solvable";
        else if (this == VERY_EASY) return "Very Easy";
        else if (this == EASY) return "Easy";
        else if (this == NORMAL) return "Normal";
        else if (this == HARD) return "Hard";
        else return "Very Hard";
    }

    public static Difficulty fromString(String str) {
        for (Difficulty diff : Difficulty.values()) {
            if (diff.toString().equalsIgnoreCase(str)) {
                return diff;
            }
        }
        return null;
    }

}
