package KittyEngine.Engine.Physics;

/** Contains information about shape*/
public abstract class KShape {

    KShape(Type type) {
        m_type = type;
    }

    public enum Type {
        CIRCLE(0),
        POLYGON(1);

        Type(int value) {
            m_value = value;
        }

        private int m_value;

        public int getValue() {
            return m_value;
        }
    }

    public Type m_type;
    public int m_i;

    public Type getType() {
        return m_type;
    }

}
