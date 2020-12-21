package com.aren.thewitnesspuzzle.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.aren.thewitnesspuzzle.R;
import com.aren.thewitnesspuzzle.puzzle.base.color.PalettePreset;
import com.aren.thewitnesspuzzle.puzzle.factory.Difficulty;
import com.aren.thewitnesspuzzle.puzzle.factory.PuzzleFactory;
import com.aren.thewitnesspuzzle.puzzle.factory.PuzzleFactoryManager;

import java.util.ArrayList;
import java.util.List;

public class CreatePatternActivity extends PuzzleEditorActivity {

    TextView instructionTextView;
    ImageView deleteImageView;
    ImageView nextImageView;
    boolean viewInit = false;

    enum State {INIT, FIRST_DRAWN, VALIDATE, DONE}

    State state = State.INIT;
    List<Integer> pattern = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        difficultyView.setVisibility(View.GONE);

        // Restore from config
        nameEditText.setText(config.getString("name", "New Pattern"));

        palette.set(config.getColorPalette("color", PalettePreset.get("Entry_1")));
        paletteView.invalidate();

        puzzleType = config.getString("puzzleType", "grid");
        if (puzzleType.equals("grid")) {
            gridPuzzleRadioButton.setChecked(true);
            widthEditText.setText(config.getInt("width", 4) + "");
            heightEditText.setText(config.getInt("height", 4) + "");
        } else if (puzzleType.equals("hexagon")) {
            hexagonPuzzleRadioButton.setChecked(true);
        } else if (puzzleType.equals("jungle")) {
            junglePuzzleRadioButton.setChecked(true);
            widthEditText.setText(config.getInt("width", 4) + "");
        } else if (puzzleType.equals("video_room")) {
            videoRoomPuzzleRadioButton.setChecked(true);
        }
        updateGridSizeUI();

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.activity_create_pattern, root, true);

        instructionTextView = view.findViewById(R.id.instruction);
        deleteImageView = view.findViewById(R.id.delete);
        nextImageView = view.findViewById(R.id.next);
        viewInit = true;

        deleteImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletePattern();
            }
        });

        nextImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (state == State.FIRST_DRAWN) {
                    puzzleRenderer.setCustomPattern(pattern);
                    puzzleRenderer.setCursor(null);
                    game.update();

                    state = State.VALIDATE;
                    updateUI();
                } else if (state == State.DONE) {
                    savePattern();
                }
            }
        });

        state = State.INIT;

        game.setOnSolved(new Runnable() {
            @Override
            public void run() {
                if (puzzleRenderer.getCustomPattern() != null) {
                    state = State.DONE;
                } else {
                    pattern = puzzleRenderer.getCursor().getVisitedVertexIndices();
                    state = State.FIRST_DRAWN;
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateUI();
                    }
                });
            }
        });
        game.setOnPreTouched(new Runnable() {
            @Override
            public void run() {
                if (state == State.FIRST_DRAWN) {
                    state = State.INIT;
                } else if (state == State.DONE) {
                    state = State.VALIDATE;
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateUI();
                    }
                });
            }
        });
        updateUI();
        resetPuzzle();
    }

    protected void updateUI() {
        if (!viewInit) return;
        if (state == State.INIT) {
            instructionTextView.setText(R.string.create_pattern_draw);
            deleteImageView.setVisibility(View.INVISIBLE);
            nextImageView.setVisibility(View.INVISIBLE);
        } else if (state == State.FIRST_DRAWN) {
            deleteImageView.setVisibility(View.INVISIBLE);
            nextImageView.setImageResource(R.drawable.ic_navigate_next_black_24dp);
            nextImageView.setVisibility(View.VISIBLE);
        } else if (state == State.VALIDATE) {
            instructionTextView.setText(R.string.create_pattern_validate);
            deleteImageView.setVisibility(View.VISIBLE);
            nextImageView.setVisibility(View.INVISIBLE);
        } else {
            nextImageView.setImageResource(R.drawable.ic_baseline_check_24);
            deleteImageView.setVisibility(View.VISIBLE);
            nextImageView.setVisibility(View.VISIBLE);
        }
    }

    protected void deletePattern() {
        puzzleRenderer.setCustomPattern(null);
        puzzleRenderer.setCursor(null);
        game.update();

        state = State.INIT;
        updateUI();
    }

    protected void savePattern() {
        PuzzleFactoryManager manager = new PuzzleFactoryManager(this);

        // Check name
        final String name = nameEditText.getText().toString().trim();
        if (name.length() == 0) {
            new AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage(String.format("Please enter a name", name))
                    .setNegativeButton("OK", null)
                    .show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("Save & Exit")
                .setMessage("Are you sure?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        config.setFactoryType("pattern");
                        config.setString("name", name);
                        config.setColorPalette("color", palette);
                        config.setString("puzzleType", puzzleType);
                        config.setString("difficulty", Difficulty.CUSTOM_PATTERN.toString());
                        if (puzzleType.equals("grid")) {
                            config.setInt("width", getWidth());
                            config.setInt("height", getHeight());
                        } else if (puzzleType.equals("jungle")) {
                            config.setInt("width", getWidth());
                        }
                        config.setIntList("pattern", pattern);
                        config.save();

                        // Clear thumbnail cache
                        PuzzleFactory factory = puzzleFactoryManager.getPuzzleFactoryByUuid(config.getUuid());
                        if (factory != null) {
                            factory.clearThumbnailCache();
                        }

                        finish();
                    }
                })
                .setNegativeButton("No", null);
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(0xff000000);
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(0xff000000);
    }

    @Override
    protected void resetPuzzle() {
        super.resetPuzzle();
        deletePattern();
    }


}