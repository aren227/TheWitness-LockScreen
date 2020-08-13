package com.aren.thewitnesspuzzle.graphics;

import android.content.Context;
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

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
        if(game.getPuzzle().shouldUpdateAnimation()){
            game.getSurfaceView().setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        }
        else{
            game.getSurfaceView().setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        }
        game.getPuzzle().prepareForDrawing();

        game.getPuzzle().updateAnimation();
        game.getPuzzle().updateDynamicShapes();

        BoundingBox frustumBB = getFrustumBoundingBox();

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
        GLES20.glVertexAttribPointer(aPositionHandle, COORD_PER_VERTEX, GLES20.GL_FLOAT, false, VERTEX_STRIDE, game.getPuzzle().getVertexBuffer());

        int aColorHandle = GLES20.glGetAttribLocation(glProgram, "aColor");

        GLES20.glEnableVertexAttribArray(aColorHandle);

        GLES20.glVertexAttribPointer(aColorHandle, COORD_PER_VERTEX, GLES20.GL_FLOAT, false, VERTEX_STRIDE, game.getPuzzle().getVertexColorBuffer());

        int MVPMatrixHandle = GLES20.glGetUniformLocation(glProgram, "MVP");

        GLES20.glUniformMatrix4fv(MVPMatrixHandle, 1, false, mMVPMatrix, 0);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, game.getPuzzle().getVertexCount());

        //GLES20.glDisableVertexAttribArray(vPositionHandle);
    }

    public static int loadShader(int type, String shaderCode){
        int shader = GLES20.glCreateShader(type);

        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }

    public float getFrustumWidth(){
        BoundingBox boundingBox = game.getPuzzle().getBoundingBox();

        float bbWidth = boundingBox.getWidth() + game.getPuzzle().getPadding() * 2;
        float bbHeight = boundingBox.getHeight() + game.getPuzzle().getPadding() * 2;

        if(bbWidth * ratio > bbHeight) return bbWidth;
        return bbHeight / ratio;
    }

    public BoundingBox getFrustumBoundingBox(){
        BoundingBox boundingBox = new BoundingBox();
        boundingBox.min = new Vector2(game.getPuzzle().getBoundingBox().getCenter().x - getFrustumWidth() * 0.5f, game.getPuzzle().getBoundingBox().getCenter().y - getFrustumWidth() * ratio * 0.5f);
        boundingBox.max = new Vector2(game.getPuzzle().getBoundingBox().getCenter().x + getFrustumWidth() * 0.5f, game.getPuzzle().getBoundingBox().getCenter().y + getFrustumWidth() * ratio * 0.5f);
        return boundingBox;
    }
}
