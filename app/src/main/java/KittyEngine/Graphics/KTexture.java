package KittyEngine.Graphics;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES31;
import android.util.Log;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import KittyEngine.Container.KArrayList;
import KittyEngine.Math.KVec2;
import KittyEngine.Utilities.KFile;

// @TODO implement dynamic texture loading
// unfortunately, texture can only be created on the GL thread
// load texture asynchronously. this will take long to implement because of texture array resizing
/**
 * As of now, texture can only be loaded before the engine starts
 * All valid image files that are located in assets/textures and sub dirs are loaded on engine start
 * Use KTexture.getTexture() to use the texture
 * */
public class KTexture {
    private KTexture(String name, int width, int height, int textureArrayIndex, int layer) {
        // @TODO lock this if we're gonna implement dynamic texture loading
        m_name = String.copyValueOf(name.toCharArray());
        m_width = width;
        m_height = height;
        m_textureArrayIndex = textureArrayIndex;
        m_layer = layer;
        m_UV = new KVec2((float)width / (float)sm_textureArrayMaxDimensions[textureArrayIndex], (float)height / (float)sm_textureArrayMaxDimensions[textureArrayIndex]);
    }

    private static final int[] sm_textureArrayMaxDimensions = {32, 64, 128, 256, 512, 1024, 2048};
    public static final int TEXTURE_ARRAY_NUM = sm_textureArrayMaxDimensions.length;
    public static final int TEXTURE_MAX_DIMENSION = 2048;
    private static int[] sm_textureIds;
    private static boolean sm_bLoaded;
    private static KArrayList<KTexture> sm_textures = new KArrayList<>();

    private String m_name;
    private int m_width;
    private int m_height;
    private int m_textureArrayIndex;
    private int m_layer;
    private KVec2 m_UV;

    /**
     * Get texture relative to assets/
     * example: "textures/chair.png" or "textures/shapes/circle.png"
     * */
    public static KTexture getTexture(String path) {
        for (int i = 0; i < sm_textures.size(); ++i) {
            if (sm_textures.get(i).m_name.equals(path)) {
                return sm_textures.get(i);
            }
        }

        return null;
    }

    public boolean isValid() {
        return m_layer != -1;
    }

    public String getName() {
        return m_name;
    }

    public int getWidth() {
        return m_width;
    }

    public int getHeight() {
        return m_height;
    }

    public int getTextureArrayIndex() {
        return m_textureArrayIndex;
    }
    public int getLayer() {
        return m_layer;
    }

    /** texture's UV in the texture array */
    public KVec2 getUV() {
        return new KVec2(m_UV);
    }

//    /**
//     * do not reuse this KTexture2D once this is called,
//     * this is for clean up purpose only, if you want to unload the texture,
//     * this will not delete the KTexture2D's memory
//     * */
//    public void destroy() {
//        // @TODO unbind
//        sm_layerOccupations[m_layer] = false;
//        m_layer = -1;
//    }

