package com.aren.thewitnesspuzzle.graphics;

import java.nio.FloatBuffer;

public class Buffer {

    FloatBuffer vertex, vertexColor;
    int vertexCount;

    public Buffer(FloatBuffer vertex, FloatBuffer vertexColor, int vertexCount){
        this.vertex = vertex;
        this.vertexColor = vertexColor;
        this.vertexCount = vertexCount;
    }

}
