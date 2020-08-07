package com.aren.thewitnesspuzzle.puzzle.factory;

import com.aren.thewitnesspuzzle.puzzle.Puzzle;

public abstract class PuzzleFactory {

    Puzzle puzzle;

    public PuzzleFactory(Puzzle puzzle){
        this.puzzle = puzzle;
    }

    public abstract void generate();

}
