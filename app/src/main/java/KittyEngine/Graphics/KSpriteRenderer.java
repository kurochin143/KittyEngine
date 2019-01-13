package KittyEngine.Graphics;

import android.opengl.GLES31;

import com.example.israel.kittyengine.R;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.TreeMap;

import KittyEngine.Container.KArrayList;
import KittyEngine.Engine.KEngine;
import KittyEngine.Math.KVec2;

public class KSpriteRenderer {

    KSpriteRenderer() {
        m_shader = new KShader(vertexShaderCode, fragmentShaderCode);

        m_vertexBuffer = new KVertexBuffer();

        textureTest = new KTexture(R.raw.awesomeface);
    }

    private static final int SPRITE_VERTEX_NUM = 6; // 2 triangles
    private static final int VERTEX_POSITION_TEXCOORD_DATA_NUM = 4; // 2 floats in a position, 2 floats in a texCoord
    private static final int VERTEX_COLOR_DATA_NUM = 4; // 4 floats in color
    private static final int VERTEX_LAYER_DATA_NUM = 1; // 1 float in layer
    private static final int VERTEX_DATA_NUM = VERTEX_POSITION_TEXCOORD_DATA_NUM + VERTEX_COLOR_DATA_NUM + VERTEX_LAYER_DATA_NUM; // total num of floats in a vertex
    private static final int BYTES_PER_FLOAT = 4;
    private static final int BYTES_PER_VERTEX_POSITION_TEXCOORD = VERTEX_POSITION_TEXCOORD_DATA_NUM * BYTES_PER_FLOAT;
    private static final int BYTES_PER_VERTEX_COLOR = VERTEX_COLOR_DATA_NUM * BYTES_PER_FLOAT;
    private static final int BYTES_PER_VERTEX_LAYER = VERTEX_LAYER_DATA_NUM * BYTES_PER_FLOAT;
    private static final int BYTES_PER_VERTEX = VERTEX_DATA_NUM * BYTES_PER_FLOAT; // also the stride
    private static final int BYTES_PER_SPRITE = BYTES_PER_VERTEX * SPRITE_VERTEX_NUM;

    public static final String vertexShaderCode =
                            "#version 300 es\n" +
                        "layout(location = 0) in vec4 inPositionTexCoord;\n" +
                        "layout(location = 1) in vec4 inColor;\n" +
                        "layout(location = 2) in float inLayer;\n" +
                        "out vec2 f_texCoord;\n" +
                        "out vec4 f_color;\n" +
                        "out float f_layer;\n" +
                        "void main() {\n" +
                        "   gl_Position = vec4(inPositionTexCoord.xy, 0.f, 1.f);\n" +
                        "   f_color = inColor;\n" +
                        "   f_layer = inLayer;\n" +
                        "   f_texCoord = inPositionTexCoord.zw;\n" +
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
    private static final int SHADER_LAYOUT_LOCATION_LAYER = 2;

    public static final String fragmentShaderCode =
                    "#version 300 es\n" +
                        "precision mediump float;\n" +
                        "in vec2 f_texCoord;\n" +
                        "in vec4 f_color;\n" +
                        "in float f_layer;\n" +
                            "precision lowp sampler2DArray;\n" +
                        "uniform sampler2DArray texArr1;\n" +
                        "out vec4 color;\n" +
                        "void main() {\n" +
                        "   color = f_color * texture(texArr1, vec3(f_texCoord, f_layer));\n" +
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


    private KShader m_shader;
    private KVertexBuffer m_vertexBuffer;
    // @TODO index buffer

    KTexture textureTest; // @TODO remove

    /**
     * used for sorted rendering
     * higher layer are drawn on top
     * sprite container mapped by sublayer index and mapped by layer index
     */
    private TreeMap<Integer, SpriteSubLayerMap> m_spriteLayerMap = new TreeMap<>();
    private int m_spritesNum;

