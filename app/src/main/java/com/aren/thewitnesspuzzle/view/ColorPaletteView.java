package com.aren.thewitnesspuzzle.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;

import com.aren.thewitnesspuzzle.core.color.PuzzleColorPalette;
import com.aren.thewitnesspuzzle.core.math.MathUtils;

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
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.BLACK);
        if (palette != null) {
            // int[] colors = new int[]{palette.getBackgroundColor(), palette.getPathColor(), palette.getCursorColor(), palette.getCursorSucceededColor(), palette.getCursorFailedColor()};
            paint.setStyle(Paint.Style.FILL);

            int w = getWidth();
            int h = getHeight();

            paint.setColor(palette.getBackgroundColor());
            canvas.drawRect(0, 0, w, h / 2f, paint);

            paint.setColor(palette.getTileColor());
            canvas.drawRect(0, h / 2f, w, h, paint);

            int iw = w - 40;
            int ih = h - 40;

            paint.setColor(palette.getPathColor());
            canvas.drawRect(20, 20, w - 20 - iw / 3f * 2, h - 20, paint);

            paint.setColor(palette.getCursorColor());
            canvas.drawRect(20 + iw / 3f, 20, w - 20 - iw / 3f, h - 20, paint);

            paint.setColor(palette.getCursorSucceededColor());
            canvas.drawRect(20 + iw / 3 * 2, 20, w - 20, h - 20 - ih / 2f, paint);

            paint.setColor(palette.getCursorFailedColor());
            canvas.drawRect(20 + iw / 3f * 2, 20 + ih / 2f, w - 20, h - 20, paint);

            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(7f);
            paint.setColor(0xffffffff);
            canvas.drawRect(0, 0, w, h, paint);
        }
        super.onDraw(canvas);
    }

    public void setPalette(PuzzleColorPalette palette) {
        this.palette = palette;
    }
}
