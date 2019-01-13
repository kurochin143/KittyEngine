package KittyEngine.Graphics;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES31;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import KittyEngine.Engine.KEngine;
import KittyEngine.Math.KVec2;

public class KTexture {

    public KTexture(int resourceId) {
        // @TODO lock this? because of sm_currentTextureLayerId
        findAndOccupyLayer();

        if (m_layer == -1) {
            Log.w("KittyLog", "Failed to create KTexture. Texture array full");
            return;
        }

        if (sm_textureIds == null) {
            Log.w("KittyLog", "Failed to create KTexture. Texture array is not created yet");
            return;
        }

        InputStream is = KEngine.get().getView().getResources().openRawResource(resourceId);
        Bitmap bitmap = BitmapFactory.decodeStream(is);
        if (bitmap == null) {
            Log.w("KittyLog", String.format("Failed to create KTexture. Failed to load texture file"));
            return;
        }

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        if (width > TEXTURE_ARRAY_WIDTH || height > TEXTURE_ARRAY_HEIGHT) {
            Log.w("KittyLog", String.format("Failed to create KTexture. Dimension exceeded. Max width %d. Max height %d", TEXTURE_ARRAY_WIDTH, TEXTURE_ARRAY_HEIGHT));
            return;
        }

        // @TODO check if this really needs to be direct
        int bc = bitmap.getByteCount();
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(bc);
        byteBuffer.order(ByteOrder.nativeOrder());
        bitmap.copyPixelsToBuffer(byteBuffer);
        byteBuffer.position(0);

        // @TODO just use single texture

        // @TODO if image is not 32bit then GL_RGBA8 might fail. see this

        GLES31.glBindTexture(GLES31.GL_TEXTURE_2D_ARRAY, sm_textureIds[0]);
        GLES31.glTexStorage3D(GLES31.GL_TEXTURE_2D_ARRAY, 1, GLES31.GL_RGBA8, TEXTURE_ARRAY_WIDTH, TEXTURE_ARRAY_HEIGHT, TEXTURE_ARRAY_LAYERS_NUM);

        GLES31.glTexSubImage3D(
                GLES31.GL_TEXTURE_2D_ARRAY,
                0,0,0, m_layer,
                width, height, 1, GLES31.GL_RGBA, GLES31.GL_UNSIGNED_BYTE, byteBuffer);
        GLES31.glBindTexture(GLES31.GL_TEXTURE_2D_ARRAY, 0);

        m_UV = new KVec2((float)width / (float)TEXTURE_ARRAY_WIDTH, (float)height / (float)TEXTURE_ARRAY_HEIGHT);
    }

    public static final int TEXTURE_ARRAY_LAYERS_NUM = 100;
    public static final int TEXTURE_ARRAY_WIDTH = 2048;
    public static final int TEXTURE_ARRAY_HEIGHT = 2048;
    private static boolean[] sm_layerOccupations = new boolean[TEXTURE_ARRAY_LAYERS_NUM];
    private static int[] sm_textureIds;

    private String m_filePath = null;
    private int m_layer;
    private KVec2 m_UV;

    private void findAndOccupyLayer() {
        for (int i = 0; i < sm_layerOccupations.length; ++i) {
            if (!sm_layerOccupations[i]) {
                m_layer = i;
                sm_layerOccupations[i] = true;
                return;
            }
        }

        m_layer = -1;
    }

    public boolean isValid() {
        return m_layer != -1;
    }

    public String getFilePath() {
        return m_filePath;
    }

    public int getLayer() {
        return m_layer;
    }

    public KVec2 getUV() {
        return m_UV;
    }

    /**
     * do not reuse this KTexture2D once this is called,
     * this is for clean up purpose only, if you want to unload the texture,
     * this will not delete the KTexture2D's memory
     * */
    public void destroy() {
        // @TODO unbind
        sm_layerOccupations[m_layer] = false;
        m_layer = -1;
    }

    static void createTextureArray() {
        if (sm_textureIds == null) {
            sm_textureIds = new int[1];
            GLES31.glGenTextures(1, sm_textureIds, 0);
        }
    }

    static void destroyTextureArray() {
        GLES31.glDeleteTextures(1, sm_textureIds, 0);
        sm_textureIds = null;
    }

    static void bind() {
        GLES31.glBindTexture(GLES31.GL_TEXTURE_2D_ARRAY, sm_textureIds[0]);
    }

    static void unbind() {
        GLES31.glBindTexture(GLES31.GL_TEXTURE_2D_ARRAY, 0);
    }

}
