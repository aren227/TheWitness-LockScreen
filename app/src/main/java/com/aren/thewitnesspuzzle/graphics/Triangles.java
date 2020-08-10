package com.aren.thewitnesspuzzle.graphics;

import android.graphics.Color;

import com.aren.thewitnesspuzzle.math.Vector3;

import java.nio.FloatBuffer;

public class Triangles implements Shape{

    public Vector3 center;
    public float size; // radius
    public int amount;
    public int color;

    public Triangles(Vector3 center, float size, int amount, int color){
        this.center = center;
        this.size = size;
        this.amount = amount;
        this.color = color;
    }

    @Override
    public void draw(FloatBuffer buffer) {
        for(int i = 0; i < amount; i++){
            float x = center.x - size * amount + size * (i * 2 + 1);
            buffer.put(x - 0.866f * size);
            buffer.put(center.y - 0.5f * size);
            buffer.put(center.z);
            buffer.put(x);
            buffer.put(center.y + size);
            buffer.put(center.z);
            buffer.put(x + 0.866f * size);
            buffer.put(center.y - 0.5f * size);
            buffer.put(center.z);
        }
    }

    public void drawColor(FloatBuffer buffer){
        float r = Color.red(color) / 255f;
        float g = Color.green(color) / 255f;
        float b = Color.blue(color) / 255f;

        for(int i = 0; i < getVertexCount(); i++){
            buffer.put(r);
            buffer.put(g);
            buffer.put(b);
        }
    }

    public int getVertexCount(){
        return 3 * amount;
    }

}
