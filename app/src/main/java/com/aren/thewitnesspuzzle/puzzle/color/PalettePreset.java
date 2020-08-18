package com.aren.thewitnesspuzzle.puzzle.color;

import java.util.HashMap;
import java.util.Map;

public class PalettePreset {

    public static PalettePreset instance = new PalettePreset();

    private Map<String, PuzzleColorPalette> presets = new HashMap<>();

    public PalettePreset(){
        register("TEST", "#ffbb02", "#4b3906", "#fefffe");
    }

    private void register(String name, String background, String path, String cursor){
        presets.put(name, new PuzzleColorPalette(ColorUtils.RGB(background), ColorUtils.RGB(path), ColorUtils.RGB(cursor)));
    }

    private void register(String name, String background, String path, String cursor, String cursorSucceeded, String cursorFailed){
        presets.put(name, new PuzzleColorPalette(ColorUtils.RGB(background), ColorUtils.RGB(path), ColorUtils.RGB(cursor), ColorUtils.RGB(cursorSucceeded), ColorUtils.RGB(cursorFailed)));
    }

    private static PalettePreset getInstance() {
        return instance;
    }

    public static PuzzleColorPalette get(String name){
        if(instance.presets.containsKey(name)) return instance.presets.get(name);
        return null;
    }

}
