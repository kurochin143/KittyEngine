package KittyEngine.Graphics;

import android.opengl.GLES31;
import android.util.Log;

public class KShader {

    public KShader(String vertexShaderCode, String fragmentShaderCode) {
        // gl calls should be inside renderer, such as onSurfaceCreated
        int vertexShaderId = GLES31.glCreateShader(GLES31.GL_VERTEX_SHADER);
        GLES31.glShaderSource(vertexShaderId, vertexShaderCode);
        GLES31.glCompileShader(vertexShaderId);

        int[] result = new int[1];
        GLES31.glGetShaderiv(vertexShaderId, GLES31.GL_COMPILE_STATUS, result, 0);
        if (result[0] == 0) { // error compile
            String errorLog = GLES31.glGetShaderInfoLog(vertexShaderId);
            Log.w("KittyLog", "Vertex shader compile error \n" + errorLog);

            GLES31.glDeleteShader(vertexShaderId);
            return;
        }

        int fragmentShaderId = GLES31.glCreateShader(GLES31.GL_FRAGMENT_SHADER);
        GLES31.glShaderSource(fragmentShaderId, fragmentShaderCode);
        GLES31.glCompileShader(fragmentShaderId);

        GLES31.glGetShaderiv(fragmentShaderId, GLES31.GL_COMPILE_STATUS, result, 0);
        if (result[0] == 0) { // error compile
            String errorLog = GLES31.glGetShaderInfoLog(fragmentShaderId);
            Log.w("KittyLog", "Fragment shader compile error \n" +  errorLog);

            GLES31.glDeleteShader(vertexShaderId);
            GLES31.glDeleteShader(fragmentShaderId);
            return;
        }

        m_programId = GLES31.glCreateProgram();
        GLES31.glAttachShader(m_programId, vertexShaderId);
        GLES31.glAttachShader(m_programId, fragmentShaderId);
        GLES31.glLinkProgram(m_programId);
        GLES31.glValidateProgram(m_programId);

        // cleanup
        GLES31.glDeleteShader(vertexShaderId);
        GLES31.glDeleteShader(fragmentShaderId);

    }

    private int m_programId;

    public int getProgram() {
        return m_programId;
    }

    public void use() {
        GLES31.glUseProgram(m_programId);
    }

    static public void unuse() {
        GLES31.glUseProgram(0);
    }

    public void setInt(String name, int value) {
        int i = GLES31.glGetUniformLocation(m_programId, name);
        GLES31.glUniform1i(i, value);
    }

    public void setVec4(String name, float[] vec4) {
        int i = GLES31.glGetUniformLocation(m_programId, name);
        GLES31.glUniform4fv(i, 1, vec4, 0);
    }


}
