package com.aren.thewitnesspuzzle.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.TextView;

import com.aren.thewitnesspuzzle.activity.CreatePatternActivity;
import com.aren.thewitnesspuzzle.activity.CreateRandomPuzzleActivity;
import com.aren.thewitnesspuzzle.R;

import androidx.annotation.NonNull;

public class NewPuzzleDialog extends Dialog {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        layoutParams.dimAmount = 0.8f;
        getWindow().setAttributes(layoutParams);

        setContentView(R.layout.new_puzzle_dialog);

        final RadioButton patternRadioButton = findViewById(R.id.new_puzzle_radio_pattern);
        final RadioButton randomRadioButton = findViewById(R.id.new_puzzle_random);

        TextView cancelText = findViewById(R.id.new_puzzle_cancel);
        cancelText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        TextView createText = findViewById(R.id.new_puzzle_create);
        createText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(patternRadioButton.isChecked()){
                    dismiss();
                    Intent intent = new Intent(getContext(), CreatePatternActivity.class);
                    getContext().startActivity(intent);
                }
                else if(randomRadioButton.isChecked()){
                    dismiss();
                    Intent intent = new Intent(getContext(), CreateRandomPuzzleActivity.class);
                    getContext().startActivity(intent);
                }
            }
        });
    }

    public NewPuzzleDialog(@NonNull Context context) {
        super(context);
    }
}
