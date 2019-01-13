package KittyEngine.Graphics;

import android.opengl.GLES31;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class KVertexBuffer {

    public KVertexBuffer() {
        GLES31.glGenBuffers(1, m_vboIds, 0);
    }

    /**
     * do not reuse this KVertexBuffer once this is called, this is for clean up purpose only
     * this is for clean up purpose only, this will not delete the KVertexBuffer's memory
     * */
    public void destroy() {
        GLES31.glDeleteBuffers(1, m_vboIds, 0);
        m_vboIds = null;
    }

    private int[] m_vboIds = new int[1];

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

    /**
     * @param num the number of the data from 0 to feed to the GPU. one data equals one float
     */
    public void setData(FloatBuffer fb, int num, int type) {
        GLES31.glBufferData(GLES31.GL_ARRAY_BUFFER, num * 4, fb, type);
    }
}
