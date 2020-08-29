package com.aren.thewitnesspuzzle.puzzle.factory;

import android.content.Context;

import com.aren.thewitnesspuzzle.game.Game;
import com.aren.thewitnesspuzzle.puzzle.GridPuzzle;
import com.aren.thewitnesspuzzle.puzzle.HexagonPuzzle;
import com.aren.thewitnesspuzzle.puzzle.Puzzle;
import com.aren.thewitnesspuzzle.puzzle.color.PalettePreset;
import com.aren.thewitnesspuzzle.puzzle.color.PuzzleColorPalette;
import com.aren.thewitnesspuzzle.puzzle.cursor.Cursor;
import com.aren.thewitnesspuzzle.puzzle.cursor.area.GridAreaSplitter;
import com.aren.thewitnesspuzzle.puzzle.graph.Vertex;
import com.aren.thewitnesspuzzle.puzzle.rules.BlocksRule;
import com.aren.thewitnesspuzzle.puzzle.rules.BrokenLineRule;
import com.aren.thewitnesspuzzle.puzzle.rules.Color;
import com.aren.thewitnesspuzzle.puzzle.rules.EliminationRule;
import com.aren.thewitnesspuzzle.puzzle.rules.HexagonRule;
import com.aren.thewitnesspuzzle.puzzle.rules.SquareRule;
import com.aren.thewitnesspuzzle.puzzle.rules.SunRule;
import com.aren.thewitnesspuzzle.puzzle.rules.TrianglesRule;
import com.aren.thewitnesspuzzle.puzzle.walker.RandomGridWalker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class CustomRandomPuzzleFactory extends PuzzleFactory {

    public CustomRandomPuzzleFactory(Context context, UUID uuid) {
        super(context, uuid);
    }

    @Override
    public Puzzle generate(Game game, Random random) {
        GridPuzzle puzzle = null;

        String puzzleType = getConfig().getString("puzzleType", "null");
        PuzzleColorPalette palette = getConfig().getColorPalette("color", PalettePreset.get("Entry_1"));

        if(!puzzleType.equals("grid")){
            return null;
        }

        if(!getConfig().containsKey("width") || !getConfig().containsKey("height")) return null;
        int width = getConfig().getInt("width", 4);
        int height = getConfig().getInt("height", 4);
        puzzle = new GridPuzzle(game, palette, width, height);
        puzzle.addStartingPoint(0, 0);
        puzzle.addEndingPoint(width, height);

        RandomGridWalker walker = new RandomGridWalker(puzzle, random, 4, 0, 0, width, height);
        ArrayList<Vertex> vertexPositions = walker.getResult();

        Cursor cursor = new Cursor(puzzle, vertexPositions, null);

        GridAreaSplitter splitter = new GridAreaSplitter(cursor);

        // Broken Line
        if(getConfig().getBoolean("brokenline", false)){
            BrokenLineRule.generate(cursor, random, getConfig().getFloat("brokenline_spawnrate", 0f));
        }

        // Hexagon
        if(getConfig().getBoolean("hexagon", false)){
            HexagonRule.generate(cursor, random, getConfig().getFloat("hexagon_spawnrate", 0f));
        }

        // Square
        if(getConfig().getBoolean("square", false)){
            splitter.assignAreaColorRandomly(random, getConfig().getColorList("square_colors", new ArrayList<Color>()));
            SquareRule.generate(splitter, random, getConfig().getFloat("square_spawnrate", 0f));
        }

        // Blocks
        if(getConfig().getBoolean("blocks", false)){
            BlocksRule.generate(splitter, random, getConfig().getColorList("blocks_colors", Arrays.asList(Color.YELLOW)).get(0), getConfig().getFloat("blocks_spawnrate", 0f), getConfig().getFloat("blocks_rotatablerate", 0f));
        }

        // Sun
        if(getConfig().getBoolean("sun", false)){
            SunRule.generate(splitter, random, getConfig().getColorList("sun_colors", new ArrayList<Color>()), getConfig().getFloat("sun_arearate", 0f), getConfig().getFloat("sun_spawnrate", 0f), getConfig().getFloat("sun_pairwithsquare", 0f));
        }

        // Triangles
        if(getConfig().getBoolean("triangles", false)){
            TrianglesRule.generate(cursor, random, getConfig().getFloat("triangles_spawnrate", 0f));
        }

        // Elimination
        if(getConfig().getBoolean("elimination", false)){
            String fake = getConfig().getString("elimination_fakerule", null);
            if(fake != null){
                if(fake.equals("hexagon")){
                    EliminationRule.generateFakeHexagon(splitter, random);
                }
                else if(fake.equals("square") && getConfig().getBoolean("square", false) && getConfig().getColorList("square_colors", new ArrayList<Color>()).size() > 1){
                    EliminationRule.generateFakeSquare(splitter, random, getConfig().getColorList("square_colors", new ArrayList<Color>()));
                }
                else if(fake.equals("blocks")){
                    EliminationRule.generateFakeBlocks(splitter, random, getConfig().getColorList("blocks_colors", Arrays.asList(Color.YELLOW)).get(0), 0f);
                }
                else if(fake.equals("sun") && getConfig().getBoolean("sun", false)){
                    EliminationRule.generateFakeSun(splitter, random, getConfig().getColorList("sun_colors", new ArrayList<Color>()));
                }
            }
        }

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
