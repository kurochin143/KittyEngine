package KittyEngine.Object;

import KittyEngine.Engine.KGame;
import KittyEngine.Engine.KObject;
import KittyEngine.Math.KVec2;

// @TODO
public class KSceneObject extends KObject {

    public KSceneObject(KGame game) {
        super(game);
    }

    private KVec2 m_position;
    private float m_angle;

    public KVec2 getPosition() {
        return m_position;
    }

    public float getAngle() {
        return m_angle;
    }
}
