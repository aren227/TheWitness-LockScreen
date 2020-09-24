package com.aren.thewitnesspuzzle.puzzle.factory;

import android.content.Context;

import com.aren.thewitnesspuzzle.game.Game;
import com.aren.thewitnesspuzzle.math.Vector2Int;
import com.aren.thewitnesspuzzle.puzzle.GridSymmetryPuzzle;
import com.aren.thewitnesspuzzle.puzzle.Puzzle;
import com.aren.thewitnesspuzzle.puzzle.color.PalettePreset;
import com.aren.thewitnesspuzzle.puzzle.cursor.SymmetryCursor;
import com.aren.thewitnesspuzzle.puzzle.factory.spawn.SpawnByCount;
import com.aren.thewitnesspuzzle.puzzle.graph.Vertex;
import com.aren.thewitnesspuzzle.puzzle.rules.BrokenLineRule;
import com.aren.thewitnesspuzzle.puzzle.rules.HexagonRule;
import com.aren.thewitnesspuzzle.puzzle.walker.RandomGridWalker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class ChallengeSymmetryPuzzleFactory extends PuzzleFactory {
    public ChallengeSymmetryPuzzleFactory(Context context) {
        super(context);
    }

    @Override
    public Puzzle generate(Game game, Random random) {
        GridSymmetryPuzzle symmetryPuzzle = new GridSymmetryPuzzle(game, PalettePreset.get("Challenge_1"), 6, 6, GridSymmetryPuzzle.SymmetryType.POINT, true);

        symmetryPuzzle.addStartingPoint(0, 0);

        Vector2Int endPoint;
        if(random.nextFloat() > 0.5f) endPoint = new Vector2Int(0, 6);
        else endPoint = new Vector2Int(6, 0);

        symmetryPuzzle.addEndingPoint(endPoint.x, endPoint.y);

        RandomGridWalker walker = new RandomGridWalker(symmetryPuzzle, random, 10, 0, 0, endPoint.x, endPoint.y);
        ArrayList<Vertex> vertexPositions = walker.getResult();

        SymmetryCursor cursor = new SymmetryCursor(symmetryPuzzle, vertexPositions, null);

        HexagonRule.generate(cursor, random, new SpawnByCount(2), new SpawnByCount(2), new SpawnByCount(2));
        BrokenLineRule.generate(cursor, random, new SpawnByCount(6));

        return symmetryPuzzle;
    }

    @Override
    public Difficulty getDifficulty() {
        return Difficulty.NORMAL;
    }

    @Override
    public String getName() {
        return "Challenge #6";
    }
}
