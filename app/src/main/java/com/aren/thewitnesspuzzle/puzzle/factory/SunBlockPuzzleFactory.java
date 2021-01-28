package com.aren.thewitnesspuzzle.puzzle.factory;

import android.content.Context;

import com.aren.thewitnesspuzzle.core.color.PalettePreset;
import com.aren.thewitnesspuzzle.core.cursor.Cursor;
import com.aren.thewitnesspuzzle.core.cursor.area.Area;
import com.aren.thewitnesspuzzle.core.cursor.area.GridAreaSplitter;
import com.aren.thewitnesspuzzle.core.graph.Tile;
import com.aren.thewitnesspuzzle.core.graph.Vertex;
import com.aren.thewitnesspuzzle.core.puzzle.GridPuzzle;
import com.aren.thewitnesspuzzle.core.rules.BlocksRule;
import com.aren.thewitnesspuzzle.core.rules.Color;
import com.aren.thewitnesspuzzle.game.Game;
import com.aren.thewitnesspuzzle.puzzle.generator.BlocksRuleGenerator;
import com.aren.thewitnesspuzzle.puzzle.generator.SunRuleGenerator;
import com.aren.thewitnesspuzzle.puzzle.walker.FastGridTreeWalker;
import com.aren.thewitnesspuzzle.render.PuzzleRenderer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class SunBlockPuzzleFactory extends PuzzleFactory {
    public SunBlockPuzzleFactory(Context context) {
        super(context);
    }

    @Override
    public PuzzleRenderer generate(Game game, Random random) {
        GridPuzzle puzzle = new GridPuzzle(PalettePreset.get("Treehouse_3"), 5, 5);

        puzzle.addStartingPoint(0, 0);
        puzzle.addEndingPoint(5, 5);

        FastGridTreeWalker walker = FastGridTreeWalker.getLongest(puzzle, random, 5, 0, 0, 5, 5);
        ArrayList<Vertex> vertexPositions = walker.getResult();

        Cursor cursor = new Cursor(puzzle, vertexPositions, null);

        GridAreaSplitter splitter = new GridAreaSplitter(cursor);

        BlocksRuleGenerator.generate(splitter, random, Color.ORANGE, Color.BLUE, 0.3f, 0.5f, 0);
        List<BlocksRule> blocks = new ArrayList<>();
        for (Area area : splitter.areaList) {
            for (Tile tile : area.tiles) {
                if (tile.getRule() instanceof BlocksRule) {
                    blocks.add((BlocksRule) tile.getRule());
                }
            }
        }

        // Mutate one blocks' color to purple so that it can be paired with sun rule
        if (blocks.size() > 0 && random.nextFloat() < 0.5f) {
            blocks.get(random.nextInt(blocks.size())).color = Color.PURPLE;
        }

        SunRuleGenerator.generate(splitter, random, Arrays.asList(Color.PURPLE), 1f, 0.8f, 1.0f);

        return new PuzzleRenderer(game, puzzle);
    }

    @Override
    public Difficulty getDifficulty() {
        return Difficulty.VERY_HARD;
    }

    @Override
    public String getName() {
        return "Treehouse #5";
    }
}
