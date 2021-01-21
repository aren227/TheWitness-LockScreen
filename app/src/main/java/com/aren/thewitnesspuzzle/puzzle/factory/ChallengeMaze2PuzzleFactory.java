package com.aren.thewitnesspuzzle.puzzle.factory;

import android.content.Context;

import com.aren.thewitnesspuzzle.core.color.PalettePreset;
import com.aren.thewitnesspuzzle.core.cursor.Cursor;
import com.aren.thewitnesspuzzle.core.graph.Edge;
import com.aren.thewitnesspuzzle.core.graph.Vertex;
import com.aren.thewitnesspuzzle.core.puzzle.GridPuzzle;
import com.aren.thewitnesspuzzle.core.rules.BrokenLineRule;
import com.aren.thewitnesspuzzle.game.Game;
import com.aren.thewitnesspuzzle.puzzle.generator.BrokenLineRuleGenerator;
import com.aren.thewitnesspuzzle.puzzle.walker.FastGridTreeWalker;
import com.aren.thewitnesspuzzle.render.PuzzleRenderer;

import java.util.ArrayList;
import java.util.Random;

public class ChallengeMaze2PuzzleFactory extends PuzzleFactory {
    public ChallengeMaze2PuzzleFactory(Context context) {
        super(context);
    }

    @Override
    public PuzzleRenderer generate(Game game, Random random) {
        GridPuzzle puzzle = new GridPuzzle(PalettePreset.get("Challenge_3"), 7, 7);

        puzzle.addStartingPoint(0, 0);
        puzzle.addEndingPoint(7, 7);

        FastGridTreeWalker walker = FastGridTreeWalker.getLongest(puzzle, random, 5,0, 0, 7, 7);
        ArrayList<Vertex> vertexPositions = walker.getResult();

        Cursor cursor = new Cursor(puzzle, vertexPositions, null);

        BrokenLineRuleGenerator.generate(cursor, random, 0.7f);

        // Thin
        for(Edge edge : puzzle.getEdges()){
            if(edge.getRule() instanceof BrokenLineRule){
                BrokenLineRule brokenLineRule = (BrokenLineRule)edge.getRule();
                brokenLineRule.setOverrideCollisionCircleRadius(brokenLineRule.getCollisionCircleRadius() / 3f);
            }
        }

        return new PuzzleRenderer(game, puzzle);
    }

    @Override
    public Difficulty getDifficulty() {
        return Difficulty.VERY_EASY;
    }

    @Override
    public String getName() {
        return "Challenge #2";
    }
}
