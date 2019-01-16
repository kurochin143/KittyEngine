package KittyEngine.Graphics;

import android.opengl.GLES31;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.TreeMap;

import KittyEngine.Collision.CollisionStatics;
import KittyEngine.Container.KArrayList;
import KittyEngine.Math.KAABB;
import KittyEngine.Math.KMat4;
import KittyEngine.Math.KVec2;
import KittyEngine.Math.KVec4;
import glm.GLM;

class KSpriteRenderer {

    KSpriteRenderer(KRenderer renderer) {
        m_renderer = renderer;

        m_shader = new KShader(vertexShaderCode, fragmentShaderCode);
        m_shader.use();
        m_shader.setInt("texArr0", 0);
        m_shader.setInt("texArr1", 1);
        m_shader.setInt("texArr2", 2);
        m_shader.setInt("texArr3", 3);
        m_shader.setInt("texArr4", 4);
        m_shader.setInt("texArr5", 5);
        m_shader.setInt("texArr6", 6);
        KShader.unuse();

        m_vertexBuffer = new KVertexBuffer();
    }

    private static final int SPRITE_VERTEX_NUM = 6; // 2 triangles
    private static final int VERTEX_POSITION_TEXCOORD_DATA_NUM = 4; // 2 floats in a position, 2 floats in a texCoord
    private static final int VERTEX_COLOR_DATA_NUM = 4; // 4 floats in color
    private static final int VERTEX_TEXARRAY_LAYER_DATA_NUM = 2; // 1 float in texArrayIndex, 1 float in layer
    private static final int VERTEX_DATA_NUM = VERTEX_POSITION_TEXCOORD_DATA_NUM + VERTEX_COLOR_DATA_NUM + VERTEX_TEXARRAY_LAYER_DATA_NUM; // total num of floats in a vertex
    private static final int BYTES_PER_FLOAT = 4;
    private static final int BYTES_PER_VERTEX_POSITION_TEXCOORD = VERTEX_POSITION_TEXCOORD_DATA_NUM * BYTES_PER_FLOAT;
    private static final int BYTES_PER_VERTEX_COLOR = VERTEX_COLOR_DATA_NUM * BYTES_PER_FLOAT;
    private static final int BYTES_PER_VERTEX_TEXARRAY_LAYER = VERTEX_TEXARRAY_LAYER_DATA_NUM * BYTES_PER_FLOAT;
    private static final int BYTES_PER_VERTEX = VERTEX_DATA_NUM * BYTES_PER_FLOAT; // also the stride
    private static final int BYTES_PER_SPRITE = BYTES_PER_VERTEX * SPRITE_VERTEX_NUM;

    public static final String vertexShaderCode =
                            "#version 300 es\n" +
                        "layout(location = 0) in vec4 inPosition_TexCoord;\n" +
                        "layout(location = 1) in vec4 inColor;\n" +
                        "layout(location = 2) in vec2 inTexArray_Layer;\n" +
                        "out vec2 f_texCoord;\n" +
                        "out vec4 f_color;\n" +
                        "out vec2 f_texArray_Layer;\n" +
                        "void main() {\n" +
                        "   gl_Position = vec4(inPosition_TexCoord.xy, 0.f, 1.f);\n" +
                        "   f_color = inColor;\n" +
                        "   f_texArray_Layer = inTexArray_Layer;\n" +
                        "   f_texCoord = inPosition_TexCoord.zw;\n" +
                        "}";
//            "#version 300 es\n" +
//                    "layout(location = 0) in vec4 inPositionTexCoord;" +
//                    "layout(location = 1) in vec4 inColor;" +
//                    "out vec2 f_texCoord;" +
//                    "out vec4 f_color;" +
//                    "void main() {" +
//                    "   f_color = inColor;" +
//                    "   f_texCoord = inPositionTextCoord.zw;" +
//                    "   gl_Position = vec4(inPositionTexCoord.xy, 0.f, 1.f);" +
//                    "}";

    private static final int SHADER_LAYOUT_LOCATION_POSITION_TEXCOORD = 0;
    private static final int SHADER_LAYOUT_LOCATION_COLOR = 1;
    private static final int SHADER_LAYOUT_LOCATION_TEXARRAY_LAYER = 2;

