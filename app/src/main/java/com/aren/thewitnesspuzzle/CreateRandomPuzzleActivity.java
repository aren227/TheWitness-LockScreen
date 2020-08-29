package com.aren.thewitnesspuzzle;

import android.app.AlertDialog;
import android.content.Context;
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

import com.aren.thewitnesspuzzle.puzzle.GridPuzzle;
import com.aren.thewitnesspuzzle.puzzle.HexagonPuzzle;
import com.aren.thewitnesspuzzle.puzzle.cursor.Cursor;
import com.aren.thewitnesspuzzle.puzzle.cursor.area.GridAreaSplitter;
import com.aren.thewitnesspuzzle.puzzle.factory.PuzzleFactoryConfig;
import com.aren.thewitnesspuzzle.puzzle.factory.PuzzleFactoryManager;
import com.aren.thewitnesspuzzle.puzzle.graph.Edge;
import com.aren.thewitnesspuzzle.puzzle.graph.Tile;
import com.aren.thewitnesspuzzle.puzzle.graph.Vertex;
import com.aren.thewitnesspuzzle.puzzle.rules.BlocksRule;
import com.aren.thewitnesspuzzle.puzzle.rules.BrokenLineRule;
import com.aren.thewitnesspuzzle.puzzle.rules.Color;
import com.aren.thewitnesspuzzle.puzzle.rules.EliminationRule;
import com.aren.thewitnesspuzzle.puzzle.rules.EndingPointRule;
import com.aren.thewitnesspuzzle.puzzle.rules.HexagonRule;
import com.aren.thewitnesspuzzle.puzzle.rules.SquareRule;
import com.aren.thewitnesspuzzle.puzzle.rules.StartingPointRule;
import com.aren.thewitnesspuzzle.puzzle.rules.SunRule;
import com.aren.thewitnesspuzzle.puzzle.rules.TrianglesRule;
import com.aren.thewitnesspuzzle.puzzle.walker.RandomGridWalker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

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

        nameEditText.setText("New Randomized Puzzle");

        // Disable hexagon puzzle
        hexagonPuzzleRadioButton.setVisibility(View.GONE);

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
        for(Color color : Color.values()){
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
        for(Color color : Color.values()){
            RadioButton button = new RadioButton(this);
            button.setText(color.toString());
            button.setTextColor(0xfffafafa);
            blocksColorRatioButtons.put(color, button);
            blocksColorRadioGroup.addView(button);
            if(color.equals(Color.YELLOW)){
                button.setChecked(true);
            }
        }
        blocksRateSeekBar = findViewById(R.id.blocks_rate);
        blocksRotatableRateSeekBar = findViewById(R.id.blocks_rotatable_rate);

        sunCheckBox = findViewById(R.id.sun_check_box);
        sunLayout = findViewById(R.id.sun_settings);
        sunColorRoot = findViewById(R.id.sun_colors_root);
        sunColorCheckBoxes = new HashMap<>();
        for(Color color : Color.values()){
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
        for(String ruleName : ruleNames){
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

        hexagonCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                hexagonLayout.setVisibility(isChecked ? View.VISIBLE : View.GONE);
                generateRules();
            }
        });

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

        squareCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                squareLayout.setVisibility(isChecked ? View.VISIBLE : View.GONE);
                generateRules();
            }
        });

        for(CheckBox checkBox : squareColorCheckBoxes.values()){
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    generateRules();
                }
            });
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

        blocksCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                blocksLayout.setVisibility(isChecked ? View.VISIBLE : View.GONE);
                generateRules();
            }
        });

        blocksColorRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                generateRules();
            }
        });

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

        sunCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sunLayout.setVisibility(isChecked ? View.VISIBLE : View.GONE);
                generateRules();
            }
        });

        for(CheckBox checkBox : sunColorCheckBoxes.values()){
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    generateRules();
                }
            });
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

        trianglesCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                trianglesLayout.setVisibility(isChecked ? View.VISIBLE : View.GONE);
                generateRules();
            }
        });

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

        eliminationCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                eliminationLayout.setVisibility(isChecked ? View.VISIBLE : View.GONE);
                generateRules();
            }
        });
        eliminationRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(final RadioGroup group, int checkedId) {
                if(checkedId == eliminationRadioButtons.get("hexagon").getId()){

                }
                else if(checkedId == eliminationRadioButtons.get("square").getId()){
                    if(!isSquareUsed()){
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                group.clearCheck();
                            }
                        });
                        Toast.makeText(CreateRandomPuzzleActivity.this, "Enable Square Rule first.", Toast.LENGTH_LONG).show();
                    }
                }
                else if(checkedId == eliminationRadioButtons.get("blocks").getId()){

                }
                else if(checkedId == eliminationRadioButtons.get("sun").getId()){
                    if(!isSunUsed()){
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

        handler = new Handler();

        generateRules();
    }

    protected boolean isBrokenLineUsed(){
        return brokenLineCheckBox.isChecked();
    }

    protected float getBrokenLineSpawnRate(){
        return (float)brokenLineRateSeekBar.getProgress() / brokenLineRateSeekBar.getMax();
    }

    protected boolean isHexagonUsed(){
        return hexagonCheckBox.isChecked();
    }

    protected float getHexagonSpawnRate(){
        return (float)hexagonRateSeekBar.getProgress() / hexagonRateSeekBar.getMax();
    }

    protected boolean isSquareUsed(){
        if(!squareCheckBox.isChecked()) return false;
        if(getSquareColors().size() == 0) return false;
        return true;
    }

    protected List<Color> getSquareColors(){
        List<Color> list = new ArrayList<>();
        for(Color color : squareColorCheckBoxes.keySet()){
            if(squareColorCheckBoxes.get(color).isChecked()){
                list.add(color);
            }
        }
        return list;
    }

    protected float getSquareSpawnRate(){
        return (float)squareRateSeekBar.getProgress() / squareRateSeekBar.getMax();
    }

    protected boolean isBlocksUsed(){
        return blocksCheckBox.isChecked();
    }

    protected Color getBlocksColor(){
        for(Color color : blocksColorRatioButtons.keySet()){
            if(blocksColorRatioButtons.get(color).isChecked()) return color;
        }
        return Color.YELLOW;
    }

    protected float getBlocksSpawnRate(){
        return (float)blocksRateSeekBar.getProgress() / blocksRateSeekBar.getMax();
    }

    protected float getBlocksRotatableRate(){
        return (float)blocksRotatableRateSeekBar.getProgress() / blocksRotatableRateSeekBar.getMax();
    }

    protected boolean isSunUsed(){
        if(!sunCheckBox.isChecked()) return false;
        if(getSunColors().size() == 0) return false;
        return true;
    }

    protected List<Color> getSunColors(){
        List<Color> list = new ArrayList<>();
        for(Color color : sunColorCheckBoxes.keySet()){
            if(sunColorCheckBoxes.get(color).isChecked()){
                list.add(color);
            }
        }
        return list;
    }

    protected float getSunAreaRate(){
        return (float)sunAreaRateSeekBar.getProgress() / sunAreaRateSeekBar.getMax();
    }

    protected float getSunSpawnRate(){
        return (float)sunSpawnRateSeekBar.getProgress() / sunSpawnRateSeekBar.getMax();
    }

    protected float getSunPairWithSquareRate(){
        return (float)sunPairWithSqaureRateSeekBar.getProgress() / sunPairWithSqaureRateSeekBar.getMax();
    }

    protected boolean isTrianglesUsed(){
        return trianglesCheckBox.isChecked();
    }

    protected float getTrianglesSpawnRate(){
        return (float)trianglesRateSeekBar.getProgress() / trianglesRateSeekBar.getMax();
    }

    protected boolean isEliminationUsed(){
        return eliminationCheckBox.isChecked();
    }

    protected String getEliminationFakeRule(){
        for(String name : eliminationRadioButtons.keySet()){
            if(eliminationRadioButtons.get(name).isChecked()) return name;
        }
        return null;
    }

    protected void generateRules(){
        // Clear previous rules
        for(Vertex vertex : puzzle.getVertices()){
            if(vertex.getRule() instanceof StartingPointRule || vertex.getRule() instanceof EndingPointRule) continue;
            vertex.removeRule();
        }
        for(Edge edge : puzzle.getEdges()){
            edge.removeRule();
        }
        for(Tile tile : puzzle.getTiles()){
            tile.removeRule();
        }

        splitter = new GridAreaSplitter(cursor);

        // Broken Line
        if(isBrokenLineUsed()){
            BrokenLineRule.generate(cursor, new Random(brokenLineSeed), getBrokenLineSpawnRate());
        }

        // Hexagon
        if(isHexagonUsed()){
            HexagonRule.generate(cursor, new Random(hexagonSeed), getHexagonSpawnRate());
        }

        // Square
        if(isSquareUsed()){
            splitter.assignAreaColorRandomly(new Random(squareAreaSeed), getSquareColors());
            SquareRule.generate(splitter, new Random(squareSeed), getSquareSpawnRate());
        }

        // Blocks
        if(isBlocksUsed()){
            BlocksRule.generate(splitter, new Random(blocksSeed), getBlocksColor(), getBlocksSpawnRate(), getBlocksRotatableRate());
        }

        // Sun
        if(isSunUsed()){
            SunRule.generate(splitter, new Random(sunSeed), getSunColors(), getSunAreaRate(), getSunSpawnRate(), getSunPairWithSquareRate());
        }

        // Triangles
        if(isTrianglesUsed()){
            TrianglesRule.generate(cursor, new Random(trianglesSeed), getTrianglesSpawnRate());
        }

        // Elimination
        if(isEliminationUsed()){
            String fake = getEliminationFakeRule();
            if(fake != null){
                if(fake.equals("hexagon")){
                    EliminationRule.generateFakeHexagon(splitter, new Random(eliminationSeed));
                }
                else if(fake.equals("square") && isSquareUsed()){
                    EliminationRule.generateFakeSquare(splitter, new Random(eliminationSeed), getSquareColors());
                }
                else if(fake.equals("blocks")){
                    EliminationRule.generateFakeBlocks(splitter, new Random(eliminationSeed), null, 0f);
                }
                else if(fake.equals("sun") && isSunUsed()){
                    EliminationRule.generateFakeSun(splitter, new Random(eliminationSeed), getSunColors());
                }
            }
        }

        puzzle.shouldUpdateStaticShapes();

        game.update();
    }

    @Override
    protected void resetPuzzle(){
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

        RandomGridWalker walker = new RandomGridWalker((GridPuzzle)puzzle, random, 5, 0, 0, getWidth(), getHeight());
        ArrayList<Vertex> vertexPositions = walker.getResult();
        cursor = new Cursor((GridPuzzle)puzzle, vertexPositions, null);

        puzzle.setCursor(cursor);

        game.update();
    }

    protected void savePuzzle(){
        PuzzleFactoryManager manager = new PuzzleFactoryManager(this);

        // Check name
        String name = nameEditText.getText().toString().trim();
        if(name.length() == 0){
            new AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage(String.format("Please enter a name", name))
                    .setNegativeButton("OK", null)
                    .show();
            return;
        }

        if(manager.getPuzzleFactoryByName(name) != null){
            new AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage(String.format("Name '%s' already exists.", name))
                    .setNegativeButton("OK", null)
                    .show();
            return;
        }

        PuzzleFactoryConfig config = new PuzzleFactoryConfig(this, UUID.randomUUID());
        config.setFactoryType("random");
        config.setString("name", name);
        config.setColorPalette("color", palette);
        config.setString("puzzleType", "grid");
        config.setInt("width", getWidth());
        config.setInt("height", getHeight());

        config.setBoolean("brokenline", isBrokenLineUsed());
        if(isBrokenLineUsed()){
            config.setFloat("brokenline_spawnrate", getBrokenLineSpawnRate());
        }

        config.setBoolean("hexagon", isHexagonUsed());
        if(isHexagonUsed()){
            config.setFloat("hexagon_spawnrate", getHexagonSpawnRate());
        }

        config.setBoolean("square", isSquareUsed());
        if(isSquareUsed()){
            config.setColorList("square_colors", getSquareColors());
            config.setFloat("square_spawnrate", getSquareSpawnRate());
        }

        config.setBoolean("blocks", isBlocksUsed());
        if(isBlocksUsed()){
            config.setColorList("blocks_colors", Arrays.asList(getBlocksColor()));
            config.setFloat("blocks_spawnrate", getBlocksSpawnRate());
            config.setFloat("blocks_rotatablerate", getBlocksRotatableRate());
        }

        config.setBoolean("sun", isSunUsed());
        if(isSunUsed()){
            config.setColorList("sun_colors", getSunColors());
            config.setFloat("sun_arearate", getSunAreaRate());
            config.setFloat("sun_spawnrate", getSunSpawnRate());
            config.setFloat("sun_pairwithsquare", getSunPairWithSquareRate());
        }

        config.setBoolean("triangles", isTrianglesUsed());
        if(isTrianglesUsed()){
            config.setFloat("triangles_spawnrate", getTrianglesSpawnRate());
        }

        config.setBoolean("elimination", isEliminationUsed());
        if(isEliminationUsed()){
            config.setString("elimination_fakerule", getEliminationFakeRule());
        }

        config.save();

        finish();
    }
}
