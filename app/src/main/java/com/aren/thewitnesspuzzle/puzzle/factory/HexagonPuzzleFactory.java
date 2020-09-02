package com.aren.thewitnesspuzzle.puzzle.factory;

import android.content.Context;

import com.aren.thewitnesspuzzle.game.Game;
import com.aren.thewitnesspuzzle.puzzle.GridPuzzle;
import com.aren.thewitnesspuzzle.puzzle.Puzzle;
import com.aren.thewitnesspuzzle.puzzle.color.PalettePreset;
import com.aren.thewitnesspuzzle.puzzle.cursor.Cursor;
import com.aren.thewitnesspuzzle.puzzle.graph.Vertex;
import com.aren.thewitnesspuzzle.puzzle.rules.BrokenLineRule;
import com.aren.thewitnesspuzzle.puzzle.rules.HexagonRule;
import com.aren.thewitnesspuzzle.puzzle.walker.RandomGridWalker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class HexagonPuzzleFactory extends PuzzleFactory {
    public HexagonPuzzleFactory(Context context) {
        super(context);
    }

    @Override
    public Puzzle generate(Game game, Random random) {
        GridPuzzle puzzle = new GridPuzzle(game, PalettePreset.get("Green_1"), 4, 4);

        List<Vertex> borderVertices = puzzle.getBorderVertices();
        List<Vertex> innerVertices = puzzle.getInnerVertices();
        Collections.shuffle(borderVertices, random);
        Collections.shuffle(innerVertices, random);

        Vertex start = innerVertices.get(0);
        Vertex end = borderVertices.get(0);

        puzzle.addStartingPoint(start.gridPosition.x, start.gridPosition.y);
        puzzle.addEndingPoint(end.gridPosition.x, end.gridPosition.y);

        RandomGridWalker walker = new RandomGridWalker(puzzle, random, 10, start.gridPosition.x, start.gridPosition.y, end.gridPosition.x, end.gridPosition.y);
        ArrayList<Vertex> vertexPositions = walker.getResult();

        Cursor cursor = new Cursor(puzzle, vertexPositions, null);

        HexagonRule.generate(cursor, random, 0.5f);
        BrokenLineRule.generate(cursor, random, 0.4f);

        return puzzle;
    }

    @Override
    public Difficulty getDifficulty() {
        return Difficulty.EASY;
    }

    @Override
    public String getName(){
        return "Green Panel";
    }
}
