package com.aren.thewitnesspuzzle.graphics.shape;

import com.aren.thewitnesspuzzle.math.Matrix2x2;
import com.aren.thewitnesspuzzle.math.Vector2;
import com.aren.thewitnesspuzzle.math.Vector3;

public class BlocksShape extends Shape {

    private static final float BLOCK_SIZE = 0.13f;
    private static final float PADDING = 0.015f;

    public boolean[][] blocks;
    public int width;
    public int height;
    public int blockCount;
    public boolean rotatable;

    public BlocksShape(boolean[][] blocks, boolean rotatable, Vector3 center, int color) {
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
                Vector2 a = new Vector2(blockCenter.x - BLOCK_SIZE * 0.5f, blockCenter.y - BLOCK_SIZE * 0.5f);
                Vector2 b = new Vector2(blockCenter.x - BLOCK_SIZE * 0.5f, blockCenter.y + BLOCK_SIZE * 0.5f);
                Vector2 c = new Vector2(blockCenter.x + BLOCK_SIZE * 0.5f, blockCenter.y + BLOCK_SIZE * 0.5f);
                Vector2 d = new Vector2(blockCenter.x + BLOCK_SIZE * 0.5f, blockCenter.y - BLOCK_SIZE * 0.5f);

                if (rotatable) {
                    a = rot.multiply(a);
                    b = rot.multiply(b);
                    c = rot.multiply(c);
                    d = rot.multiply(d);
                }

                int idx = addVertex(a);
                addVertex(b);
                addVertex(c);
                addVertex(d);

                addTriangle(idx, idx + 1, idx + 2);
                addTriangle(idx, idx + 2, idx + 3);
            }
        }
    }
}
