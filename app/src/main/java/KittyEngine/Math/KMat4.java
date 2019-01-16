package KittyEngine.Math;

import android.renderscript.Matrix4f;

public class KMat4 extends Matrix4f {

    public KMat4() {
        super();
    }

    public KMat4(KMat4 copy) {
        super(copy.getArray());
    }

    public KMat4(float[] arr) {
        super(arr);
    }

}