    public void render() {
        float[] worldProjection = new float[16];

        // @TODO should we implement camera rotation? nah

        ByteBuffer bb = ByteBuffer.allocateDirect(BYTES_PER_SPRITE /** *m_spritesNum */); // @TODO uncomment
        bb.order(ByteOrder.nativeOrder());
        FloatBuffer vertexData = bb.asFloatBuffer();
        vertexData.position(0);

        for (TreeMap.Entry<Integer, SpriteSubLayerMap> spriteLayer : m_spriteLayerMap.entrySet()) {
            SpriteSubLayerMap spriteSubLayerMap = spriteLayer.getValue();
            for (TreeMap.Entry<Integer, SpriteContainer> spriteSubLayerEntry : spriteSubLayerMap.map.entrySet()) {
                SpriteContainer spriteContainer = spriteSubLayerEntry.getValue();

                if (spriteContainer.sprites.size() != 0) {
                    for (int i = 0; i < spriteContainer.sprites.size(); ++i) {
                        addToBuffer(spriteContainer.sprites.get(i));
                    }
                }
                else {

                }
            }
        }

        KVec2 UV = textureTest.getUV();
        vertexData.put(0, -1.f);
        vertexData.put(1, 1.f);
        vertexData.put(2, 0.f);
        vertexData.put(3, 0.f);
        vertexData.put(4, 1.f);
        vertexData.put(5, 1.f);
        vertexData.put(6, 1.f);
        vertexData.put(7, 1.f);
        vertexData.put(8, textureTest.getLayer());

        vertexData.put(9, 0.f);
        vertexData.put(10, 0.f);
        vertexData.put(11, UV.x);
        vertexData.put(12, UV.y);
        vertexData.put(13, 1.f);
        vertexData.put(14, 1.f);
        vertexData.put(15, 1.f);
        vertexData.put(16, 1.f);
        vertexData.put(17, textureTest.getLayer());

        vertexData.put(18, -1.f);
        vertexData.put(19, 0.f);
        vertexData.put(20, 0.f);
        vertexData.put(21, UV.y);
        vertexData.put(22, 1.f);
        vertexData.put(23, 1.f);
        vertexData.put(24, 1.f);
        vertexData.put(25, 1.f);
        vertexData.put(26, textureTest.getLayer());

        vertexData.put(27, -1.f);
        vertexData.put(28, 1.f);
        vertexData.put(29, 0.f);
        vertexData.put(30, 0.f);
        vertexData.put(31, 1.f);
        vertexData.put(32, 1.f);
        vertexData.put(33, 1.f);
        vertexData.put(34, 1.f);
        vertexData.put(35, textureTest.getLayer());

        vertexData.put(36, 0.f);
        vertexData.put(37, 1.f);
        vertexData.put(38, UV.x);
        vertexData.put(39, 0.f);
        vertexData.put(40, 1.f);
        vertexData.put(41, 1.f);
        vertexData.put(42, 1.f);
        vertexData.put(43,1.f);
        vertexData.put(44, textureTest.getLayer());

        vertexData.put(45, 0.f);
        vertexData.put(46, 0.f);
        vertexData.put(47, UV.x);
        vertexData.put(48, UV.y);
        vertexData.put(49, 1.f);
        vertexData.put(50, 1.f);
        vertexData.put(51, 1.f);
        vertexData.put(52, 1.f);
        vertexData.put(53, textureTest.getLayer());

        GLES31.glEnable(GLES31.GL_BLEND); // allow blend function
        GLES31.glBlendFunc(GLES31.GL_SRC_ALPHA, GLES31.GL_ONE_MINUS_SRC_ALPHA); // allow alpha

        m_shader.use();

        m_vertexBuffer.bind();
        m_vertexBuffer.setData(vertexData, vertexData.capacity(), GLES31.GL_STATIC_DRAW);

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
        GLES31.glVertexAttribPointer(SHADER_LAYOUT_LOCATION_LAYER,
                VERTEX_LAYER_DATA_NUM,
                GLES31.GL_FLOAT,
                false,
                BYTES_PER_VERTEX,
                BYTES_PER_VERTEX_POSITION_TEXCOORD + BYTES_PER_VERTEX_COLOR);

        GLES31.glEnableVertexAttribArray(SHADER_LAYOUT_LOCATION_POSITION_TEXCOORD);
        GLES31.glEnableVertexAttribArray(SHADER_LAYOUT_LOCATION_COLOR);
        GLES31.glEnableVertexAttribArray(SHADER_LAYOUT_LOCATION_LAYER);

        KTexture.bind();

        GLES31.glDrawArrays(GLES31.GL_TRIANGLES, 0, SPRITE_VERTEX_NUM);

        KTexture.unbind();

        GLES31.glDisableVertexAttribArray(SHADER_LAYOUT_LOCATION_LAYER);
        GLES31.glDisableVertexAttribArray(SHADER_LAYOUT_LOCATION_COLOR);
        GLES31.glDisableVertexAttribArray(SHADER_LAYOUT_LOCATION_POSITION_TEXCOORD);

        KVertexBuffer.unbind();

        KShader.unuse();

        GLES31.glDisable(GLES31.GL_BLEND);
    }

    private void addToBuffer(KSprite sprite) {


    }

    public class SpriteContainer {
        KArrayList<KSprite> sprites;
        KSpriteSparseGrid spriteSparseGrid;
    }

    public class SpriteSubLayerMap {
        TreeMap<Integer, SpriteContainer> map = new TreeMap<>();
    }

}
