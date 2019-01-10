package com.example.israel.kittyengine;

import android.opengl.GLES31;

import java.util.Vector;

import glm.GLM;

class KHUDRenderer {

    public KHUDRenderer() {
        m_triangleRenderer = new KTriangleRenderer();
    }

    private KTriangleRenderer m_triangleRenderer;

    public void render() {
        m_triangleRenderer.render();
    }

    // triangle renderer
    private class KTriangleRenderer {

        public KTriangleRenderer() {
            m_shader = new KShader(vertexCode, fragmentCode);

            m_vertexBuffer = new KVertexBuffer();
        }

        //public static final int MAX_FLUSH_NUM = 1024;
        public static final int TRIANGLE_VERTEX_NUM = 3; // triangle duh
        public static final int VERTEX_POSITION_DATA_NUM = 2; // 2 floats in a position
        public static final int VERTEX_COLOR_DATA_NUM = 4; // 4 floats in color
        public static final int BYTES_PER_FLOAT = 4;
        public static final int BYTES_PER_VERTEX_POSITION = VERTEX_POSITION_DATA_NUM * BYTES_PER_FLOAT;
        public static final int BYTES_PER_VERTEX_COLOR = VERTEX_COLOR_DATA_NUM * BYTES_PER_FLOAT;
        //public static final int VERTEX_BUFFER_DATA_NUM = MAX_FLUSH_NUM * TRIANGLE_VERTEX_NUM * VERTEX_DATA_NUM;

        public static final String vertexCode =
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

        public static final String fragmentCode =
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
        private final int m_positionHandle = 0;
        private final int m_colorHandle = 1;
        private KVertexBuffer m_vertexBuffer;

        private Vector<Float> m_vertexData = new Vector<>();
        private int m_vertexNum;

        public void render() {

            float[] triangle = new float[6];
            triangle[0] = 0.f;
            triangle[1] = 200.f;
            triangle[2] = -200.f;
            triangle[3] = 0.f;
            triangle[4] = 200.f;
            triangle[5] = 0.f;

            float[] color = new float[4];
            color[0] = 1.f;
            color[1] = 1.f;
            color[2] = 0.f;
            color[3] = 1.f;
            addTriangle(triangle, color);

            float[] vertexData = new float[m_vertexData.size()];
            for (int i = 0; i < m_vertexData.size(); ++i) {
                vertexData[i] = m_vertexData.get(i);
            }

            m_shader.use();

            m_vertexBuffer.bind();
            m_vertexBuffer.setData(vertexData, GLES31.GL_STATIC_DRAW);

            final int stride = (VERTEX_POSITION_DATA_NUM + VERTEX_COLOR_DATA_NUM) * BYTES_PER_FLOAT;

            // position
            GLES31.glVertexAttribPointer(m_positionHandle,
                    VERTEX_POSITION_DATA_NUM,
                    GLES31.GL_FLOAT,
                    false,
                    stride,
                    0);

            // color
            GLES31.glVertexAttribPointer(m_colorHandle,
                    VERTEX_COLOR_DATA_NUM,
                    GLES31.GL_FLOAT,
                    false,
                    stride,
                    BYTES_PER_VERTEX_POSITION);

            GLES31.glEnableVertexAttribArray(m_positionHandle);
            GLES31.glEnableVertexAttribArray(m_colorHandle);

            GLES31.glDrawArrays(GLES31.GL_TRIANGLES, 0, m_vertexNum);

            GLES31.glDisableVertexAttribArray(m_colorHandle);
            GLES31.glDisableVertexAttribArray(m_positionHandle);

            KVertexBuffer.unbind();

            KShader.unuse();
            m_vertexData.clear();

            m_vertexNum = 0;
        }

        public boolean addTriangle(float[] screenVertexPositions, float[] color) {
            if (screenVertexPositions.length != 6 && color.length != 4) {
                return false;
            }

            for (int vertexStartIndex = 0; vertexStartIndex < 6; vertexStartIndex += 2) {
                float[] screenVertexPosition = new float[4];
                screenVertexPosition[0] = screenVertexPositions[vertexStartIndex];
                screenVertexPosition[1] = screenVertexPositions[vertexStartIndex + 1];
                screenVertexPosition[2] = 0.f;
                screenVertexPosition[3] = 1.f;
                float[] projectedVertexPosition = GLM.mat4MulVec4(KRenderer.getScreenProjection(), screenVertexPosition);

                m_vertexData.add(projectedVertexPosition[0]);
                m_vertexData.add(projectedVertexPosition[1]);
                m_vertexData.add(color[0]);
                m_vertexData.add(color[1]);
                m_vertexData.add(color[2]);
                m_vertexData.add(color[3]);
            }

            m_vertexNum += 3;

            return true;
        }
    }

    public void drawPolygon() {

    }
}