    public static final String fragmentShaderCode =
            "#version 300 es\n" +
                    "precision mediump float;\n" +
                    "in vec2 f_texCoord;\n" +
                    "in vec4 f_color;\n" +
                    "in vec2 f_texArray_Layer;\n" +
                    "precision lowp sampler2DArray;\n" +
                    "uniform sampler2DArray texArr0;\n" +
                    "uniform sampler2DArray texArr1;\n" +
                    "uniform sampler2DArray texArr2;\n" +
                    "uniform sampler2DArray texArr3;\n" +
                    "uniform sampler2DArray texArr4;\n" +
                    "uniform sampler2DArray texArr5;\n" +
                    "uniform sampler2DArray texArr6;\n" +
                    "out vec4 color;\n" +
                    "void main() {\n" +
                    "switch (int(f_texArray_Layer.x))\n" +
                    "   {\n" +
                    "   case 0: color = f_color * texture(texArr0, vec3(f_texCoord, f_texArray_Layer.y)); break;\n" +
                    "   case 1: color = f_color * texture(texArr1, vec3(f_texCoord, f_texArray_Layer.y)); break;\n" +
                    "   case 2: color = f_color * texture(texArr2, vec3(f_texCoord, f_texArray_Layer.y)); break;\n" +
                    "   case 3: color = f_color * texture(texArr3, vec3(f_texCoord, f_texArray_Layer.y)); break;\n" +
                    "   case 4: color = f_color * texture(texArr4, vec3(f_texCoord, f_texArray_Layer.y)); break;\n" +
                    "   case 5: color = f_color * texture(texArr5, vec3(f_texCoord, f_texArray_Layer.y)); break;\n" +
                    "   case 6: color = f_color * texture(texArr6, vec3(f_texCoord, f_texArray_Layer.y)); break;\n" +
                    "   default: color = f_color;\n" +
                    "   }\n" +
                    "}";
//            "#version 300 es\n" +
//                    "precision mediump float;" +
//                    "in vec2 f_texCoord;" +
//                    "in vec4 f_color;" +
//                    "layout(location = 2) uniform sampler2D image;" +
//                    "out vec4 color;" +
//                    "void main() {" +
//                    "   color = f_color * texture(image, f_texCoord);" +
//                    "}";

    private static final KVec4[] sm_PositionVertexBufferData =
    {
            new KVec4(0.f, 0.f, 0.f, 1.f), // TL
            new KVec4(1.f, 1.f, 0.f, 1.f), // BR
            new KVec4(0.f, 1.f, 0.f, 1.f), // BL
            new KVec4(1.f, 0.f, 0.f, 1.f), // TR
    };

    private KRenderer m_renderer;
    private KShader m_shader;
    private KVertexBuffer m_vertexBuffer;
    // @TODO index buffer
    private FloatBuffer m_vertexData;

    /**
     * sprite container mapped by layer index
     * used for sorted rendering
     * higher layer are drawn on top
     */
    TreeMap<Integer, SpriteContainer> m_spriteLayerMap = new TreeMap<>();
    int m_spritesNum;

