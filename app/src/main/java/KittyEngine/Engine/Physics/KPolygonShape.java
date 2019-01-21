package KittyEngine.Engine.Physics;

import android.util.Log;

import KittyEngine.Math.KVec2;

public class KPolygonShape extends KShape {

    public KPolygonShape() {
        super(Type.POLYGON);

    }

    public static final int MIN_POLYGON_VERTICES = 3;
    public static final int MAX_POLYGON_VERTICES = getMaxPolygonVertices();

    private KVec2[] m_vertices = new KVec2[3]; // if failed to set, we still have 3

    /**
     * size cannot be below MIN_POLYGON_VERTICES
     * any size that exceeded MAX_POLYGON_VERTICES will not be added
     * */
    public void setVertices(KVec2[] inVertices) {
        int minedSize = Math.min(inVertices.length, MAX_POLYGON_VERTICES);

        if (minedSize < MIN_POLYGON_VERTICES) {
            Log.w("KittyLog", String.format("Failed to set polygon vertices, inVertices size %d is below min size %d", inVertices.length, MIN_POLYGON_VERTICES));
            return;
        }

        m_vertices = new KVec2[minedSize];
        for (int i = 0; i < minedSize; ++i) {
            m_vertices[i] = inVertices[i];
        }
    }

    private static native int getMaxPolygonVertices();

}
