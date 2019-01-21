package KittyEngine.Engine.Physics;

public class KFixture {

    KFixture(long nativePtr) {
        m_nativePtr = nativePtr;
    }

    long m_nativePtr;
    int categoryBits;
    int maskBits;
    int groupIndex;
    float density;
    float friction;
    float restitution;
    boolean bSensor;
    KPhysicsObject physicsObject;

    public KFixture getNext() {
        long nextNativePtr = getNext(m_nativePtr);
        if (nextNativePtr == 0) {
            return null;
        }

        return new KFixture(nextNativePtr);
    }

    private native long getNext(long nativePtr);

}