    public void render() {

        // @TODO should we implement camera rotation? nah

        KVec2 screenDimension = m_renderer.getScreenDimension();
        KAABB screenAABB = new KAABB(KVec2.ZERO, screenDimension);

        KVec2 screenExtent = screenAABB.getExtent(); // also the center position of the screen in screen space

        KCamera camera = m_renderer.getCamera();
        KMat4 worldProjection = m_renderer.getCamera().getWorldProjection();
        KVec2 cameraPosition = camera.getPosition();
        KVec2 screenExtentScaled = screenExtent.mul(camera.getOrthoScale());
        KAABB unrotatedCameraAABB = new KAABB(cameraPosition.sub(screenExtentScaled), cameraPosition.add(screenExtentScaled));
        float cameraWorldRadius = unrotatedCameraAABB.getExternalRadius();

        KArrayList<KSprite> validSprites = new KArrayList<>();

        for (TreeMap.Entry<Integer, SpriteContainer> spriteLayerEntry : m_spriteLayerMap.entrySet()) {
            SpriteContainer spriteContainer = spriteLayerEntry.getValue();
            if (spriteContainer.sprites != null) {
                for (int i = 0; i < spriteContainer.sprites.size(); ++i) {
                    KSprite sprite = spriteContainer.sprites.get(i);
                    if (isValidSprite(sprite, cameraPosition, cameraWorldRadius)) {
                        validSprites.add(sprite);
                    }
                }
            }
            else {
                // @TODO sparse grid
            }
        }

        if (validSprites.size() == 0) {
            return;
        }

        ByteBuffer bb = ByteBuffer.allocateDirect(BYTES_PER_SPRITE * validSprites.size());
        bb.order(ByteOrder.nativeOrder());
        m_vertexData = bb.asFloatBuffer();
        m_vertexData.position(0);

        for (int i = 0; i < validSprites.size(); ++i) {
            KSprite sprite = validSprites.get(i);
            addToVertexData(i * VERTEX_DATA_NUM, sprite, worldProjection);
        }

        GLES31.glEnable(GLES31.GL_BLEND); // allow blend function
        GLES31.glBlendFunc(GLES31.GL_SRC_ALPHA, GLES31.GL_ONE_MINUS_SRC_ALPHA); // allow alpha

        m_shader.use();

        m_vertexBuffer.bind();
        m_vertexBuffer.setData(m_vertexData, m_vertexData.capacity(), GLES31.GL_STATIC_DRAW);

        // position and texCoord
        GLES31.glVertexAttribPointer(SHADER_LAYOUT_LOCATION_POSITION_TEXCOORD,
                VERTEX_POSITION_TEXCOORD_DATA_NUM,
                GLES31.GL_FLOAT,
                false,
                BYTES_PER_VERTEX,
                0);

        // color
        GLES31.glVertexAttribPointer(SHADER_LAYOUT_LOCATION_COLOR,
                VERTEX_COLOR_DATA_NUM,
                GLES31.GL_FLOAT,
                false,
                BYTES_PER_VERTEX,
                BYTES_PER_VERTEX_POSITION_TEXCOORD);

        // layer
        GLES31.glVertexAttribPointer(SHADER_LAYOUT_LOCATION_TEXARRAY_LAYER,
                VERTEX_TEXARRAY_LAYER_DATA_NUM,
                GLES31.GL_FLOAT,
                false,
                BYTES_PER_VERTEX,
                BYTES_PER_VERTEX_POSITION_TEXCOORD + BYTES_PER_VERTEX_COLOR);

        GLES31.glEnableVertexAttribArray(SHADER_LAYOUT_LOCATION_POSITION_TEXCOORD);
        GLES31.glEnableVertexAttribArray(SHADER_LAYOUT_LOCATION_COLOR);
        GLES31.glEnableVertexAttribArray(SHADER_LAYOUT_LOCATION_TEXARRAY_LAYER);

        KTexture.bind();

        GLES31.glDrawArrays(GLES31.GL_TRIANGLES, 0, SPRITE_VERTEX_NUM);

        KTexture.unbind();

        GLES31.glDisableVertexAttribArray(SHADER_LAYOUT_LOCATION_TEXARRAY_LAYER);
        GLES31.glDisableVertexAttribArray(SHADER_LAYOUT_LOCATION_COLOR);
        GLES31.glDisableVertexAttribArray(SHADER_LAYOUT_LOCATION_POSITION_TEXCOORD);

        KVertexBuffer.unbind();

        KShader.unuse();

        GLES31.glDisable(GLES31.GL_BLEND);

        m_vertexData = null;
    }

    /** is this sprite even worth sending to the GPU*/
    private boolean isValidSprite(KSprite sprite, KVec2 cameraPosition, float cameraWorldRadius) {
        if (sprite.isHidden()) {
            return false;
        }

        if (sprite.getTexture() == null) { // no texture
            return false;
        }

        KVec2 spriteSize = sprite.getSize();
        if (spriteSize == KVec2.ZERO) { // no size
            return false;
        }

        if (sprite.getColor().w == 0.f) { // fully transparent
            return false;
        }

        // out of screen culling
        boolean bNotCulled;
        if (sprite.shouldCull()) {
            // @TODO culling
            KVec2 spriteSizeHalf = spriteSize.mul(0.5f);
            float spriteWorldRadius = spriteSizeHalf.length();
            KVec2 spritePosition = sprite.getPosition();
            bNotCulled = CollisionStatics.circleAndCircle(spritePosition, spriteWorldRadius, cameraPosition, cameraWorldRadius);
        }
        else {
            bNotCulled = true;
        }

        return bNotCulled;
    }

