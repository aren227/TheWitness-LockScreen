package com.aren.thewitnesspuzzle.gallery;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.aren.thewitnesspuzzle.puzzle.factory.Difficulty;
import com.aren.thewitnesspuzzle.puzzle.factory.PuzzleFactory;

public class GalleryPuzzlePreview {

    public PuzzleFactory puzzleFactory;
    public Bitmap bitmap;
    public String name;
    public Difficulty difficulty;

    public GalleryPuzzlePreview(PuzzleFactory puzzleFactory, Bitmap bitmap, String name) {
        this.puzzleFactory = puzzleFactory;
        this.bitmap = bitmap;
        this.name = name;
    }

    public Difficulty getDifficulty() {
        return puzzleFactory.getDifficulty();
    }

}
