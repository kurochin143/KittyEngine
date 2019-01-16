package KittyEngine.Engine;

import KittyEngine.Container.KArrayList;

/**
 * Extend from this class and add it to the engine and do your deeds in that class
 * Adding this after the has engine started will not call onEngineStarted
 */
public abstract class KGame {

    public KGame() {
    }

    KArrayList<KObject> m_objects = new KArrayList<>();
    KArrayList<KObject> m_updateEnabledObjects = new KArrayList<>();

    public void onEngineStarted() {

    }

    public void update(float deltaSeconds) {
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

}
