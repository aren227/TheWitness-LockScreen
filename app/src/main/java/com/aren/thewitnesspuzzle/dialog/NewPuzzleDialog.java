package com.aren.thewitnesspuzzle.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.TextView;

import com.aren.thewitnesspuzzle.R;
import com.aren.thewitnesspuzzle.activity.CreateCustomPuzzleActivity;
import com.aren.thewitnesspuzzle.activity.CreatePatternActivity;
import com.aren.thewitnesspuzzle.activity.CreateRandomPuzzleActivity;

import java.util.UUID;

import androidx.annotation.NonNull;

public class NewPuzzleDialog extends Dialog {
    private UUID folderUuid;

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
        final RadioButton customRadioButton = findViewById(R.id.new_puzzle_custom);

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
                Intent intent = null;
                if (patternRadioButton.isChecked()) {
                    dismiss();
                    intent = new Intent(getContext(), CreatePatternActivity.class);
                } else if (randomRadioButton.isChecked()) {
                    dismiss();
                    intent = new Intent(getContext(), CreateRandomPuzzleActivity.class);
                } else if (customRadioButton.isChecked()) {
                    dismiss();
                    intent = new Intent(getContext(), CreateCustomPuzzleActivity.class);
                }

                if (intent != null) {
                    intent.putExtra("folderUuid", folderUuid);
                    getContext().startActivity(intent);
                }
            }
        });
    }

    public NewPuzzleDialog(@NonNull Context context, UUID folderUuid) {
        super(context);

        this.folderUuid = folderUuid;
    }
}
