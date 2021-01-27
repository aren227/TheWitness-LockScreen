package com.aren.thewitnesspuzzle.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.aren.thewitnesspuzzle.R;
import com.aren.thewitnesspuzzle.puzzle.factory.PuzzleFactoryManager;

import java.util.UUID;

import androidx.annotation.NonNull;

public class NewFolderDialog extends Dialog {
    PuzzleFactoryManager puzzleFactoryManager;
    UUID parentFolderUuid;

    public NewFolderDialog(@NonNull Context context, final PuzzleFactoryManager puzzleFactoryManager, final UUID parentFolderUuid) {
        super(context);

        this.puzzleFactoryManager = puzzleFactoryManager;
        this.parentFolderUuid = parentFolderUuid;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        layoutParams.dimAmount = 0.8f;
        getWindow().setAttributes(layoutParams);

        setContentView(R.layout.new_folder_dialog);

        final EditText editText = findViewById(R.id.folder_name);
        final TextView errorText = findViewById(R.id.error);
        errorText.setVisibility(View.GONE);

        findViewById(R.id.new_folder_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        findViewById(R.id.new_folder_create).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText.getText().length() < 1) {
                    errorText.setVisibility(View.VISIBLE);
                    errorText.setText("Please enter a name.");
                } else {
                    puzzleFactoryManager.createFolder(editText.getText().toString(), parentFolderUuid);
                    dismiss();
                }
            }
        });
    }
}
