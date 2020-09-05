package com.aren.thewitnesspuzzle.activity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.aren.thewitnesspuzzle.R;
import com.aren.thewitnesspuzzle.dialog.ColorPaletteDialog;
import com.aren.thewitnesspuzzle.game.Game;
import com.aren.thewitnesspuzzle.puzzle.GridPuzzle;
import com.aren.thewitnesspuzzle.puzzle.HexagonPuzzle;
import com.aren.thewitnesspuzzle.puzzle.Puzzle;
import com.aren.thewitnesspuzzle.puzzle.color.PalettePreset;
import com.aren.thewitnesspuzzle.puzzle.color.PuzzleColorPalette;
import com.aren.thewitnesspuzzle.puzzle.factory.Difficulty;
import com.aren.thewitnesspuzzle.puzzle.factory.PuzzleFactoryConfig;
import com.aren.thewitnesspuzzle.puzzle.factory.PuzzleFactoryManager;
import com.aren.thewitnesspuzzle.view.ColorPaletteView;

import java.util.UUID;

import androidx.appcompat.app.AppCompatActivity;

public class PuzzleEditorActivity extends AppCompatActivity {

    protected static final Difficulty[] DIFFICULTIES = new Difficulty[]{Difficulty.ALWAYS_SOLVABLE, Difficulty.VERY_EASY, Difficulty.EASY, Difficulty.NORMAL, Difficulty.HARD, Difficulty.VERY_HARD};

    Game game;
    Puzzle puzzle;

    boolean isGridPuzzle = true;
    PuzzleColorPalette palette;

    LinearLayout editorWindow;
    EditText nameEditText;
    RadioGroup puzzleTypeRadioGroup;
    RadioButton gridPuzzleRadioButton;
    RadioButton hexagonPuzzleRadioButton;
    ColorPaletteView paletteView;
    LinearLayout difficultyView;
    SeekBar difficultySeekBar;
    TextView difficultyTextView;
    LinearLayout gridSizeView;
    EditText widthEditText;
    EditText heightEditText;
    ImageView sizeRefreshImageView;
    RelativeLayout root;

    PuzzleFactoryManager puzzleFactoryManager;
    PuzzleFactoryConfig config;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puzzle_editor);

        UUID uuid = UUID.randomUUID();
        if (getIntent().getExtras() != null && getIntent().getExtras().getSerializable("uuid") != null) {
            uuid = (UUID) getIntent().getExtras().getSerializable("uuid");
        }

        puzzleFactoryManager = new PuzzleFactoryManager(this);
        if (puzzleFactoryManager.getPuzzleFactoryByUuid(uuid) != null) {
            config = puzzleFactoryManager.getPuzzleFactoryByUuid(uuid).getConfig();
        } else {
            config = new PuzzleFactoryConfig(this, uuid);
        }

        palette = PalettePreset.get("Entry_1");

        editorWindow = findViewById(R.id.editor_window);
        nameEditText = findViewById(R.id.name);
        puzzleTypeRadioGroup = findViewById(R.id.puzzle_type);
        gridPuzzleRadioButton = findViewById(R.id.grid_puzzle);
        hexagonPuzzleRadioButton = findViewById(R.id.hexagon_puzzle);
        paletteView = findViewById(R.id.palette);
        difficultyView = findViewById(R.id.difficulty_container);
        difficultySeekBar = findViewById(R.id.puzzle_difficulty);
        difficultyTextView = findViewById(R.id.puzzle_difficulty_text);
        gridSizeView = findViewById(R.id.grid_size);
        widthEditText = findViewById(R.id.width);
        heightEditText = findViewById(R.id.height);
        sizeRefreshImageView = findViewById(R.id.size_refresh);
        root = findViewById(R.id.puzzle_view);

        puzzleTypeRadioGroup.check(R.id.grid_puzzle);
        gridPuzzleRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isGridPuzzle = true;
                resetPuzzle();
                updateGridSizeUI();
            }
        });

        hexagonPuzzleRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isGridPuzzle = false;
                resetPuzzle();
                updateGridSizeUI();
            }
        });

        paletteView.setPalette(palette);

        paletteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorPaletteDialog dialog = new ColorPaletteDialog(PuzzleEditorActivity.this, palette, new Runnable() {
                    @Override
                    public void run() {
                        paletteView.invalidate();
                        puzzle.shouldUpdateStaticShapes();
                        game.update();
                    }
                });
                dialog.show();
            }
        });

        difficultySeekBar.setMax(DIFFICULTIES.length - 1);
        difficultySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                difficultyTextView.setText(DIFFICULTIES[difficultySeekBar.getProgress()].toUserFriendlyString());
                difficultyTextView.setTextColor(DIFFICULTIES[difficultySeekBar.getProgress()].getColor());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        sizeRefreshImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int w = getWidth();
                int h = getHeight();
                widthEditText.setText(Integer.toString(w));
                heightEditText.setText(Integer.toString(h));

                resetPuzzle();
            }
        });

        game = new Game(this, Game.Mode.EDITOR);
        resetPuzzle();
        updateGridSizeUI();

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        root.addView(game.getSurfaceView(), params);
    }

    protected void resetPuzzle() {
        if (isGridPuzzle) {
            puzzle = new GridPuzzle(game, palette, getWidth(), getHeight(), false);
            ((GridPuzzle) puzzle).addStartingPoint(0, 0);
            ((GridPuzzle) puzzle).addEndingPoint(getWidth(), getHeight());
        } else puzzle = new HexagonPuzzle(game, palette, false);
        game.setPuzzle(puzzle);
        game.update();
    }

    protected void updateGridSizeUI() {
        if (isGridPuzzle) gridSizeView.setVisibility(View.VISIBLE);
        else gridSizeView.setVisibility(View.GONE);
    }

    protected int getWidth() {
        if (widthEditText.getText().length() == 0) return 4;
        int w = Integer.parseInt(widthEditText.getText().toString());
        return Math.min(Math.max(w, 1), 7);
    }

    protected int getHeight() {
        if (heightEditText.getText().length() == 0) return 4;
        int h = Integer.parseInt(heightEditText.getText().toString());
        return Math.min(Math.max(h, 1), 7);
    }

    protected Difficulty getDifficulty() {
        return DIFFICULTIES[difficultySeekBar.getProgress()];
    }
}