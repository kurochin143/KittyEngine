package KittyEngine.Engine.Physics;

import KittyEngine.Container.KArrayList;
import KittyEngine.Engine.KGame;
import KittyEngine.Math.KVec2;

// @TODO utility functions. get/set mass, density, damping, etc...
/**
 * Scene object with collision and physical properties
 * */
public class KPhysicsObject extends KSceneObject {

    public KPhysicsObject(KGame game, KShape shape) {
        super(game);
        m_bPhysicsEnabled = true;
        m_shape = shape;

        // we're always the root when we are created
        m_body = game.getWorld().createBody(
                KVec2.ZERO,
                0.f,
                KVec2.ZERO,
                0.f,
                0.f,
                0.f,
                true,
                false,
                false,
                KBody.Type.DYNAMIC,
                1.f,
                this);

        m_fixture = m_body.createFixture(
                m_shape,
                new KVec2(0.f), // @TODO make this world
                0.f, // @TODO make this world
                0, // collision with
                0, // collision type
                0,
                1.f,
                0.f,
                1.f,
                false,
                this);
    }

    private boolean m_bPhysicsEnabled;
    private KBody m_body;
    private KFixture m_fixture;
    private KShape m_shape;
    private boolean m_bPhysicsUpdate;

    /** Can only be enabled if the the root is a physicsObject*/
    public void setPhysicsEnabled(boolean bEnabled) {
        if (bEnabled == m_bPhysicsEnabled) {
            return;
        }

        if (bEnabled) {
            m_bPhysicsEnabled = true;

            if (isAttachRoot()) {
                getGame().getWorld().recreateBody(
                        m_body,
                        getRelativePosition(), // actually the world position
                        getRelativeAngle(), // actually the world angle
                        new KVec2(0.f),
                        0.f
                        );
            }

            m_body.recreateFixture(m_fixture, m_shape, getWorldPosition(), getWorldAngle());

        }
        else { // disable physics
            m_bPhysicsEnabled = false;

            if (isAttachRoot()) {
                getGame().getWorld().destroyBody(m_body); // this will destroy all native fixture

                KArrayList<KSceneObject> children = getAttachChildren();
                for (KSceneObject child : children) {
                    // this will only do m_bPhysicsEnabled = false
                    setPhysicsDisabled_Descendants_WithoutDestroyFixture(child);
                }
            }
            else {
                m_body.destroyFixture(m_fixture);
            }
        }
    }

    @Override
    public void attach(KSceneObject parent, KVec2 position, float angle, boolean bRelative) {
        if (parent == null) {
            return;
        }

        KSceneObject newAttachRoot_Scene = parent.getAttachRoot();
        if (newAttachRoot_Scene instanceof KPhysicsObject) { // must be physics object
            KPhysicsObject newAttachRoot_Physics = (KPhysicsObject)newAttachRoot_Scene;
            if (newAttachRoot_Physics.m_bPhysicsEnabled) {
                if (isAttachRoot()) { // we must take care of the body
                    KBody oldBody = m_body;

                    // this will remove all descendants' ref to oldBody
                    // note that this will not set the fixture transform to scene transform
                    // this will happen on super.attach
                    moveToNewBody_Descendants(newAttachRoot_Physics.m_body, this);

                    // destroy the old body's native ptr
                    // must be destroyed after moveToNewBody
                    getGame().getWorld().destroyBody(oldBody);
                }
                else { // we don't need to take care of the body
                    moveToNewBody_Descendants(newAttachRoot_Physics.m_body, this);
                }
            }
            else { // new root has disabled physics
                // destroys body and fixtures
                disablePhysics_OursAndDescendants();

                // only does m_body = newBody
                moveToNewBody_Descendants_NoTransfer(newAttachRoot_Physics.m_body, this);
            }
        }
        else { // new root is not a physics object
            // destroys body and fixtures
            disablePhysics_OursAndDescendants();
        }

        // this will move the fixture's transform to the scene's transform
        super.attach(parent, position, angle, bRelative);
    }

    @Override
    public void detach() {
        if (isAttachRoot()) {
            return;
        }

        KSceneObject oldAttachRoot_Scene = getAttachRoot();
        if (oldAttachRoot_Scene instanceof KPhysicsObject) { // must be physics object
            KPhysicsObject oldAttachRoot_Physics = (KPhysicsObject) oldAttachRoot_Scene;
            if (oldAttachRoot_Physics.m_bPhysicsEnabled) {
                KBody newBody = getGame().getWorld().createBody(
                        getWorldPosition(),
                        getWorldAngle(),
                        new KVec2(0.f),
                        0.f,
                        oldAttachRoot_Physics.m_body.linearDamping,
                        oldAttachRoot_Physics.m_body.angularDamping,
                        oldAttachRoot_Physics.m_body.bAllowSleep,
                        oldAttachRoot_Physics.m_body.bFixedRotation,
                        oldAttachRoot_Physics.m_body.bBullet,
                        oldAttachRoot_Physics.m_body.bodyType,
                        oldAttachRoot_Physics.m_body.gravityScale,
                        this);

                moveToNewBody_Descendants(newBody, this);
            }
            // else. old root has disabled physics. no body
        }
        // else. old root is not a physics object. no body

        super.detach();
    }

