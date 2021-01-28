package com.aren.thewitnesspuzzle.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.aren.thewitnesspuzzle.R;
import com.aren.thewitnesspuzzle.activity.GalleryActivity;
import com.aren.thewitnesspuzzle.puzzle.factory.PuzzleFactoryManager;

import androidx.annotation.NonNull;

public class FolderDialog extends Dialog {
    PuzzleFactoryManager puzzleFactoryManager;
    PuzzleFactoryManager.Folder folder;

    public FolderDialog(@NonNull Context context, PuzzleFactoryManager puzzleFactoryManager, PuzzleFactoryManager.Folder folder) {
        super(context);

        this.puzzleFactoryManager = puzzleFactoryManager;
        this.folder = folder;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        layoutParams.dimAmount = 0.8f;
        getWindow().setAttributes(layoutParams);

        setContentView(R.layout.folder_dialog);

        final TextView errorText = findViewById(R.id.error);
        errorText.setVisibility(View.GONE);

        final EditText editText = findViewById(R.id.folder_name);
        editText.setText(folder.getName());

        findViewById(R.id.folder_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        findViewById(R.id.folder_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Remove Folder");
                builder.setMessage("Are you sure?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        puzzleFactoryManager.removeFolder(folder);
                        puzzleFactoryManager.notifyObservers();
                        dismiss();
                    }
                });
                builder.setNegativeButton("No", null);
                AlertDialog dialog = builder.create();
                dialog.show();

                dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(0xff000000);
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(0xff000000);
            }
        });

        findViewById(R.id.folder_apply).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText.length() < 1) {
                    errorText.setVisibility(View.VISIBLE);
                    errorText.setText("Please enter a name.");
                } else {
                    folder.setName(editText.getText().toString());
                    puzzleFactoryManager.notifyObservers();
                    dismiss();
                }
            }
        });
    }
}