    static void loadKittyTextures(Context context) {
        if (sm_bLoaded) { // only load once
            return;
        }
        sm_bLoaded = true;

        // generate gl textures
        sm_textureIds = new int[TEXTURE_ARRAY_NUM];
        GLES31.glGenTextures(TEXTURE_ARRAY_NUM, sm_textureIds, 0);

        class NamedBitmap {
            String name;
            Bitmap bitmap;
        }
        KArrayList<KArrayList<NamedBitmap>> namedBitmapsByTextureArraySize = new KArrayList<>();
        for (int i = 0; i < TEXTURE_ARRAY_NUM; ++i) {
            namedBitmapsByTextureArraySize.add(new KArrayList<NamedBitmap>());
        }

        KArrayList<String> files = new KArrayList<>();
        KFile.getFilesInAssetDir(context, "textures", files); // get files in assets/textures

        if (files.size() == 0) {
            return;
        }

        for (int i = 0; i < files.size(); ++i) {
            try {
                String texturePath = files.get(i);
                InputStream is = context.getAssets().open(texturePath);

                Bitmap bitmap = BitmapFactory.decodeStream(is);
                if (bitmap == null) {
                    Log.w("KittyLog", String.format("Failed to load texture. %s is not a valid texture file", texturePath));
                    continue;
                }

                int width = bitmap.getWidth();
                int height = bitmap.getHeight();

                if (width > TEXTURE_MAX_DIMENSION || height > TEXTURE_MAX_DIMENSION) {
                    Log.w("KittyLog", String.format("Failed to load texture. Dimension exceeded. Max dimension is %dx%d. Width %d. Height %d. Of file %s", TEXTURE_MAX_DIMENSION, TEXTURE_MAX_DIMENSION, width, height, texturePath));
                    continue;
                }

                int max = Math.max(width, height);
                int normalizedStorageIndex = -1; // normalized dimension
                for (int j = 0; j < sm_textureArrayMaxDimensions.length; ++j) {
                    if (max <= sm_textureArrayMaxDimensions[j]) {
                        normalizedStorageIndex = j;
                        break;
                    }
                }

                NamedBitmap namedBitmap = new NamedBitmap();
                namedBitmap.name = texturePath;
                namedBitmap.bitmap = bitmap;

                namedBitmapsByTextureArraySize.get(normalizedStorageIndex).add(namedBitmap);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        // texStorage3D
        for (int textureArrayIndex = 0; textureArrayIndex < TEXTURE_ARRAY_NUM; ++textureArrayIndex) {
            KArrayList<NamedBitmap> namedBitmaps = namedBitmapsByTextureArraySize.get(textureArrayIndex);

            GLES31.glBindTexture(GLES31.GL_TEXTURE_2D_ARRAY, sm_textureIds[textureArrayIndex]);
            // create tex storage with a fixed size of namedBitmaps.size()
            int textureArrayDimension = sm_textureArrayMaxDimensions[textureArrayIndex];
            GLES31.glTexStorage3D(GLES31.GL_TEXTURE_2D_ARRAY, 1, GLES31.GL_RGBA8, textureArrayDimension, textureArrayDimension, namedBitmaps.size());
            for (int layer = 0; layer < namedBitmaps.size(); ++layer) {
                NamedBitmap namedBitmap = namedBitmaps.get(layer);
                int width = namedBitmap.bitmap.getWidth();
                int height = namedBitmap.bitmap.getHeight();

                // @TODO check if this really needs to be direct
                ByteBuffer byteBuffer = ByteBuffer.allocateDirect(namedBitmap.bitmap.getByteCount());
                byteBuffer.order(ByteOrder.nativeOrder());
                namedBitmap.bitmap.copyPixelsToBuffer(byteBuffer);
                byteBuffer.position(0);

                // @TODO if image is not 32bit then GL_RGBA8 might fail. see this

                GLES31.glTexSubImage3D(
                        GLES31.GL_TEXTURE_2D_ARRAY,
                        0,0,0, layer,
                        width, height, 1, GLES31.GL_RGBA, GLES31.GL_UNSIGNED_BYTE, byteBuffer);

                KTexture newTexture = new KTexture(namedBitmap.name, width, height, textureArrayIndex, layer);
                sm_textures.add(newTexture);

            }
            GLES31.glBindTexture(GLES31.GL_TEXTURE_2D_ARRAY, 0);

        }
    }

    static void bind() {
        // bind all texture arrays
        for (int i = 0; i < sm_textureIds.length; ++i) {
            GLES31.glActiveTexture(GLES31.GL_TEXTURE0 + i);
            GLES31.glBindTexture(GLES31.GL_TEXTURE_2D_ARRAY, sm_textureIds[i]);
        }

    }

    static void unbind() {
        GLES31.glBindTexture(GLES31.GL_TEXTURE_2D_ARRAY, 0);
    }

}
