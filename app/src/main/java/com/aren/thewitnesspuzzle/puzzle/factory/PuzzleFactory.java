package com.aren.thewitnesspuzzle.puzzle.factory;

import com.aren.thewitnesspuzzle.puzzle.Game;
import com.aren.thewitnesspuzzle.puzzle.Puzzle;

import java.util.Random;

public interface PuzzleFactory {

    PuzzleFactory[] factories = new PuzzleFactory[]{
            new MultipleSunColorsPuzzleFactory(),
    };

    Puzzle generate(Game game, Random random);

    Difficulty getDifficulty();
}
