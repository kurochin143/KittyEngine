package KittyEngine.Graphics;

import KittyEngine.Math.KMat4;
import KittyEngine.Math.KMath;
import KittyEngine.Math.KVec2;
import glm.GLM;

/** world camera */
public class KCamera {

    KCamera(KRenderer renderer) {
        m_renderer = renderer;
    }

    private KRenderer m_renderer;
    private float m_orthoScale = 1.f; // zoom out level
    private KVec2 m_position = new KVec2();
    private float m_angle; // @TODO unimplemented. because camera culling with angle is complicated

    public float getOrthoScale() {
        return m_orthoScale;
    }

    public void setOrthoScale(float newOrthoScale) {
        m_orthoScale = Math.max(KMath.SMALL_NUM, newOrthoScale); // cannot be 0
    }

    public KVec2 getPosition() {
        return new KVec2(m_position);
    }

    public void setPosition(KVec2 newPosition) {
        m_position.set(newPosition);
    }

    public final KMat4 getWorldProjection() {
        float[] worldProjection = m_renderer.getScreenProjection().getArray();
        KVec2 screenDimensionScaled = m_renderer.getScreenDimension().mul(m_orthoScale);
        float scaleX = 2.f / screenDimensionScaled.x;
        float scaleY = 2.f / -screenDimensionScaled.y;
        worldProjection[0 * 4 + 0] = scaleX; // scale x
        worldProjection[1 * 4 + 1] = scaleY; // scale y
        worldProjection[2 * 4 + 2] = -1.f; // far
        worldProjection[3 * 4 + 3] = 1.f; // near

        if (m_angle == 0.f) {
            worldProjection[3 * 4 + 0] = scaleX * -m_position.x; // origin x
            worldProjection[3 * 4 + 1] = scaleY * -m_position.y; // origin y
        }
        else {
            KVec2 cameraAngleV = new KVec2(0.f, 1.f).getRotatedInverse(-m_angle);
            float[] lookAtMatrix = GLM.lookAtRH(
                    new float[] {m_position.x, m_position.y, 0.f},
                    new float[] {m_position.x, m_position.y, -1.f},
                    new float[] {cameraAngleV.x, cameraAngleV.y, 0.f});
            worldProjection = GLM.mat4MulMat4(worldProjection, lookAtMatrix);
        }

        return new KMat4(worldProjection);
    }

    public KVec2 convertScreenToWorld(KVec2 screenPosition) {
        KVec2 screenDimension = m_renderer.getScreenDimension();
        KVec2 worldPosition = screenPosition.sub(screenDimension.div(2.f)).mul(m_orthoScale);
        if (m_angle != 0.f) { // unrotate around the camera
            worldPosition = worldPosition.getRotated(m_angle);
        }

        return worldPosition.add(m_position);
    }

    public KVec2 convertWorldToScreen(KVec2 worldPosition) {
        KVec2 unscaledScreenPosition;
        if (m_angle == 0.f)
        {
            unscaledScreenPosition = worldPosition.sub(m_position);
        }
		else
        {
            KVec2 relativePosition = worldPosition.sub(m_position);
            unscaledScreenPosition = relativePosition.getRotatedInverse(m_angle); // rotate around the camera
        }

        KVec2 screenDimension = m_renderer.getScreenDimension();
        return unscaledScreenPosition.div(m_orthoScale).add(screenDimension.div(2.f)); // scale and move to the origin
    }

}
