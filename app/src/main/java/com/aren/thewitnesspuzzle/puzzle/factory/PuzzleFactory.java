package com.aren.thewitnesspuzzle.puzzle.factory;

import android.graphics.Bitmap;

import com.aren.thewitnesspuzzle.game.Game;
import com.aren.thewitnesspuzzle.puzzle.Puzzle;

import java.util.Random;
import java.util.UUID;

public abstract class PuzzleFactory {

    private Bitmap thumbnailCache;

    public abstract Puzzle generate(Game game, Random random);

    public Difficulty getDifficulty(){
        return null;
    }

    public abstract String getName();

    public UUID getUuid(){
        return UUID.nameUUIDFromBytes(getClass().getName().getBytes());
    }

    public void setThumbnailCache(Bitmap bitmap){
        thumbnailCache = bitmap;
    }

    public Bitmap getThumbnailCache(){
        return thumbnailCache;
    }
}
