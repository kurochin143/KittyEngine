package KittyEngine.Math;

import KittyEngine.Container.KArrayList;

public class KAABB {

    public KAABB() {
    }

    public KAABB(KVec2 inUpperBound, KVec2 inLowerBound) {
        upperBound.set(inUpperBound);
        lowerBound.set(inLowerBound);
    }

    public KAABB(KVec2 v) {
        upperBound.set(v);
        lowerBound.set(v);
    }

    public KAABB(KArrayList<KVec2> inArrayList) {
        for (int i = 0; i < inArrayList.size(); ++i) {
            this.addSet(inArrayList.get(i));
        }
    }

    public KVec2 upperBound = new KVec2();
    public KVec2 lowerBound = new KVec2();

    public void set(KVec2 v) {
        upperBound.set(v);
        lowerBound.set(v);
    }

    public KVec2 getCenter() { return upperBound.add(lowerBound).mul(0.5f); }
    public KVec2 getExtent() { return upperBound.sub(lowerBound).mul(0.5f); }
    public KVec2 getSize() { return upperBound.sub(lowerBound); }
    public float getExternalRadius() { return getExtent().length(); }
    public float getExternalDiameter() { return getSize().length(); }
    public float getXLength() { return upperBound.x - lowerBound.x; }
    public float getYLength() { return upperBound.y - lowerBound.y; }

    public void addSet(KVec2 v) {
        lowerBound.x = Math.min(lowerBound.x, v.x);
        lowerBound.y = Math.min(lowerBound.y, v.y);

        upperBound.x = Math.max(upperBound.x, v.x);
        upperBound.y = Math.max(upperBound.y, v.y);
    }
}
