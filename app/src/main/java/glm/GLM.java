package glm;

import android.renderscript.Matrix4f;

import KittyEngine.Math.KVec4;

public class GLM {
    static {
        System.loadLibrary("native-lib");
    }

    /**
     * @param inMat4 float[16]
     * @param inVec4 float[4]
     * @return float[4]
     * */
    public static native float[] mat4MulVec4(float[] inMat4, float[] inVec4);

    /**
     * @param inMat41 float[16]
     * @param inMat42 float[16]
     * @return float[16]
     * */
    public static native float[] mat4MulMat4(float[] inMat41, float[] inMat42);

    /**
     * @param eye float[16]
     * @param center float[16]
     * @param up float[3]
     * @return float[16]
     * */
    public static native float[] lookAtRH(float[] eye, float[] center, float[] up);

    public static native float[] rotateZ(float[] inMat4, float inAngle);

    // just for fun. object conversion is hard stick with array or primitives. boy is this inefficient
    public static native KVec4 mat4MulVec4v(Matrix4f inMat4, KVec4 inVec4);

}
