package KittyEngine.Engine.Physics;

import KittyEngine.Engine.KGame;
import KittyEngine.Math.KVec2;

/**
 * Physics world
 * Don't create this
 * Get this from KGame
 * */
public final class KWorld2D {

    public KWorld2D(KGame game) {
        m_nativePtr = createWorld2D();
        m_game = game;
    }

    private long m_nativePtr;
    private KGame m_game;
    private int m_velocityIterations = 10;
    private int m_positionIterations = 8;

    private KBody m_bodyList;

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
            step(m_nativePtr, clampedTimeStep, m_velocityIterations, m_positionIterations);
            remainingDeltaSeconds -= clampedTimeStep;

            // @TODO physics tick. @NOTE: iterate fixtures
            // @TODO since b2World::Step may be called multiple times per game tick,
            // @TODO PPhysics object could have multiple contacts each game tick
            // @TODO should DispatchContact be called here for better accuracy
        }

        // tell sceneObjects their transform was updated
        KBody body = m_bodyList;
        while (body != null) {
            if (body.isActive() && body.isAwake() && body.getType() != KBody.Type.STATIC)
            {
                // update transform of the physics object
                body.physicsObject.updatePhysicsTransform();
            }

            body = body.next;
        }
    }

    public KVec2 getGravity() {
        float[] gravity = getGravity(m_nativePtr);
        return new KVec2(gravity[0], gravity[1]);
    }

    /** Create a brand new body*/
    KBody createBody(KVec2 position,
                     float angle,
                     KVec2 linearVelocity,
                     float angularVelocity,
                     float linearDamping,
                     float angularDamping,
                     boolean bAllowSleep,
                     boolean bFixedRotation,
                     boolean bBullet,
                     KBody.Type bodyType,
                     float gravityScale,
                     KPhysicsObject physicsObject) {
        KBody newBody = new KBody(createBody(m_nativePtr,
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
                bodyType.getValue(),
                gravityScale));
        newBody.linearDamping = linearDamping;
        newBody.angularDamping = angularDamping;
        newBody.bAllowSleep = bAllowSleep;
        newBody.bFixedRotation = bFixedRotation;
        newBody.bBullet = bBullet;
        newBody.bodyType = bodyType;
        newBody.gravityScale = gravityScale;
        newBody.physicsObject = physicsObject;

        addBodyLink(newBody);

        return newBody;
    }

    void recreateBody(KBody body,
                      KVec2 position,
                      float angle,
                     KVec2 linearVelocity,
                     float angularVelocity) {
        body.m_nativePtr = createBody(m_nativePtr,
                position.x,
                position.y,
                angle,
                linearVelocity.x,
                linearVelocity.y,
                angularVelocity,
                body.linearDamping,
                body.angularDamping,
                body.bAllowSleep,
                body.bFixedRotation,
                body.bBullet,
                body.bodyType.getValue(),
                body.gravityScale);

        addBodyLink(body);
    }

    public void destroyBody(KBody body) {
        removeBodyLink(body);

        destroyBody(m_nativePtr, body.m_nativePtr);
    }

    private void addBodyLink(KBody body) {
        body.previous = null;
        body.next = m_bodyList;
        if (m_bodyList != null) {
            m_bodyList.previous = body;
        }
        m_bodyList = body;
    }

    private void removeBodyLink(KBody body) {
        if (body.previous != null) {
            body.previous.next = body.next;
        }

        if (body.next != null) {
            body.next.previous = body.previous;
        }

        if (body == m_bodyList) {
            m_bodyList = body.next;
        }
    }

    private native long createWorld2D();
    private native void step(long nativePtr, float timeStep, int velocityIterations, int positionIterations);
    private native float[] getGravity(long nativePtr);
    private native long createBody(long nativePtr,
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
    private native void destroyBody(long nativePtr, long bodyNativePtr);
    private native long getBodyList(long nativePtr);

}