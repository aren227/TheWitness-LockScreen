package com.aren.thewitnesspuzzle.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.aren.thewitnesspuzzle.R;
import com.aren.thewitnesspuzzle.core.rules.Symmetry;
import com.aren.thewitnesspuzzle.core.rules.SymmetryColor;
import com.aren.thewitnesspuzzle.core.rules.SymmetryType;

import androidx.annotation.NonNull;

public class SymmetryDialog extends Dialog {

    Symmetry targetSymmetry;
    SymmetryDialogResult symmetryDialogResult;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        layoutParams.dimAmount = 0.8f;
        getWindow().setAttributes(layoutParams);

        setContentView(R.layout.symmetry_dialog);

        String title;
        String desc;
        if (targetSymmetry == null) {
            title = "Remove Symmetry";
            desc = "This operation will delete colored hexagons.";
        } else if (targetSymmetry.type == SymmetryType.VLINE) {
            title = "Apple Vertical Symmetry";
            desc = "This operation will delete start & end points.";
        } else {
            title = "Apple Rotational Symmetry";
            desc = "This operation will delete start & end points.";
        }

        ((TextView) findViewById(R.id.title)).setText(title);
        ((TextView) findViewById(R.id.description)).setText(desc);

        // Why it allows multiple selection?
        final RadioGroup lineColorRadioGroup = findViewById(R.id.line_color_radiogroup);
        final RadioButton lineColorNone = findViewById(R.id.line_color_none);
        final RadioButton lineColorLight = findViewById(R.id.line_color_light);
        final RadioButton lineColorDark = findViewById(R.id.line_color_dark);

        if (targetSymmetry != null && targetSymmetry.type == SymmetryType.POINT) {
            findViewById(R.id.line_color_container).setVisibility(View.VISIBLE);

            lineColorNone.setChecked(true);

            lineColorNone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    lineColorNone.setChecked(true);
                    lineColorLight.setChecked(false);
                    lineColorDark.setChecked(false);
                }
            });
            lineColorLight.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    lineColorNone.setChecked(false);
                    lineColorLight.setChecked(true);
                    lineColorDark.setChecked(false);
                }
            });
            lineColorDark.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    lineColorNone.setChecked(false);
                    lineColorLight.setChecked(false);
                    lineColorDark.setChecked(true);
                }
            });
        } else {
            findViewById(R.id.line_color_container).setVisibility(View.GONE);
        }

        findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                symmetryDialogResult.result(false, targetSymmetry);
                dismiss();
            }
        });

        findViewById(R.id.apply).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (targetSymmetry != null && targetSymmetry.type == SymmetryType.POINT) {
                    if (lineColorNone.isChecked()) {
                        targetSymmetry.color = SymmetryColor.NONE;
                    } else if (lineColorLight.isChecked()) {
                        targetSymmetry.color = SymmetryColor.CYAN;
                    } else {
                        targetSymmetry.color = SymmetryColor.CYAN2;
                    }
                }
                symmetryDialogResult.result(true, targetSymmetry);
                dismiss();
            }
        });
    }

    public SymmetryDialog(@NonNull Context context, Symmetry targetSymmetry, SymmetryDialogResult symmetryDialogResult) {
        super(context);
        this.targetSymmetry = targetSymmetry;
        this.symmetryDialogResult = symmetryDialogResult;
    }

    public interface SymmetryDialogResult {

        void result(boolean apply, Symmetry symmetry);

    }
}
