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
import com.aren.thewitnesspuzzle.activity.CreatePatternActivity;
import com.aren.thewitnesspuzzle.activity.CreateRandomPuzzleActivity;

import androidx.annotation.NonNull;

public class NewProfileDialog extends Dialog {

    private View.OnClickListener onDefaultClicked;
    private View.OnClickListener onSequenceClicked;

    public NewProfileDialog(@NonNull Context context, View.OnClickListener onDefaultClicked, View.OnClickListener onSequenceClicked) {
        super(context);
        this.onDefaultClicked = onDefaultClicked;
        this.onSequenceClicked = onSequenceClicked;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        layoutParams.dimAmount = 0.8f;
        getWindow().setAttributes(layoutParams);

        setContentView(R.layout.new_profile_dialog);

        final RadioButton defaultRadioButton = findViewById(R.id.new_profile_radio_default);
        final RadioButton sequenceRadioButton = findViewById(R.id.new_profile_radio_sequence);

        TextView cancelText = findViewById(R.id.new_profile_cancel);
        cancelText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        TextView createText = findViewById(R.id.new_profile_create);
        createText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (defaultRadioButton.isChecked()) {
                    dismiss();
                    onDefaultClicked.onClick(null);
                } else if (sequenceRadioButton.isChecked()) {
                    dismiss();
                    onSequenceClicked.onClick(null);
                }
            }
        });
    }
}
