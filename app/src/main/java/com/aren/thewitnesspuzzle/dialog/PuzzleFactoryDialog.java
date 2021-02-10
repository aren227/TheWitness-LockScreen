package com.aren.thewitnesspuzzle.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aren.thewitnesspuzzle.R;
import com.aren.thewitnesspuzzle.activity.CreateCustomPuzzleActivity;
import com.aren.thewitnesspuzzle.activity.CreatePatternActivity;
import com.aren.thewitnesspuzzle.activity.CreateRandomPuzzleActivity;
import com.aren.thewitnesspuzzle.activity.GalleryActivity;
import com.aren.thewitnesspuzzle.activity.PlayActivity;
import com.aren.thewitnesspuzzle.puzzle.factory.PuzzleFactory;
import com.aren.thewitnesspuzzle.puzzle.factory.PuzzleFactoryManager;

import java.util.UUID;

import androidx.annotation.NonNull;

public class PuzzleFactoryDialog extends Dialog {

    PuzzleFactory factory;
    GalleryActivity galleryActivity;
    UUID folderUuid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        layoutParams.dimAmount = 0.8f;
        getWindow().setAttributes(layoutParams);

        setContentView(R.layout.puzzle_factory_dialog);

        TextView nameTextView = findViewById(R.id.name);
        nameTextView.setText(factory.getName());

        if (factory.getConfig().getFactoryType() != null) {
            if (factory.getConfig().getFactoryType().equals("pattern")) {
                nameTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_gesture_24, 0);
            } else if (factory.getConfig().getFactoryType().equals("random")) {
                nameTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_casino_24, 0);
            } else {
                nameTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_create_24, 0);
            }
        }

        ImageView thumbnailImageView = findViewById(R.id.thumbnail);
        thumbnailImageView.setImageBitmap(factory.getThumbnailCache());

        if (factory.isCreatedByUser()) {
            findViewById(R.id.puzzle_cant_be_removed).setVisibility(View.GONE);
            findViewById(R.id.puzzle_settings).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();

                    Intent intent = null;
                    if (factory.getConfig().getFactoryType().equals("pattern")) {
                        intent = new Intent(getContext(), CreatePatternActivity.class);
                    } else if (factory.getConfig().getFactoryType().equals("random")) {
                        intent = new Intent(getContext(), CreateRandomPuzzleActivity.class);
                    } else if (factory.getConfig().getFactoryType().equals("fixed")) {
                        intent = new Intent(getContext(), CreateCustomPuzzleActivity.class);
                    }

                    if (intent != null) {
                        intent.putExtra("uuid", factory.getUuid());
                        intent.putExtra("folderUuid", folderUuid);
                        getContext().startActivity(intent);
                    }
                }
            });

            findViewById(R.id.puzzle_set_folder).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    galleryActivity.enablePuzzleDropMode(factory);
                    dismiss();
                }
            });

            findViewById(R.id.puzzle_remove).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                            .setTitle("Delete")
                            .setMessage("Are you sure?")
                            .setPositiveButton("Yes", new OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dismiss();
                                    new PuzzleFactoryManager(getContext()).remove(factory);
                                    if (galleryActivity != null) {
                                        galleryActivity.updateGallery();
                                    }
                                }
                            })
                            .setNegativeButton("No", null);
                    AlertDialog dialog = builder.create();
                    dialog.show();

                    dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(0xff000000);
                    dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(0xff000000);
                }
            });
        } else {
            findViewById(R.id.puzzle_settings).setVisibility(View.GONE);
            findViewById(R.id.puzzle_set_folder).setVisibility(View.GONE);
            findViewById(R.id.puzzle_remove).setVisibility(View.GONE);
        }

        findViewById(R.id.puzzle_play).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), PlayActivity.class);
                intent.putExtra("factory-uuid", factory.getUuid().toString());
                getContext().startActivity(intent);
            }
        });
    }

    public PuzzleFactoryDialog(@NonNull Context context, PuzzleFactory factory, UUID folderUuid) {
        super(context);
        this.factory = factory;
        this.folderUuid = folderUuid;

        if (context instanceof GalleryActivity) {
            galleryActivity = (GalleryActivity) context;
        }
    }
}
