package com.aren.thewitnesspuzzle.puzzle.factory;

import android.content.Context;

import com.aren.thewitnesspuzzle.game.Game;
import com.aren.thewitnesspuzzle.puzzle.GridPuzzle;
import com.aren.thewitnesspuzzle.puzzle.Puzzle;
import com.aren.thewitnesspuzzle.puzzle.color.PalettePreset;
import com.aren.thewitnesspuzzle.puzzle.cursor.Cursor;
import com.aren.thewitnesspuzzle.puzzle.cursor.area.Area;
import com.aren.thewitnesspuzzle.puzzle.cursor.area.GridAreaSplitter;
import com.aren.thewitnesspuzzle.puzzle.graph.Tile;
import com.aren.thewitnesspuzzle.puzzle.graph.Vertex;
import com.aren.thewitnesspuzzle.puzzle.rules.BlocksRule;
import com.aren.thewitnesspuzzle.puzzle.rules.Color;
import com.aren.thewitnesspuzzle.puzzle.rules.SquareRule;
import com.aren.thewitnesspuzzle.puzzle.rules.SunRule;
import com.aren.thewitnesspuzzle.puzzle.walker.RandomGridWalker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class SunBlockPuzzleFactory extends PuzzleFactory {
    public SunBlockPuzzleFactory(Context context) {
        super(context);
    }

    @Override
    public Puzzle generate(Game game, Random random) {
        GridPuzzle puzzle = new GridPuzzle(game, PalettePreset.get("Treehouse_3"), 5, 5);

        puzzle.addStartingPoint(0, 0);
        puzzle.addEndingPoint(5, 5);

        RandomGridWalker walker = new RandomGridWalker(puzzle, random, 5, 0, 0, 5, 5);
        ArrayList<Vertex> vertexPositions = walker.getResult();

        Cursor cursor = new Cursor(puzzle, vertexPositions, null);

        GridAreaSplitter splitter = new GridAreaSplitter(cursor);

        BlocksRule.generate(splitter, random, 0.3f, 0.5f);
        List<BlocksRule> blocks = new ArrayList<>();
        for(Area area : splitter.areaList){
            for(Tile tile : area.tiles){
                if(tile.getRule() instanceof BlocksRule){
                    ((BlocksRule)tile.getRule()).color = Color.ORANGE;
                    blocks.add((BlocksRule)tile.getRule());
                }
            }
        }

        // Mutate one blocks' color to purple so that it can be paired with sun rule
        if(blocks.size() > 0 && random.nextFloat() < 0.5f){
            blocks.get(random.nextInt(blocks.size())).color = Color.PURPLE;
        }

        SunRule.generate(splitter, random, new Color[]{Color.PURPLE}, 1f, 0.8f, 1.0f);

        return puzzle;
    }

    @Override
    public Difficulty getDifficulty() {
        return Difficulty.VERY_HARD;
    }

    @Override
    public String getName(){
        return "Treehouse #5";
    }
}
