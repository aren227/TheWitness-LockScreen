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
    public boolean isForAddBtn;

    public GalleryPuzzlePreview(PuzzleFactory puzzleFactory, Bitmap bitmap, String name) {
        this.puzzleFactory = puzzleFactory;
        this.bitmap = bitmap;
        this.name = name;
    }

    public static GalleryPuzzlePreview addButton() {
        Bitmap add = Bitmap.createBitmap(512, 512, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(add);
        canvas.drawColor(Color.DKGRAY);

        Paint paint = new Paint();
        paint.setColor(Color.LTGRAY);
        paint.setStrokeWidth(10f);

        canvas.drawRect(256 - 16, 128, 256 + 16, 512 - 128, paint);
        canvas.drawRect(128, 256 - 16, 512 - 128, 256 + 16, paint);
        canvas.drawBitmap(add, 0, 0, null);

        GalleryPuzzlePreview preview = new GalleryPuzzlePreview(null, add, "");
        preview.isForAddBtn = true;
        return preview;
    }

    public Difficulty getDifficulty() {
        return puzzleFactory.getDifficulty();
    }

}
