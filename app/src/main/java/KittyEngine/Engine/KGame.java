package KittyEngine.Engine;

import KittyEngine.Container.KArrayList;
import KittyEngine.Engine.Physics.KWorld2D;

/**
 * Extend from this class and add it to the engine and do your deeds in that class
 * Adding this after the has engine started will not call onEngineStarted
 * This is usually the context of the game
 */
public abstract class KGame {
    public KGame() {
    }

    KArrayList<KObject> m_objects = new KArrayList<>();
    KArrayList<KObject> m_updateEnabledObjects = new KArrayList<>();

    private KWorld2D m_world2D = new KWorld2D(this);

    public void onEngineStarted() {

    }

    public void update(float deltaSeconds) {
        m_world2D.updatePhysics(deltaSeconds);

        updateObjects(deltaSeconds);
    }

    private void updateObjects(float deltaSeconds) {
        for (int i = m_updateEnabledObjects.lastIndex(); i != -1; --i) {
            KObject object = m_objects.get(i);
            if (object != null) {
                object.update(deltaSeconds);
            }
            else {
                m_objects.removeSwap(i);
            }
        }
    }

    /** dont modify*/
    public KArrayList<KObject> getObjects() {
        return m_objects;
    }

    public KWorld2D getWorld() {
        return m_world2D;
    }

}
