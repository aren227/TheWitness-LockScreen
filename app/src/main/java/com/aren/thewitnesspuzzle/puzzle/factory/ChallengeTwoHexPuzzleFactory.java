package com.aren.thewitnesspuzzle.puzzle.factory;

import android.content.Context;
import android.graphics.Color;

import com.aren.thewitnesspuzzle.core.color.PalettePreset;
import com.aren.thewitnesspuzzle.core.cursor.Cursor;
import com.aren.thewitnesspuzzle.core.graph.Edge;
import com.aren.thewitnesspuzzle.core.graph.Vertex;
import com.aren.thewitnesspuzzle.core.puzzle.GridPuzzle;
import com.aren.thewitnesspuzzle.core.rules.HexagonRule;
import com.aren.thewitnesspuzzle.game.Game;
import com.aren.thewitnesspuzzle.puzzle.factory.spawn.SpawnByCount;
import com.aren.thewitnesspuzzle.puzzle.generator.BrokenLineRuleGenerator;
import com.aren.thewitnesspuzzle.puzzle.generator.HexagonRuleGenerator;
import com.aren.thewitnesspuzzle.puzzle.walker.FastGridTreeWalker;
import com.aren.thewitnesspuzzle.render.PuzzleRenderer;

import java.util.ArrayList;
import java.util.Random;

public class ChallengeTwoHexPuzzleFactory extends PuzzleFactory {
    public ChallengeTwoHexPuzzleFactory(Context context) {
        super(context);
    }

    @Override
    public PuzzleRenderer generate(Game game, Random random) {
        GridPuzzle puzzle = new GridPuzzle(PalettePreset.get("Challenge_4"), 5, 5);

        puzzle.addStartingPoint(0, 0);
        puzzle.addEndingPoint(5, 5);

        FastGridTreeWalker walker = FastGridTreeWalker.getLongest(puzzle, random, 5, 0, 0, 5, 5);
        ArrayList<Vertex> vertexPositions = walker.getResult();

        Cursor cursor = new Cursor(puzzle, vertexPositions, null);

        BrokenLineRuleGenerator.generate(cursor, random, 0.7f);
        HexagonRuleGenerator.generate(cursor, random, new SpawnByCount(2), true);

        for(Edge edge : puzzle.getEdges()){
            if(edge.getRule() instanceof HexagonRule){
                ((HexagonRule)edge.getRule()).setOverrideColor(Color.parseColor("#edac34"));
            }
        }

        return new PuzzleRenderer(game, puzzle);
    }

    @Override
    public Difficulty getDifficulty() {
        return Difficulty.EASY;
    }

    @Override
    public String getName() {
        return "Challenge #4";
    }
}
