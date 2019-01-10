package com.example.israel.kittyengine;

import android.opengl.GLES31;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class KRenderer implements GLSurfaceView.Renderer {

    private KHUDRenderer m_HUDRenderer;
    private static float[] m_screenProjection = new float[16];
    private static float[] m_worldCameraProjection = new float[16];

    public static float[] getScreenProjection() {
        return m_screenProjection;
    }

    public static float[] worldCameraProjection() {
        return m_worldCameraProjection;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        m_HUDRenderer = new KHUDRenderer();

        // Set the background frame color
        GLES31.glClearColor(1.0f, 0.0f, 0.0f, 1.0f);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        //Redraw background color
        GLES31.glClear(GLES31.GL_COLOR_BUFFER_BIT);

        m_HUDRenderer.render();

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES31.glViewport(0, 0, width, height);

        m_screenProjection[0] = 2.f / width;
        m_screenProjection[4 + 1] = 2.f / height;
        m_screenProjection[8 + 2] = -1.f;
        m_screenProjection[12 + 3] = 1.f;
    }
}
