package com.aren.thewitnesspuzzle.puzzle.factory;

import com.aren.thewitnesspuzzle.game.Game;
import com.aren.thewitnesspuzzle.puzzle.Puzzle;

import java.util.Random;
import java.util.UUID;

public abstract class PuzzleFactory {

    public abstract Puzzle generate(Game game, Random random);

    public Difficulty getDifficulty(){
        return null;
    }

    public abstract String getName();

    public UUID getUuid(){
        return UUID.nameUUIDFromBytes(getClass().getName().getBytes());
    }
}
