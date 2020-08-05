package com.aren.thewitnesspuzzle.graphics;

import android.graphics.Color;

import com.aren.thewitnesspuzzle.math.Vector3;

import java.nio.FloatBuffer;
import java.util.ArrayList;

public class RoundSquare implements Shape {

    private static final int CORNER_TRIANGLE = 6;
    private static final float CORNER_RATE = 0.65f;

    public Vector3 center;
    public float radius;
    public int color;

    public RoundSquare(Vector3 center, float radius, int color){
        this.center = center;
        this.radius = radius;
        this.color = color;
    }

    @Override
    public void draw(FloatBuffer buffer) {
        int[] signX = {1, -1, -1, 1};
        int[] signY = {1, 1, -1, -1};

        ArrayList<Vector3> points = new ArrayList<>();

        //4개 구석 둥근 코너를 그리자
        for(int i = 0; i < 4; i++){
            float cx = radius - radius * CORNER_RATE;
            float cy = radius - radius * CORNER_RATE;
            cx *= signX[i];
            cy *= signY[i];
            Vector3 c = center.add(new Vector3(cx, cy, 0));

            for(int j = 0; j < CORNER_TRIANGLE; j++){
                float a = 2 * (float)Math.PI * (CORNER_TRIANGLE * i + j) / (CORNER_TRIANGLE * 4);
                float b = 2 * (float)Math.PI * (CORNER_TRIANGLE * i + j + 1) / (CORNER_TRIANGLE * 4);

                Vector3 pa = c.add(new Vector3((float)Math.cos(a) * radius * CORNER_RATE, (float)Math.sin(a) * radius * CORNER_RATE, 0));
                Vector3 pb = c.add(new Vector3((float)Math.cos(b) * radius * CORNER_RATE, (float)Math.sin(b) * radius * CORNER_RATE, 0));

                buffer.put(pa.x);
                buffer.put(pa.y);
                buffer.put(pa.z);
                buffer.put(center.x);
                buffer.put(center.y);
                buffer.put(center.z);
                buffer.put(pb.x);
                buffer.put(pb.y);
                buffer.put(pb.z);

                if(j == 0) points.add(pa);
                if(j == CORNER_TRIANGLE - 1) points.add(pb);
            }
        }

        //4개의 평평한 면을 그리자
        for(int i = 0; i < 4; i++){
            Vector3 pa = points.get(i * 2 + 1);
            Vector3 pb = points.get((i * 2 + 2) % 8);

            buffer.put(pa.x);
            buffer.put(pa.y);
            buffer.put(pa.z);
            buffer.put(center.x);
            buffer.put(center.y);
            buffer.put(center.z);
            buffer.put(pb.x);
            buffer.put(pb.y);
            buffer.put(pb.z);
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

    @Override
    public int getVertexCount() {
        return (CORNER_TRIANGLE * 4 + 4) * 3;
    }
}
