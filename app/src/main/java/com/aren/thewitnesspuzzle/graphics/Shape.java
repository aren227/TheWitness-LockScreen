package com.aren.thewitnesspuzzle.graphics;

import java.nio.FloatBuffer;

public interface Shape {

    void draw(FloatBuffer buffer);

    void drawColor(FloatBuffer buffer);

    int getVertexCount();

}
