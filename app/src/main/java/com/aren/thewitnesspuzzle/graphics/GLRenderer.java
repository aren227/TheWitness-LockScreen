package com.aren.thewitnesspuzzle.graphics;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import com.aren.thewitnesspuzzle.PuzzleGLSurfaceView;
import com.aren.thewitnesspuzzle.R;
import com.aren.thewitnesspuzzle.math.BoundingBox;
import com.aren.thewitnesspuzzle.math.Vector2;
import com.aren.thewitnesspuzzle.math.Vector3;
import com.aren.thewitnesspuzzle.puzzle.Game;
import com.aren.thewitnesspuzzle.puzzle.Puzzle;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class GLRenderer implements GLSurfaceView.Renderer {

    private Game game;
    private Context context;

    private int vertexShader, fragmentShader;
    private int glProgram;

    private float ratio = 1;

    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];

    public GLRenderer(Game game, Context context){
        this.game = game;
        this.context = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(1.0f, 0.0f, 0.0f, 1.0f);

        vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, context.getString(R.string.vertex));

        fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, context.getString(R.string.fragment));

        glProgram = GLES20.glCreateProgram();

        GLES20.glAttachShader(glProgram, vertexShader);

        GLES20.glAttachShader(glProgram, fragmentShader);

        GLES20.glLinkProgram(glProgram);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);

        ratio = (float) height / width;
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        Log.i("GL", game.getPuzzle() + " Rendered");

        if(game.getPuzzle() == null) {
            GLES20.glClearColor(1, 0, 1, 1.0f);
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
            return;
        }

        Puzzle puzzle = game.getPuzzle();

        if(puzzle.shouldUpdateAnimation()){
            game.getSurfaceView().setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        }
        else{
            game.getSurfaceView().setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        }
        puzzle.prepareForDrawing();

        puzzle.updateAnimation();
        puzzle.updateDynamicShapes();

        BoundingBox frustumBB = getFrustumBoundingBox(puzzle);

        Matrix.frustumM(mProjectionMatrix, 0, -frustumBB.getWidth() / 2, frustumBB.getWidth() / 2, -frustumBB.getHeight() / 2, frustumBB.getHeight() / 2, 1, 100);

        int backgroundColor = game.getBackgroundColor();
        GLES20.glClearColor(Color.red(backgroundColor) / 255f, Color.green(backgroundColor) / 255f, Color.blue(backgroundColor) / 255f, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        //카메라 좌표
        Matrix.setLookAtM(mViewMatrix, 0, frustumBB.getCenter().x, frustumBB.getCenter().y, 1, frustumBB.getCenter().x, frustumBB.getCenter().y, 0f, 0f, 1.0f, 0.0f);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);

        //GL START
        GLES20.glUseProgram(glProgram);

        int aPositionHandle = GLES20.glGetAttribLocation(glProgram, "aPosition");

        GLES20.glEnableVertexAttribArray(aPositionHandle);

        int COORD_PER_VERTEX = 3;
        int VERTEX_STRIDE = 3 * 4;
        GLES20.glVertexAttribPointer(aPositionHandle, COORD_PER_VERTEX, GLES20.GL_FLOAT, false, VERTEX_STRIDE, puzzle.getVertexBuffer());

        int aColorHandle = GLES20.glGetAttribLocation(glProgram, "aColor");

        GLES20.glEnableVertexAttribArray(aColorHandle);

        GLES20.glVertexAttribPointer(aColorHandle, COORD_PER_VERTEX, GLES20.GL_FLOAT, false, VERTEX_STRIDE, puzzle.getVertexColorBuffer());

        int MVPMatrixHandle = GLES20.glGetUniformLocation(glProgram, "MVP");

        GLES20.glUniformMatrix4fv(MVPMatrixHandle, 1, false, mMVPMatrix, 0);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, puzzle.getVertexCount());

        //GLES20.glDisableVertexAttribArray(vPositionHandle);
        synchronized(this){
            this.notifyAll();
        }
    }

    public static int loadShader(int type, String shaderCode){
        int shader = GLES20.glCreateShader(type);

        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }

    public float getFrustumWidth(Puzzle puzzle){
        BoundingBox boundingBox = puzzle.getBoundingBox();

        float bbWidth = boundingBox.getWidth() + puzzle.getPadding() * 2;
        float bbHeight = boundingBox.getHeight() + puzzle.getPadding() * 2;

        if(bbWidth * ratio > bbHeight) return bbWidth;
        return bbHeight / ratio;
    }

    public BoundingBox getFrustumBoundingBox(Puzzle puzzle){
        BoundingBox boundingBox = new BoundingBox();
        boundingBox.min = new Vector2(puzzle.getBoundingBox().getCenter().x - getFrustumWidth(puzzle) * 0.5f, puzzle.getBoundingBox().getCenter().y - getFrustumWidth(puzzle) * ratio * 0.5f);
        boundingBox.max = new Vector2(puzzle.getBoundingBox().getCenter().x + getFrustumWidth(puzzle) * 0.5f, puzzle.getBoundingBox().getCenter().y + getFrustumWidth(puzzle) * ratio * 0.5f);
        return boundingBox;
    }

    // https://stackoverflow.com/questions/5514149/capture-screen-of-glsurfaceview-to-bitmap
    public Bitmap captureToBitmap(int x, int y, int w, int h){
        int bitmapBuffer[] = new int[w * h];
        int bitmapSource[] = new int[w * h];
        IntBuffer intBuffer = IntBuffer.wrap(bitmapBuffer);
        intBuffer.position(0);

        GLES20.glReadPixels(x, y, w, h, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, intBuffer);

        int offset1, offset2;
        for (int i = 0; i < h; i++) {
            offset1 = i * w;
            offset2 = (h - i - 1) * w;
            for (int j = 0; j < w; j++) {
                int texturePixel = bitmapBuffer[offset1 + j];
                int blue = (texturePixel >> 16) & 0xff;
                int red = (texturePixel << 16) & 0x00ff0000;
                int pixel = (texturePixel & 0xff00ff00) | red | blue;
                bitmapSource[offset2 + j] = pixel;
            }
        }

        return Bitmap.createBitmap(bitmapSource, w, h, Bitmap.Config.ARGB_8888);
    }
}
