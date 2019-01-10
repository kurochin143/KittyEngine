package com.example.israel.kittyengine;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.KeyEvent;

public class KGLSurfaceView extends GLSurfaceView {

    private final KRenderer m_renderer;

    public KGLSurfaceView(Context context) {
        super(context);

        setEGLContextClientVersion(3); // 3.1

        m_renderer = new KRenderer();

        setRenderer(m_renderer);
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

}
