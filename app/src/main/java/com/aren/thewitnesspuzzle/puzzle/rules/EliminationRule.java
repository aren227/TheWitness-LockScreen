package com.aren.thewitnesspuzzle.puzzle.rules;

import com.aren.thewitnesspuzzle.graphics.shape.EliminatorShape;
import com.aren.thewitnesspuzzle.graphics.shape.Shape;
import com.aren.thewitnesspuzzle.math.Vector2Int;
import com.aren.thewitnesspuzzle.math.Vector3;
import com.aren.thewitnesspuzzle.puzzle.base.PuzzleBase;
import com.aren.thewitnesspuzzle.puzzle.cursor.area.Area;
import com.aren.thewitnesspuzzle.puzzle.cursor.area.GridAreaSplitter;
import com.aren.thewitnesspuzzle.puzzle.graph.Tile;
import com.aren.thewitnesspuzzle.puzzle.graph.Vertex;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class EliminationRule extends Colorable {

    public static final String NAME = "elimination";

    public static final int COLOR = android.graphics.Color.parseColor("#fafafa");

    public EliminationRule(){
        this(Color.WHITE);
    }

    public EliminationRule(Color color) {
        super(color);
    }

    @Override
    public Shape generateShape() {
        if (!(getGraphElement() instanceof Tile)) return null;
        return new EliminatorShape(new Vector3(getGraphElement().x, getGraphElement().y, 0), color.getRGB());
    }

    @Override
    public boolean canValidateLocally() {
        return false;
    }

    @Override
    public String getName() {
        return NAME;
    }

    public static EliminationRule deserialize(JSONObject jsonObject) throws JSONException {
        Color color = Color.fromString(jsonObject.getString("color"));
        return new EliminationRule(color);
    }

    public static void generateFakeHexagon(GridAreaSplitter splitter, Random random) {
        List<Area> areas = new ArrayList<>(splitter.areaList);
        Collections.shuffle(areas, random);
        for (Area area : areas) {
            List<Tile> tiles = new ArrayList<>();
            for (Tile tile : area.tiles) {
                if (tile.getRule() == null) tiles.add(tile);
            }
            if (tiles.size() == 0) continue;

            // There is no hexagon dot in the area. So we can add them all!
            List<Vertex> vertices = new ArrayList<>(area.getVertices(splitter.getCursor()));
            if (vertices.size() == 0) continue;

            // Add fake rule
            vertices.get(random.nextInt(vertices.size())).setRule(new HexagonRule());
            tiles.get(random.nextInt(tiles.size())).setRule(new EliminationRule());

            break;
        }
    }

    public static void generateFakeSquare(GridAreaSplitter splitter, Random random, List<Color> colors) {
        List<Area> areas = new ArrayList<>(splitter.areaList);
        Collections.shuffle(areas, random);
        for (Area area : splitter.areaList) {
            List<Tile> tiles = new ArrayList<>();
            List<Tile> squareTiles = new ArrayList<>();
            Set<Color> availableColors = new HashSet<>(colors);
            for (Tile tile : area.tiles) {
                if (tile.getRule() == null) {
                    tiles.add(tile);
                } else if (tile.getRule() instanceof SquareRule) {
                    squareTiles.add(tile);
                    availableColors.remove(((SquareRule) tile.getRule()).color);
                }
                /*else if(tile.getRule() instanceof SunRule){
                    availableColors.remove(((SunRule)tile.getRule()).color);
                }*/
            }
            if (squareTiles.size() < 1 || tiles.size() + squareTiles.size() < 3) continue;
            if (availableColors.size() == 0) continue;
            List<Color> availableColorList = new ArrayList<>(availableColors);

            Collections.shuffle(tiles, random);
            Collections.shuffle(squareTiles, random);

            List<Tile> wholeTiles = new ArrayList<>();
            wholeTiles.addAll(tiles);
            wholeTiles.addAll(squareTiles);

            // Empty tiles have high priority
            wholeTiles.get(0).setRule(new EliminationRule());
            wholeTiles.get(1).setRule(new SquareRule(availableColorList.get(random.nextInt(availableColorList.size()))));
            break;
        }
    }

    public static void generateFakeBlocks(GridAreaSplitter splitter, Random random, Color color, float rotatableProb) {
        List<Area> areas = new ArrayList<>(splitter.areaList);
        Collections.shuffle(areas, random);
        for (Area area : splitter.areaList) {
            List<Tile> tiles = new ArrayList<>();
            for (Tile tile : area.tiles) {
                if (tile.getRule() == null) {
                    tiles.add(tile);
                }
            }

            if (tiles.size() < 2) continue;

            boolean[][] grid = new boolean[4][4];
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    grid[i][j] = true;
                }
            }
            List<Vector2Int> result = new ArrayList<>();
            BlocksRule.connectTiles(result, random, grid, 4, 4, 0, 0, random.nextInt(3) + 2);

            boolean rotatable = random.nextFloat() < rotatableProb;
            BlocksRule rule = new BlocksRule(BlocksRule.listToGridArray(result), splitter.getPuzzle().getHeight(), rotatable, false, color);

            // Check if this one block matches with the area
            List<Vector2Int> areaList = new ArrayList<>();
            for (Tile tile : area.tiles) {
                areaList.add(tile.gridPosition);
            }
            BlocksRule areaRule = new BlocksRule(BlocksRule.listToGridArray(areaList), splitter.getPuzzle().getHeight(), false, false);
            boolean pass = true;
            if (rotatable) {
                for (int i = 0; i < 4; i++) {
                    if (rule.blockBits[i] == areaRule.blockBits[i]) {
                        pass = false;
                    }
                    rule = BlocksRule.rotateRule(rule, 1);
                }
            } else if (rule.blockBits[0] == areaRule.blockBits[0]) {
                pass = false;
            }

            if (!pass) continue;

            Collections.shuffle(tiles, random);

            tiles.get(0).setRule(new EliminationRule());
            tiles.get(1).setRule(rule);
            break;
        }
    }

    public static void generateFakeSun(GridAreaSplitter splitter, Random random, List<Color> colors) {
        List<Area> areas = new ArrayList<>(splitter.areaList);
        Collections.shuffle(areas, random);
        for (Area area : splitter.areaList) {
            List<Tile> tiles = new ArrayList<>();
            for (Tile tile : area.tiles) {
                if (tile.getRule() == null) {
                    tiles.add(tile);
                }
            }
            if (tiles.size() < 2) continue;
            List<Color> colorList = new ArrayList<>(colors);
            Collections.shuffle(colorList, random);
            boolean placed = false;
            for (Color color : colorList) {
                int count = 0;
                for (Tile tile : area.tiles) {
                    if (tile.getRule() instanceof Colorable && ((Colorable) tile.getRule()).color == color) {
                        count++;
                    }
                }
                // Can be paired with this one
                if (count == 1) continue;

                Collections.shuffle(tiles, random);
                tiles.get(0).setRule(new EliminationRule());
                tiles.get(1).setRule(new SunRule(color));
                placed = true;
                break;
            }
            if (placed) break;
        }
    }
}
