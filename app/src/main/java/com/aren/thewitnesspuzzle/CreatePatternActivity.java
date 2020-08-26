package com.aren.thewitnesspuzzle;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.aren.thewitnesspuzzle.game.Game;
import com.aren.thewitnesspuzzle.puzzle.GridPuzzle;
import com.aren.thewitnesspuzzle.puzzle.HexagonPuzzle;
import com.aren.thewitnesspuzzle.puzzle.Puzzle;
import com.aren.thewitnesspuzzle.puzzle.color.PalettePreset;
import com.aren.thewitnesspuzzle.puzzle.color.PuzzleColorPalette;
import com.aren.thewitnesspuzzle.puzzle.rules.EndingPointRule;
import com.aren.thewitnesspuzzle.puzzle.rules.StartingPointRule;

public class CreatePatternActivity extends AppCompatActivity {

    Game game;
    Puzzle puzzle;

    boolean isGridPuzzle = true;
    PuzzleColorPalette palette;

    EditText nameEditText;
    RadioGroup puzzleTypeRadioGroup;
    RadioButton gridPuzzleRadioButton;
    RadioButton hexagonPuzzleRadioButton;
    ColorPaletteView paletteView;
    LinearLayout gridSizeView;
    EditText widthEditText;
    EditText heightEditText;
    ImageView sizeRefreshImageView;
    RelativeLayout root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_pattern);

        palette = PalettePreset.get("Entry_1");

        nameEditText = findViewById(R.id.name);
        puzzleTypeRadioGroup = findViewById(R.id.puzzle_type);
        gridPuzzleRadioButton = findViewById(R.id.grid_puzzle);
        hexagonPuzzleRadioButton = findViewById(R.id.hexagon_puzzle);
        paletteView = findViewById(R.id.palette);
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
                updateUI();
            }
        });

        hexagonPuzzleRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isGridPuzzle = false;
                resetPuzzle();
                updateUI();
            }
        });

        paletteView.setPalette(palette);

        paletteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorPaletteDialog dialog = new ColorPaletteDialog(CreatePatternActivity.this, palette);
                dialog.show();
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

        game = new Game(this, true);
        resetPuzzle();
        updateUI();

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        root.addView(game.getSurfaceView(), params);
    }

    public void resetPuzzle(){
        if(isGridPuzzle){
            puzzle = new GridPuzzle(game, palette, getWidth(), getHeight(), false);
            ((GridPuzzle)puzzle).addStartingPoint(0, 0);
            ((GridPuzzle)puzzle).addEndingPoint(getWidth(), getHeight());
        }
        else puzzle = new HexagonPuzzle(game, palette, false);
        game.setPuzzle(puzzle);
        game.update();
    }

    public void updateUI(){
        if(isGridPuzzle) gridSizeView.setVisibility(View.VISIBLE);
        else gridSizeView.setVisibility(View.GONE);
    }

    public int getWidth(){
        if(widthEditText.getText().length() == 0) return 4;
        int w = Integer.parseInt(widthEditText.getText().toString());
        return Math.min(Math.max(w, 1), 7);
    }

    public int getHeight(){
        if(heightEditText.getText().length() == 0) return 4;
        int h = Integer.parseInt(heightEditText.getText().toString());
        return Math.min(Math.max(h, 1), 7);
    }
}