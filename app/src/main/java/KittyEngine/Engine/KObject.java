package KittyEngine.Engine;

public class KObject {

    public KObject(KGame game) {
        m_game = game;
        m_gameObjectsIndex = m_game.m_objects.size();
        m_game.m_objects.add(this);
    }

    private KGame m_game;
    private int m_gameObjectsIndex;
    private int m_updateEnabledIndex = -1;

    public KGame getGame() {
        return m_game;
    }

    /**
     * remove all engine reference of this object
     */
    public void destroy() {
        setUpdateEnabled(false);
        m_game.m_objects.removeSwap(m_gameObjectsIndex);
        if (m_gameObjectsIndex < m_game.m_objects.size()) {
            m_game.m_objects.get(m_gameObjectsIndex).m_gameObjectsIndex = m_gameObjectsIndex;
        }

        m_game = null;
    }

    public void update(float deltaSeconds) {

    }

    public boolean isUpdateEnabled() {
        return m_updateEnabledIndex != -1;
    }

    public void setUpdateEnabled(boolean bUpdateEnabled) {
        if (m_game == null) {
            return;
        }

        boolean bSelfUpdateEnabled = m_updateEnabledIndex != -1;
        if (bSelfUpdateEnabled == bUpdateEnabled) {
            return;
        }

        // allows disabling and enabling update during update

        if (bUpdateEnabled) {
            m_updateEnabledIndex = m_game.m_updateEnabledObjects.size();
            m_game.m_updateEnabledObjects.add(this);
        }
        else {
            m_game.m_updateEnabledObjects.set(m_updateEnabledIndex, null);
            m_updateEnabledIndex = -1;
        }
    }

    public void setMotionEventListener(KInput.MotionEventListener motionEventListener) {
        KEngine.get().getInput().addMotionEventListener(this, motionEventListener);
    }

    public void unsetMotionEventListener() {
        KEngine.get().getInput().removeMotionEventListener(this);
    }

}
