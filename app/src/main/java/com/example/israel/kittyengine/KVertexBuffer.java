package com.example.israel.kittyengine;

import android.opengl.GLES31;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class KVertexBuffer {

    public KVertexBuffer() {
        m_vboIds = new int[1];
        GLES31.glGenBuffers(1, m_vboIds, 0);
    }

    public void destroy() {
        GLES31.glDeleteBuffers(1, m_vboIds, 0);
    }

    private int[] m_vboIds;

    public void bind() {
        GLES31.glBindBuffer(GLES31.GL_ARRAY_BUFFER, m_vboIds[0]);
    }

    static public void unbind() {
        GLES31.glBindBuffer(GLES31.GL_ARRAY_BUFFER, 0);
    }

    public void setData(float[] data, int type) {
        int sizeBytes = data.length * 4;
        // @NOTE: allocateDirect
        ByteBuffer bb = ByteBuffer.allocateDirect(sizeBytes);
        bb.order(ByteOrder.nativeOrder());

        FloatBuffer fb = bb.asFloatBuffer();
        fb.put(data);
        fb.position(0);
        GLES31.glBufferData(GLES31.GL_ARRAY_BUFFER, sizeBytes, fb, type);
    }
}
