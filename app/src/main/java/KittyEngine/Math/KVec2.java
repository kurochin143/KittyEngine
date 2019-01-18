package KittyEngine.Math;

public class KVec2 {

    /** default constructor */
    public KVec2() {

    }

    /** copy constructor */
    public KVec2(KVec2 copy) {
        x = copy.x;
        y = copy.y;
    }

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

    public void set(KVec2 v) {
        this.x = v.x;
        this.y = v.y;
    }

    public float[] getAsFloatArray() {
        float[] out = new float[2];
        out[0] = x;
        out[1] = y;
        return out;
    }

    public KVec2 add(KVec2 v) {
        return new KVec2(this.x + v.x, this.y + v.y);
    }

    public KVec2 sub(KVec2 v) {
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

    public KVec2 sub(float scalar) {
        return new KVec2(this.x - scalar, this.y - scalar);
    }

    public KVec2 mul(float scalar) {
        return new KVec2(this.x * scalar, this.y * scalar);
    }

    public KVec2 div(float scalar) {
        // @TODO should we try this for divide 0?
        return new KVec2(this.x / scalar, this.y / scalar);
    }

    public void addSet(KVec2 v) {
        this.x += v.x;
        this.y += v.y;
    }

    public void subSet(KVec2 v) {
        this.x -= v.x;
        this.y -= v.y;
    }

    public void mulSet(KVec2 v) {
        this.x *= v.x;
        this.y *= v.y;
    }

    public void divSet(KVec2 v) {
        this.x /= v.x;
        this.y /= v.y;
    }

    public void addSet(float scalar) {
        this.x += scalar;
        this.y += scalar;
    }

    public void subSet(float scalar) {
        this.x -= scalar;
        this.y -= scalar;
    }

    public void mulSet(float scalar) {
        this.x *= scalar;
        this.y *= scalar;
    }

    public void divSet(float scalar) {
        this.x /= scalar;
        this.y /= scalar;
    }

    public KVec2 neg() {
        return new KVec2(-x, -y);
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

    /**
     * Get a rotated vector around the z-axis
     */
    public KVec2 getRotated(float angle) {
        float[] sinCos = KMath.sinCos(angle);
        float sn = sinCos[0];
        float cs = sinCos[1];
        return new KVec2(x*cs - y*sn, x*sn + y*cs);
    }

    /**
     * Get a rotated vector around the z-axis inverse
     */
    public KVec2 getRotatedInverse(float angle) {
        float[] sinCos = KMath.sinCos(angle);
        float sn = sinCos[0];
        float cs = sinCos[1];
        return new KVec2(x*cs + y*sn, -x*sn + y*cs);
    }

    public float lengthSquared() {
        return x*x + y*y;
    }

    public float length() {
        return KMath.sqrt(lengthSquared());
    }

    public float distanceSquared(KVec2 v) {
        return KMath.square(v.x - this.x) + KMath.square(v.y - this.y);
    }

    public float distance(KVec2 v) {
        return KMath.sqrt(distanceSquared(v));
    }

}
