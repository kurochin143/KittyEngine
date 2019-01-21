package KittyEngine.Engine.Physics;

import KittyEngine.Math.KVec2;

public class KBody {

    KBody(long nativePtr) {
        m_nativePtr = nativePtr;
    }

    public enum Type {
        STATIC(0),
        KINEMATIC(1),
        DYNAMIC(2);

        private final int m_value;
        Type(int value) {
            m_value = value;
        }

        public int getValue() {
            return m_value;
        }
    }

    long m_nativePtr;
    float linearDamping;
    float angularDamping;
    boolean bAllowSleep;
    boolean bFixedRotation;
    boolean bBullet;
    Type bodyType;
    float gravityScale;
    KPhysicsObject physicsObject;

    KBody previous;
    KBody next;

    public boolean isActive() {
        return isActive(m_nativePtr);
    }

    public boolean isAwake() {
        return isAwake(m_nativePtr);
    }

    public Type getType() {
        switch (getType(m_nativePtr)) {
            case 0: return Type.STATIC;
            case 1: return Type.KINEMATIC;
            case 2: return Type.DYNAMIC;
        }

        return Type.STATIC;
    }

    public KBody getNext() {
        long nextNativePtr = getNext(m_nativePtr);
        if (nextNativePtr != 0) {
            return new KBody(nextNativePtr);
        }

        return null;
    }

    public KFixture createFixture(KShape shape,
                                  KVec2 relativePosition,
                                  float relativeAngle,
                                  int categoryBits,
                                  int maskBits,
                                  int groupIndex,
                                  float density,
                                  float friction,
                                  float restitution,
                                  boolean bSensor,
                                  KPhysicsObject physicsObject) {
        KFixture newFixture = new KFixture(createFixture(
                m_nativePtr,
                shape,
                relativePosition.x,
                relativePosition.y,
                relativeAngle,
                categoryBits,
                maskBits,
                groupIndex,
                density,
                friction,
                restitution,
                bSensor));

        newFixture.categoryBits = categoryBits;
        newFixture.maskBits = maskBits;
        newFixture.groupIndex = groupIndex;
        newFixture.density = density;
        newFixture.friction = friction;
        newFixture.restitution = restitution;
        newFixture.bSensor = bSensor;
        newFixture.physicsObject = physicsObject;

        return newFixture;
    }

    public void recreateFixture(KFixture oldFixture,
                                KShape shape,
                                KVec2 worldPosition,
                                float worldAngle
                                ) {
        oldFixture.m_nativePtr = createFixture(
                m_nativePtr,
                shape,
                worldPosition.x,
                worldPosition.y,
                worldAngle,
                oldFixture.categoryBits,
                oldFixture.maskBits,
                oldFixture.groupIndex,
                oldFixture.density,
                oldFixture.friction,
                oldFixture.restitution,
                oldFixture.bSensor);
    }

    public void destroyFixture(KFixture fixture) {
        destroyFixture(m_nativePtr, fixture.m_nativePtr);
    }

    public void transferFixture(KFixture fixture, KShape shape, KVec2 worldPosition, float worldAngle) {
        fixture.m_nativePtr = transferFixture(m_nativePtr, fixture.m_nativePtr, shape, worldPosition.x, worldPosition.y, worldAngle);
    }

    public KFixture getFixtureList() {
        long fixtureNativePtr = getFixtureList(m_nativePtr);
        if (fixtureNativePtr == 0) {
            return null;
        }
        return new KFixture(fixtureNativePtr);
    }

    public void setTransform(KVec2 position, float angle) {
        setTransform(m_nativePtr, position.x, position.y, angle);
    }

    public KVec2 getPosition() {
        float[] position = getPosition(m_nativePtr);
        return new KVec2(position[0], position[1]);
    }

    public float getAngle() {
        return getAngle(m_nativePtr);
    }

    public void setLinearVelocity(KVec2 newLinearVelocity) {
        setLinearVelocity(m_nativePtr, newLinearVelocity.x, newLinearVelocity.y);
    }

    private native boolean isActive(long nativePtr);
    private native boolean isAwake(long nativePtr);
    private native int getType(long nativePtr);
    private native long getNext(long nativePtr);
    private native long createFixture(long nativePtr,
                                      KShape shape,
                                      float relativePositionX,
                                      float relativePositionY,
                                      float relativeAngle,
                                      int categoryBits,
                                      int maskBits,
                                      int groupIndex,
                                      float density,
                                      float friction,
                                      float restitution,
                                      boolean bSensor);
    private native void destroyFixture(long nativePtr, long fixtureNativePtr);
    private native long transferFixture(long nativePtr,
                                        long fixtureNativePtr,
                                        KShape shape,
                                        float worldPositionX,
                                        float worldPositionY,
                                        float worldAngle);

    private native long getFixtureList(long nativePtr);
    private native void setTransform(long nativePtr, float positionX, float positionY, float angle);
    private native float[] getPosition(long nativePtr);
    private native float getAngle(long nativePtr);
    private native void setLinearVelocity(long nativePtr, float x, float y);

}
