package KittyEngine.Engine.Physics;

import KittyEngine.Container.KArrayList;
import KittyEngine.Engine.KGame;
import KittyEngine.Engine.KObject;
import KittyEngine.Math.KMath;
import KittyEngine.Math.KVec2;

/**
 * Object with transform
 * Scene object can be attached with each other
 * they will follow the attachParent's transform
 * */
public class KSceneObject extends KObject {

    public KSceneObject(KGame game) {
        super(game);
    }

    private KVec2 m_relativePosition = new KVec2(0.f);
    private float m_relativeAngle;

    KSceneObject m_attachParent;
    KArrayList<KSceneObject> m_attachChildren = new KArrayList<>();

    public KSceneObject getAttachParent() {
        return m_attachParent;
    }

    public KArrayList<KSceneObject> getAttachChildren() {
        return m_attachChildren;
    }

    // @TODO memberized?
    public KSceneObject getAttachRoot() {
        if (m_attachParent != null) {
            return m_attachParent.getAttachRoot();
        }

        return this;
    }

    public boolean isAttachRoot() {
        return m_attachParent == null;
    }

    public KVec2 getRelativePosition() {
        return m_relativePosition;
    }

    /**
     * set this object's position relative to its parent
     * if it has no parent then it will set the position relative to the world
     * */
    public void setRelativePosition(KVec2 newRelativePosition) {
        m_relativePosition.set(newRelativePosition);

        onPositionUpdated_Internal(getAttachRoot() == this);
    }

    public float getRelativeAngle() {
        return m_relativeAngle;
    }

    /**
     * set this object's angle relative to its parent
     * if it has no parent then it will set the angle relative to the world
     * */
    public void setRelativeAngle(float newRelativeAngle) {
        m_relativeAngle = KMath.clampAxisRad(newRelativeAngle);

        onAngleUpdated_Internal(getAttachRoot() == this);
    }

    public KVec2 getWorldPosition() {
        if (m_attachParent != null) {
            // @TODO cache, cache will be invalidated when onPositionUpdated is called
            return m_relativePosition.getRotated(m_attachParent.getWorldAngle()).add(m_attachParent.getWorldPosition()); // recursive until root
        }

        return new KVec2(m_relativePosition);
    }

    /**
     * set this object's position relative to the world
     * if the object is attached then the relative position to the parent will be affected
     * */
    public void setWorldPosition(KVec2 newWorldPosition) {
        if (m_attachParent != null) {
            m_relativePosition = newWorldPosition.sub(m_attachParent.getWorldPosition());
        }
        else {
            m_relativePosition.set(newWorldPosition);
        }

        onPositionUpdated_Internal(getAttachRoot() == this);
    }

    public float getWorldAngle() {
        if (m_attachParent != null) {
            // @TODO cache, cache will be invalidated when onAngleUpdated is called
            return KMath.clampAxisRad(m_attachParent.getWorldAngle() + m_relativeAngle); // recursive until root
        }

        return m_relativeAngle;
    }

    /**
     * set this object's angle relative to the world
     * if the object is attached then the relative angle to the parent will be affected
     * */
    public void setWorldAngle(float newWorldAngle) {
        if (m_attachParent != null) {
            m_relativeAngle = KMath.clampAxisRad(KMath.clampAxisRad(newWorldAngle) - m_attachParent.getWorldAngle());
        }
        else {
            m_relativeAngle = KMath.clampAxisRad(newWorldAngle);
        }

        onAngleUpdated_Internal(getAttachRoot() == this);
    }

    /**
     * set the attachRoot position
     * */
    public void setRootPosition(KVec2 newPosition) {
        getAttachRoot().setRelativePosition(newPosition);
    }

    /**
     * set the attachRoot angle
     * */
    public void setRootAngle(float newAngle) {
        getAttachRoot().setRelativeAngle(newAngle);
    }

    /**
     * if there's any change in relative or world position
     * @param bFromRoot did it start from the root
     * */
    protected void onPositionUpdated(boolean bFromRoot) {

    }

    /**
     * if there's any change in relative or world angle
     * @param bFromRoot did it start from the root
     * */
    protected void onAngleUpdated(boolean bFromRoot) {

    }

    private void onPositionUpdated_Internal(boolean bRoot) {
        onPositionUpdated(bRoot);

        for (KSceneObject children : m_attachChildren) {
            children.onPositionUpdated_Internal(bRoot);
        }
    }

    private void onAngleUpdated_Internal(boolean bRoot) {
        onAngleUpdated(bRoot);

        for (KSceneObject children : m_attachChildren) {
            children.onAngleUpdated_Internal(bRoot);
        }
    }

    public void attach(KSceneObject parent, KVec2 position, float angle, boolean bRelative) {
        if (parent == null) {
            return;
        }

        // currently attached to someone else
        if (m_attachParent != null) {
            m_attachParent.m_attachChildren.remove(this);
        }

        m_attachParent = parent;
        m_attachParent.m_attachChildren.add(this);

        if (bRelative) {
            m_relativePosition = position;
            m_relativeAngle = angle;
        }
        else {
            m_relativePosition = position.sub(m_attachParent.getWorldPosition());
            m_relativeAngle = KMath.clampAxisRad(KMath.clampAxisRad(angle) - m_attachParent.getWorldAngle());
        }

        onPositionUpdated_Internal(getAttachRoot() == this);

        onAngleUpdated_Internal(getAttachRoot() == this);

        // children don't need to change transform because we're using relative transform

    }

    /** detach from the parent while keeping the world position*/
    public void detach() {
        if (m_attachParent == null) {
            return;
        }

        m_relativePosition = getWorldPosition();
        m_relativeAngle = getRelativeAngle();

        // no need to tell the children the transform was updated
        // because it really didn't change

        m_attachParent.m_attachChildren.remove(this);
        m_attachParent = null;
    }

}
