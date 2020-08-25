package com.aren.thewitnesspuzzle.puzzle.color;

public class ColorUtils {

    public static int RGB(String hex){
        return android.graphics.Color.parseColor(hex);
    }

    public static int lerp(int a, int b, float t){
        return androidx.core.graphics.ColorUtils.blendARGB(a, b, t);
    }

}
