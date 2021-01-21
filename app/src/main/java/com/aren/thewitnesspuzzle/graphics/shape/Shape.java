package com.aren.thewitnesspuzzle.graphics.shape;

import android.graphics.Color;

import com.aren.thewitnesspuzzle.core.math.Vector2;
import com.aren.thewitnesspuzzle.core.math.Vector3;
import com.aren.thewitnesspuzzle.puzzle.animation.value.Value;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;

public abstract class Shape {

    public Vector3 center;
    public Value<Float> scale;
    public Value<Float> zIndex;
    public Value<Integer> color;

    private List<Vector3> vertices;
    private List<Short> indices;

    private int globalVertexIndex;

    public Shape(Vector3 center, float scale, int color) {
        this.center = center;
        this.scale = new Value<>(scale);
        this.zIndex = new Value<>(0f);
        this.color = new Value<>(color);
        vertices = new ArrayList<>();
        indices = new ArrayList<>();
    }

    public void draw() {
        vertices.clear();
        indices.clear();
    }

    public int addVertex(Vector2 vector2) {
        return addVertex(vector2.toVector3());
    }

    public int addVertex(Vector3 vector3) {
        int idx = vertices.size();
        vertices.add(new Vector3(vector3.x, vector3.y, vector3.z));
        return idx;
    }

    public void addTriangle(int a, int b, int c){
        indices.add((short)a);
        indices.add((short)b);
        indices.add((short)c);
    }

    public void fillVertexBuffer(FloatBuffer buffer) {
        globalVertexIndex = buffer.position() / 3;
        for (Vector3 vector3 : vertices) {
            buffer.put(vector3.x * scale.get() + center.x);
            buffer.put(vector3.y * scale.get() + center.y);
            buffer.put(vector3.z * scale.get() + center.z + zIndex.get());
        }
    }

    public void fillIndexBuffer(ShortBuffer buffer){
        for(Short s : indices){
            buffer.put((short)(globalVertexIndex + s));
        }
    }

    public void fillColorBuffer(FloatBuffer buffer) {
        float r = Color.red(color.get()) / 255f;
        float g = Color.green(color.get()) / 255f;
        float b = Color.blue(color.get()) / 255f;

        for (int i = 0; i < getVertexCount(); i++) {
            buffer.put(r);
            buffer.put(g);
            buffer.put(b);
        }
    }

    public int getVertexCount() {
        return vertices.size();
    }

    public int getIndexCount(){
        return indices.size();
    }

}
