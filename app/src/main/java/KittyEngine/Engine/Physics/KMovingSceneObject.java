package KittyEngine.Engine.Physics;


import KittyEngine.Engine.KGame;


/**
 * Scene object with velocity, but no collision
 * More efficient than KPhysicsObject if you only need movement
 * */
public class KMovingSceneObject extends KSceneObject {

    public KMovingSceneObject(KGame game) {
        super(game);
    }

    // @TODO
}
