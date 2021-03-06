package com.aren.thewitnesspuzzle.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.aren.thewitnesspuzzle.R;
import com.aren.thewitnesspuzzle.core.color.PalettePreset;
import com.aren.thewitnesspuzzle.core.color.PuzzleColorPalette;
import com.aren.thewitnesspuzzle.view.ColorPaletteView;

import androidx.annotation.NonNull;

public class ColorPaletteDialog extends Dialog {

    public PuzzleColorPalette palette;

    public View backgroundColorView;
    public View tileColorView;
    public View pathColorView;
    public View lineColorView;
    public View successColorView;
    public View failureColorView;
    public SeekBar bloomIntensitySeekBar;

    int[] backgroundColorRef;
    int[] tileColorRef;
    int[] pathColorRef;
    int[] lineColorRef;
    int[] successColorRef;
    int[] failureColorRef;

    public TextView cancelTextView, applyTextView;
    public ImageView copyFromBGImageView;

    public GridLayout presetList;

    public Runnable onExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        layoutParams.dimAmount = 0.8f;
        getWindow().setAttributes(layoutParams);

        setContentView(R.layout.color_palette_dialog);

        backgroundColorView = findViewById(R.id.background_color);
        tileColorView = findViewById(R.id.tile_color);
        pathColorView = findViewById(R.id.path_color);
        lineColorView = findViewById(R.id.line_color);
        successColorView = findViewById(R.id.success_color);
        failureColorView = findViewById(R.id.failure_color);
        bloomIntensitySeekBar = findViewById(R.id.bloom_intensity);
        presetList = findViewById(R.id.presets);
        cancelTextView = findViewById(R.id.cancel);
        applyTextView = findViewById(R.id.apply);
        applyTextView = findViewById(R.id.apply);

        copyFromBGImageView = findViewById(R.id.copy_from_bg);

        final Runnable onColorPickerExit = new Runnable() {
            @Override
            public void run() {
                updateColors();
            }
        };
        backgroundColorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorPickerDialog dialog = new ColorPickerDialog(getContext(), backgroundColorRef, onColorPickerExit);
                dialog.show();
            }
        });
        tileColorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorPickerDialog dialog = new ColorPickerDialog(getContext(), tileColorRef, onColorPickerExit);
                dialog.show();
            }
        });
        pathColorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorPickerDialog dialog = new ColorPickerDialog(getContext(), pathColorRef, onColorPickerExit);
                dialog.show();
            }
        });
        lineColorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorPickerDialog dialog = new ColorPickerDialog(getContext(), lineColorRef, onColorPickerExit);
                dialog.show();
            }
        });
        successColorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorPickerDialog dialog = new ColorPickerDialog(getContext(), successColorRef, onColorPickerExit);
                dialog.show();
            }
        });
        failureColorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorPickerDialog dialog = new ColorPickerDialog(getContext(), failureColorRef, onColorPickerExit);
                dialog.show();
            }
        });
        bloomIntensitySeekBar.setMax(100);
        bloomIntensitySeekBar.setProgress((int) (palette.getBloomIntensity() * 100));

        copyFromBGImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tileColorRef[0] = backgroundColorRef[0];
                updateColors();
            }
        });

        updateColors();

        for (final PuzzleColorPalette palette : PalettePreset.getAll()) {
            ColorPaletteView colorPaletteView = new ColorPaletteView(getContext());
            colorPaletteView.setPalette(palette);
            //colorPaletteView.setAdjustViewBounds(true);
            //colorPaletteView.setScaleType(ImageView.ScaleType.FIT_XY);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(300, 70);
            params.setMargins(16, 16, 16, 16);
            presetList.addView(colorPaletteView, params);

            colorPaletteView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    backgroundColorRef[0] = palette.getBackgroundColor();
                    tileColorRef[0] = palette.getTileColor();
                    pathColorRef[0] = palette.getPathColor();
                    lineColorRef[0] = palette.getCursorColor();
                    successColorRef[0] = palette.getCursorSucceededColor();
                    failureColorRef[0] = palette.getCursorFailedColor();
                    bloomIntensitySeekBar.setProgress((int)(palette.getBloomIntensity() * 100));
                    updateColors();
                }
            });
        }

        cancelTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        applyTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                palette.setBackgroundColor(backgroundColorRef[0]);
                palette.setTileColor(tileColorRef[0]);
                palette.setPathColor(pathColorRef[0]);
                palette.setCursorColor(lineColorRef[0]);
                palette.setCursorSucceededColor(successColorRef[0]);
                palette.setCursorFailedColor(failureColorRef[0]);
                palette.setBloomIntensity(bloomIntensitySeekBar.getProgress() / 100f);
                if (onExit != null) onExit.run();
                dismiss();
            }
        });
    }

    public ColorPaletteDialog(@NonNull Context context, PuzzleColorPalette palette, Runnable onExit) {
        super(context);
        this.palette = palette;
        this.onExit = onExit;

        backgroundColorRef = new int[]{palette.getBackgroundColor()};
        tileColorRef = new int[]{palette.getTileColor()};
        pathColorRef = new int[]{palette.getPathColor()};
        lineColorRef = new int[]{palette.getCursorColor()};
        successColorRef = new int[]{palette.getCursorSucceededColor()};
        failureColorRef = new int[]{palette.getCursorFailedColor()};
    }

    public void updateColors() {
        backgroundColorView.setBackgroundColor(backgroundColorRef[0]);
        tileColorView.setBackgroundColor(tileColorRef[0]);
        pathColorView.setBackgroundColor(pathColorRef[0]);
        lineColorView.setBackgroundColor(lineColorRef[0]);
        successColorView.setBackgroundColor(successColorRef[0]);
        failureColorView.setBackgroundColor(failureColorRef[0]);
    }
}
