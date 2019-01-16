package KittyEngine.Collision;

import KittyEngine.Math.KVec2;

public class CollisionStatics {

    public static boolean circleAndCircle(KVec2 position1, float radius1, KVec2 position2, float radius2) {
        float distance = position1.distance(position2);
        return distance <= (radius1 + radius2);
    }
}