    private void addToVertexData(int initialIndex, KSprite sprite, KMat4 worldProjection) {

        KVec2 position = sprite.getPosition();
        float angle = sprite.getAngle();
        KVec2 size = sprite.getSize();
        KVec2 sizeHalf = size.mul(0.5f);
        KVec4 color = sprite.getColor();
        KTexture texture = sprite.getTexture();
        KVec2[] textureUV = sprite.getTextureUV();

        KMat4 spriteModelMatrix = new KMat4();

        // move
        spriteModelMatrix.translate(position.x, position.y, 0.f);

        // rotate
        spriteModelMatrix.rotate(angle, 0.f, 0.f, 1.f);

        // move the vertices by sizeHalf so that the origin is the center rather than the vertex
        // @NOTE: this should be done after rotate
        spriteModelMatrix.translate(-sizeHalf.x, -sizeHalf.y, 0.f);

        // resize
        spriteModelMatrix.scale(size.x, size.y, 0.f);

        // project sprite into world
        KMat4 projectedSpriteModelMatrix = new KMat4();
        projectedSpriteModelMatrix.loadMultiply(worldProjection, spriteModelMatrix);

        float[] glVertexPosition_TL = GLM.mat4MulVec4(projectedSpriteModelMatrix.getArray(), sm_PositionVertexBufferData[0].getAsFloatArray());
        float[] glVertexPosition_BR = GLM.mat4MulVec4(projectedSpriteModelMatrix.getArray(), sm_PositionVertexBufferData[1].getAsFloatArray());
        float[] glVertexPosition_BL = GLM.mat4MulVec4(projectedSpriteModelMatrix.getArray(), sm_PositionVertexBufferData[2].getAsFloatArray());
        float[] glVertexPosition_TR = GLM.mat4MulVec4(projectedSpriteModelMatrix.getArray(), sm_PositionVertexBufferData[3].getAsFloatArray());

        // TL
        m_vertexData.put(initialIndex + 0, glVertexPosition_TL[0]);
        m_vertexData.put(initialIndex + 1, glVertexPosition_TL[1]);
        m_vertexData.put(initialIndex + 2, textureUV[0].x);
        m_vertexData.put(initialIndex + 3, textureUV[0].y);
        m_vertexData.put(initialIndex + 4, color.x);
        m_vertexData.put(initialIndex + 5, color.y);
        m_vertexData.put(initialIndex + 6, color.z);
        m_vertexData.put(initialIndex + 7, color.w);
        m_vertexData.put(initialIndex + 8, texture.getTextureArrayIndex());
        m_vertexData.put(initialIndex + 9, texture.getLayer());

        // BR
        m_vertexData.put(initialIndex + 10, glVertexPosition_BR[0]);
        m_vertexData.put(initialIndex + 11, glVertexPosition_BR[1]);
        m_vertexData.put(initialIndex + 12, textureUV[1].x);
        m_vertexData.put(initialIndex + 13, textureUV[1].y);
        m_vertexData.put(initialIndex + 14, color.x);
        m_vertexData.put(initialIndex + 15, color.y);
        m_vertexData.put(initialIndex + 16, color.z);
        m_vertexData.put(initialIndex + 17, color.w);
        m_vertexData.put(initialIndex + 18, texture.getTextureArrayIndex());
        m_vertexData.put(initialIndex + 19, texture.getLayer());

        // BL
        m_vertexData.put(initialIndex + 20, glVertexPosition_BL[0]);
        m_vertexData.put(initialIndex + 21, glVertexPosition_BL[1]);
        m_vertexData.put(initialIndex + 22, textureUV[2].x);
        m_vertexData.put(initialIndex + 23, textureUV[2].y);
        m_vertexData.put(initialIndex + 24, color.x);
        m_vertexData.put(initialIndex + 25, color.y);
        m_vertexData.put(initialIndex + 26, color.z);
        m_vertexData.put(initialIndex + 27, color.w);
        m_vertexData.put(initialIndex + 28, texture.getTextureArrayIndex());
        m_vertexData.put(initialIndex + 29, texture.getLayer());

        // TL
        m_vertexData.put(initialIndex + 30, glVertexPosition_TL[0]);
        m_vertexData.put(initialIndex + 31, glVertexPosition_TL[1]);
        m_vertexData.put(initialIndex + 32, textureUV[0].x);
        m_vertexData.put(initialIndex + 33, textureUV[0].y);
        m_vertexData.put(initialIndex + 34, color.x);
        m_vertexData.put(initialIndex + 35, color.y);
        m_vertexData.put(initialIndex + 36, color.z);
        m_vertexData.put(initialIndex + 37, color.w);
        m_vertexData.put(initialIndex + 38, texture.getTextureArrayIndex());
        m_vertexData.put(initialIndex + 39, texture.getLayer());

        // TR
        m_vertexData.put(initialIndex + 40, glVertexPosition_TR[0]);
        m_vertexData.put(initialIndex + 41, glVertexPosition_TR[1]);
        m_vertexData.put(initialIndex + 42, textureUV[3].x);
        m_vertexData.put(initialIndex + 43, textureUV[3].y);
        m_vertexData.put(initialIndex + 44, color.x);
        m_vertexData.put(initialIndex + 45, color.y);
        m_vertexData.put(initialIndex + 46, color.z);
        m_vertexData.put(initialIndex + 47,color.w);
        m_vertexData.put(initialIndex + 48, texture.getTextureArrayIndex());
        m_vertexData.put(initialIndex + 49, texture.getLayer());

        // BR
        m_vertexData.put(initialIndex + 50, glVertexPosition_BR[0]);
        m_vertexData.put(initialIndex + 51, glVertexPosition_BR[1]);
        m_vertexData.put(initialIndex + 52, textureUV[1].x);
        m_vertexData.put(initialIndex + 53, textureUV[1].y);
        m_vertexData.put(initialIndex + 54, color.x);
        m_vertexData.put(initialIndex + 55, color.y);
        m_vertexData.put(initialIndex + 56, color.z);
        m_vertexData.put(initialIndex + 57, color.w);
        m_vertexData.put(initialIndex + 58, texture.getTextureArrayIndex());
        m_vertexData.put(initialIndex + 59, texture.getLayer());

//        KVec2 UV = textureTest.getUV();
//        m_vertexData.put(initialIndex + 0, -1.f);
//        m_vertexData.put(initialIndex + 1, 1.f);
//        m_vertexData.put(initialIndex + 2, 0.f);
//        m_vertexData.put(initialIndex + 3, 0.f);
//        m_vertexData.put(initialIndex + 4, 1.f);
//        m_vertexData.put(initialIndex + 5, 1.f);
//        m_vertexData.put(initialIndex + 6, 1.f);
//        m_vertexData.put(initialIndex + 7, 1.f);
//        m_vertexData.put(initialIndex + 8, textureTest.getLayer());
//
//        m_vertexData.put(initialIndex + 9, 0.f);
//        m_vertexData.put(initialIndex + 10, 0.f);
//        m_vertexData.put(initialIndex + 11, UV.x);
//        m_vertexData.put(initialIndex + 12, UV.y);
//        m_vertexData.put(initialIndex + 13, 1.f);
//        m_vertexData.put(initialIndex + 14, 1.f);
//        m_vertexData.put(initialIndex + 15, 1.f);
//        m_vertexData.put(initialIndex + 16, 1.f);
//        m_vertexData.put(initialIndex + 17, textureTest.getLayer());
//
//        m_vertexData.put(initialIndex + 18, -1.f);
//        m_vertexData.put(initialIndex + 19, 0.f);
//        m_vertexData.put(initialIndex + 20, 0.f);
//        m_vertexData.put(initialIndex + 21, UV.y);
//        m_vertexData.put(initialIndex + 22, 1.f);
//        m_vertexData.put(initialIndex + 23, 1.f);
//        m_vertexData.put(initialIndex + 24, 1.f);
//        m_vertexData.put(initialIndex + 25, 1.f);
//        m_vertexData.put(initialIndex + 26, textureTest.getLayer());
//
//        m_vertexData.put(initialIndex + 27, -1.f);
//        m_vertexData.put(initialIndex + 28, 1.f);
//        m_vertexData.put(initialIndex + 29, 0.f);
//        m_vertexData.put(initialIndex + 30, 0.f);
//        m_vertexData.put(initialIndex + 31, 1.f);
//        m_vertexData.put(initialIndex + 32, 1.f);
//        m_vertexData.put(initialIndex + 33, 1.f);
//        m_vertexData.put(initialIndex + 34, 1.f);
//        m_vertexData.put(initialIndex + 35, textureTest.getLayer());
//
//        m_vertexData.put(initialIndex + 36, 0.f);
//        m_vertexData.put(initialIndex + 37, 1.f);
//        m_vertexData.put(initialIndex + 38, UV.x);
//        m_vertexData.put(initialIndex + 39, 0.f);
//        m_vertexData.put(initialIndex + 40, 1.f);
//        m_vertexData.put(initialIndex + 41, 1.f);
//        m_vertexData.put(initialIndex + 42, 1.f);
//        m_vertexData.put(initialIndex + 43,1.f);
//        m_vertexData.put(initialIndex + 44, textureTest.getLayer());
//
//        m_vertexData.put(initialIndex + 45, 0.f);
//        m_vertexData.put(initialIndex + 46, 0.f);
//        m_vertexData.put(initialIndex + 47, UV.x);
//        m_vertexData.put(initialIndex + 48, UV.y);
//        m_vertexData.put(initialIndex + 49, 1.f);
//        m_vertexData.put(initialIndex + 50, 1.f);
//        m_vertexData.put(initialIndex + 51, 1.f);
//        m_vertexData.put(initialIndex + 52, 1.f);
//        m_vertexData.put(initialIndex + 53, textureTest.getLayer());

    }

    public static class SpriteContainer {
        KArrayList<KSprite> sprites;
        KSpriteSparseGrid spriteSparseGrid;
    }

}
