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
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.aren.thewitnesspuzzle.R;
import com.aren.thewitnesspuzzle.core.color.PalettePreset;
import com.aren.thewitnesspuzzle.core.color.PuzzleColorPalette;
import com.aren.thewitnesspuzzle.core.graph.Edge;
import com.aren.thewitnesspuzzle.core.graph.GraphElement;
import com.aren.thewitnesspuzzle.core.graph.Vertex;
import com.aren.thewitnesspuzzle.core.math.Vector2;
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
import com.aren.thewitnesspuzzle.dialog.SymmetryDialog;
import com.aren.thewitnesspuzzle.game.event.ClickEvent;
import com.aren.thewitnesspuzzle.puzzle.factory.Difficulty;
import com.aren.thewitnesspuzzle.puzzle.factory.PuzzleFactory;
import com.aren.thewitnesspuzzle.render.PuzzleRenderer;

import java.util.HashMap;
import java.util.Map;

public class CreateCustomPuzzleActivity extends PuzzleEditorActivity implements ClickEvent {

    private enum ToolType {
        PLAY, ERASE, START, BROKEN_LINE, END, HEXAGON, SQUARE, SUN, BLOCKS, ELIMINATION, TRIANGLES
    }

    Map<ToolType, ImageView> toolTypeImageViewMap;
    ToolType currentToolType;

    SquareRule squareRule = new SquareRule(com.aren.thewitnesspuzzle.core.rules.Color.BLACK);
    SunRule sunRule = new SunRule(com.aren.thewitnesspuzzle.core.rules.Color.ORANGE);
    BlocksRule blocksRule = new BlocksRule(new boolean[][]{{true, true}}, 4, false, false);
    EliminationRule eliminationRule = new EliminationRule();
    TrianglesRule trianglesRule = new TrianglesRule(1);

    PuzzleRenderer puzzleRenderer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setGridPuzzle(new GridPuzzle(PalettePreset.get("Entry_1"), 4, 4));

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

        ((ImageView) findViewById(R.id.no_symmetry)).setColorFilter(Color.WHITE);
        ((ImageView) findViewById(R.id.v_symmetry)).setColorFilter(Color.DKGRAY);
        ((ImageView) findViewById(R.id.r_symmetry)).setColorFilter(Color.DKGRAY);

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

        selectTool(ToolType.PLAY);

        game.addClickListener(this);

        LinearLayout colorContainer = findViewById(R.id.color_grid);
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

            colorContainer.addView(colorImageView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        }
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
                    gridPuzzle.removeEndingPoint(gx, gy);
                } else if (graphElement.getRule() instanceof StartingPointRule) {
                    gridPuzzle.removeStartingPoint(gx, gy);
                } else {
                    graphElement.removeRule();
                }
            } else {
                graphElement.removeRule();
            }
        } else if (currentToolType == ToolType.START) {
            if (gridPuzzle.isEndingPoint(gx, gy))
                gridPuzzle.removeEndingPoint(gx, gy);
            graphElement.removeRule();

            gridPuzzle.addStartingPoint(gx, gy);
        } else if (currentToolType == ToolType.END) {
            if (gx == 0 || gy == 0 || gx == gridPuzzle.getWidth() || gy == gridPuzzle.getHeight()) {
                graphElement.removeRule();

                gridPuzzle.addEndingPoint(gx, gy);
            }
        } else if (currentToolType == ToolType.BROKEN_LINE) {
            graphElement.setRule(new BrokenLineRule());
        } else if (currentToolType == ToolType.HEXAGON) {
            graphElement.setRule(new HexagonRule());
        } else if (currentToolType == ToolType.SQUARE) {
            graphElement.setRule(squareRule.clone());
        } else if (currentToolType == ToolType.SUN) {
            graphElement.setRule(sunRule.clone());
        } else if (currentToolType == ToolType.BLOCKS) {
            graphElement.setRule(blocksRule.clone());
        } else if (currentToolType == ToolType.ELIMINATION) {
            graphElement.setRule(eliminationRule.clone());
        } else if (currentToolType == ToolType.TRIANGLES) {
            graphElement.setRule(trianglesRule.clone());
        }

        game.getPuzzle().shouldUpdateStaticShapes();
    }

}
