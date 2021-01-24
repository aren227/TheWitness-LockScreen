package com.aren.thewitnesspuzzle.graphics.shape;

import com.aren.thewitnesspuzzle.core.math.Matrix2x2;
import com.aren.thewitnesspuzzle.core.math.Vector2;
import com.aren.thewitnesspuzzle.core.math.Vector3;

public class BlocksShape extends Shape {

    private static final float BLOCK_SIZE = 0.13f;
    private static final float PADDING = 0.015f;
    private static final float SUBTRACTIVE_STROKE = 0.032f;

    public boolean[][] blocks;
    public int width;
    public int height;
    public int blockCount;
    public boolean rotatable;
    public boolean subtractive;

    public BlocksShape(boolean[][] blocks, boolean rotatable, boolean subtractive, Vector3 center, int color) {
        super(center, 1, color);

        this.blocks = blocks;
        width = blocks.length;
        height = blocks[0].length;
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (this.blocks[i][j]) blockCount++;
            }
        }
        this.rotatable = rotatable;
        this.subtractive = subtractive;
    }

    @Override
    public void draw() {
        super.draw();
        float bs = BLOCK_SIZE + PADDING * 2;

        Matrix2x2 rot = null;
        if (rotatable) rot = Matrix2x2.getRotationMatrix((float) Math.toRadians(15f));

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (!blocks[i][j]) continue;
                Vector2 blockCenter = new Vector2(-width * bs * 0.5f + (i + 0.5f) * bs, -height * bs * 0.5f + (j + 0.5f) * bs);

                if (subtractive) {
                    Vector2 a = new Vector2(blockCenter.x - BLOCK_SIZE * 0.5f, blockCenter.y + BLOCK_SIZE * 0.5f);
                    Vector2 b = new Vector2(blockCenter.x + BLOCK_SIZE * 0.5f - SUBTRACTIVE_STROKE, blockCenter.y + BLOCK_SIZE * 0.5f);
                    Vector2 c = new Vector2(blockCenter.x + BLOCK_SIZE * 0.5f, blockCenter.y + BLOCK_SIZE * 0.5f - SUBTRACTIVE_STROKE);

                    Vector2 d = new Vector2(blockCenter.x - BLOCK_SIZE * 0.5f, blockCenter.y - BLOCK_SIZE * 0.5f + SUBTRACTIVE_STROKE);
                    Vector2 e = new Vector2(blockCenter.x - BLOCK_SIZE * 0.5f + SUBTRACTIVE_STROKE, blockCenter.y - BLOCK_SIZE * 0.5f);
                    Vector2 f = new Vector2(blockCenter.x + BLOCK_SIZE * 0.5f, blockCenter.y - BLOCK_SIZE * 0.5f);

                    drawRectangle(a, c, rot);
                    drawRectangle(d, f, rot);
                    drawRectangle(a, e, rot);
                    drawRectangle(b, f, rot);
                } else {
                    Vector2 a = new Vector2(blockCenter.x - BLOCK_SIZE * 0.5f, blockCenter.y - BLOCK_SIZE * 0.5f);
                    Vector2 b = new Vector2(blockCenter.x + BLOCK_SIZE * 0.5f, blockCenter.y + BLOCK_SIZE * 0.5f);

                    drawRectangle(a, b, rot);
                }
            }
        }
    }

    public void drawRectangle(Vector2 a, Vector2 b, Matrix2x2 rot) {
        Vector2 ll = new Vector2(Math.min(a.x, b.x), Math.min(a.y, b.y));
        Vector2 lr = new Vector2(Math.max(a.x, b.x), Math.min(a.y, b.y));
        Vector2 ul = new Vector2(Math.min(a.x, b.x), Math.max(a.y, b.y));
        Vector2 ur = new Vector2(Math.max(a.x, b.x), Math.max(a.y, b.y));

        if (rot != null) {
            ll = rot.multiply(ll);
            lr = rot.multiply(lr);
            ul = rot.multiply(ul);
            ur = rot.multiply(ur);
        }

        int idx = addVertex(ll);
        addVertex(ul);
        addVertex(ur);
        addVertex(lr);

        addTriangle(idx, idx + 1, idx + 2);
        addTriangle(idx, idx + 2, idx + 3);
    }
}
