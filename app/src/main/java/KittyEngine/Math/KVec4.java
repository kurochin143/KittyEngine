package KittyEngine.Math;

public class KVec4 {

    /** default constructor */
    public KVec4() {

    }

    /** copy constructor */
    public KVec4(KVec4 copy) {
        x = copy.x;
        y = copy.y;
        z = copy.z;
        w = copy.w;
    }

    public KVec4(float scalar) {
        x = scalar;
        y = scalar;
        z = scalar;
        w = scalar;
    }

    public KVec4(float inX, float inY, float inZ, float inW) {
        x = inX;
        y = inY;
        z = inZ;
        w = inW;
    }

    public float x;
    public float y;
    public float z;
    public float w;

    public void set(KVec4 v) {
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
        this.w = v.w;
    }

    public KVec4 add(KVec4 v) {
        return new KVec4(this.x + v.x, this.y + v.y, this.z + v.z, this.w + v.w);
    }

    public KVec4 sub(KVec4 v) {
        return new KVec4(this.x - v.x, this.y - v.y, this.z - v.z, this.w - v.w);
    }

    public KVec4 mul(KVec4 v) {
        return new KVec4(this.x * v.x, this.y * v.y, this.z * v.z, this.w * v.w);
    }

    public KVec4 div(KVec4 v) {
        // @TODO should we try this for divide 0?
        return new KVec4(this.x / v.x, this.y / v.y, this.z / v.z, this.w / v.w);
    }

    public float[] getAsFloatArray() {
        return new float[] {x, y, z, w};
    }

}
