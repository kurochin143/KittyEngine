package KittyEngine.Math;

public class KIntVec2 {

    public KIntVec2(int scalar) {
        x = scalar;
        y = scalar;
    }

    public KIntVec2(int inX, int inY) {
        x = inX;
        y = inY;
    }

    public int x;
    public int y;

    public KIntVec2 add(KIntVec2 v) {
        return new KIntVec2(this.x + v.x, this.y + v.y);
    }

    public KIntVec2 min(KIntVec2 v) {
        return new KIntVec2(this.x - v.x, this.y - v.y);
    }

    public KIntVec2 mul(KIntVec2 v) {
        return new KIntVec2(this.x * v.x, this.y * v.y);
    }

    public KIntVec2 div(KIntVec2 v) {
        return new KIntVec2(this.x / v.x, this.y / v.y);
    }

}
