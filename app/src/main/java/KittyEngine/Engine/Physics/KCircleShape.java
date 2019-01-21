package KittyEngine.Engine.Physics;

public class KCircleShape extends KShape {

    public KCircleShape(float inRadius) {
        super(Type.CIRCLE);
        radius = inRadius;
    }

    public float radius;
}
