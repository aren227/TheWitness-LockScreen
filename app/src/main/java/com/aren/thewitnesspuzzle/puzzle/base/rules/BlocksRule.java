package com.aren.thewitnesspuzzle.puzzle.base.rules;

import android.util.Log;

import com.aren.thewitnesspuzzle.math.Vector2Int;
import com.aren.thewitnesspuzzle.puzzle.base.GridPuzzle;
import com.aren.thewitnesspuzzle.puzzle.base.cursor.area.Area;
import com.aren.thewitnesspuzzle.puzzle.base.cursor.area.GridAreaSplitter;
import com.aren.thewitnesspuzzle.puzzle.base.graph.Tile;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class BlocksRule extends Colorable {

    public static final String NAME = "blocks";

    public boolean[][] blocks;
    public long[] blockBits;
    public int[] firstBitY;
    public int[] bitWidth;
    public int[] bitHeight;
    public int width;
    public int height;
    public int puzzleHeight;
    public boolean rotatable;
    public boolean subtractive;

    public BlocksRule(boolean[][] blocks, int puzzleHeight, boolean rotatable, boolean subtractive) {
        this(blocks, puzzleHeight, rotatable, subtractive, Color.YELLOW);
    }

    public BlocksRule(boolean[][] blocks, int puzzleHeight, boolean rotatable, boolean subtractive, Color color) {
        super(color);

        this.blocks = blocks;
        width = blocks.length;
        height = blocks[0].length;
        this.puzzleHeight = puzzleHeight;
        this.rotatable = rotatable;
        this.subtractive = subtractive;

        preCalculateBitMagic();
    }

    public BlocksRule(JSONObject jsonObject) throws JSONException {
        super(jsonObject);

        width = jsonObject.getInt("width");
        height = jsonObject.getInt("height");
        String blocksStr = jsonObject.getString("blocks");

        blocks = new boolean[width][height];
        for(int i = 0; i < width; i++){
            for(int j = 0; j < height; j++){
                blocks[i][j] = blocksStr.charAt(i * height + j) == '1';
            }
        }

        rotatable = jsonObject.getBoolean("rotatable");
        subtractive = jsonObject.getBoolean("subtractive");

        preCalculateBitMagic();
    }

    public void preCalculateBitMagic() {
        if (rotatable) {
            blockBits = new long[4];
            firstBitY = new int[4];
            bitWidth = new int[4];
            bitHeight = new int[4];
            for (int i = 0; i < 4; i++) {
                long bit = getBitMagicFromBlocks(blocks, i, puzzleHeight);
                int fbY = Long.numberOfTrailingZeros(bit);
                bit >>= fbY;
                blockBits[i] = bit;
                firstBitY[i] = fbY;
                bitWidth[i] = (i % 2 == 0) ? width : height;
                bitHeight[i] = (i % 2 == 0) ? height : width;
            }
        } else {
            long bit = getBitMagicFromBlocks(blocks, 0, puzzleHeight);
            int fbY = Long.numberOfTrailingZeros(bit);
            bit >>= fbY;
            blockBits = new long[]{bit};
            firstBitY = new int[]{fbY};
            bitWidth = new int[]{width};
            bitHeight = new int[]{height};
        }
    }

    public static long getBitMagicFromBlocks(boolean[][] blocks, int rotation, int puzzleHeight) {
        long bit = 0;
        for (int i = 0; i < blocks.length; i++) {
            for (int j = 0; j < blocks[i].length; j++) {
                if (!blocks[i][j]) continue;

                // ccw
                if (rotation == 0) bit |= (1L << (j + (i * puzzleHeight)));
                else if (rotation == 1)
                    bit |= (1L << (i + ((blocks[i].length - j - 1) * puzzleHeight)));
                else if (rotation == 2)
                    bit |= (1L << ((blocks[i].length - j - 1) + ((blocks.length - i - 1) * puzzleHeight)));
                else bit |= (1L << ((blocks.length - i - 1) + j * puzzleHeight));
            }
        }
        return bit;
    }

    public static BlocksRule rotateRule(BlocksRule rule, int rotation) {
        boolean[][] rotated;
        if (rotation % 2 == 0) rotated = new boolean[rule.width][rule.height];
        else rotated = new boolean[rule.height][rule.width];

        for (int i = 0; i < rule.width; i++) {
            for (int j = 0; j < rule.height; j++) {
                if (!rule.blocks[i][j]) continue;

                //ccw
                if (rotation == 0) rotated[i][j] = true;
                else if (rotation == 1) rotated[rule.height - j - 1][i] = true;
                else if (rotation == 2) rotated[rule.width - i - 1][rule.height - j - 1] = true;
                else rotated[j][rule.width - i - 1] = true;
            }
        }

        return new BlocksRule(rotated, rule.puzzleHeight, rule.rotatable, rule.subtractive, rule.color);
    }

    public int getBlockSize() {
        return Long.bitCount(blockBits[0]);
    }

    /*public static boolean[][] getBlocksFromBitMagic(long bit, int width, int height){
        boolean[][] blocks = new boolean[width][height];
        for(int i = 0; i < width; i++){
            for(int j = 0; j < height; j++){
                if((bit >> (j + i * height) & 1) > 0){
                    blocks[i][j] = true;
                }
            }
        }
        return blocks;
    }*/

    @Override
    public boolean canValidateLocally() {
        return false;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void serialize(JSONObject jsonObject) throws JSONException {
        super.serialize(jsonObject);
        jsonObject.put("width", width);
        jsonObject.put("height", height);

        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < width; i++){
            for(int j = 0; j < height; j++){
                builder.append(blocks[i][j] ? '1' : '0');
            }
        }
        jsonObject.put("blocks", builder.toString());
        jsonObject.put("rotatable", rotatable);
        jsonObject.put("subtractive", subtractive);
    }

    private static long board = 0;
    private static int[][] boardDebug;

    public static boolean tryAllPermutation(List<BlocksRule> rules, boolean[] taken, int takenCount, int puzzleWidth, int puzzleHeight) {
        if (takenCount == taken.length) {
            return true;
        } else {
            // Try to fill rightmost zero bit => Fill the board from the left-bottom first
            int index = Long.numberOfTrailingZeros(~board);
            int indexX = index / puzzleHeight;
            int indexY = index % puzzleHeight;
            for (int i = 0; i < taken.length; i++) {
                if (taken[i]) continue;
                BlocksRule block = rules.get(i);
                // If the block is rotatable, try all directions
                for (int j = 0; j < block.blockBits.length; j++) {
                    // check top boundary (If firstBitY > 0, it means left-bottom of the block is empty
                    if (puzzleHeight < block.bitHeight[j] - block.firstBitY[j] + indexY) continue;
                    // bottom boundary
                    if (0 > indexY - block.firstBitY[j]) continue;
                    // no need to check left boundary
                    // right boundary
                    if (puzzleWidth < block.bitWidth[j] + indexX) continue;

                    // Placing the block in the right position by shifting bits
                    long b = block.blockBits[j] << index;
                    if ((board & b) == 0) {
                        taken[i] = true;
                        board ^= b;

                        // Debug
                        /*long bb = block.blockBits[j];
                        int xx = 0;
                        int yy = block.firstBitY[j];
                        int indexYY = indexY - block.firstBitY[j];
                        while(bb > 0){
                            if(yy >= puzzleHeight){
                                yy = 0;
                                xx++;
                            }
                            if((bb & 1) > 0){
                                boardDebug[xx + indexX][yy + indexYY] = i;
                            }
                            yy++;
                            bb >>= 1;
                        }*/

                        if (tryAllPermutation(rules, taken, takenCount + 1, puzzleWidth, puzzleHeight)) {
                            return true;
                        }
                        board ^= b;
                        taken[i] = false;
                    }
                }
            }
            return false;
        }
    }

    public static List<RuleBase> areaValidate(Area area) {
        List<BlocksRule> blockRules = new ArrayList<>();
        List<RuleBase> rules = new ArrayList<>();
        int blockCount = 0;
        for (Tile tile : area.tiles) {
            if (tile.getRule() instanceof BlocksRule) {
                BlocksRule block = (BlocksRule) tile.getRule();
                if (block.eliminated) continue;
                blockRules.add(block);
                rules.add(block);
                blockCount += Long.bitCount(block.blockBits[0]);
            }
        }

        // Total block count should equal to area size
        if (area.tiles.size() != blockCount) {
            return rules;
        }

        board = ~0L;
        GridPuzzle gridPuzzle = (GridPuzzle) area.puzzle;
        boardDebug = new int[gridPuzzle.getWidth()][gridPuzzle.getHeight()];
        for (int i = 0; i < gridPuzzle.getWidth(); i++) {
            for (int j = 0; j < gridPuzzle.getHeight(); j++) {
                boardDebug[i][j] = -1;
            }
        }
        for (Tile tile : area.tiles) {
            board &= ~(1L << (tile.getGridY() + (tile.getGridX() * gridPuzzle.getHeight())));
        }
        if (!tryAllPermutation(blockRules, new boolean[blockRules.size()], 0, gridPuzzle.getWidth(), gridPuzzle.getHeight())) {
            return rules;
        }
        for (int i = gridPuzzle.getHeight() - 1; i >= 0; i--) {
            String str = "";
            for (int j = 0; j < gridPuzzle.getWidth(); j++) {
                str += "\t" + boardDebug[j][i];
            }
            Log.i("BLOCK", str);
        }
        return new ArrayList<>();
    }

    public static boolean[][] listToGridArray(List<Vector2Int> blocks) {
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;
        for (Vector2Int v : blocks) {
            minX = Math.min(minX, v.x);
            minY = Math.min(minY, v.y);
            maxX = Math.max(maxX, v.x);
            maxY = Math.max(maxY, v.y);
        }
        boolean[][] grid = new boolean[maxX - minX + 1][maxY - minY + 1];
        for (Vector2Int v : blocks) {
            grid[v.x - minX][v.y - minY] = true;
        }
        return grid;
    }

}
