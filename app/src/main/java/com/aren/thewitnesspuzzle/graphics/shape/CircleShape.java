package com.aren.thewitnesspuzzle.graphics.shape;

import android.graphics.Color;

import com.aren.thewitnesspuzzle.graphics.shape.Shape;
import com.aren.thewitnesspuzzle.math.Vector2;
import com.aren.thewitnesspuzzle.math.Vector3;

import java.nio.FloatBuffer;

public class CircleShape extends Shape {

    private static final int TRIANGLES = 32;

    public float radius;

    public CircleShape(Vector3 center, float radius, int color){
        super(center, 1, color);
        this.radius = radius;
    }

    @Override
    public void draw() {
        super.draw();
        for(int i = 0; i < TRIANGLES; i++){
            float a = 2 * (float)Math.PI * i / TRIANGLES;
            float b = 2 * (float)Math.PI * (i + 1) / TRIANGLES;

            Vector2 pa = new Vector2((float)Math.cos(a) * radius, (float)Math.sin(a) * radius);
            Vector2 pb = new Vector2((float)Math.cos(b) * radius, (float)Math.sin(b) * radius);

            addVertex(pa);
            addVertex(new Vector2(0, 0));
            addVertex(pb);
        }
    }
}
