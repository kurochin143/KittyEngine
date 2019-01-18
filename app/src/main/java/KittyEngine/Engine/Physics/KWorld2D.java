package KittyEngine.Engine.Physics;

import KittyEngine.Engine.KGame;
import KittyEngine.Math.KVec2;

/** Physics world*/
public class KWorld2D {

    static {
        System.loadLibrary("native-lib");
    }

    public KWorld2D(KGame game) {
        m_nativePtr = createWorld2D();
        m_game = game;
    }

    private long m_nativePtr;
    private KGame m_game;
    private int m_VelocityIterations = 10;
    private int m_PositionIterations = 8;

    public void updatePhysics(float deltaSeconds) {

        /*
         * aka max physics delta seconds,
         * the lower the better the physics but the more expensive it is
         */
        final float fixedPhysicsTimeStep = 1.f / 120.f;

        float remainingDeltaSeconds = deltaSeconds;
        while (remainingDeltaSeconds > 0.f)
        {
            float clampedTimeStep = Math.min(fixedPhysicsTimeStep, remainingDeltaSeconds);
            step(m_nativePtr, clampedTimeStep, m_VelocityIterations, m_PositionIterations);
            remainingDeltaSeconds -= clampedTimeStep;

            // @TODO physics tick. @NOTE: iterate fixtures
            // @TODO since b2World::Step may be called multiple times per game tick,
            // @TODO PPhysics object could have multiple contacts each game tick
            // @TODO should DispatchContact be called here for better accuracy
        }

        // tell sceneObjects their transform was updated
        long bodyListNativePtr = getBodyList(m_nativePtr);
        if (bodyListNativePtr != 0) {
            KBody body = new KBody(bodyListNativePtr);
            do {
                if (body.isActive() && body.isAwake() && body.getType() != KBody.Type.STATIC)
                {
                    int physicsObjectGameIndex = body.getPhysicsObjectGameIndex();
                    KPhysicsObject physicsObject = (KPhysicsObject)m_game.getObjects().get(physicsObjectGameIndex);
                    physicsObject.physicsTransformUpdate(); // update the transform of scene object
                }

                body = body.getNext();
            } while (body != null);
        }
    }

    public KVec2 getGravity() {
        float[] gravity = getGravity(m_nativePtr);
        return new KVec2(gravity[0], gravity[1]);
    }

    KBody createBody(int physicsObjectGameIndex,
                     KVec2 position,
                     float angle,
                     KVec2 linearVelocity,
                     float angularVelocity,
                     float linearDamping,
                     float angularDamping,
                     boolean bAllowSleep,
                     boolean bFixedRotation,
                     boolean bBullet,
                     int bodyType,
                     float gravityScale) {
        return new KBody(createBody(m_nativePtr,
                physicsObjectGameIndex,
                position.x,
                position.y,
                angle,
                linearVelocity.x,
                linearVelocity.y,
                angularVelocity,
                linearDamping,
                angularDamping,
                bAllowSleep,
                bFixedRotation,
                bBullet,
                bodyType,
                gravityScale));
    }

    private native long createWorld2D();
    private native void step(long nativePtr, float timeStep, int velocityIterations, int positionIterations);
    private native float[] getGravity(long nativePtr);
    private native long getBodyList(long nativePtr);
    private native long createBody(long worldPtr, int physicsObjectGameIndex,
                                   float positionX,
                                   float positionY,
                                   float angle,
                                   float linearVelocityX,
                                   float linearVelocityY,
                                   float angularVelocity,
                                   float linearDamping,
                                   float angularDamping,
                                   boolean bAllowSleep,
                                   boolean bFixedRotation,
                                   boolean bBullet,
                                   int bodyType,
                                   float gravityScale);

}