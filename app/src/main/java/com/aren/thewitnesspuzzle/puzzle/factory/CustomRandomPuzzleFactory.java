package com.aren.thewitnesspuzzle.puzzle.factory;

import android.content.Context;

import com.aren.thewitnesspuzzle.core.color.PalettePreset;
import com.aren.thewitnesspuzzle.core.color.PuzzleColorPalette;
import com.aren.thewitnesspuzzle.core.cursor.Cursor;
import com.aren.thewitnesspuzzle.core.cursor.area.GridAreaSplitter;
import com.aren.thewitnesspuzzle.core.graph.Vertex;
import com.aren.thewitnesspuzzle.core.puzzle.GridPuzzle;
import com.aren.thewitnesspuzzle.core.rules.Color;
import com.aren.thewitnesspuzzle.game.Game;
import com.aren.thewitnesspuzzle.puzzle.generator.BlocksRuleGenerator;
import com.aren.thewitnesspuzzle.puzzle.generator.BrokenLineRuleGenerator;
import com.aren.thewitnesspuzzle.puzzle.generator.EliminationRuleGenerator;
import com.aren.thewitnesspuzzle.puzzle.generator.HexagonRuleGenerator;
import com.aren.thewitnesspuzzle.puzzle.generator.SquareRuleGenerator;
import com.aren.thewitnesspuzzle.puzzle.generator.SunRuleGenerator;
import com.aren.thewitnesspuzzle.puzzle.generator.TrianglesRuleGenerator;
import com.aren.thewitnesspuzzle.puzzle.walker.FastGridTreeWalker;
import com.aren.thewitnesspuzzle.render.PuzzleRenderer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.UUID;

public class CustomRandomPuzzleFactory extends PuzzleFactory {

    public CustomRandomPuzzleFactory(Context context, UUID uuid) {
        super(context, uuid);
    }

    @Override
    public PuzzleRenderer generate(Game game, Random random) {
        GridPuzzle puzzle = null;

        String puzzleType = getConfig().getString("puzzleType", "null");
        PuzzleColorPalette palette = getConfig().getColorPalette("color", PalettePreset.get("Entry_1"));

        if (!puzzleType.equals("grid")) {
            return null;
        }

        if (!getConfig().containsKey("width") || !getConfig().containsKey("height")) return null;
        int width = getConfig().getInt("width", 4);
        int height = getConfig().getInt("height", 4);
        puzzle = new GridPuzzle(palette, width, height);
        puzzle.addStartingPoint(0, 0);
        puzzle.addEndingPoint(width, height);

        FastGridTreeWalker walker = FastGridTreeWalker.getLongest(puzzle, random, 4, 0, 0, width, height);
        ArrayList<Vertex> vertexPositions = walker.getResult();

        Cursor cursor = new Cursor(puzzle, vertexPositions, null);

        GridAreaSplitter splitter = new GridAreaSplitter(cursor);

        // Broken Line
        if (getConfig().getBoolean("brokenline", false)) {
            BrokenLineRuleGenerator.generate(cursor, random, getConfig().getFloat("brokenline_spawnrate", 0f));
        }

        // Hexagon
        if (getConfig().getBoolean("hexagon", false)) {
            HexagonRuleGenerator.generate(cursor, random, getConfig().getFloat("hexagon_spawnrate", 0f));
        }

        // Square
        if (getConfig().getBoolean("square", false)) {
            splitter.assignAreaColorRandomly(random, getConfig().getColorList("square_colors", new ArrayList<Color>()));
            SquareRuleGenerator.generate(splitter, random, getConfig().getFloat("square_spawnrate", 0f));
        }

        // Blocks
        if (getConfig().getBoolean("blocks", false)) {
            BlocksRuleGenerator.generate(splitter, random, getConfig().getColorList("blocks_colors", Arrays.asList(Color.YELLOW)).get(0), Color.BLUE, getConfig().getFloat("blocks_spawnrate", 0f), getConfig().getFloat("blocks_rotatablerate", 0f), 0);
        }

        // Sun
        if (getConfig().getBoolean("sun", false)) {
            SunRuleGenerator.generate(splitter, random, getConfig().getColorList("sun_colors", new ArrayList<Color>()), getConfig().getFloat("sun_arearate", 0f), getConfig().getFloat("sun_spawnrate", 0f), getConfig().getFloat("sun_pairwithsquare", 0f));
        }

        // Triangles
        if (getConfig().getBoolean("triangles", false)) {
            TrianglesRuleGenerator.generate(cursor, random, getConfig().getFloat("triangles_spawnrate", 0f));
        }

        // Elimination
        if (getConfig().getBoolean("elimination", false)) {
            String fake = getConfig().getString("elimination_fakerule", null);
            if (fake != null) {
                if (fake.equals("hexagon")) {
                    EliminationRuleGenerator.generateFakeHexagon(splitter, random);
                } else if (fake.equals("square") && getConfig().getBoolean("square", false) && getConfig().getColorList("square_colors", new ArrayList<Color>()).size() > 1) {
                    EliminationRuleGenerator.generateFakeSquare(splitter, random, getConfig().getColorList("square_colors", new ArrayList<Color>()));
                } else if (fake.equals("blocks")) {
                    EliminationRuleGenerator.generateFakeBlocks(splitter, random, getConfig().getColorList("blocks_colors", Arrays.asList(Color.YELLOW)).get(0), 0f);
                } else if (fake.equals("sun") && getConfig().getBoolean("sun", false)) {
                    EliminationRuleGenerator.generateFakeSun(splitter, random, getConfig().getColorList("sun_colors", new ArrayList<Color>()));
                }
            }
        }

        return new PuzzleRenderer(game, puzzle);
    }

    @Override
    public Difficulty getDifficulty() {
        return Difficulty.fromString(getConfig().getString("difficulty", "ALWAYS_SOLVABLE"));
    }

    @Override
    public String getName() {
        return getConfig().getString("name", "No Name");
    }

    @Override
    public boolean isCreatedByUser() {
        return true;
    }
}
