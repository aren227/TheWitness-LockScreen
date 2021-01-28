package com.aren.thewitnesspuzzle.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Toast;

import com.aren.thewitnesspuzzle.R;
import com.aren.thewitnesspuzzle.core.color.PalettePreset;
import com.aren.thewitnesspuzzle.core.cursor.Cursor;
import com.aren.thewitnesspuzzle.core.cursor.area.GridAreaSplitter;
import com.aren.thewitnesspuzzle.core.graph.Edge;
import com.aren.thewitnesspuzzle.core.graph.EdgeProportion;
import com.aren.thewitnesspuzzle.core.graph.Tile;
import com.aren.thewitnesspuzzle.core.graph.Vertex;
import com.aren.thewitnesspuzzle.core.puzzle.GridPuzzle;
import com.aren.thewitnesspuzzle.core.rules.Color;
import com.aren.thewitnesspuzzle.core.rules.EndingPointRule;
import com.aren.thewitnesspuzzle.core.rules.StartingPointRule;
import com.aren.thewitnesspuzzle.puzzle.factory.Difficulty;
import com.aren.thewitnesspuzzle.puzzle.factory.PuzzleFactory;
import com.aren.thewitnesspuzzle.puzzle.generator.BlocksRuleGenerator;
import com.aren.thewitnesspuzzle.puzzle.generator.BrokenLineRuleGenerator;
import com.aren.thewitnesspuzzle.puzzle.generator.EliminationRuleGenerator;
import com.aren.thewitnesspuzzle.puzzle.generator.HexagonRuleGenerator;
import com.aren.thewitnesspuzzle.puzzle.generator.SquareRuleGenerator;
import com.aren.thewitnesspuzzle.puzzle.generator.SunRuleGenerator;
import com.aren.thewitnesspuzzle.puzzle.generator.TrianglesRuleGenerator;
import com.aren.thewitnesspuzzle.puzzle.walker.RandomGridTreeWalker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class CreateRandomPuzzleActivity extends PuzzleEditorActivity {

    long brokenLineSeed, hexagonSeed, squareAreaSeed, squareSeed, blocksSeed, sunSeed, trianglesSeed, eliminationSeed;

    Cursor cursor;
    GridAreaSplitter splitter;

    CheckBox brokenLineCheckBox, hexagonCheckBox, squareCheckBox, blocksCheckBox, sunCheckBox, trianglesCheckBox, eliminationCheckBox;
    LinearLayout brokenLineLayout, hexagonLayout, squareLayout, blocksLayout, sunLayout, trianglesLayout, eliminationLayout;

    SeekBar brokenLineRateSeekBar;

    SeekBar hexagonRateSeekBar;

    LinearLayout squareColorRoot;
    Map<Color, CheckBox> squareColorCheckBoxes;
    SeekBar squareRateSeekBar;

    RadioGroup blocksColorRadioGroup;
    Map<Color, RadioButton> blocksColorRatioButtons;
    SeekBar blocksRateSeekBar;
    SeekBar blocksRotatableRateSeekBar;

    LinearLayout sunColorRoot;
    Map<Color, CheckBox> sunColorCheckBoxes;
    SeekBar sunAreaRateSeekBar;
    SeekBar sunSpawnRateSeekBar;
    SeekBar sunPairWithSqaureRateSeekBar;

    SeekBar trianglesRateSeekBar;

    RadioGroup eliminationRadioGroup;
    Map<String, RadioButton> eliminationRadioButtons;

    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // SHITTY AS HELL
        // PLZ HELP ME

        super.onCreate(savedInstanceState);

        sizeRefreshImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int w = getWidth();
                int h = getHeight();
                widthEditText.setText(Integer.toString(w));
                heightEditText.setText(Integer.toString(h));

                resetPuzzle();
                generateRules();
            }
        });

        // Restore from config
        nameEditText.setText(config.getString("name", "New Randomized Puzzle"));

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

        // Disable hexagon puzzle
        hexagonPuzzleRadioButton.setVisibility(View.GONE);
        junglePuzzleRadioButton.setVisibility(View.GONE);
        videoRoomPuzzleRadioButton.setVisibility(View.GONE);

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.activity_create_random_puzzle_overlay, root, true);

        findViewById(R.id.refresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPuzzle();
                generateRules();
            }
        });

        findViewById(R.id.done).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePuzzle();
            }
        });

        View editorView = inflater.inflate(R.layout.activity_create_random_puzzle_editor, editorWindow, true);

        Random random = new Random();
        brokenLineSeed = random.nextLong();
        hexagonSeed = random.nextLong();
        squareAreaSeed = random.nextLong();
        squareSeed = random.nextLong();
        blocksSeed = random.nextLong();
        sunSeed = random.nextLong();
        trianglesSeed = random.nextLong();
        eliminationSeed = random.nextLong();

        brokenLineCheckBox = findViewById(R.id.broken_line_check_box);
        brokenLineLayout = findViewById(R.id.broken_line_settings);
        brokenLineRateSeekBar = findViewById(R.id.broken_line_rate);

        hexagonCheckBox = findViewById(R.id.hexagon_check_box);
        hexagonLayout = findViewById(R.id.hexagon_settings);
        hexagonRateSeekBar = findViewById(R.id.hexagon_rate);

        squareCheckBox = findViewById(R.id.square_check_box);
        squareLayout = findViewById(R.id.square_settings);
        squareColorRoot = findViewById(R.id.square_colors_root);
        squareColorCheckBoxes = new HashMap<>();
        for (Color color : Color.values()) {
            CheckBox checkBox = new CheckBox(this);
            checkBox.setText(color.toString());
            checkBox.setTextColor(0xfffafafa);
            squareColorCheckBoxes.put(color, checkBox);
            squareColorRoot.addView(checkBox);
        }
        squareRateSeekBar = findViewById(R.id.square_rate);

        blocksCheckBox = findViewById(R.id.blocks_check_box);
        blocksLayout = findViewById(R.id.blocks_settings);
        blocksColorRadioGroup = findViewById(R.id.blocks_color_radio_group);
        blocksColorRatioButtons = new HashMap<>();
        for (Color color : Color.values()) {
            RadioButton button = new RadioButton(this);
            button.setText(color.toString());
            button.setTextColor(0xfffafafa);
            blocksColorRatioButtons.put(color, button);
            blocksColorRadioGroup.addView(button);
            if (color.equals(Color.YELLOW)) {
                button.setChecked(true);
            }
        }
        blocksRateSeekBar = findViewById(R.id.blocks_rate);
        blocksRotatableRateSeekBar = findViewById(R.id.blocks_rotatable_rate);

        sunCheckBox = findViewById(R.id.sun_check_box);
        sunLayout = findViewById(R.id.sun_settings);
        sunColorRoot = findViewById(R.id.sun_colors_root);
        sunColorCheckBoxes = new HashMap<>();
        for (Color color : Color.values()) {
            CheckBox checkBox = new CheckBox(this);
            checkBox.setText(color.toString());
            checkBox.setTextColor(0xfffafafa);
            sunColorCheckBoxes.put(color, checkBox);
            sunColorRoot.addView(checkBox);
        }
        sunAreaRateSeekBar = findViewById(R.id.sun_area_rate);
        sunSpawnRateSeekBar = findViewById(R.id.sun_spawn_rate);
        sunPairWithSqaureRateSeekBar = findViewById(R.id.sun_pair_with_square_rate);

        trianglesCheckBox = findViewById(R.id.triangles_check_box);
        trianglesLayout = findViewById(R.id.triangles_settings);
        trianglesRateSeekBar = findViewById(R.id.triangles_rate);

        eliminationCheckBox = findViewById(R.id.elimination_check_box);
        eliminationLayout = findViewById(R.id.elimination_settings);
        eliminationRadioGroup = findViewById(R.id.elimination_radio_group);
        eliminationRadioButtons = new HashMap<>();
        String[] ruleNames = new String[]{"hexagon", "square", "blocks", "sun"};
        for (String ruleName : ruleNames) {
            RadioButton button = new RadioButton(this);
            button.setText(ruleName);
            button.setTextColor(0xfffafafa);
            eliminationRadioButtons.put(ruleName, button);
            eliminationRadioGroup.addView(button);
        }

        brokenLineLayout.setVisibility(View.GONE);
        hexagonLayout.setVisibility(View.GONE);
        squareLayout.setVisibility(View.GONE);
        blocksLayout.setVisibility(View.GONE);
        sunLayout.setVisibility(View.GONE);
        trianglesLayout.setVisibility(View.GONE);
        eliminationLayout.setVisibility(View.GONE);

        brokenLineCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                brokenLineLayout.setVisibility(isChecked ? View.VISIBLE : View.GONE);
                generateRules();
            }
        });

        brokenLineCheckBox.setChecked(config.getBoolean("brokenline", false));

        brokenLineRateSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                generateRules();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                generateRules();
            }
        });

        brokenLineRateSeekBar.setProgress((int) (config.getFloat("brokenline_spawnrate", 0f) * brokenLineRateSeekBar.getMax()));

        hexagonCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                hexagonLayout.setVisibility(isChecked ? View.VISIBLE : View.GONE);
                generateRules();
            }
        });

        hexagonCheckBox.setChecked(config.getBoolean("hexagon", false));

        hexagonRateSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                generateRules();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                generateRules();
            }
        });

        hexagonRateSeekBar.setProgress((int) (config.getFloat("hexagon_spawnrate", 0) * hexagonRateSeekBar.getMax()));

        squareCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                squareLayout.setVisibility(isChecked ? View.VISIBLE : View.GONE);
                generateRules();
            }
        });

        squareCheckBox.setChecked(config.getBoolean("square", false));

        List<Color> squareSavedColors = config.getColorList("square_colors", new ArrayList<Color>());
        for (Color color : squareColorCheckBoxes.keySet()) {
            squareColorCheckBoxes.get(color).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    generateRules();
                }
            });
            squareColorCheckBoxes.get(color).setChecked(squareSavedColors.contains(color));
        }

        squareRateSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                generateRules();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                generateRules();
            }
        });

        squareRateSeekBar.setProgress((int) (config.getFloat("square_spawnrate", 0f) * squareRateSeekBar.getMax()));

        blocksCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                blocksLayout.setVisibility(isChecked ? View.VISIBLE : View.GONE);
                generateRules();
            }
        });

        blocksCheckBox.setChecked(config.getBoolean("blocks", false));

        blocksColorRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                generateRules();
            }
        });

        Color blocksSavedColor = config.getColorList("blocks_colors", Arrays.asList(Color.YELLOW)).get(0);
        for (Color color : blocksColorRatioButtons.keySet()) {
            if (color.equals(blocksSavedColor)) {
                blocksColorRatioButtons.get(color).setChecked(true);
                break;
            }
        }

        blocksRateSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                generateRules();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                generateRules();
            }
        });

        blocksRateSeekBar.setProgress((int) (config.getFloat("blocks_spawnrate", 0f) * blocksRateSeekBar.getMax()));

        blocksRotatableRateSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                generateRules();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                generateRules();
            }
        });

        blocksRotatableRateSeekBar.setProgress((int) (config.getFloat("blocks_rotatablerate", 0f) * blocksRotatableRateSeekBar.getMax()));

        sunCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sunLayout.setVisibility(isChecked ? View.VISIBLE : View.GONE);
                generateRules();
            }
        });

        sunCheckBox.setChecked(config.getBoolean("sun", false));

        List<Color> sunSavedColors = config.getColorList("sun_colors", new ArrayList<Color>());
        for (Color color : sunColorCheckBoxes.keySet()) {
            sunColorCheckBoxes.get(color).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    generateRules();
                }
            });
            if (sunSavedColors.contains(color)) {
                sunColorCheckBoxes.get(color).setChecked(true);
            }
        }

        sunAreaRateSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                generateRules();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                generateRules();
            }
        });

        sunAreaRateSeekBar.setProgress((int) (config.getFloat("sun_arearate", 0f) * sunAreaRateSeekBar.getMax()));

        sunSpawnRateSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                generateRules();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                generateRules();
            }
        });

        sunSpawnRateSeekBar.setProgress((int) (config.getFloat("sun_spawnrate", 0f) * sunSpawnRateSeekBar.getMax()));

        sunPairWithSqaureRateSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                generateRules();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                generateRules();
            }
        });

        sunPairWithSqaureRateSeekBar.setProgress((int) (config.getFloat("sun_pairwithsquare", 0f) * sunPairWithSqaureRateSeekBar.getMax()));

        trianglesCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                trianglesLayout.setVisibility(isChecked ? View.VISIBLE : View.GONE);
                generateRules();
            }
        });

        trianglesCheckBox.setChecked(config.getBoolean("triangles", false));

        trianglesRateSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                generateRules();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                generateRules();
            }
        });

        trianglesRateSeekBar.setProgress((int) (config.getFloat("triangles_spawnrate", 0f) * trianglesRateSeekBar.getMax()));

        eliminationCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                eliminationLayout.setVisibility(isChecked ? View.VISIBLE : View.GONE);
                generateRules();
            }
        });

        eliminationCheckBox.setChecked(config.getBoolean("elimination", false));

        eliminationRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(final RadioGroup group, int checkedId) {
                if (checkedId == eliminationRadioButtons.get("hexagon").getId()) {

                } else if (checkedId == eliminationRadioButtons.get("square").getId()) {
                    if (!isSquareUsed()) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                group.clearCheck();
                            }
                        });
                        Toast.makeText(CreateRandomPuzzleActivity.this, "Enable Square Rule first.", Toast.LENGTH_LONG).show();
                    }
                } else if (checkedId == eliminationRadioButtons.get("blocks").getId()) {

                } else if (checkedId == eliminationRadioButtons.get("sun").getId()) {
                    if (!isSunUsed()) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                group.clearCheck();
                            }
                        });
                        Toast.makeText(CreateRandomPuzzleActivity.this, "Enable Sun Rule first.", Toast.LENGTH_LONG).show();
                    }
                }
                generateRules();
            }
        });

        if (config.getString("elimination_fakerule", null) != null && eliminationRadioButtons.containsKey(config.getString("elimination_fakerule", null))) {
            eliminationRadioButtons.get(config.getString("elimination_fakerule", null)).setChecked(true);
        }

        handler = new Handler();

        resetPuzzle();
        generateRules();
    }

    protected boolean isBrokenLineUsed() {
        return brokenLineCheckBox.isChecked();
    }

    protected float getBrokenLineSpawnRate() {
        return (float) brokenLineRateSeekBar.getProgress() / brokenLineRateSeekBar.getMax();
    }

    protected boolean isHexagonUsed() {
        return hexagonCheckBox.isChecked();
    }

    protected float getHexagonSpawnRate() {
        return (float) hexagonRateSeekBar.getProgress() / hexagonRateSeekBar.getMax();
    }

    protected boolean isSquareUsed() {
        if (!squareCheckBox.isChecked()) return false;
        return getSquareColors().size() != 0;
    }

    protected List<Color> getSquareColors() {
        List<Color> list = new ArrayList<>();
        for (Color color : squareColorCheckBoxes.keySet()) {
            if (squareColorCheckBoxes.get(color).isChecked()) {
                list.add(color);
            }
        }
        return list;
    }

    protected float getSquareSpawnRate() {
        return (float) squareRateSeekBar.getProgress() / squareRateSeekBar.getMax();
    }

    protected boolean isBlocksUsed() {
        return blocksCheckBox.isChecked();
    }

    protected Color getBlocksColor() {
        for (Color color : blocksColorRatioButtons.keySet()) {
            if (blocksColorRatioButtons.get(color).isChecked()) return color;
        }
        return Color.YELLOW;
    }

    protected float getBlocksSpawnRate() {
        return (float) blocksRateSeekBar.getProgress() / blocksRateSeekBar.getMax();
    }

    protected float getBlocksRotatableRate() {
        return (float) blocksRotatableRateSeekBar.getProgress() / blocksRotatableRateSeekBar.getMax();
    }

    protected boolean isSunUsed() {
        if (!sunCheckBox.isChecked()) return false;
        return getSunColors().size() != 0;
    }

    protected List<Color> getSunColors() {
        List<Color> list = new ArrayList<>();
        for (Color color : sunColorCheckBoxes.keySet()) {
            if (sunColorCheckBoxes.get(color).isChecked()) {
                list.add(color);
            }
        }
        return list;
    }

    protected float getSunAreaRate() {
        return (float) sunAreaRateSeekBar.getProgress() / sunAreaRateSeekBar.getMax();
    }

    protected float getSunSpawnRate() {
        return (float) sunSpawnRateSeekBar.getProgress() / sunSpawnRateSeekBar.getMax();
    }

    protected float getSunPairWithSquareRate() {
        return (float) sunPairWithSqaureRateSeekBar.getProgress() / sunPairWithSqaureRateSeekBar.getMax();
    }

    protected boolean isTrianglesUsed() {
        return trianglesCheckBox.isChecked();
    }

    protected float getTrianglesSpawnRate() {
        return (float) trianglesRateSeekBar.getProgress() / trianglesRateSeekBar.getMax();
    }

    protected boolean isEliminationUsed() {
        return eliminationCheckBox.isChecked();
    }

    protected String getEliminationFakeRule() {
        for (String name : eliminationRadioButtons.keySet()) {
            if (eliminationRadioButtons.get(name).isChecked()) return name;
        }
        return null;
    }

    protected void generateRules() {
        // Synchronize with Render thread
        synchronized (puzzleRenderer) {
            // Clear previous rules
            for (Vertex vertex : puzzleRenderer.getPuzzleBase().getVertices()) {
                if (vertex.getRule() instanceof StartingPointRule || vertex.getRule() instanceof EndingPointRule)
                    continue;
                vertex.removeRule();
            }
            for (Edge edge : puzzleRenderer.getPuzzleBase().getEdges()) {
                edge.removeRule();
            }
            for (Tile tile : puzzleRenderer.getPuzzleBase().getTiles()) {
                tile.removeRule();
            }

            splitter = new GridAreaSplitter(cursor);

            // Broken Line
            if (isBrokenLineUsed()) {
                BrokenLineRuleGenerator.generate(cursor, new Random(brokenLineSeed), getBrokenLineSpawnRate());
            }

            // Hexagon
            if (isHexagonUsed()) {
                HexagonRuleGenerator.generate(cursor, new Random(hexagonSeed), getHexagonSpawnRate());
            }

            // Square
            if (isSquareUsed()) {
                splitter.assignAreaColorRandomly(new Random(squareAreaSeed), getSquareColors());
                SquareRuleGenerator.generate(splitter, new Random(squareSeed), getSquareSpawnRate());
            }

            // Blocks
            if (isBlocksUsed()) {
                BlocksRuleGenerator.generate(splitter, new Random(blocksSeed), getBlocksColor(), Color.BLUE, getBlocksSpawnRate(), getBlocksRotatableRate(), 0);
            }

            // Sun
            if (isSunUsed()) {
                SunRuleGenerator.generate(splitter, new Random(sunSeed), getSunColors(), getSunAreaRate(), getSunSpawnRate(), getSunPairWithSquareRate());
            }

            // Triangles
            if (isTrianglesUsed()) {
                TrianglesRuleGenerator.generate(cursor, new Random(trianglesSeed), getTrianglesSpawnRate());
            }

            // Elimination
            if (isEliminationUsed()) {
                String fake = getEliminationFakeRule();
                if (fake != null) {
                    if (fake.equals("hexagon")) {
                        EliminationRuleGenerator.generateFakeHexagon(splitter, new Random(eliminationSeed));
                    } else if (fake.equals("square") && isSquareUsed()) {
                        EliminationRuleGenerator.generateFakeSquare(splitter, new Random(eliminationSeed), getSquareColors());
                    } else if (fake.equals("blocks")) {
                        EliminationRuleGenerator.generateFakeBlocks(splitter, new Random(eliminationSeed), getBlocksColor(), 0f);
                    } else if (fake.equals("sun") && isSunUsed()) {
                        EliminationRuleGenerator.generateFakeSun(splitter, new Random(eliminationSeed), getSunColors());
                    }
                }
            }

            puzzleRenderer.shouldUpdateStaticShapes();

            game.update();
        }
    }

    @Override
    protected void resetPuzzle() {
        super.resetPuzzle();

        Random random = new Random();
        brokenLineSeed = random.nextLong();
        hexagonSeed = random.nextLong();
        squareAreaSeed = random.nextLong();
        squareSeed = random.nextLong();
        blocksSeed = random.nextLong();
        sunSeed = random.nextLong();
        trianglesSeed = random.nextLong();
        eliminationSeed = random.nextLong();

        //RandomGridWalker walker = new RandomGridWalker((GridPuzzle)puzzle, random, 5, 0, 0, getWidth(), getHeight());
        //ArrayList<Vertex> vertexPositions = walker.getResult();
        ArrayList<Vertex> vertexPositions = RandomGridTreeWalker.getLongest(((GridPuzzle) puzzleRenderer.getPuzzleBase()).getWidth(),
                ((GridPuzzle) puzzleRenderer.getPuzzleBase()).getHeight(), random, 5, 0, 0, getWidth(), getHeight()).getResult((GridPuzzle) puzzleRenderer.getPuzzleBase(), getWidth(), getHeight());

        // Connect to the ending point
        Vertex vertex = null;
        for (Vertex v : puzzleRenderer.getPuzzleBase().getVertices()) {
            if (v.getRule() instanceof EndingPointRule) {
                vertex = v;
                break;
            }
        }
        EdgeProportion lastEdge = null;
        if (vertex != null && puzzleRenderer.getPuzzleBase().getEdgeByVertex(((GridPuzzle) puzzleRenderer.getPuzzleBase()).getVertexAt(getWidth(), getHeight()), vertex) != null) {
            lastEdge = new EdgeProportion(puzzleRenderer.getPuzzleBase().getEdgeByVertex(((GridPuzzle) puzzleRenderer.getPuzzleBase()).getVertexAt(getWidth(), getHeight()), vertex));
            lastEdge.proportion = 1f;
        }

        cursor = new Cursor(puzzleRenderer.getPuzzleBase(), vertexPositions, lastEdge);

        puzzleRenderer.setCursor(cursor);
        puzzleRenderer.setUntouchable(true);

        game.update();
    }

    protected void savePuzzle() {
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
                        config.setFactoryType("random");
                        config.setString("name", name);
                        config.setColorPalette("color", palette);
                        config.setString("puzzleType", "grid");
                        config.setString("difficulty", getDifficulty().toString());
                        config.setInt("width", getWidth());
                        config.setInt("height", getHeight());

                        config.setBoolean("brokenline", isBrokenLineUsed());
                        if (isBrokenLineUsed()) {
                            config.setFloat("brokenline_spawnrate", getBrokenLineSpawnRate());
                        }

                        config.setBoolean("hexagon", isHexagonUsed());
                        if (isHexagonUsed()) {
                            config.setFloat("hexagon_spawnrate", getHexagonSpawnRate());
                        }

                        config.setBoolean("square", isSquareUsed());
                        if (isSquareUsed()) {
                            config.setColorList("square_colors", getSquareColors());
                            config.setFloat("square_spawnrate", getSquareSpawnRate());
                        }

                        config.setBoolean("blocks", isBlocksUsed());
                        if (isBlocksUsed()) {
                            config.setColorList("blocks_colors", Arrays.asList(getBlocksColor()));
                            config.setFloat("blocks_spawnrate", getBlocksSpawnRate());
                            config.setFloat("blocks_rotatablerate", getBlocksRotatableRate());
                        }

                        config.setBoolean("sun", isSunUsed());
                        if (isSunUsed()) {
                            config.setColorList("sun_colors", getSunColors());
                            config.setFloat("sun_arearate", getSunAreaRate());
                            config.setFloat("sun_spawnrate", getSunSpawnRate());
                            config.setFloat("sun_pairwithsquare", getSunPairWithSquareRate());
                        }

                        config.setBoolean("triangles", isTrianglesUsed());
                        if (isTrianglesUsed()) {
                            config.setFloat("triangles_spawnrate", getTrianglesSpawnRate());
                        }

                        config.setBoolean("elimination", isEliminationUsed());
                        if (isEliminationUsed()) {
                            config.setString("elimination_fakerule", getEliminationFakeRule());
                        }

                        config.setParentFolderUuid(folderUuid);

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
}
