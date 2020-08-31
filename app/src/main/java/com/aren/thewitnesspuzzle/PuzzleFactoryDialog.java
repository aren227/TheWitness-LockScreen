package com.aren.thewitnesspuzzle;

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

import com.aren.thewitnesspuzzle.puzzle.factory.PuzzleFactory;
import com.aren.thewitnesspuzzle.puzzle.factory.PuzzleFactoryManager;

import androidx.annotation.NonNull;

public class PuzzleFactoryDialog extends Dialog {

    PuzzleFactory factory;
    GalleryActivity galleryActivity;

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

        if(factory.getConfig().getFactoryType() != null){
            if(factory.getConfig().getFactoryType().equals("pattern")){
                nameTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_gesture_24, 0);
            }
            else{
                nameTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_casino_24, 0);
            }
        }

        ImageView thumbnailImageView = findViewById(R.id.thumbnail);
        thumbnailImageView.setImageBitmap(factory.getThumbnailCache());

        if(factory.isCreatedByUser()){
            findViewById(R.id.puzzle_cant_be_removed).setVisibility(View.GONE);
            findViewById(R.id.puzzle_settings).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                    if(factory.getConfig().getFactoryType().equals("pattern")){
                        Intent intent = new Intent(getContext(), CreatePatternActivity.class);
                        intent.putExtra("uuid", factory.getUuid());
                        getContext().startActivity(intent);
                    }
                    else if(factory.getConfig().getFactoryType().equals("random")){
                        Intent intent = new Intent(getContext(), CreateRandomPuzzleActivity.class);
                        intent.putExtra("uuid", factory.getUuid());
                        getContext().startActivity(intent);
                    }
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
                                    if(galleryActivity != null){
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
        }
        else{
            findViewById(R.id.puzzle_settings).setVisibility(View.GONE);
            findViewById(R.id.puzzle_remove).setVisibility(View.GONE);
        }
    }

    public PuzzleFactoryDialog(@NonNull Context context, PuzzleFactory factory) {
        super(context);
        this.factory = factory;

        if(context instanceof GalleryActivity){
            galleryActivity = (GalleryActivity)context;
        }
    }
}
