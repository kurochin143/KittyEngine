package KittyEngine.Math;

public class KVec2 {

    public KVec2(float scalar) {
        x = scalar;
        y = scalar;
    }

    public KVec2(float inX, float inY) {
        x = inX;
        y = inY;
    }

    public float x;
    public float y;

    public static final KVec2 ZERO = new KVec2(0.f);

    public KVec2 add(KVec2 v) {
        return new KVec2(this.x + v.x, this.y + v.y);
    }

    public KVec2 min(KVec2 v) {
        return new KVec2(this.x - v.x, this.y - v.y);
    }

    public KVec2 mul(KVec2 v) {
        return new KVec2(this.x * v.x, this.y * v.y);
    }

    public KVec2 div(KVec2 v) {
        // @TODO should we try this for divide 0?
        return new KVec2(this.x / v.x, this.y / v.y);
    }

    public KVec2 add(float scalar) {
        return new KVec2(this.x + scalar, this.y + scalar);
    }

    public KVec2 min(float scalar) {
        return new KVec2(this.x - scalar, this.y - scalar);
    }

    public KVec2 mul(float scalar) {
        return new KVec2(this.x * scalar, this.y * scalar);
    }

    public KVec2 div(float scalar) {
        // @TODO should we try this for divide 0?
        return new KVec2(this.x / scalar, this.y / scalar);
    }

    public float[] getAsFloatArray() {
        float[] out = new float[2];
        out[0] = x;
        out[1] = y;
        return out;
    }

    public KVec2 getNormalized() {
        float squareSum = x*x + y*y;

        if (squareSum == 1.f)
        {
            return this;
        }
        else if (squareSum < KMath.SMALL_NUM)
        {
            return KVec2.ZERO;
        }

        float scale = KMath.invSqrt(squareSum);
        return new KVec2(x*scale, y*scale);
    }

}
