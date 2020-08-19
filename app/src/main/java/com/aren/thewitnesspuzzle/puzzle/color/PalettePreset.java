package com.aren.thewitnesspuzzle.puzzle.color;

import java.util.HashMap;
import java.util.Map;

public class PalettePreset {

    public static PalettePreset instance = new PalettePreset();

    private Map<String, PuzzleColorPalette> presets = new HashMap<>();

    public PalettePreset(){
        register("General_Slide", "#fd9700", "#8a4700", "#fffef9", "#fbe50d");
        register("General_Panel", "#babcb9", "#5a4d3f", "#fccf97", "#ff8326");
        register("Entry_1", "#ffcb00", "#422c06", "#feffc5", "#fef656");
        register("Green_1", "#00ee4a", "#006615", "#eaeabc", "#b8ff14");
        register("Blue_1", "#4f4cff", "#1405a1", "#aaccf4", "#659cff");
        register("GlassFactory_1", "#96bce5", "#374b8d", "#e4e6e1", "#18fe9d");
        register("SymmetryIsland_1", "#96c2e9", "#555553", "#fafafa", "#fafafa"); // cursor color is cyan & yellow
        register("Swamp_1", "#b7c900", "#e9e370", "#f5f6ab", "#fcf31a");
        register("Swamp_2", "#e09b19", "#dbbc6b", "#f4e791", "#f2f528");
        register("Swamp_3", "#be0802", "#ef4632", "#f1f2d2", "#fdf61a");
        register("Treehouse_1", "#505b5f", "#0f1412", "#fef7e1", "#fda20f");
        register("Treehouse_2", "#505b5f", "#0f1412", "#fef7e1", "#ac01be");
        register("Quarry_1", "#363838", "#59877c", "#ffffff", "#ffffff");
        register("Challenge_1", "#00bc58", "#385641", "#f5bd95", "#f5c388");
        register("Challenge_2", "#00a382", "#102931", "#dee0df", "#dce1de");
        register("Challenge_3", "#000000", "#79600e", "#ffd585", "#fe9f00");
    }

    private void register(String name, String background, String path, String cursor){
        presets.put(name, new PuzzleColorPalette(ColorUtils.RGB(background), ColorUtils.RGB(path), ColorUtils.RGB(cursor)));
    }

    private void register(String name, String background, String path, String cursor, String cursorSucceeded){
        presets.put(name, new PuzzleColorPalette(ColorUtils.RGB(background), ColorUtils.RGB(path), ColorUtils.RGB(cursor), ColorUtils.RGB(cursorSucceeded)));
    }

    private void register(String name, String background, String path, String cursor, String cursorSucceeded, String cursorFailed){
        presets.put(name, new PuzzleColorPalette(ColorUtils.RGB(background), ColorUtils.RGB(path), ColorUtils.RGB(cursor), ColorUtils.RGB(cursorSucceeded), ColorUtils.RGB(cursorFailed)));
    }

    public static PuzzleColorPalette get(String name){
        if(instance.presets.containsKey(name)) return instance.presets.get(name).clone();
        return null;
    }

}