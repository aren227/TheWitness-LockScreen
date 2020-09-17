package com.aren.thewitnesspuzzle.graphics;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import com.aren.thewitnesspuzzle.R;
import com.aren.thewitnesspuzzle.game.Game;
import com.aren.thewitnesspuzzle.math.BoundingBox;
import com.aren.thewitnesspuzzle.math.Vector2;
import com.aren.thewitnesspuzzle.puzzle.Puzzle;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class GLRenderer implements GLSurfaceView.Renderer {

    private Game game;
    private Context context;

    private Program main, blurDown, blurDownPrelift, blurUp, blurUpInit;

    private int width, height;
    private int[] texWidth = new int[6], texHeight = new int[6];

    private int[] textureIds = new int[6];
    private int[] depthTextureIds = new int[1];

    // Main, Downscaling, Downscaling, Downscaling, Downscaling, Sub Main
    private IntBuffer frameBuffer = IntBuffer.allocate(6);

    private float ratio = 1;

    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];

    private float[] quadPos = {
            -1.0f, 1.0f, 0.0f,
            -1.0f, -1.0f, 0.0f,
            1.0f, -1.0f, 0.0f,
            1.0f, 1.0f, 0.0f
    };
    private FloatBuffer quadPosBuffer;

    private float[] quadUV = {
            0, 1,
            0, 0,
            1, 0,
            1, 1
    };
    private FloatBuffer quadUVBuffer;

    private short[] quadIndex = {0, 1, 2, 0, 2, 3}; // order to draw vertices
    private ShortBuffer quadIndexBuffer;

    private ConcurrentLinkedQueue<Puzzle> renderQueue = new ConcurrentLinkedQueue<>(); // only used in gallery
    private ConcurrentLinkedQueue<Bitmap> renderResults = new ConcurrentLinkedQueue<>();
    private boolean renderMode = false;

    private boolean bloom = false;

    public GLRenderer(Game game, Context context) {
        this.game = game;
        this.context = context;

        ByteBuffer bb1 = ByteBuffer.allocateDirect(quadPos.length * 4);
        bb1.order(ByteOrder.nativeOrder());
        quadPosBuffer = bb1.asFloatBuffer();
        quadPosBuffer.put(quadPos);
        quadPosBuffer.position(0);

        ByteBuffer bb2 = ByteBuffer.allocateDirect(quadUV.length * 4);
        bb2.order(ByteOrder.nativeOrder());
        quadUVBuffer = bb2.asFloatBuffer();
        quadUVBuffer.put(quadUV);
        quadUVBuffer.position(0);

        ByteBuffer bb3 = ByteBuffer.allocateDirect(quadIndex.length * 2);
        bb3.order(ByteOrder.nativeOrder());
        quadIndexBuffer = bb3.asShortBuffer();
        quadIndexBuffer.put(quadIndex);
        quadIndexBuffer.position(0);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(1.0f, 0.0f, 0.0f, 1.0f);

        Shader mainVertexShader = new Shader("Main", GLES20.GL_VERTEX_SHADER, context.getString(R.string.vertex));
        Shader mainFragmentShader = new Shader("Main", GLES20.GL_FRAGMENT_SHADER, context.getString(R.string.fragment));
        Shader quadVertexShader = new Shader("Quad", GLES20.GL_VERTEX_SHADER, context.getString(R.string.vertex_fb));
        Shader blurDownFragmentShader = new Shader("BlurDown", GLES20.GL_FRAGMENT_SHADER, context.getString(R.string.fragment_fb_boxblur_downscale));
        Shader blurDownPreliftFragmentShader = new Shader("BlurDown_Prelift", GLES20.GL_FRAGMENT_SHADER, context.getString(R.string.fragment_fb_boxblur_downscale_prelift));
        Shader blurUpFragmentShader = new Shader("BlurUp", GLES20.GL_FRAGMENT_SHADER, context.getString(R.string.fragment_fb_boxblur_upscale_final));
        Shader blurUpInitFragmentShader = new Shader("BlurUp_Init", GLES20.GL_FRAGMENT_SHADER, context.getString(R.string.fragment_fb_boxblur_upscale_init));

        main = new Program(mainVertexShader, mainFragmentShader);
        blurDown = new Program(quadVertexShader, blurDownFragmentShader);
        blurDownPrelift = new Program(quadVertexShader, blurDownPreliftFragmentShader);
        blurUp = new Program(quadVertexShader, blurUpFragmentShader);
        blurUpInit = new Program(quadVertexShader, blurUpInitFragmentShader);

        main.use();

        texWidth[0] = texHeight[0] = 0;
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        this.width = width;
        this.height = height;
        GLES20.glViewport(0, 0, width, height);

        ratio = (float) height / width;

        bloom = game.getSettings().getBloomEnabled();
    }

    public void setupTextures() {
        if (textureIds[0] != 0) {
            GLES20.glDeleteTextures(6, textureIds, 0);
            GLES20.glDeleteFramebuffers(6, frameBuffer);

            GLES20.glDeleteTextures(1, depthTextureIds, 0);
        }

        if (bloom) {
            GLES20.glGenTextures(6, textureIds, 0);
            GLES20.glGenFramebuffers(6, frameBuffer);

            GLES20.glGenTextures(1, depthTextureIds, 0);

            int w = width;
            int h = height;
            for (int i = 0; i < 5; i++) {
                texWidth[i] = w;
                texHeight[i] = h;
                w /= 2;
                h /= 2;
            }
            texWidth[5] = texWidth[0];
            texHeight[5] = texHeight[0];

            for (int i = 0; i < 6; i++) {
                GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBuffer.get(i));

                GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + i);
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureIds[i]);
                GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
                GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
                GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
                GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
                GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, texWidth[i], texHeight[i], 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
                GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, textureIds[i], 0);
            }

            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBuffer.get(0));

            GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + 6);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, depthTextureIds[0]);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_DEPTH_COMPONENT16, texWidth[0], texHeight[0], 0, GLES20.GL_DEPTH_COMPONENT, GLES20.GL_UNSIGNED_SHORT, null);
            GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT, GLES20.GL_TEXTURE_2D, depthTextureIds[0], 0);

        }
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        //Log.i("GL", game.getPuzzle() + " Rendered");

        if (!renderMode && game.getPuzzle() == null || renderMode && renderQueue.isEmpty()) {
            GLES20.glClearColor(1, 0, 1, 1.0f);
            GLES20.glClearDepthf(1.0f);
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
            return;
        }

        if (texWidth[0] != width || texHeight[0] != height) {
            setupTextures();
        }

        Puzzle puzzle = game.getPuzzle();
        if (renderMode) {
            puzzle = renderQueue.poll();
        }

        if (puzzle.shouldUpdateAnimation() || renderMode) {
            game.getSurfaceView().setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        } else {
            game.getSurfaceView().setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        }

        int COORD_PER_VERTEX = 3;
        int VERTEX_STRIDE = 3 * 4;

        synchronized (puzzle) {
            puzzle.prepareForDrawing();

            puzzle.updateAnimation();
            puzzle.updateDynamicShapes();

            // TODO: Rename variables to orthographic
            BoundingBox frustumBB = getFrustumBoundingBox(puzzle);

            Matrix.orthoM(mProjectionMatrix, 0, -frustumBB.getWidth() / 2, frustumBB.getWidth() / 2, -frustumBB.getHeight() / 2, frustumBB.getHeight() / 2, 0.1f, 100f);

            GLES20.glViewport(0, 0, width, height);

            // Draw the puzzle to the first frame buffer
            main.use();

            GLES20.glEnable(GLES20.GL_DEPTH_TEST);
            GLES20.glDepthFunc(GLES20.GL_LEQUAL);

            if (bloom) GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBuffer.get(0));
            else GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);

            int backgroundColor = puzzle.getColorPalette().getBackgroundColor();
            float fi = puzzle.getFadeIntensity().get();

            GLES20.glClearColor(Color.red(backgroundColor) / 255f * fi, Color.green(backgroundColor) / 255f * fi, Color.blue(backgroundColor) / 255f * fi, 1.0f);
            GLES20.glClearDepthf(1.0f);
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

            //카메라 좌표
            Matrix.setLookAtM(mViewMatrix, 0, frustumBB.getCenter().x, frustumBB.getCenter().y, 1, frustumBB.getCenter().x, frustumBB.getCenter().y, 0f, 0f, 1.0f, 0.0f);
            Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);

            main.setVertexAttrib("aPosition", 3, puzzle.getVertexBuffer());
            main.setVertexAttrib("aColor", 3, puzzle.getVertexColorBuffer());
            main.setUniformMatrix4fv("MVP", mMVPMatrix);

            GLES20.glDrawElements(GLES20.GL_TRIANGLES, puzzle.getIndexCount(), GLES20.GL_UNSIGNED_SHORT, puzzle.getIndexBuffer());
        }

        GLES20.glDisable(GLES20.GL_DEPTH_TEST);

        if (bloom) {
            for (int i = 1; i < 5; i++) {
                if (i == 1) {
                    blurDownPrelift.use();
                    GLES20.glViewport(0, 0, texWidth[i], texHeight[i]);
                    GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBuffer.get(i));

                    blurDownPrelift.setVertexAttrib("aPosition", 3, quadPosBuffer);
                    blurDownPrelift.setVertexAttrib("aTextureCoord", 2, quadUVBuffer);
                    blurDownPrelift.setUniform1i("tex", i - 1);
                    blurDownPrelift.setUniform2f("texel", 1f / texWidth[i], 1f / texHeight[i]);
                } else {
                    blurDown.use();
                    GLES20.glViewport(0, 0, texWidth[i], texHeight[i]);
                    GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBuffer.get(i));

                    blurDown.setVertexAttrib("aPosition", 3, quadPosBuffer);
                    blurDown.setVertexAttrib("aTextureCoord", 2, quadUVBuffer);
                    blurDown.setUniform1i("tex", i - 1);
                    blurDown.setUniform2f("texel", 1f / texWidth[i], 1f / texHeight[i]);
                }
                GLES20.glDrawElements(GLES20.GL_TRIANGLES, quadIndex.length, GLES20.GL_UNSIGNED_SHORT, quadIndexBuffer);
            }

            int lastBufferIdx = 5;
            for (int i = 5; i < 9; i++) {
                if (i == 5) {
                    blurUpInit.use();
                    GLES20.glViewport(0, 0, texWidth[lastBufferIdx], texHeight[lastBufferIdx]);
                    GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBuffer.get(lastBufferIdx));

                    blurUpInit.setVertexAttrib("aPosition", 3, quadPosBuffer);
                    blurUpInit.setVertexAttrib("aTextureCoord", 2, quadUVBuffer);
                    blurUpInit.setUniform1i("tex", i - 4);
                    blurUpInit.setUniform1i("source", 0);
                    blurUpInit.setUniform1f("amount", puzzle.getColorPalette().getBloomIntensity() * 0.25f);
                    blurUpInit.setUniform1f("division", 1.0f);

                    GLES20.glDrawElements(GLES20.GL_TRIANGLES, quadIndex.length, GLES20.GL_UNSIGNED_SHORT, quadIndexBuffer);
                } else {
                    blurUp.use();
                    if (i == 8) {
                        GLES20.glViewport(0, 0, width, height);
                        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
                    } else {
                        GLES20.glViewport(0, 0, texWidth[lastBufferIdx], texHeight[lastBufferIdx]);
                        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBuffer.get(lastBufferIdx));
                    }

                    blurUp.setVertexAttrib("aPosition", 3, quadPosBuffer);
                    blurUp.setVertexAttrib("aTextureCoord", 2, quadUVBuffer);
                    blurUp.setUniform1i("tex", i - 4);
                    blurUp.setUniform1i("source", lastBufferIdx == 0 ? 5 : 0);
                    blurUp.setUniform1f("amount", puzzle.getColorPalette().getBloomIntensity() * 0.25f);

                    GLES20.glDrawElements(GLES20.GL_TRIANGLES, quadIndex.length, GLES20.GL_UNSIGNED_SHORT, quadIndexBuffer);
                }

                lastBufferIdx = lastBufferIdx == 0 ? 5 : 0;
            }
        }

        if (renderMode) {
            saveToBitmap(puzzle);
        }

        synchronized (this) {
            this.notifyAll();
        }
    }

    public float getFrustumWidth(Puzzle puzzle) {
        BoundingBox boundingBox = puzzle.getBoundingBox();

        float bbWidth = boundingBox.getWidth() + puzzle.getPadding() * 2;
        float bbHeight = boundingBox.getHeight() + puzzle.getPadding() * 2;

        if (bbWidth * ratio > bbHeight) return bbWidth;
        return bbHeight / ratio;
    }

    public BoundingBox getFrustumBoundingBox(Puzzle puzzle) {
        BoundingBox boundingBox = new BoundingBox();
        boundingBox.min = new Vector2(puzzle.getBoundingBox().getCenter().x - getFrustumWidth(puzzle) * 0.5f, puzzle.getBoundingBox().getCenter().y - getFrustumWidth(puzzle) * ratio * 0.5f);
        boundingBox.max = new Vector2(puzzle.getBoundingBox().getCenter().x + getFrustumWidth(puzzle) * 0.5f, puzzle.getBoundingBox().getCenter().y + getFrustumWidth(puzzle) * ratio * 0.5f);
        return boundingBox;
    }

    // https://stackoverflow.com/questions/5514149/capture-screen-of-glsurfaceview-to-bitmap
    public void saveToBitmap(Puzzle puzzle) {
        int[] bitmapBuffer = new int[width * height];
        int[] bitmapSource = new int[width * height];
        IntBuffer intBuffer = IntBuffer.wrap(bitmapBuffer);
        intBuffer.position(0);

        GLES20.glReadPixels(0, 0, width, height, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, intBuffer);

        int offset1, offset2;
        for (int i = 0; i < height; i++) {
            offset1 = i * width;
            offset2 = (height - i - 1) * width;
            for (int j = 0; j < width; j++) {
                int texturePixel = bitmapBuffer[offset1 + j];
                int blue = (texturePixel >> 16) & 0xff;
                int red = (texturePixel << 16) & 0x00ff0000;
                int pixel = (texturePixel & 0xff00ff00) | red | blue;
                bitmapSource[offset2 + j] = pixel;
            }
        }

        renderResults.add(Bitmap.createBitmap(bitmapSource, width, height, Bitmap.Config.ARGB_8888));
    }

    public void addRenderQueue(Puzzle puzzle) {
        renderQueue.add(puzzle);
    }

    public ConcurrentLinkedQueue<Bitmap> getRenderedResults() {
        return renderResults;
    }

    public void setGalleryRenderMode() {
        renderMode = true;
        game.getSurfaceView().setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }
}
