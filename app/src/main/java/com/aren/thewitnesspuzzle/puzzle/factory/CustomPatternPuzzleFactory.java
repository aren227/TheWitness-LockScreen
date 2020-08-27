package com.aren.thewitnesspuzzle.puzzle.factory;

import android.content.Context;

import com.aren.thewitnesspuzzle.game.Game;
import com.aren.thewitnesspuzzle.puzzle.GridPuzzle;
import com.aren.thewitnesspuzzle.puzzle.HexagonPuzzle;
import com.aren.thewitnesspuzzle.puzzle.Puzzle;
import com.aren.thewitnesspuzzle.puzzle.color.PalettePreset;
import com.aren.thewitnesspuzzle.puzzle.color.PuzzleColorPalette;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class CustomPatternPuzzleFactory extends PuzzleFactory {

    public CustomPatternPuzzleFactory(Context context, UUID uuid) {
        super(context, uuid);
    }

    @Override
    public Puzzle generate(Game game, Random random) {
        Puzzle puzzle = null;

        String puzzleType = getConfig().getString("puzzleType", "null");
        PuzzleColorPalette palette = getConfig().getColorPalette("color", PalettePreset.get("Entry_1"));

        if(puzzleType.equals("grid")){
            if(!getConfig().containsKey("width") || !getConfig().containsKey("height")) return null;
            int width = getConfig().getInt("width", 4);
            int height = getConfig().getInt("height", 4);
            puzzle = new GridPuzzle(game, palette, width, height);
        }
        else if(puzzleType.equals("hexagon")){
            puzzle = new HexagonPuzzle(game, palette);
        }
        else{
            return null;
        }

        if(!getConfig().containsKey("pattern")) return null;

        List<Integer> pattern = getConfig().getIntList("pattern", new ArrayList<Integer>());
        puzzle.setCustomPattern(pattern);

        return puzzle;
    }

    @Override
    public Difficulty getDifficulty(){
        return Difficulty.ALWAYS_SOLVABLE;
    }

    @Override
    public String getName() {
        return getConfig().getString("name", "No Name");
    }

    @Override
    public boolean isCreatedByUser(){
        return true;
    }
}
