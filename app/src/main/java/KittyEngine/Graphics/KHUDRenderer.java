package KittyEngine.Graphics;

import android.opengl.GLES31;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import KittyEngine.Math.KMath;
import KittyEngine.Math.KVec2;
import KittyEngine.Math.KVec4;
import glm.GLM;

// @TODO draw concave polygon
// @TODO draw AABB
// @TODO draw string. when font is implemented

public class KHUDRenderer {

    KHUDRenderer() {
        m_triangleRenderer = new KTriangleRenderer();
    }

    private KTriangleRenderer m_triangleRenderer;

    public void render() {
        m_triangleRenderer.render();
    }

    /** Draw line on the screen for one frame*/
    public void drawLine(KVec2 screenPosition1, KVec2 screenPosition2, KVec4 color, float thickness) {
        KVec2 normalizedVector = screenPosition2.min(screenPosition1).getNormalized();
        KVec2 normal = new KVec2(normalizedVector.y, -normalizedVector.x);

        KVec2 halfNormal = normal.mul(thickness * 0.5f);

        KVec2[] triangle1 = new KVec2[3];
        KVec2[] triangle2 = new KVec2[3];

        KVec2 vertexPos = screenPosition1.add(halfNormal);
        triangle1[0] = vertexPos; // 1st triangle1 - 1st vertex

        vertexPos = screenPosition2.add(halfNormal);
        triangle1[1] = vertexPos; // 2nd t1-2
        triangle2[0] = vertexPos; // 4th t2-1

        vertexPos = screenPosition1.min(halfNormal);
        triangle1[2] = vertexPos; // 3rd t1-3
        triangle2[1] = vertexPos; // 5th t2-2

        vertexPos = screenPosition2.min(halfNormal);
        triangle2[2] = vertexPos; // 6th t2-3

        m_triangleRenderer.addTriangle(triangle1, color);
        m_triangleRenderer.addTriangle(triangle2, color);
    }

    /**
     * Draw convex polygon on screen for one frame
     * @param screenVertexPositions Min 3 vertices, and must be arranged clockwise
     * @param bFilled Fill the polygon instead of line
     */
    public void drawConvexPolygon(KVec2[] screenVertexPositions, KVec4 color, boolean bFilled, float unfilledThickness) {
        if (screenVertexPositions.length < 3) {
            Log.w("KittyLog", "Failed to draw convex polygon. Need at least 3 vertices");
            return;
        }

        if (bFilled)
        {	// draw triangles
            // @TODO this can be optimized by using EveryOtherVertex

            if (screenVertexPositions.length == 3)
            {	// draw one triangle
                m_triangleRenderer.addTriangle(screenVertexPositions, color);
                return;
            }

            KVec2 centroid = KVec2.ZERO;
            for (int i = 0; i < screenVertexPositions.length; ++i)
            {
                centroid = centroid.add(screenVertexPositions[i]);
            }

            centroid = centroid.div(screenVertexPositions.length); // average location of all the vertices

            int lastIndex = screenVertexPositions.length - 1;
            KVec2[] triangle = new KVec2[3];
            triangle[0] = screenVertexPositions[lastIndex];
            triangle[1] = centroid;
            triangle[2] = screenVertexPositions[0];
            m_triangleRenderer.addTriangle(triangle, color); // add the last triangle first
            for (int i = 0; i < lastIndex; ++i)
            {
                triangle[0] = screenVertexPositions[i];
                triangle[2] = screenVertexPositions[i + 1];
                m_triangleRenderer.addTriangle(triangle, color);
            }
        }
        else
        {	// draw lines
            int lastIndex = screenVertexPositions.length - 1;
            drawLine(screenVertexPositions[lastIndex], screenVertexPositions[0], color, unfilledThickness);
            for (int i = 0; i < lastIndex; ++i)
            {
                drawLine(screenVertexPositions[i], screenVertexPositions[i + 1], color, unfilledThickness);
            }
        }
    }

