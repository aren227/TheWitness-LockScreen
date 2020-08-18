package com.aren.thewitnesspuzzle;

import android.graphics.Bitmap;

import com.aren.thewitnesspuzzle.puzzle.factory.Difficulty;
import com.aren.thewitnesspuzzle.puzzle.factory.PuzzleFactory;

public class GalleryPreview {

    public PuzzleFactory puzzleFactory;
    public Bitmap bitmap;
    public String name;
    public Difficulty difficulty;

    public GalleryPreview(PuzzleFactory puzzleFactory, Bitmap bitmap, String name){
        this.puzzleFactory = puzzleFactory;
        this.bitmap = bitmap;
        this.name = name;
    }

    public Difficulty getDifficulty(){
        return puzzleFactory.getDifficulty();
    }

}
