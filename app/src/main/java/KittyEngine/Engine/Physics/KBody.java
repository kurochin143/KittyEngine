package KittyEngine.Engine.Physics;

public class KBody {

    static {
        System.loadLibrary("native-lib");
    }

    public KBody(long nativePtr) {
        m_nativePtr = nativePtr;
    }

    public enum Type {
        STATIC,
        DYNAMIC
    }

    private long m_nativePtr;

    public boolean isActive() {
        return isActive(m_nativePtr);
    }

    public boolean isAwake() {
        return isAwake(m_nativePtr);
    }

    public Type getType() {
        return Type.values()[getType(m_nativePtr)];
    }

    public KBody getNext() {
        long nextNativePtr = getNext(m_nativePtr);
        if (nextNativePtr != 0) {
            return new KBody(nextNativePtr);
        }

        return null;
    }

    public int getPhysicsObjectGameIndex() {
        return getPhysicsObjectGameIndex(m_nativePtr);
    }

    private native boolean isActive(long nativePtr);
    private native boolean isAwake(long nativePtr);
    private native int getType(long nativePtr);
    private native long getNext(long nativePtr);
    private native int getPhysicsObjectGameIndex(long nativePtr);

}
