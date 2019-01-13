package KittyEngine.Graphics;

import android.opengl.GLES31;
import android.opengl.GLSurfaceView;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class KRenderer implements GLSurfaceView.Renderer {

    KRenderer() {
    }

    private static float[] m_screenProjection = new float[16];
    private static float[] m_worldCameraProjection = new float[16];
    private KSpriteRenderer m_SpriteRenderer;
    private KHUDRenderer m_HUDRenderer;

    /**
     * this has two jobs for KEngine, first is onSurfaceCreated and when onDrawFrame is finished
     * */
    AtomicBoolean m_bDrawFrameFinished = new AtomicBoolean(false);

    // @TODO java returning array not const correct?
    public static float[] getScreenProjection() {
        return m_screenProjection;
    }

    // @TODO camera class
    public static float[] worldCameraProjection() {
        return m_worldCameraProjection;
    }

    public KHUDRenderer getHUDRenderer() {
        return m_HUDRenderer;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        KTexture.createTextureArray();
        m_SpriteRenderer = new KSpriteRenderer();
        m_HUDRenderer = new KHUDRenderer();

        // Set the background frame color
        GLES31.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        m_bDrawFrameFinished.set(true);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        //Redraw background color
        GLES31.glClear(GLES31.GL_COLOR_BUFFER_BIT);

        m_SpriteRenderer.render();

        // hud is rendered last so it is always on top
        m_HUDRenderer.render();

        m_bDrawFrameFinished.set(true);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES31.glViewport(0, 0, width, height);

        m_screenProjection[0 * 4 + 0] = 2.f / width; // scale x
        m_screenProjection[1 * 4 + 1] = 2.f / -height; // scale y
        m_screenProjection[3 * 4 + 0] = -1.f; // origin x
        m_screenProjection[3 * 4 + 1] = 1.f; // origin y
        m_screenProjection[2 * 4 + 2] = -1.f; // far
        m_screenProjection[3 * 4 + 3] = 1.f; // near
    }

}
