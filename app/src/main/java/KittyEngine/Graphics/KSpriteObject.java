package KittyEngine.Graphics;

import KittyEngine.Engine.KGame;
import KittyEngine.Engine.Physics.KSceneObject;
import KittyEngine.Math.KVec2;

public class KSpriteObject extends KSceneObject {

    public KSpriteObject(KGame game) {
        super(game);

    }

    private final KSpriteObject m_spriteObjectThis = this;

    public final KSprite sprite = new KSprite() {
        @Override
        public KVec2 getPosition() {
            return m_spriteObjectThis.getWorldPosition();
        }

        @Override
        public float getAngle() {
            return m_spriteObjectThis.getWorldAngle();
        }
    };

    @Override
    protected void onPositionUpdated() {
        sprite.positionUpdated();
    }

    @Override
    protected void onAngleUpdated() {
        sprite.angleUpdated();
    }
}
