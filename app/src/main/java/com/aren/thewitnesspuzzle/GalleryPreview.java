package com.aren.thewitnesspuzzle;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Handler;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.aren.thewitnesspuzzle.puzzle.Game;
import com.aren.thewitnesspuzzle.puzzle.Puzzle;
import com.aren.thewitnesspuzzle.puzzle.factory.Difficulty;
import com.aren.thewitnesspuzzle.puzzle.factory.PuzzleFactory;

import java.util.Random;
import java.util.UUID;

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