    /**
     * Draw circle on screen for for one frame
     * @param bFilled Fill the circle instead of lines
     */
    public void drawCircle(KVec2 screenPosition, float screenRadius, KVec4 color, boolean bFilled, int sides, float unfilledThickness) {
        if (sides < 3) {
            Log.w("KittyLog", "Failed to draw circle. Need at least 3 sides");
            return;
        }

        float angle = 0.f;
        final float angleDelta = KMath.PI2 / (float)sides;
        if (bFilled)
        {	// draw circle using triangle fan
            KVec2[] triangle = new KVec2[3];
            triangle[1] = screenPosition;
            for (int i = 0; i < sides; ++i)
            {
                float[] sinCos = KMath.sinCos(angle);
                float x = screenRadius * sinCos[0] + screenPosition.x;
                float y = screenRadius * sinCos[1] + screenPosition.y;
                triangle[0] = new KVec2(x, y);

                angle += angleDelta; // next vertex
                sinCos = KMath.sinCos(angle);
                x = screenRadius * sinCos[0] + screenPosition.x;
                y = screenRadius * sinCos[1] + screenPosition.y;
                triangle[2] = new KVec2(x, y);

                m_triangleRenderer.addTriangle(triangle, color);
            }
        }
        else
        {	// draw lines
            for (int i = 0; i < sides; ++i)
            {
                float[] sinCos = KMath.sinCos(angle);
                float x1 = screenRadius * sinCos[0] + screenPosition.x;
                float y1 = screenRadius * sinCos[1] + screenPosition.y;

                angle += angleDelta; // next point
                sinCos = KMath.sinCos(angle);
                float x2 = screenRadius * sinCos[0] + screenPosition.x;
                float y2 = screenRadius * sinCos[1] + screenPosition.y;

                drawLine(new KVec2(x1, y1), new KVec2(x2, y2), color, unfilledThickness);
            }
        }
    }

    // triangle renderer
    private class KTriangleRenderer {

        public KTriangleRenderer() {
            m_shader = new KShader(vertexShaderCode, fragmentShaderCode);

            m_vertexBuffer = new KVertexBuffer();

            ByteBuffer bb = ByteBuffer.allocateDirect(VERTEX_DATA_BYTES_ALLOCATION_INCREMENT);
            bb.order(ByteOrder.nativeOrder());
            m_vertexDataF = bb.asFloatBuffer();
        }

        private static final int TRIANGLE_VERTEX_NUM = 3; // triangle duh
        private static final int VERTEX_POSITION_DATA_NUM = 2; // 2 floats in a position
        private static final int VERTEX_COLOR_DATA_NUM = 4; // 4 floats in color
        private static final int VERTEX_DATA_NUM = VERTEX_POSITION_DATA_NUM + VERTEX_COLOR_DATA_NUM; // total num of floats in a vertex
        private static final int BYTES_PER_FLOAT = 4;
        private static final int BYTES_PER_VERTEX_POSITION = VERTEX_POSITION_DATA_NUM * BYTES_PER_FLOAT;
        private static final int BYTES_PER_VERTEX_COLOR = VERTEX_COLOR_DATA_NUM * BYTES_PER_FLOAT;
        private static final int BYTES_PER_VERTEX = VERTEX_DATA_NUM * BYTES_PER_FLOAT; // also the stride
        private static final int VERTEX_DATA_BYTES_ALLOCATION_INCREMENT = BYTES_PER_VERTEX * TRIANGLE_VERTEX_NUM * 1024; // 1024 triangles

        private static final String vertexShaderCode =
                "#version 300 es\n" +
                        "layout(location = 0) in vec2 inPosition;" +
                        "layout(location = 1) in vec4 inColor;" +
                        "out vec4 f_color;" +
                        "void main() {" +
                        "   f_color = inColor;" +
                        "   gl_Position = vec4(inPosition.xy, 0.f, 1.f);" +
                        "}";
//                                "attribute vec4 vPosition;" +
//                                        "void main() {" +
//                                        "  gl_Position = vPosition;" +
//                                        "}";

        private final int SHADER_LAYOUT_LOCATION_POSITION = 0;
        private final int SHADER_LAYOUT_LOCATION_COLOR = 1;

