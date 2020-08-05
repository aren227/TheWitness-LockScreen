package com.aren.thewitnesspuzzle.graphics;

import android.content.Context;
import android.graphics.Color;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import com.aren.thewitnesspuzzle.PuzzleGLSurfaceView;
import com.aren.thewitnesspuzzle.R;
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

    Game game;
    Context context;

    int vertexShader, fragmentShader;
    int glProgram;

    float ratio = 1;

    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];

    public GLRenderer(Game game, Context context){
        this.game = game;
        this.context = context;

        //shapes.add(new Triangle(new Vector3(0, 0, 0), new Vector3(0, 1, 0), new Vector3(1, 0, 0)));
        //shapes.add(new Circle(new Vector3(0, 0, 0), 1));
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(1.0f, 0.0f, 0.0f, 1.0f);

        //vertex shader 타입의 객체를 생성하여 vertexShaderCode에 저장된 소스코드를 로드한 후,
        //   컴파일합니다.
        vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, context.getString(R.string.vertex));

        //fragment shader 타입의 객체를 생성하여 fragmentShaderCode에 저장된 소스코드를 로드한 후,
        //  컴파일합니다.
        fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, context.getString(R.string.fragment));

        // Program 객체를 생성한다.
        glProgram = GLES20.glCreateProgram();

        // vertex shader를 program 객체에 추가
        GLES20.glAttachShader(glProgram, vertexShader);

        // fragment shader를 program 객체에 추가
        GLES20.glAttachShader(glProgram, fragmentShader);

        // program객체를 OpenGL에 연결한다. program에 추가된 shader들이 OpenGL에 연결된다.
        GLES20.glLinkProgram(glProgram);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);

        ratio = (float) height / width;
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        float center = game.getSceneWidth() / 2;
        float padding = game.getPaddingWidth();

        Matrix.frustumM(mProjectionMatrix, 0, -padding, center * 2 + padding, center + -ratio * center - ratio * padding, center + ratio * center + ratio * padding, 1, 100);

        int backgroundColor = game.getBackgroundColor();
        GLES20.glClearColor(Color.red(backgroundColor) / 255f, Color.green(backgroundColor) / 255f, Color.blue(backgroundColor) / 255f, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        //카메라 좌표
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, 1, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
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
}
