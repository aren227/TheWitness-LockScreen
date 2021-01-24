package com.aren.thewitnesspuzzle.puzzle.generator;

import android.util.Log;

import com.aren.thewitnesspuzzle.core.cursor.area.Area;
import com.aren.thewitnesspuzzle.core.cursor.area.GridAreaSplitter;
import com.aren.thewitnesspuzzle.core.graph.Tile;
import com.aren.thewitnesspuzzle.core.math.Vector2Int;
import com.aren.thewitnesspuzzle.core.puzzle.GridPuzzle;
import com.aren.thewitnesspuzzle.core.rules.BlocksRule;
import com.aren.thewitnesspuzzle.core.rules.Color;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class BlocksRuleGenerator {

    public static void connectTiles(List<Vector2Int> connected, Random random, boolean[][] tiles, int width, int height, int x, int y, int targetCount) {
        connected.add(new Vector2Int(x, y));
        tiles[x][y] = false;
        targetCount--;

        if (targetCount == 0) return;

        List<Vector2Int> candidates = new ArrayList<>();
        if (x > 0 && tiles[x - 1][y]) candidates.add(new Vector2Int(x - 1, y));
        if (x < width - 1 && tiles[x + 1][y]) candidates.add(new Vector2Int(x + 1, y));
        if (y > 0 && tiles[x][y - 1]) candidates.add(new Vector2Int(x, y - 1));
        if (y < height - 1 && tiles[x][y + 1]) candidates.add(new Vector2Int(x, y + 1));

        if (candidates.size() == 0) return;

        Vector2Int selected = candidates.get(random.nextInt(candidates.size()));

        connectTiles(connected, random, tiles, width, height, selected.x, selected.y, targetCount);
    }

    public static void generate(GridAreaSplitter splitter, Random random, Color color, float spawnRate, float rotatableRate) {
        GridPuzzle puzzle = splitter.getPuzzle();

        List<Area> areas = new ArrayList<>(splitter.areaList);
        Collections.shuffle(areas, random);

        int filled = 0;

        Random rotatableRandom = new Random(random.nextInt()); // Just for consistency in editor

        for (Area area : areas) {
            if ((float) filled / (puzzle.getWidth() * puzzle.getHeight()) >= spawnRate) break;
            if (area.tiles.size() < 3) continue;

            boolean[][] tiles = new boolean[puzzle.getWidth()][puzzle.getHeight()];

            // Make some blocks by taking some tiles from the area
            // Try several times to get better result
            List<List<Vector2Int>> blocksList = new ArrayList<>();

            long start = System.currentTimeMillis();
            for (int i = 0; i < 100; i++) {
                // Init
                List<List<Vector2Int>> blocksListCandidate = new ArrayList<>();
                int tileCount = 0;
                for (Tile tile : area.tiles) {
                    tiles[tile.getGridX()][tile.getGridY()] = true;
                    tileCount++;
                }

                while (true) {
                    List<Vector2Int> candidates = new ArrayList<>();
                    for (int x = 0; x < puzzle.getWidth(); x++) {
                        for (int y = 0; y < puzzle.getHeight(); y++) {
                            if (!tiles[x][y]) continue;
                            candidates.add(new Vector2Int(x, y));
                        }
                    }
                    if (candidates.size() == 0) break;

                    Vector2Int selectedPosition = candidates.get(random.nextInt(candidates.size()));

                    List<Vector2Int> blocks = new ArrayList<>();
                    connectTiles(blocks, random, tiles, puzzle.getWidth(), puzzle.getHeight(), selectedPosition.x, selectedPosition.y, random.nextInt(3) + 2);

                    blocksListCandidate.add(blocks);
                }

                // Minimize the number of blocks
                if (blocksList.size() == 0 || blocksListCandidate.size() < blocksList.size()) {
                    blocksList = blocksListCandidate;
                }

                // Minimize variance
                if (blocksList.size() == blocksListCandidate.size()) {
                    float m = (float) blocksList.size() / tileCount;
                    float currentV = 0, newV = 0;
                    for (List<Vector2Int> blocks : blocksList) {
                        currentV += (blocks.size() - m) * (blocks.size() - m);
                    }
                    for (List<Vector2Int> blocks : blocksListCandidate) {
                        newV += (blocks.size() - m) * (blocks.size() - m);
                    }
                    if (newV < currentV) {
                        blocksList = blocksListCandidate;
                    }
                }
            }

            Log.i("BLOCK", "Iterations took " + (System.currentTimeMillis() - start) + "ms.");

            List<BlocksRule> blocksRuleList = new ArrayList<>();
            int rotatableCount = (int) (blocksList.size() * rotatableRate);
            for (int j = 0; j < blocksList.size(); j++) {
                int minX = Integer.MAX_VALUE;
                int minY = Integer.MAX_VALUE;
                int maxX = Integer.MIN_VALUE;
                int maxY = Integer.MIN_VALUE;
                for (Vector2Int v : blocksList.get(j)) {
                    minX = Math.min(minX, v.x);
                    minY = Math.min(minY, v.y);
                    maxX = Math.max(maxX, v.x);
                    maxY = Math.max(maxY, v.y);
                }

                boolean[][] result = new boolean[maxX - minX + 1][maxY - minY + 1];
                for (Vector2Int v : blocksList.get(j)) {
                    result[v.x - minX][v.y - minY] = true;
                }

                BlocksRule rule = new BlocksRule(result, rotatableCount > j, false, color);
                // Rotate randomly to hide original shape
                if (rule.rotatable) rule = BlocksRule.rotateRule(rule, rotatableRandom.nextInt(4));

                blocksRuleList.add(rule);
            }

            List<Tile> placing = new ArrayList<>();
            for (Tile tile : area.tiles) {
                if (tile.getRule() == null) placing.add(tile);
            }

            // Can't place all these blocks
            if (placing.size() < blocksRuleList.size()) return;

            Collections.shuffle(placing, random);

            for (int j = 0; j < blocksRuleList.size(); j++) {
                placing.get(j).setRule(blocksRuleList.get(j));
                filled += blocksRuleList.get(j).getBlockCount();
            }
        }
    }

}
