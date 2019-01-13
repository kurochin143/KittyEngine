package KittyEngine.Math;

public class KVec4 {

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

    public KVec4 add(KVec4 v) {
        return new KVec4(this.x + v.x, this.y + v.y, this.z + v.z, this.w + v.w);
    }

    public KVec4 min(KVec4 v) {
        return new KVec4(this.x - v.x, this.y - v.y, this.z - v.z, this.w - v.w);
    }

    public KVec4 mul(KVec4 v) {
        return new KVec4(this.x * v.x, this.y * v.y, this.z * v.z, this.w * v.w);
    }

    public KVec4 div(KVec4 v) {
        // @TODO should we try this for divide 0?
        return new KVec4(this.x / v.x, this.y / v.y, this.z / v.z, this.w / v.w);
    }

}
