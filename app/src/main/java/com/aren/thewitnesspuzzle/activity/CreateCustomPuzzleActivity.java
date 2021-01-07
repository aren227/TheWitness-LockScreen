package com.aren.thewitnesspuzzle.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.aren.thewitnesspuzzle.R;
import com.aren.thewitnesspuzzle.core.color.ColorUtils;
import com.aren.thewitnesspuzzle.core.color.PalettePreset;
import com.aren.thewitnesspuzzle.core.color.PuzzleColorPalette;
import com.aren.thewitnesspuzzle.core.cursor.SymmetryCursor;
import com.aren.thewitnesspuzzle.core.graph.Edge;
import com.aren.thewitnesspuzzle.core.graph.GraphElement;
import com.aren.thewitnesspuzzle.core.graph.Vertex;
import com.aren.thewitnesspuzzle.core.math.Vector2;
import com.aren.thewitnesspuzzle.core.math.Vector2Int;
import com.aren.thewitnesspuzzle.core.puzzle.GridPuzzle;
import com.aren.thewitnesspuzzle.core.puzzle.GridSymmetryPuzzle;
import com.aren.thewitnesspuzzle.core.rules.BlocksRule;
import com.aren.thewitnesspuzzle.core.rules.BrokenLineRule;
import com.aren.thewitnesspuzzle.core.rules.EliminationRule;
import com.aren.thewitnesspuzzle.core.rules.HexagonRule;
import com.aren.thewitnesspuzzle.core.rules.SquareRule;
import com.aren.thewitnesspuzzle.core.rules.StartingPointRule;
import com.aren.thewitnesspuzzle.core.rules.SunRule;
import com.aren.thewitnesspuzzle.core.rules.Symmetry;
import com.aren.thewitnesspuzzle.core.rules.SymmetryColor;
import com.aren.thewitnesspuzzle.core.rules.SymmetryType;
import com.aren.thewitnesspuzzle.core.rules.TrianglesRule;
import com.aren.thewitnesspuzzle.dialog.ColorPaletteDialog;
import com.aren.thewitnesspuzzle.dialog.SymmetryDialog;
import com.aren.thewitnesspuzzle.game.event.ClickEvent;
import com.aren.thewitnesspuzzle.puzzle.factory.CustomFixedPuzzleFactory;
import com.aren.thewitnesspuzzle.puzzle.factory.Difficulty;
import com.aren.thewitnesspuzzle.puzzle.factory.PuzzleFactory;
import com.aren.thewitnesspuzzle.render.PuzzleRenderer;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class CreateCustomPuzzleActivity extends PuzzleEditorActivity implements ClickEvent {

    private enum ToolType {
        PLAY, ERASE, START, BROKEN_LINE, END, HEXAGON, SQUARE, SUN, BLOCKS, ELIMINATION, TRIANGLES
    }

    Map<ToolType, ImageView> toolTypeImageViewMap;
    ToolType currentToolType;

    HexagonRule hexagonRule = new HexagonRule();
    SquareRule squareRule = new SquareRule(com.aren.thewitnesspuzzle.core.rules.Color.BLACK);
    SunRule sunRule = new SunRule(com.aren.thewitnesspuzzle.core.rules.Color.ORANGE);
    BlocksRule blocksRule = new BlocksRule(new boolean[][]{{true, true}}, 4, false, false);
    EliminationRule eliminationRule = new EliminationRule();
    TrianglesRule trianglesRule = new TrianglesRule(1);

    PuzzleRenderer puzzleRenderer;

    boolean[][] blocks = new boolean[5][5];

    ViewGroup colorViewGroup, hexColorViewGroup, trianglesViewGroup, blocksViewGroup;

    ImageView noSymmetryImageView, vSymmetryImageView, rSymmetryImageView;

    CustomFixedPuzzleFactory factory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent().hasExtra("factoryUuid")) {
            factory = new CustomFixedPuzzleFactory(this, UUID.fromString(getIntent().getStringExtra("factoryUuid")));
            setGridPuzzle((GridPuzzle) factory.generate(game, new Random()).getPuzzleBase());
        } else {
            setGridPuzzle(new GridPuzzle(PalettePreset.get("Entry_1"), 4, 4));
        }



        paletteView.setPalette(getGridPuzzle().getColorPalette());

        paletteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorPaletteDialog dialog = new ColorPaletteDialog(CreateCustomPuzzleActivity.this, getGridPuzzle().getColorPalette(), new Runnable() {
                    @Override
                    public void run() {
                        paletteView.invalidate();
                        puzzleRenderer.shouldUpdateStaticShapes();
                        game.update();
                    }
                });
                dialog.show();
            }
        });

        sizeRefreshImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getSymmetry() != null && getSymmetry().type == SymmetryType.POINT && getWidth() != getHeight()) {
                    Toast.makeText(CreateCustomPuzzleActivity.this, "Width and height should be equal in rotational symmetry mode.", Toast.LENGTH_LONG).show();
                    return;
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(CreateCustomPuzzleActivity.this)
                        .setTitle("Change Dimensions")
                        .setMessage("Some symbols will be discarded!")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                apply(getWidth(), getHeight(), getSymmetry());
                                widthEditText.setText(getGridPuzzle().getWidth() + "");
                                heightEditText.setText(getGridPuzzle().getHeight() + "");
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                widthEditText.setText(getGridPuzzle().getWidth() + "");
                                heightEditText.setText(getGridPuzzle().getHeight() + "");
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
                dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(0xff000000);
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(0xff000000);
            }
        });

        nameEditText.setText(config.getString("name", "My Puzzle"));

        palette.set(getGridPuzzle().getColorPalette());
        paletteView.invalidate();

        difficultyView.setVisibility(View.GONE);

        widthEditText.setText(getGridPuzzle().getWidth() + "");
        heightEditText.setText(getGridPuzzle().getHeight() + "");

        findViewById(R.id.symmetry_container).setVisibility(View.VISIBLE);

        findViewById(R.id.no_symmetry).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SymmetryDialog dialog = new SymmetryDialog(CreateCustomPuzzleActivity.this, null, new SymmetryDialog.SymmetryDialogResult() {
                    @Override
                    public void result(boolean apply, Symmetry symmetry) {
                        if (apply)
                            apply(getGridPuzzle().getWidth(), getGridPuzzle().getHeight(), symmetry);
                    }
                });
                dialog.show();
            }
        });

        findViewById(R.id.v_symmetry).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SymmetryDialog dialog = new SymmetryDialog(CreateCustomPuzzleActivity.this, new Symmetry(SymmetryType.VLINE), new SymmetryDialog.SymmetryDialogResult() {
                    @Override
                    public void result(boolean apply, Symmetry symmetry) {
                        if (apply)
                            apply(getGridPuzzle().getWidth(), getGridPuzzle().getHeight(), symmetry);
                    }
                });
                dialog.show();
            }
        });

        findViewById(R.id.r_symmetry).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getGridPuzzle().getWidth() != getGridPuzzle().getHeight()) {
                    Toast.makeText(CreateCustomPuzzleActivity.this, "Width and height should be equal in rotational symmetry mode.", Toast.LENGTH_LONG).show();
                    return;
                }
                SymmetryDialog dialog = new SymmetryDialog(CreateCustomPuzzleActivity.this, new Symmetry(SymmetryType.POINT), new SymmetryDialog.SymmetryDialogResult() {
                    @Override
                    public void result(boolean apply, Symmetry symmetry) {
                        if (apply)
                            apply(getGridPuzzle().getWidth(), getGridPuzzle().getHeight(), symmetry);
                    }
                });
                dialog.show();
            }
        });

        noSymmetryImageView = findViewById(R.id.no_symmetry);
        vSymmetryImageView = findViewById(R.id.v_symmetry);
        rSymmetryImageView = findViewById(R.id.r_symmetry);

        noSymmetryImageView.setColorFilter(Color.WHITE);
        vSymmetryImageView.setColorFilter(Color.DKGRAY);
        rSymmetryImageView.setColorFilter(Color.DKGRAY);

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

        colorViewGroup = findViewById(R.id.color_grid);
        for (final com.aren.thewitnesspuzzle.core.rules.Color color : com.aren.thewitnesspuzzle.core.rules.Color.values()) {
            ImageView colorImageView = new ImageView(this);
            colorImageView.setImageResource(R.drawable.circle_image);
            colorImageView.setColorFilter(color.getRGB());
            colorImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectColor(color);
                }
            });

            colorViewGroup.addView(colorImageView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        }

        hexColorViewGroup = findViewById(R.id.hex_color_grid);
        for (final SymmetryColor symmetryColor : new SymmetryColor[]{SymmetryColor.NONE, SymmetryColor.CYAN, SymmetryColor.YELLOW}) {
            ImageView colorImageView = new ImageView(this);
            colorImageView.setImageResource(R.drawable.circle_image);
            colorImageView.setColorFilter(symmetryColor == SymmetryColor.NONE ? ColorUtils.RGB(1, 1, 1) : symmetryColor.getRGB());
            colorImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hexagonRule.setSymmetricColor(symmetryColor);
                }
            });

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(10, 10, 10, 10);

            hexColorViewGroup.addView(colorImageView, params);
        }

        trianglesViewGroup = findViewById(R.id.triangles_container);

        final TextView trianglesTextView = findViewById(R.id.triangles_text);
        SeekBar trianglesSeekBar = findViewById(R.id.triangles_count);
        trianglesSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int count = progress + 1;
                trianglesTextView.setText(count + " Triangle" + (count > 1 ? "s" : ""));
                trianglesRule.count = count;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        blocksViewGroup = findViewById(R.id.blocks_container);
        ViewGroup blocksGrid = findViewById(R.id.blocks_grid);
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                final ImageView colorImageView = new ImageView(this);
                colorImageView.setImageResource(R.drawable.slightly_rounded_image);
                colorImageView.setColorFilter(Color.DKGRAY);

                final int y = 4 - i;
                final int x = j;
                colorImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (blocks[x][y])
                            colorImageView.setColorFilter(Color.DKGRAY);
                        else
                            colorImageView.setColorFilter(com.aren.thewitnesspuzzle.core.rules.Color.YELLOW.getRGB());
                        blocks[x][y] = !blocks[x][y];
                        updateBlocks();
                    }
                });

                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.setMargins(4, 4, 4, 4);

                blocksGrid.addView(colorImageView, params);
            }
        }
        ((CheckBox) findViewById(R.id.blocks_rotatable_checkbox)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                blocksRule.rotatable = isChecked;
            }
        });

        findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CreateCustomPuzzleActivity.this)
                        .setTitle("Save & Exit")
                        .setMessage("Are you sure?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (factory == null)
                                    factory = new CustomFixedPuzzleFactory(CreateCustomPuzzleActivity.this, UUID.randomUUID());

                                try {
                                    factory.setEdited(nameEditText.getText().toString(), puzzleRenderer.getPuzzleBase());
                                    finish();
                                } catch (JSONException e) {
                                    Toast.makeText(CreateCustomPuzzleActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                                    e.printStackTrace();
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

        selectTool(ToolType.PLAY);

        game.addClickListener(this);
    }

    private void apply(int w, int h, Symmetry newSymmetry) {
        GridPuzzle gridPuzzle = getGridPuzzle();

        if (newSymmetry == null || newSymmetry.type != SymmetryType.POINT || newSymmetry.color == SymmetryColor.NONE) {
            for (Vertex vertex : gridPuzzle.getVertices()) {
                if (vertex.getRule() instanceof HexagonRule && ((HexagonRule) vertex.getRule()).hasSymmetricColor())
                    ((HexagonRule) vertex.getRule()).setSymmetricColor(SymmetryColor.NONE);
            }
            for (Edge edge : gridPuzzle.getEdges()) {
                if (edge.getRule() instanceof HexagonRule && ((HexagonRule) edge.getRule()).hasSymmetricColor())
                    ((HexagonRule) edge.getRule()).setSymmetricColor(SymmetryColor.NONE);
            }
        }

        if (newSymmetry != null) {
            for (int i = 0; i <= gridPuzzle.getWidth(); i++) {
                for (int j = 0; j <= gridPuzzle.getHeight(); j++) {
                    if (gridPuzzle.getVertexAt(i, j).getRule() instanceof StartingPointRule)
                        gridPuzzle.removeStartingPoint(i, j);
                    // Ending points are not copied
                }
            }
        }

        // Assert
        if (newSymmetry != null && newSymmetry.type == SymmetryType.POINT && w != h)
            return;

        int ow = gridPuzzle.getWidth();
        int oh = gridPuzzle.getHeight();

        PuzzleColorPalette colorPalette = gridPuzzle.getColorPalette();

        GridPuzzle newGridPuzzle;
        if (newSymmetry == null)
            newGridPuzzle = new GridPuzzle(colorPalette, w, h);
        else
            newGridPuzzle = new GridSymmetryPuzzle(colorPalette, w, h, newSymmetry);

        for (int i = 0; i <= Math.min(ow, w); i++) {
            for (int j = 0; j <= Math.min(oh, h); j++) {
                newGridPuzzle.getVertexAt(i, j).setRule(gridPuzzle.getVertexAt(i, j).getRule());
            }
        }

        for (int i = 0; i < Math.min(ow, w); i++) {
            for (int j = 0; j <= Math.min(oh, h); j++) {
                newGridPuzzle.getEdgeAt(i, j, true).setRule(gridPuzzle.getEdgeAt(i, j, true).getRule());
            }
        }

        for (int i = 0; i <= Math.min(ow, w); i++) {
            for (int j = 0; j < Math.min(oh, h); j++) {
                newGridPuzzle.getEdgeAt(i, j, false).setRule(gridPuzzle.getEdgeAt(i, j, false).getRule());
            }
        }

        for (int i = 0; i < Math.min(ow, w); i++) {
            for (int j = 0; j < Math.min(oh, h); j++) {
                newGridPuzzle.getTileAt(i, j).setRule(gridPuzzle.getTileAt(i, j).getRule());
            }
        }

        setGridPuzzle(newGridPuzzle);
        selectTool(currentToolType);

        if (hexagonRule.hasSymmetricColor() && getSymmetry() == null)
            hexagonRule.setSymmetricColor(SymmetryColor.NONE);

        // Update puzzle height of the block rule
        blocksRule = new BlocksRule(blocksRule.blocks, getGridPuzzle().getHeight(), blocksRule.rotatable, blocksRule.subtractive, blocksRule.color);
        for (int i = 0; i < Math.min(ow, w); i++) {
            for (int j = 0; j < Math.min(oh, h); j++) {
                if (newGridPuzzle.getTileAt(i, j).getRule() instanceof BlocksRule) {
                    BlocksRule prevBlocksRule = (BlocksRule) newGridPuzzle.getTileAt(i, j).getRule();
                    newGridPuzzle.getTileAt(i, j).setRule(new BlocksRule(prevBlocksRule.blocks, getGridPuzzle().getHeight(), prevBlocksRule.rotatable, prevBlocksRule.subtractive, prevBlocksRule.color));
                }
            }
        }

        noSymmetryImageView.setColorFilter(Color.DKGRAY);
        vSymmetryImageView.setColorFilter(Color.DKGRAY);
        rSymmetryImageView.setColorFilter(Color.DKGRAY);
        if (getSymmetry() == null)
            noSymmetryImageView.setColorFilter(Color.WHITE);
        else if (getSymmetry().type == SymmetryType.VLINE)
            vSymmetryImageView.setColorFilter(Color.WHITE);
        else
            rSymmetryImageView.setColorFilter(Color.WHITE);
    }

    private void selectTool(ToolType toolType) {
        if (currentToolType != null)
            toolTypeImageViewMap.get(currentToolType).setColorFilter(Color.DKGRAY);
        currentToolType = toolType;
        toolTypeImageViewMap.get(currentToolType).setColorFilter(Color.WHITE);

        if (toolType == ToolType.PLAY)
            game.getPuzzle().setUntouchable(false);
        else
            game.getPuzzle().setUntouchable(true);

        if (toolType == ToolType.SQUARE)
            toolTypeImageViewMap.get(currentToolType).setColorFilter(squareRule.color.getRGB());
        else if (toolType == ToolType.SUN)
            toolTypeImageViewMap.get(currentToolType).setColorFilter(sunRule.color.getRGB());
        else if (toolType == ToolType.BLOCKS)
            toolTypeImageViewMap.get(currentToolType).setColorFilter(blocksRule.color.getRGB());
        else if (toolType == ToolType.ELIMINATION)
            toolTypeImageViewMap.get(currentToolType).setColorFilter(eliminationRule.color.getRGB());

        colorViewGroup.setVisibility(View.GONE);
        hexColorViewGroup.setVisibility(View.GONE);
        trianglesViewGroup.setVisibility(View.GONE);
        blocksViewGroup.setVisibility(View.GONE);

        if (toolType == ToolType.SQUARE || toolType == ToolType.SUN || toolType == ToolType.BLOCKS || toolType == ToolType.ELIMINATION)
            colorViewGroup.setVisibility(View.VISIBLE);
        if (toolType == ToolType.HEXAGON)
            hexColorViewGroup.setVisibility(View.VISIBLE);
        if (toolType == ToolType.TRIANGLES)
            trianglesViewGroup.setVisibility(View.VISIBLE);
        if (toolType == ToolType.BLOCKS)
            blocksViewGroup.setVisibility(View.VISIBLE);
    }

    private void selectColor(com.aren.thewitnesspuzzle.core.rules.Color color) {
        if (currentToolType == ToolType.SQUARE)
            squareRule.color = color;
        else if (currentToolType == ToolType.SUN)
            sunRule.color = color;
        else if (currentToolType == ToolType.BLOCKS)
            blocksRule.color = color;
        else if (currentToolType == ToolType.ELIMINATION)
            eliminationRule.color = color;

        selectTool(currentToolType); // Update tool icons
    }

    private void updateBlocks() {
        List<Vector2Int> list = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            for(int j = 0; j < 5; j++) {
                if (blocks[i][j])
                    list.add(new Vector2Int(i, j));
            }
        }
        blocksRule = new BlocksRule(BlocksRule.listToGridArray(list), getGridPuzzle().getHeight(), blocksRule.rotatable, blocksRule.subtractive, blocksRule.color);
    }

    private GridPuzzle getGridPuzzle() {
        return (GridPuzzle) game.getPuzzle().getPuzzleBase();
    }

    private void setGridPuzzle(GridPuzzle gridPuzzle) {
        puzzleRenderer = new PuzzleRenderer(game, gridPuzzle);
        game.setPuzzle(puzzleRenderer);
        game.update();
    }

    private Symmetry getSymmetry() {
        if (getGridPuzzle() instanceof GridSymmetryPuzzle)
            return ((GridSymmetryPuzzle) getGridPuzzle()).getSymmetry();
        return null;
    }

    private void removeEndingPointWithCursor(GraphElement graphElement) {
        GridPuzzle gridPuzzle = getGridPuzzle();
        if (puzzleRenderer.getCursor() != null) {
            Vertex last = puzzleRenderer.getCursor().getLastVisitedVertex();
            if (last == graphElement
                    || (gridPuzzle instanceof GridSymmetryPuzzle && last == ((GridSymmetryPuzzle) gridPuzzle).getOppositeVertex((Vertex) graphElement)))
                puzzleRenderer.setCursor(null);
        }
        gridPuzzle.removeEndingPoint(graphElement.getGridX(), graphElement.getGridY());
    }

    private void removeStartingPointWithCursor(GraphElement graphElement) {
        GridPuzzle gridPuzzle = getGridPuzzle();
        if (puzzleRenderer.getCursor() != null) {
            Vertex first = puzzleRenderer.getCursor().getFirstVisitedVertex();
            if (first == graphElement
                    || (gridPuzzle instanceof GridSymmetryPuzzle && first == ((GridSymmetryPuzzle) gridPuzzle).getOppositeVertex((Vertex) graphElement)))
                puzzleRenderer.setCursor(null);
        }
        gridPuzzle.removeStartingPoint(graphElement.getGridX(), graphElement.getGridY());
    }

    @Override
    public void onClick(float x, float y, int action) {
        if (action != MotionEvent.ACTION_DOWN || currentToolType == ToolType.PLAY)
            return;

        if (!game.getPuzzle().getBoundingBox().test(new Vector2(x, y)))
            return;

        boolean containsVertices = false;
        boolean containsEdges = false;
        boolean containsTiles = false;

        switch (currentToolType) {
            case ERASE:
                containsVertices = containsEdges = containsTiles = true;
                break;
            case START:
            case END:
                containsVertices = true;
                break;
            case BROKEN_LINE:
                containsEdges = true;
                break;
            case HEXAGON:
                containsVertices = containsEdges = true;
                break;
            case SQUARE:
            case SUN:
            case BLOCKS:
            case ELIMINATION:
            case TRIANGLES:
                containsTiles = true;
                break;
        }

        GraphElement graphElement = game.getPuzzle().getPuzzleBase().getNearestGraphElement(new Vector2(x, y),
                containsVertices, containsEdges, containsTiles);

        if (graphElement == null)
            return;

        GridPuzzle gridPuzzle = (GridPuzzle) game.getPuzzle().getPuzzleBase();

        int gx = graphElement.getGridX();
        int gy = graphElement.getGridY();

        if (currentToolType == ToolType.ERASE) {
            if (graphElement instanceof Vertex) {
                if (gridPuzzle.isEndingPoint(gx, gy)) {
                    removeEndingPointWithCursor(graphElement);
                } else if (graphElement.getRule() instanceof StartingPointRule) {
                    removeStartingPointWithCursor(graphElement);
                } else {
                    graphElement.removeRule();
                }
            } else {
                graphElement.removeRule();
            }
        } else if (currentToolType == ToolType.START) {
            if (gridPuzzle.isEndingPoint(gx, gy))
                removeEndingPointWithCursor(graphElement);
            graphElement.removeRule();

            gridPuzzle.addStartingPoint(gx, gy);
        } else if (currentToolType == ToolType.END) {
            if (gx == 0 || gy == 0 || gx == gridPuzzle.getWidth() || gy == gridPuzzle.getHeight()) {
                if (graphElement.getRule() instanceof StartingPointRule)
                    removeStartingPointWithCursor(graphElement);
                graphElement.removeRule();

                gridPuzzle.addEndingPoint(gx, gy);
            }
        } else if (currentToolType == ToolType.BROKEN_LINE) {
            if (puzzleRenderer.getCursor() != null && puzzleRenderer.getCursor().containsEdge((Edge) graphElement))
                puzzleRenderer.setCursor(null);
            graphElement.setRule(new BrokenLineRule());
        } else if (currentToolType == ToolType.HEXAGON) {
            graphElement.setRule(hexagonRule.clone());
        } else if (currentToolType == ToolType.SQUARE) {
            graphElement.setRule(squareRule.clone());
        } else if (currentToolType == ToolType.SUN) {
            graphElement.setRule(sunRule.clone());
        } else if (currentToolType == ToolType.BLOCKS) {
            boolean hasBlock = false;
            for (int i = 0; i < 5; i++) {
                for (int j = 0; j < 5; j++) {
                    if (blocks[i][j]) {
                        hasBlock = true;
                        break;
                    }
                }
                if (hasBlock)
                    break;
            }

            if (hasBlock)
                graphElement.setRule(blocksRule.clone());
        } else if (currentToolType == ToolType.ELIMINATION) {
            graphElement.setRule(eliminationRule.clone());
        } else if (currentToolType == ToolType.TRIANGLES) {
            graphElement.setRule(trianglesRule.clone());
        }

        game.getPuzzle().shouldUpdateStaticShapes();
    }

}
