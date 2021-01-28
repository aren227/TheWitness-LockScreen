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

    public static void connectTiles(List<Vector2Int> connected, Random random, int[][] tiles, int width, int height, int x, int y, int targetCount, int delta) {
        connected.add(new Vector2Int(x, y));
        tiles[x][y] += delta;
        targetCount--;

        boolean[][] visit = new boolean[width][height];
        visit[x][y] = true;

        for (int i = 0; i < targetCount; i++) {
            List<Vector2Int> candidates = new ArrayList<>();
            for (int xx = 0; xx < width; xx++) {
                for (int yy = 0; yy < height; yy++) {
                    if (visit[xx][yy] || (delta < 0 && tiles[xx][yy] <= 0))
                        continue;
                    if ((xx > 0 && visit[xx - 1][yy])
                        || (xx < width - 1 && visit[xx + 1][yy])
                        || (yy > 0 && visit[xx][yy - 1])
                        || (yy < height - 1 && visit[xx][yy + 1]))
                        candidates.add(new Vector2Int(xx, yy));
                }
            }

            if (candidates.size() == 0)
                break;

            Vector2Int picked = candidates.get(random.nextInt(candidates.size()));
            connected.add(picked);
            tiles[picked.x][picked.y] += delta;
            visit[picked.x][picked.y] = true;
        }
    }

    public static List<List<Vector2Int>> makeBlocks(Random random, int[][] tiles, int width, int height, int iter, int blockMinCount, int blockMaxCount) {
        List<List<Vector2Int>> blocksList = new ArrayList<>();

        if (width == 0 || height == 0)
            throw new IllegalArgumentException("width == 0 || height == 0");
        if (tiles.length != width)
            throw new IllegalArgumentException("tiles.length != width");
        if (tiles[0].length != height)
            throw new IllegalArgumentException("tiles[0].length != height");

        int tileCount = 0;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (tiles[x][y] > 0)
                    tileCount += tiles[x][y];
            }
        }

        if (tileCount == 0)
            throw new IllegalArgumentException("Empty tiles");

        int[][] tempTiles = new int[width][height];

        long start = System.currentTimeMillis();
        for (int i = 0; i < iter; i++) {
            for (int j = 0; j < width; j++) {
                System.arraycopy(tiles[j], 0, tempTiles[j], 0, height);
            }

            List<List<Vector2Int>> blocksListCandidate = new ArrayList<>();

            while (true) {
                List<Vector2Int> candidates = new ArrayList<>();
                for (int x = 0; x < width; x++) {
                    for (int y = 0; y < height; y++) {
                        if (tempTiles[x][y] <= 0) continue;
                        candidates.add(new Vector2Int(x, y));
                    }
                }
                if (candidates.size() == 0) break;

                Vector2Int selectedPosition = candidates.get(random.nextInt(candidates.size()));

                List<Vector2Int> blocks = new ArrayList<>();
                connectTiles(blocks, random, tempTiles, width, height, selectedPosition.x, selectedPosition.y, blockMinCount + random.nextInt(blockMaxCount - blockMinCount + 1), -1);

                blocksListCandidate.add(blocks);
            }

            // Minimize the number of blocks
            if (blocksList.size() == 0 || blocksListCandidate.size() < blocksList.size()) {
                blocksList = blocksListCandidate;
            }

            // Minimize variance
            if (blocksList.size() == blocksListCandidate.size()) {
                float m = (float) tileCount / blocksList.size();
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

        return blocksList;
    }

    public static void generate(GridAreaSplitter splitter, Random random, Color color, Color subtractiveColor, float spawnRate, float rotatableRate, float subtractiveRate) {
        GridPuzzle puzzle = splitter.getPuzzle();

        List<Area> areas = new ArrayList<>(splitter.areaList);
        Collections.shuffle(areas, random);

        int filled = 0;

        Random rotatableRandom = new Random(random.nextInt()); // Just for consistency in editor

        int availableAreas = 0;
        for (Area area : areas) {
            if ((float) filled / (puzzle.getWidth() * puzzle.getHeight()) >= spawnRate) break;
            if (area.tiles.size() < 3) continue;

            filled += area.tiles.size();

            availableAreas++;
        }
        filled = 0;

        for (Area area : areas) {
            if ((float) filled / (puzzle.getWidth() * puzzle.getHeight()) >= spawnRate) break;
            if (area.tiles.size() < 3) continue;

            int[][] tiles = new int[puzzle.getWidth()][puzzle.getHeight()];

            boolean useSubtractiveBlocks = (float) filled / (puzzle.getWidth() * puzzle.getHeight()) < spawnRate * subtractiveRate;

            // Make some blocks by taking some tiles from the area
            // Try several times to get better result
            List<List<Vector2Int>> blocksList = new ArrayList<>();
            List<List<Vector2Int>> subtractiveBlocksList = new ArrayList<>();

            int tileCount = 0;
            for (Tile tile : area.tiles) {
                tiles[tile.getGridX()][tile.getGridY()] = 1;
                tileCount++;
            }

            if (useSubtractiveBlocks) {
                // Erase all blocks (non-subtractive blocks == subtractive blocks)
                // In this situation, we can use any area shape that fits in the grid.
                // But if we use this to all areas, any solutions will be correct, so we keep track availableAreas.
                if (availableAreas > 1 && random.nextFloat() < 0.5f) {
                    List<Vector2Int> subArea = new ArrayList<>();
                    connectTiles(subArea, random, tiles, puzzle.getWidth(), puzzle.getHeight(), area.tiles.get(0).getGridX(), area.tiles.get(0).getGridY(), random.nextInt(area.tiles.size()) + 1, 0);

                    tiles = new int[puzzle.getWidth()][puzzle.getHeight()];
                    for (Vector2Int v : subArea)
                        tiles[v.x][v.y] = 1;

                    subtractiveBlocksList = makeBlocks(random, tiles, puzzle.getWidth(), puzzle.getHeight(), 50, 1, 4);
                    availableAreas--;
                } else {
                    // TODO: Use multiple subtractive blocks when space is available
                    int subtractiveBlocksCount = 1;
                    for (int i = 0; i < subtractiveBlocksCount; i++) {
                        List<Vector2Int> position = new ArrayList<>();
                        for (int x = 0; x < puzzle.getWidth(); x++) {
                            for (int y = 0; y < puzzle.getHeight(); y++) {
                                if (tiles[x][y] > 0 || (x > 0 && tiles[x - 1][y] > 0) || (x < puzzle.getWidth() - 1 && tiles[x + 1][y] > 0) || (y > 0 && tiles[x][y - 1] > 0) || (y < puzzle.getHeight() - 1 && tiles[x][y + 1] > 0)) {
                                    position.add(new Vector2Int(x, y));
                                }
                            }
                        }
                        if (position.size() == 0)
                            break;

                        List<Vector2Int> connected = new ArrayList<>();
                        Vector2Int start = position.get(random.nextInt(position.size()));
                        connectTiles(connected, random, tiles, puzzle.getWidth(), puzzle.getHeight(), start.x, start.y, random.nextInt(3) + 1, 1);

                        subtractiveBlocksList.add(connected);
                    }
                }
            }

            blocksList = makeBlocks(random, tiles, puzzle.getWidth(), puzzle.getHeight(), 50, 2, 4);

            List<BlocksRule> blocksRuleList = new ArrayList<>();

            int rotatableCount = (int) (blocksList.size() * rotatableRate);
            for (int j = 0; j < blocksList.size(); j++) {
                BlocksRule rule = new BlocksRule(BlocksRule.listToGridArray(blocksList.get(j)), rotatableCount > j, false, color);
                // Rotate randomly to hide original shape
                if (rule.rotatable) rule = BlocksRule.rotateRule(rule, rotatableRandom.nextInt(4));

                blocksRuleList.add(rule);
            }
            rotatableCount = (int) (subtractiveBlocksList.size() * rotatableRate);
            for (int j = 0; j < subtractiveBlocksList.size(); j++) {
                BlocksRule rule = new BlocksRule(BlocksRule.listToGridArray(subtractiveBlocksList.get(j)), rotatableCount > j, true, subtractiveColor);
                if (rule.rotatable) rule = BlocksRule.rotateRule(rule, rotatableRandom.nextInt(4));

                blocksRuleList.add(rule);
            }

            List<Tile> placing = new ArrayList<>();
            for (Tile tile : area.tiles) {
                if (tile.getRule() == null) placing.add(tile);
            }

            // Can't place all these blocks
            if (placing.size() < blocksRuleList.size()) {
                System.out.println("Too many blocks");
                return;
            }

            Collections.shuffle(placing, random);

            for (int j = 0; j < blocksRuleList.size(); j++) {
                placing.get(j).setRule(blocksRuleList.get(j));
            }
            filled += area.tiles.size();
        }
    }

}
