package com.aren.thewitnesspuzzle.puzzle.factory;

import com.aren.thewitnesspuzzle.puzzle.ColorFactory;
import com.aren.thewitnesspuzzle.puzzle.Game;
import com.aren.thewitnesspuzzle.puzzle.GridSymmetryPuzzle;
import com.aren.thewitnesspuzzle.puzzle.Puzzle;
import com.aren.thewitnesspuzzle.puzzle.cursor.Cursor;
import com.aren.thewitnesspuzzle.puzzle.cursor.SymmetryCursor;
import com.aren.thewitnesspuzzle.puzzle.graph.Vertex;
import com.aren.thewitnesspuzzle.puzzle.rules.BrokenLineRule;
import com.aren.thewitnesspuzzle.puzzle.walker.RandomGridWalker;

import java.util.ArrayList;
import java.util.Random;

public class SimpleSymmetryPuzzleFactory implements PuzzleFactory {

    @Override
    public Puzzle generate(Game game, Random random) {
        GridSymmetryPuzzle.SymmetryType symmetryType = random.nextFloat() > 0.5f ? GridSymmetryPuzzle.SymmetryType.VLINE : GridSymmetryPuzzle.SymmetryType.POINT;
        GridSymmetryPuzzle symmetryPuzzle;

        int startX, startY, endX, endY;

        if(symmetryType == GridSymmetryPuzzle.SymmetryType.VLINE){
            symmetryPuzzle = new GridSymmetryPuzzle(game, 5, 7, symmetryType, false);

            startX = random.nextInt(2);
            startY = 0;
            endX = random.nextInt(2);
            endY = 7;

        }
        else{
            symmetryPuzzle = new GridSymmetryPuzzle(game, 6, 6, symmetryType, false);

            startX = 0;
            startY = 0;
            endX = random.nextInt(6);
            endY = 6;
        }

        ColorFactory.setRandomColor(symmetryPuzzle);

        symmetryPuzzle.addStartingPoint(startX, startY);
        symmetryPuzzle.addEndingPoint(endX, endY);

        RandomGridWalker walker = new RandomGridWalker(symmetryPuzzle, random, startX, startY, endX, endY);
        ArrayList<Vertex> vertexPositions = walker.getResult();

        SymmetryCursor cursor = new SymmetryCursor(symmetryPuzzle, vertexPositions, null);

        BrokenLineRule.generate(cursor, random, 0.7f);

        return symmetryPuzzle;
    }

    @Override
    public Difficulty getDifficulty() {
        return null;
    }
}
