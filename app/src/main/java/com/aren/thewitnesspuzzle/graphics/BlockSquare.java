package com.aren.thewitnesspuzzle.graphics;

import android.graphics.Color;

import com.aren.thewitnesspuzzle.math.Matrix2x2;
import com.aren.thewitnesspuzzle.math.Vector2;
import com.aren.thewitnesspuzzle.math.Vector3;

import java.nio.FloatBuffer;

public class BlockSquare implements Shape {

    private static final float BLOCK_SIZE = 0.13f;
    private static final float PADDING = 0.015f;

    public boolean[][] blocks;
    public int width;
    public int height;
    public int blockCount;
    public boolean rotatable;
    public Vector3 center;
    public int color;

    public BlockSquare(boolean[][] blocks, boolean rotatable, Vector3 center, int color){
        this.blocks = blocks;
        width = blocks.length;
        height = blocks[0].length;
        for(int i = 0; i < width; i++){
            for(int j = 0; j < height; j++){
                if(this.blocks[i][j]) blockCount++;
            }
        }
        this.rotatable = rotatable;
        this.center = center;
        this.color = color;
    }

    @Override
    public void draw(FloatBuffer buffer) {
        float bs = BLOCK_SIZE + PADDING * 2;

        Matrix2x2 rot = null;
        if(rotatable) rot = Matrix2x2.getRotationMatrix((float)Math.toRadians(15f));

        for(int i = 0; i < width; i++){
            for(int j = 0; j < height; j++){
                if(!blocks[i][j]) continue;
                Vector2 blockCenter = new Vector2(-width * bs * 0.5f + (i + 0.5f) * bs, -height * bs * 0.5f + (j + 0.5f) * bs);
                Vector2 a = new Vector2(blockCenter.x - BLOCK_SIZE * 0.5f, blockCenter.y - BLOCK_SIZE * 0.5f);
                Vector2 b = new Vector2(blockCenter.x - BLOCK_SIZE * 0.5f, blockCenter.y + BLOCK_SIZE * 0.5f);
                Vector2 c = new Vector2(blockCenter.x + BLOCK_SIZE * 0.5f, blockCenter.y + BLOCK_SIZE * 0.5f);
                Vector2 d = new Vector2(blockCenter.x + BLOCK_SIZE * 0.5f, blockCenter.y - BLOCK_SIZE * 0.5f);

                if(rotatable){
                    a = rot.multiply(a);
                    b = rot.multiply(b);
                    c = rot.multiply(c);
                    d = rot.multiply(d);
                }

                Vector3 aa = new Vector3(center.x + a.x, center.y + a.y, center.z);
                Vector3 bb = new Vector3(center.x + b.x, center.y + b.y, center.z);
                Vector3 cc = new Vector3(center.x + c.x, center.y + c.y, center.z);
                Vector3 dd = new Vector3(center.x + d.x, center.y + d.y, center.z);

                buffer.put(aa.x);
                buffer.put(aa.y);
                buffer.put(aa.z);
                buffer.put(bb.x);
                buffer.put(bb.y);
                buffer.put(bb.z);
                buffer.put(cc.x);
                buffer.put(cc.y);
                buffer.put(cc.z);

                buffer.put(aa.x);
                buffer.put(aa.y);
                buffer.put(aa.z);
                buffer.put(cc.x);
                buffer.put(cc.y);
                buffer.put(cc.z);
                buffer.put(dd.x);
                buffer.put(dd.y);
                buffer.put(dd.z);
            }
        }
    }

    @Override
    public void drawColor(FloatBuffer buffer) {
        float r = Color.red(color) / 255f;
        float g = Color.green(color) / 255f;
        float b = Color.blue(color) / 255f;

        for(int i = 0; i < getVertexCount(); i++){
            buffer.put(r);
            buffer.put(g);
            buffer.put(b);
        }
    }

    @Override
    public int getVertexCount() {
        return 6 * blockCount;
    }
}
