package KittyEngine.Graphics;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.support.annotation.MainThread;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;

import KittyEngine.Engine.KInput;

// @TODO rewrite all graphics related stuff in C++ when I've practiced Java long enough

public class KGLSurfaceView extends GLSurfaceView {

    public KGLSurfaceView(Context context, KInput input) {
        super(context);

        m_input = input;

        setEGLContextClientVersion(3); // 3.1

        m_renderer = new KRenderer(context);

        setRenderer(m_renderer);

        // only render when requestRender is called
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    private KInput m_input;
    private KRenderer m_renderer;

    public KRenderer getRenderer() {
        return m_renderer;
    }

    @Override
    public void requestRender() {
        if (!m_renderer.m_bDrawFrameFinished.get()) {
            return;
        }

        m_renderer.m_bDrawFrameFinished.set(false);
        super.requestRender();
    }

    public boolean isRenderFinished() {
        return m_renderer.m_bDrawFrameFinished.get();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent keyEvent) {
        queueEvent(new Runnable() {
            @Override
            public void run() {

            }
        });
        return super.onKeyDown(keyCode, keyEvent);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        super.onTouchEvent(e);

        m_input.recieveMotionEvent(e);

        return true;
    }

}
