package com.aren.thewitnesspuzzle.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;

import com.aren.thewitnesspuzzle.math.MathUtils;
import com.aren.thewitnesspuzzle.puzzle.color.PalettePreset;
import com.aren.thewitnesspuzzle.puzzle.color.PuzzleColorPalette;

public class ColorPaletteView extends androidx.appcompat.widget.AppCompatImageView {

    Paint paint;

    PuzzleColorPalette palette;

    public ColorPaletteView(Context context) {
        this(context, null);
    }

    public ColorPaletteView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);

        paint = new Paint();
        paint.setColor(Color.LTGRAY);
    }

    @Override
    protected void onDraw(Canvas canvas){
        canvas.drawColor(Color.BLACK);
        if(palette != null){
            int[] colors = new int[]{palette.getBackgroundColor(), palette.getPathColor(), palette.actualCursorColor.getOriginalValue(), palette.getCursorSucceededColor(), palette.getCursorFailedColor()};
            paint.setStyle(Paint.Style.FILL);
            for(int i = 0; i < 5; i++){
                paint.setColor(colors[i]);
                canvas.drawRect(MathUtils.lerp(0, canvas.getWidth(), i / 5f), 0, MathUtils.lerp(0, canvas.getWidth(), (i + 1) / 5f), canvas.getHeight(), paint);
            }
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(10f);
            paint.setColor(0xffffffff);
            canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), paint);
        }
        super.onDraw(canvas);
    }

    public void setPalette(PuzzleColorPalette palette){
        this.palette = palette;
    }
}
