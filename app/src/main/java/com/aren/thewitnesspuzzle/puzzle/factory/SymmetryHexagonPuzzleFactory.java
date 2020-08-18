package com.aren.thewitnesspuzzle.puzzle.factory;

import com.aren.thewitnesspuzzle.puzzle.Game;
import com.aren.thewitnesspuzzle.puzzle.GridSymmetryPuzzle;
import com.aren.thewitnesspuzzle.puzzle.Puzzle;
import com.aren.thewitnesspuzzle.puzzle.color.PalettePreset;
import com.aren.thewitnesspuzzle.puzzle.cursor.SymmetryCursor;
import com.aren.thewitnesspuzzle.puzzle.graph.Vertex;
import com.aren.thewitnesspuzzle.puzzle.rules.BrokenLineRule;
import com.aren.thewitnesspuzzle.puzzle.rules.HexagonRule;
import com.aren.thewitnesspuzzle.puzzle.walker.RandomGridWalker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class SymmetryHexagonPuzzleFactory extends PuzzleFactory{
    @Override
    public Puzzle generate(Game game, Random random) {
        GridSymmetryPuzzle symmetryPuzzle = new GridSymmetryPuzzle(game, PalettePreset.get("SymmetryIsland_1"), 5, 5, GridSymmetryPuzzle.SymmetryType.POINT, true);

        List<Vertex> borderVertices = symmetryPuzzle.getBorderVertices();
        borderVertices.remove(symmetryPuzzle.getVertexAt(0, 0));
        borderVertices.remove(symmetryPuzzle.getVertexAt(5, 5));
        Collections.shuffle(borderVertices, random);

        Vertex end = borderVertices.get(0);

        symmetryPuzzle.addStartingPoint(0, 0);
        symmetryPuzzle.addEndingPoint(end.gridPosition.x, end.gridPosition.y);

        RandomGridWalker walker = new RandomGridWalker(symmetryPuzzle, random, 10, 0, 0, end.gridPosition.x, end.gridPosition.y);
        ArrayList<Vertex> vertexPositions = walker.getResult();

        SymmetryCursor cursor = new SymmetryCursor(symmetryPuzzle, vertexPositions, null);

        HexagonRule.generate(cursor, random, 0.3f);
        BrokenLineRule.generate(cursor, random, 0.1f);

        return symmetryPuzzle;
    }

    @Override
    public Difficulty getDifficulty() {
        return Difficulty.NORMAL;
    }

    @Override
    public String getName(){
        return "Symmetry Island #1";
    }
}
