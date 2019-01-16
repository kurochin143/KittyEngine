package KittyEngine.Graphics;

import android.content.Context;
import android.opengl.GLES31;
import android.opengl.GLSurfaceView;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import KittyEngine.Math.KMat4;
import KittyEngine.Math.KVec2;

public class KRenderer implements GLSurfaceView.Renderer {

    KRenderer(Context context) {
        m_context = context;
    }

    private Context m_context;
    private KVec2 m_screenDimension = new KVec2(); // or resolution
    private KMat4 m_screenProjection = new KMat4();
    private KCamera m_camera;
    private KSpriteRenderer m_SpriteRenderer;
    private KHUDRenderer m_HUDRenderer;

    AtomicBoolean m_bDrawFrameFinished = new AtomicBoolean(false);

    public KVec2 getScreenDimension() {
        return new KVec2(m_screenDimension);
    }

    public KMat4 getScreenProjection() {
        return new KMat4(m_screenProjection);
    }

    public KCamera getCamera() {
        return m_camera;
    }

    public KSpriteRenderer getSpriteRenderer() {
        return m_SpriteRenderer;
    }

    public KHUDRenderer getHUDRenderer() {
        return m_HUDRenderer;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        m_camera = new KCamera(this);
        KTexture.loadKittyTextures(m_context);
        m_SpriteRenderer = new KSpriteRenderer(this);
        m_HUDRenderer = new KHUDRenderer(this);

        // Set the background frame color
        GLES31.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
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

        m_screenDimension.x = width;
        m_screenDimension.y = height;

        float[] screenProjectionArr = m_screenProjection.getArray();
        screenProjectionArr[0 * 4 + 0] = 2.f / width; // scale x
        screenProjectionArr[1 * 4 + 1] = 2.f / -height; // scale y
        screenProjectionArr[3 * 4 + 0] = -1.f; // origin x
        screenProjectionArr[3 * 4 + 1] = 1.f; // origin y
        screenProjectionArr[2 * 4 + 2] = -1.f; // far
        screenProjectionArr[3 * 4 + 3] = 1.f; // near

    }

}
