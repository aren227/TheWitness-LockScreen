package com.aren.thewitnesspuzzle.puzzle.factory;

import android.content.Context;

import com.aren.thewitnesspuzzle.core.color.PalettePreset;
import com.aren.thewitnesspuzzle.core.cursor.SymmetryCursor;
import com.aren.thewitnesspuzzle.core.graph.Vertex;
import com.aren.thewitnesspuzzle.core.math.Vector2Int;
import com.aren.thewitnesspuzzle.core.puzzle.GridSymmetryPuzzle;
import com.aren.thewitnesspuzzle.core.rules.Symmetry;
import com.aren.thewitnesspuzzle.core.rules.SymmetryColor;
import com.aren.thewitnesspuzzle.core.rules.SymmetryType;
import com.aren.thewitnesspuzzle.game.Game;
import com.aren.thewitnesspuzzle.puzzle.factory.spawn.SpawnByCount;
import com.aren.thewitnesspuzzle.puzzle.generator.BrokenLineRuleGenerator;
import com.aren.thewitnesspuzzle.puzzle.generator.HexagonRuleGenerator;
import com.aren.thewitnesspuzzle.puzzle.walker.FastGridTreeWalker;
import com.aren.thewitnesspuzzle.render.PuzzleRenderer;

import java.util.ArrayList;
import java.util.Random;

public class ChallengeSymmetryPuzzleFactory extends PuzzleFactory {
    public ChallengeSymmetryPuzzleFactory(Context context) {
        super(context);
    }

    @Override
    public PuzzleRenderer generate(Game game, Random random) {
        GridSymmetryPuzzle symmetryPuzzle = new GridSymmetryPuzzle(PalettePreset.get("Challenge_1"), 6, 6, new Symmetry(SymmetryType.POINT, SymmetryColor.CYAN2));

        symmetryPuzzle.addStartingPoint(0, 0);

        Vector2Int endPoint;
        if(random.nextFloat() > 0.5f) endPoint = new Vector2Int(0, 6);
        else endPoint = new Vector2Int(6, 0);

        symmetryPuzzle.addEndingPoint(endPoint.x, endPoint.y);

        FastGridTreeWalker walker = FastGridTreeWalker.getLongest(symmetryPuzzle, random, 15,0, 0, endPoint.x, endPoint.y);
        ArrayList<Vertex> vertexPositions = walker.getResult();

        SymmetryCursor cursor = new SymmetryCursor(symmetryPuzzle, vertexPositions, null);

        HexagonRuleGenerator.generate(cursor, random, new SpawnByCount(2), new SpawnByCount(2), new SpawnByCount(2));
        BrokenLineRuleGenerator.generate(cursor, random, new SpawnByCount(6));

        return new PuzzleRenderer(game, symmetryPuzzle);
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
