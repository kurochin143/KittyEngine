package glm;

public class GLM {
    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    public static native float[] mat4MulVec4(float[] inMat4, float[] inVec4);
}