    public void setLinearVelocity(final KVec2 newLinearVelocity) {
        m_body.setLinearVelocity(newLinearVelocity);
    }

    void updatePhysicsTransform() {
        m_bPhysicsUpdate = true;
        setRelativePosition(m_body.getPosition());
        setRelativeAngle(m_body.getAngle());
        m_bPhysicsUpdate = false;
    }

    @Override
    protected void onPositionUpdated(boolean bFromRoot) {
        if (m_bPhysicsEnabled) {
            if (isAttachRoot()) {
                if (!m_bPhysicsUpdate) {
                    m_body.setTransform(getRelativePosition(), getRelativeAngle());
                }
            }
            else {
                // do not update if from root because it is already updated in the native body
                if (!bFromRoot) {
                    // physics is enabled, it means that the root is a KPhysicsObject
                    KPhysicsObject attachRoot_Physics = (KPhysicsObject)(getAttachRoot());
                    if (!attachRoot_Physics.m_bPhysicsUpdate) {
                        updateFixture();
                    }
                }
            }
        }
    }

    @Override
    protected void onAngleUpdated(boolean bFromRoot) {
        if (m_bPhysicsEnabled) {
            if (isAttachRoot()) {
                if (!m_bPhysicsUpdate) {
                    m_body.setTransform(getRelativePosition(), getRelativeAngle());
                }
            }
            else {
                // do not update if from root because it is already updated in the native body
                if (!bFromRoot) {
                    // physics is enabled, it means that the root is a KPhysicsObject
                    KPhysicsObject attachRoot_Physics = (KPhysicsObject)(getAttachRoot());
                    if (!attachRoot_Physics.m_bPhysicsUpdate) {
                        updateFixture();
                    }
                }
            }
        }
    }

    /** Update the fixture transform, using the current KSceneObject transform*/
    private void updateFixture() {
        m_body.transferFixture(
                m_fixture,
                m_shape,
                getWorldPosition(),
                getWorldAngle());
    }

    private static void setPhysicsDisabled_Descendants_WithoutDestroyFixture(KSceneObject from) {
        if (from instanceof KPhysicsObject) {
            ((KPhysicsObject)from).m_bPhysicsEnabled = false;
        }

        KArrayList<KSceneObject> children = from.getAttachChildren();
        for (KSceneObject child : children) {
            setPhysicsDisabled_Descendants_WithoutDestroyFixture(child);
        }
    }

    private void disablePhysics_OursAndDescendants() {
        if (isAttachRoot()) {
            setPhysicsEnabled(false); // this will destroy the native body and all native fixtures
        }
        else {
            // this will destroy native fixtures from this object and below
            KArrayList<KSceneObject> children = getAttachChildren();
            for (KSceneObject child : children) {
                setPhysicsDisabled_Descendants_WithDestroyFixture(child);
            }
        }
    }

    /** @WARNING must not be called from root*/
    private static void setPhysicsDisabled_Descendants_WithDestroyFixture(KSceneObject from) {
        if (from instanceof KPhysicsObject) {
            ((KPhysicsObject)from).setPhysicsEnabled(false);
        }

        KArrayList<KSceneObject> children = from.getAttachChildren();
        for (KSceneObject child : children) {
            setPhysicsDisabled_Descendants_WithDestroyFixture(child);
        }
    }

    private static void moveToNewBody_Descendants(KBody newBody, KSceneObject from) {
        if (from instanceof KPhysicsObject) {
            KPhysicsObject physicsObject = ((KPhysicsObject)from);
            if (physicsObject.m_bPhysicsEnabled) {
                physicsObject.m_body = newBody;
                physicsObject.m_body.transferFixture(
                        physicsObject.m_fixture,
                        physicsObject.m_shape,
                        physicsObject.getWorldPosition(),
                        physicsObject.getWorldAngle());
            }
        }

        KArrayList<KSceneObject> children = from.getAttachChildren();
        for (KSceneObject child : children) {
            moveToNewBody_Descendants(newBody, child);
        }
    }

    private static void moveToNewBody_Descendants_NoTransfer(KBody newBody, KSceneObject from) {
        if (from instanceof KPhysicsObject) {
            KPhysicsObject physicsObject = ((KPhysicsObject)from);
            if (physicsObject.m_bPhysicsEnabled) {
                physicsObject.m_body = newBody;
            }
        }

        KArrayList<KSceneObject> children = from.getAttachChildren();
        for (KSceneObject child : children) {
            moveToNewBody_Descendants_NoTransfer(newBody, child);
        }
    }
}