        private static final String fragmentShaderCode =
                "#version 300 es\n" +
                        "precision mediump float;" +
                        "in vec4 f_color;" +
                        "out vec4 color;" +
                        "void main() {" +
                        "   color = f_color;" +
                        "}";
//                "precision mediump float;" +
//                        "uniform vec4 vColor;" +
//                        "void main() {" +
//                        "  gl_FragColor = vColor;" +
//                        "}";

        private KShader m_shader;
        private KVertexBuffer m_vertexBuffer;

        private FloatBuffer m_vertexDataF;
        private int m_vertexNum;

        public void render() {
            if (m_vertexNum == 0) {
                return;
            }

            m_shader.use();

            m_vertexBuffer.bind();
            m_vertexBuffer.setData(m_vertexDataF, m_vertexNum * VERTEX_DATA_NUM, GLES31.GL_STATIC_DRAW);

            // position
            GLES31.glVertexAttribPointer(SHADER_LAYOUT_LOCATION_POSITION,
                    VERTEX_POSITION_DATA_NUM,
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
                    BYTES_PER_VERTEX_POSITION);

            GLES31.glEnableVertexAttribArray(SHADER_LAYOUT_LOCATION_POSITION);
            GLES31.glEnableVertexAttribArray(SHADER_LAYOUT_LOCATION_COLOR);

            GLES31.glDrawArrays(GLES31.GL_TRIANGLES, 0, m_vertexNum);

            GLES31.glDisableVertexAttribArray(SHADER_LAYOUT_LOCATION_COLOR);
            GLES31.glDisableVertexAttribArray(SHADER_LAYOUT_LOCATION_POSITION);

            KVertexBuffer.unbind();

            KShader.unuse();

            // reset size if the size was increased
            if ((m_vertexDataF.capacity() * 4) > VERTEX_DATA_BYTES_ALLOCATION_INCREMENT) {
                ByteBuffer bb = ByteBuffer.allocateDirect(VERTEX_DATA_BYTES_ALLOCATION_INCREMENT);
                bb.order(ByteOrder.nativeOrder());
                m_vertexDataF = bb.asFloatBuffer();
            }

            m_vertexNum = 0;
        }

        public void addTriangle(KVec2[] screenVertexPositions, KVec4 color) {
            // allocate bigger FloatBuffer
            if (((m_vertexNum + TRIANGLE_VERTEX_NUM)*VERTEX_DATA_NUM) > m_vertexDataF.capacity()) {
                ByteBuffer bb = ByteBuffer.allocateDirect((m_vertexDataF.capacity() * BYTES_PER_FLOAT) + VERTEX_DATA_BYTES_ALLOCATION_INCREMENT);
                bb.order(ByteOrder.nativeOrder());
                FloatBuffer fb = bb.asFloatBuffer();

                fb.put(m_vertexDataF);
                fb.position(0);
                m_vertexDataF = fb;
            }
            // add 3 vertices
            for (int i = 0; i < TRIANGLE_VERTEX_NUM; ++i) {
                float[] screenVertexPosition = new float[4];
                screenVertexPosition[0] = screenVertexPositions[i].x;
                screenVertexPosition[1] = screenVertexPositions[i].y;
                screenVertexPosition[2] = 0.f;
                screenVertexPosition[3] = 1.f;
                float[] projectedVertexPosition = GLM.mat4MulVec4(KRenderer.getScreenProjection(), screenVertexPosition);

                int vertexDataFirstIndex = m_vertexNum * VERTEX_DATA_NUM;
                m_vertexDataF.put(vertexDataFirstIndex, projectedVertexPosition[0]);
                m_vertexDataF.put(vertexDataFirstIndex + 1, projectedVertexPosition[1]);
                m_vertexDataF.put(vertexDataFirstIndex + 2, color.x);
                m_vertexDataF.put(vertexDataFirstIndex + 3, color.y);
                m_vertexDataF.put(vertexDataFirstIndex + 4, color.z);
                m_vertexDataF.put(vertexDataFirstIndex + 5, color.w);

                m_vertexNum += 1;
            }
        }
    }
}
