package com.aren.thewitnesspuzzle.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

import com.aren.thewitnesspuzzle.core.math.BoundingBox;
import com.aren.thewitnesspuzzle.core.math.MathUtils;
import com.aren.thewitnesspuzzle.game.Game;
import com.aren.thewitnesspuzzle.graphics.GLRenderer;

public class PuzzleGLSurfaceView extends GLSurfaceView {

    private Game game;

    public GLRenderer glRenderer;

    public Bitmap bitmap;
    public boolean bitmapRendered = false;

    public PuzzleGLSurfaceView(Game game, Context context) {
        super(context);

        this.game = game;

        setEGLContextClientVersion(2);

        glRenderer = new GLRenderer(game, context);
        setRenderer(glRenderer);

        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    //터치 좌표계는 왼쪽 상단이 (0, 0)
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        // MotionEvent reports input details from the touch screen
        // and other input controls. In this case, you are only
        // interested in events where the touch position changed.
        if (e.getAction() == MotionEvent.ACTION_DOWN || e.getAction() == MotionEvent.ACTION_MOVE || e.getAction() == MotionEvent.ACTION_UP) {
            float x = e.getX();
            float y = e.getY();

            float ratio = (float) getHeight() / getWidth();

            BoundingBox frustumBB = glRenderer.getFrustumBoundingBox(game.getPuzzle());

            x = MathUtils.lerp(frustumBB.min.x, frustumBB.max.x, x / getWidth());
            y = getHeight() - y; // Bottom-right should be (0, 0)
            y = MathUtils.lerp(frustumBB.min.y, frustumBB.max.y, y / getHeight());

            final float finalX = x;
            final float finalY = y;
            final int finalAction = e.getAction();
            queueEvent(new Runnable() {
                @Override
                public void run() {
                    game.touchEvent(finalX, finalY, finalAction);
                }
            });
        }

        return true;
    }

    public void capture() {
        // Why It needs to draw several times to get a rendered result?
        // First one or two results are always black. wtf?
        // idk but i think it's buffer related issue.
        for (int i = 0; i < 1; i++) {
            synchronized (glRenderer) {
                while (true) {
                    requestRender();
                    try {
                        glRenderer.wait();
                        //if(glRenderer.lastDrawn == puzzle) break;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        queueEvent(new Runnable() {
            @Override
            public void run() {
                //bitmap = glRenderer.captureToBitmap(0, 0, getWidth(), getHeight());

                synchronized (PuzzleGLSurfaceView.this) {
                    PuzzleGLSurfaceView.this.notifyAll();
                }
            }
        });
    }

}
