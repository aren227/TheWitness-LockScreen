package com.aren.thewitnesspuzzle.activity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.aren.thewitnesspuzzle.R;
import com.aren.thewitnesspuzzle.core.color.PalettePreset;
import com.aren.thewitnesspuzzle.puzzle.factory.Difficulty;

import java.util.HashMap;
import java.util.Map;

public class CreateCustomPuzzleActivity extends PuzzleEditorActivity {

    private enum ToolType {
        PLAY, ERASE, START, BROKEN_LINE, END, HEXAGON, SQUARE, SUN, BLOCKS, ELIMINATION, TRIANGLES
    }

    Map<ToolType, ImageView> toolTypeImageViewMap;
    ToolType currentToolType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sizeRefreshImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO
            }
        });

        nameEditText.setText(config.getString("name", "My Puzzle"));

        palette.set(config.getColorPalette("color", PalettePreset.get("Entry_1")));
        paletteView.invalidate();

        Difficulty difficulty = Difficulty.fromString(config.getString("difficulty", "ALWAYS_SOLVABLE"));
        int difficultyIndex = 0;
        for (int i = 0; i < DIFFICULTIES.length; i++) {
            if (DIFFICULTIES[i] == difficulty) {
                difficultyIndex = i;
                break;
            }
        }
        difficultySeekBar.setProgress(difficultyIndex);

        widthEditText.setText(config.getInt("width", 4) + "");
        heightEditText.setText(config.getInt("height", 4) + "");

        // Disable other puzzle types
        hexagonPuzzleRadioButton.setVisibility(View.GONE);
        junglePuzzleRadioButton.setVisibility(View.GONE);
        videoRoomPuzzleRadioButton.setVisibility(View.GONE);

        //findViewById(R.id.done).setVisibility(View.GONE);
        //findViewById(R.id.refresh).setVisibility(View.GONE);

        // Large puzzle view
        findViewById(R.id.puzzle_view).setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 2f));

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View editorView = inflater.inflate(R.layout.activity_create_custom_puzzle_editor, editorWindow, true);

        toolTypeImageViewMap = new HashMap<>();
        toolTypeImageViewMap.put(ToolType.PLAY, (ImageView) findViewById(R.id.tool_play));
        toolTypeImageViewMap.put(ToolType.ERASE, (ImageView) findViewById(R.id.tool_erase));
        toolTypeImageViewMap.put(ToolType.START, (ImageView) findViewById(R.id.tool_start));
        toolTypeImageViewMap.put(ToolType.BROKEN_LINE, (ImageView) findViewById(R.id.tool_broken_line));
        toolTypeImageViewMap.put(ToolType.END, (ImageView) findViewById(R.id.tool_end));
        toolTypeImageViewMap.put(ToolType.HEXAGON, (ImageView) findViewById(R.id.tool_hexagon));
        toolTypeImageViewMap.put(ToolType.SQUARE, (ImageView) findViewById(R.id.tool_square));
        toolTypeImageViewMap.put(ToolType.SUN, (ImageView) findViewById(R.id.tool_sun));
        toolTypeImageViewMap.put(ToolType.BLOCKS, (ImageView) findViewById(R.id.tool_blocks));
        toolTypeImageViewMap.put(ToolType.ELIMINATION, (ImageView) findViewById(R.id.tool_elimination));
        toolTypeImageViewMap.put(ToolType.TRIANGLES, (ImageView) findViewById(R.id.tool_triangles));

        for (final ToolType key : toolTypeImageViewMap.keySet()) {
            toolTypeImageViewMap.get(key).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectTool(key);
                }
            });
            toolTypeImageViewMap.get(key).setColorFilter(Color.DKGRAY);
        }

        selectTool(ToolType.START);
    }

    private void selectTool(ToolType toolType) {
        if (currentToolType != null)
            toolTypeImageViewMap.get(currentToolType).setColorFilter(Color.DKGRAY);
        currentToolType = toolType;
        toolTypeImageViewMap.get(currentToolType).setColorFilter(Color.WHITE);
    }

}
