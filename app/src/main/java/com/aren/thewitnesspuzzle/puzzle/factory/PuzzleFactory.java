package com.aren.thewitnesspuzzle.puzzle.factory;

import com.aren.thewitnesspuzzle.puzzle.Game;
import com.aren.thewitnesspuzzle.puzzle.Puzzle;

import java.util.Random;

public interface PuzzleFactory {

    PuzzleFactory[] factories = new PuzzleFactory[]{
            new MultipleSunColorsPuzzleFactory(),
            new RotatableBlocksPuzzleFactory(),
            new SecondPuzzleFactory(),
            new SimpleBlocksPuzzleFactory(),
            new SimpleHexagonEliminationPuzzleFactory(),
            new SimpleHexagonPuzzleFactory(),
            new SimpleMazePuzzleFactory(),
            new SimpleSquareEliminationPuzzleFactory(),
            new SimpleSquarePuzzleFactory(),
            new SimpleSunPuzzleFactory(),
            new SimpleSunSquarePuzzleFactory(),
            new SimpleSymmetryPuzzleFactory(),
            new SlidePuzzleFactory(),
            new SunPairWithSquarePuzzleFactory(),
            new SymmetryHexagonPuzzleFactory(),
    };


    Puzzle generate(Game game, Random random);

    Difficulty getDifficulty();
}
