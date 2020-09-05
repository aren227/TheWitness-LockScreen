package com.aren.thewitnesspuzzle.puzzle.rules;

import com.aren.thewitnesspuzzle.graphics.shape.RoundedSquareShape;
import com.aren.thewitnesspuzzle.graphics.shape.Shape;
import com.aren.thewitnesspuzzle.math.Vector3;
import com.aren.thewitnesspuzzle.puzzle.cursor.area.Area;
import com.aren.thewitnesspuzzle.puzzle.cursor.area.GridAreaSplitter;
import com.aren.thewitnesspuzzle.puzzle.graph.Tile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class SquareRule extends Colorable {

    public SquareRule(Color color) {
        super(color);
    }

    @Override
    public Shape generateShape() {
        if (!(getGraphElement() instanceof Tile)) return null;
        return new RoundedSquareShape(new Vector3(getGraphElement().x, getGraphElement().y, 0), 0.18f, color.getRGB());
    }

    @Override
    public boolean canValidateLocally() {
        return false;
    }

    public static List<Rule> areaValidate(Area area) {
        Map<Color, ArrayList<Rule>> squareColors = new HashMap<>();
        for (Tile tile : area.tiles) {
            if (tile.getRule() instanceof SquareRule) {
                SquareRule square = (SquareRule) tile.getRule();
                if (square.eliminated) continue;
                if (!squareColors.containsKey(square.color))
                    squareColors.put(square.color, new ArrayList<Rule>());
                squareColors.get(square.color).add(square);
            }
        }

        if (squareColors.keySet().size() <= 1) return new ArrayList<>();

        List<Integer> sizes = new ArrayList<>();
        for (Color color : squareColors.keySet()) {
            sizes.add(squareColors.get(color).size());
        }
        Collections.sort(sizes);
        int errorMaxSize = sizes.get(sizes.size() - 2);

        List<Rule> areaErrors = new ArrayList<>();
        for (Color color : squareColors.keySet()) {
            if (squareColors.get(color).size() <= errorMaxSize) {
                areaErrors.addAll(squareColors.get(color));
            }
        }
        return areaErrors;
    }

    public static void generate(GridAreaSplitter splitter, Random random, float spawnRate) {
        for (Area area : splitter.areaList) {
            ArrayList<Tile> tiles = new ArrayList<>();
            for (Tile tile : area.tiles) {
                if (tile.getRule() == null) tiles.add(tile);
            }
            if (tiles.size() == 0) continue;

            int squareCount = Math.max((int) (tiles.size() * spawnRate), 1);
            Collections.shuffle(tiles, random);
            for (int j = 0; j < squareCount; j++) {
                tiles.get(j).setRule(new SquareRule(area.color));
            }
        }
    }

}
