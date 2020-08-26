package com.aren.thewitnesspuzzle;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.GridLayout;
import android.widget.LinearLayout;

import com.aren.thewitnesspuzzle.puzzle.color.PalettePreset;
import com.aren.thewitnesspuzzle.puzzle.color.PuzzleColorPalette;

import androidx.annotation.NonNull;

public class ColorPaletteDialog extends Dialog {

    public PuzzleColorPalette palette;

    public View backgroundColorView;
    public View pathColorView;
    public View lineColorView;
    public View successColorView;
    public View failureColorView;

    public GridLayout presetList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        layoutParams.dimAmount = 0.8f;
        getWindow().setAttributes(layoutParams);

        setContentView(R.layout.color_palette_dialog);

        backgroundColorView = findViewById(R.id.background_color);
        pathColorView = findViewById(R.id.path_color);
        lineColorView = findViewById(R.id.line_color);
        successColorView = findViewById(R.id.success_color);
        failureColorView = findViewById(R.id.failure_color);
        presetList = findViewById(R.id.presets);

        backgroundColorView.setBackgroundColor(palette.getBackgroundColor());
        pathColorView.setBackgroundColor(palette.getPathColor());
        lineColorView.setBackgroundColor(palette.actualCursorColor.getOriginalValue());
        successColorView.setBackgroundColor(palette.getCursorSucceededColor());
        failureColorView.setBackgroundColor(palette.getCursorFailedColor());

        for(PuzzleColorPalette palette : PalettePreset.getAll()){
            ColorPaletteView colorPaletteView = new ColorPaletteView(getContext());
            colorPaletteView.setPalette(palette);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(400, 100);
            params.setMargins(16, 16, 16, 16);
            presetList.addView(colorPaletteView, params);
        }
    }

    public ColorPaletteDialog(@NonNull Context context, PuzzleColorPalette palette) {
        super(context);
        this.palette = palette;
    }
}
