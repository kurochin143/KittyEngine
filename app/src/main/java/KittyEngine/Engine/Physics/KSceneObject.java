package KittyEngine.Engine.Physics;

import KittyEngine.Container.KArrayList;
import KittyEngine.Engine.KGame;
import KittyEngine.Engine.KObject;
import KittyEngine.Math.KMath;
import KittyEngine.Math.KVec2;

// @TODO
public class KSceneObject extends KObject {

    public KSceneObject(KGame game) {
        super(game);
    }

    private KVec2 m_relativePosition = new KVec2(0.f);
    private float m_relativeAngle;

    KSceneObject m_attachParent;
    KArrayList<KSceneObject> m_attachChildren = new KArrayList<>();

    public KVec2 getRelativePosition() {
        return m_relativePosition;
    }

    public void setRelativePosition(KVec2 newRelativePosition) {
        m_relativePosition.set(newRelativePosition);

        onPositionUpdated_Internal();
    }

    public float getRelativeAngle() {
        return m_relativeAngle;
    }

    public void setRelativeAngle(float newRelativeAngle) {
        m_relativeAngle = KMath.clampAxisRad(newRelativeAngle);

        onAngleUpdated_Internal();
    }

    public KVec2 getWorldPosition() {
        if (m_attachParent != null) {
            return m_relativePosition.getRotated(m_attachParent.getWorldAngle()).add(m_attachParent.getWorldPosition()); // recursive until root
        }

        return new KVec2(m_relativePosition);
    }

    public void setWorldPosition(KVec2 newWorldPosition) {
        if (m_attachParent != null) {
            m_relativePosition = newWorldPosition.sub(m_attachParent.getWorldPosition());
        }
        else {
            m_relativePosition.set(newWorldPosition);
        }

        onPositionUpdated_Internal();
    }

    public float getWorldAngle() {
        if (m_attachParent != null) {
            return KMath.clampAxisRad(m_attachParent.getWorldAngle() + m_relativeAngle); // recursive until root
        }

        return m_relativeAngle;
    }

    public void setWorldAngle(float newWorldAngle) {
        if (m_attachParent != null) {
            m_relativeAngle = KMath.clampAxisRad(KMath.clampAxisRad(newWorldAngle) - m_attachParent.getWorldAngle());
        }
        else {
            m_relativeAngle = KMath.clampAxisRad(newWorldAngle);
        }

        onAngleUpdated_Internal();
    }

    protected void onPositionUpdated() {

    }

    protected void onAngleUpdated() {

    }

    private void onPositionUpdated_Internal() {
        onPositionUpdated();

        for (KSceneObject children : m_attachChildren) {
            children.onPositionUpdated_Internal();
        }
    }

    private void onAngleUpdated_Internal() {
        onAngleUpdated();

        for (KSceneObject children : m_attachChildren) {
            children.onAngleUpdated_Internal();
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

        onPositionUpdated_Internal();

        onAngleUpdated_Internal();

        // children don't need to change transform because we're using relative transform
    }

    public void detach() {
        if (m_attachParent == null) {
            return;
        }

        m_relativePosition = getWorldPosition();
        m_relativeAngle = getRelativeAngle();

        m_attachParent.m_attachChildren.remove(this);
        m_attachParent = null;
    }

}
