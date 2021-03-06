package KittyEngine.Graphics;

import android.util.Log;

import KittyEngine.Container.KArrayList;
import KittyEngine.Engine.KEngine;
import KittyEngine.Math.KAABB;
import KittyEngine.Math.KVec2;
import KittyEngine.Math.KVec4;

// @TODO sparse grid
// @TODO screen space
public abstract class KSprite {

    public KSprite() {
        m_spriteRenderer = KEngine.get().getGLSurfaceView().getRenderer().getSpriteRenderer();

        addToSpriteArray();

        ++m_spriteRenderer.m_spritesNum;
    }

    public void destroy() {
        if (m_spriteSparseGrid != null) {
            removeFromSparseGrid();
        }
        else {
            removeFromSpriteArray();
        }

        --m_spriteRenderer.m_spritesNum;
    }

    private KSpriteRenderer m_spriteRenderer;
    private int m_SpriteArrayIndex;

    private boolean m_bHidden;
    private boolean m_bShouldDrawScreenBounds;
    private boolean m_bInScreenSpace;
    private KVec2 m_size = new KVec2(100.f);
    private KVec4 m_color = new KVec4(1.f);
    private KTexture m_texture;
    private KVec2[] m_spriteUV = {new KVec2(0.f, 0.f), new KVec2(1.f, 1.f), new KVec2(0.f, 1.f), new KVec2(1.f, 0.f)};
    private KVec2[] m_textureUV = {new KVec2(0.f, 0.f), new KVec2(1.f, 1.f), new KVec2(0.f, 1.f), new KVec2(1.f, 0.f)};
    private int m_layer;

    private KSpriteSparseGrid m_spriteSparseGrid;

    public boolean isHidden() {
        return m_bHidden;
    }

    public void setHidden(boolean bHidden) {
        m_bHidden = bHidden;
    }

    public boolean shouldDrawScreenBounds() {
        return m_bShouldDrawScreenBounds;
    }

    public void setShouldDrawScreenBounds(boolean bShouldDrawScreenBounds) {
        m_bShouldDrawScreenBounds = bShouldDrawScreenBounds;
    }

    public abstract KVec2 getPosition();

    public void positionUpdated() {
        if (isUsingSparseGrid()) {
            removeFromSparseGrid();
            addToSparseGrid();
        }
    }

    public abstract float getAngle();

    public void angleUpdated() {
        if (isUsingSparseGrid()) {
            removeFromSparseGrid();
            addToSparseGrid();
        }
    }

    public KVec2 getSize() {
        return new KVec2(m_size);
    }

    public void setSize(KVec2 newSize) {
        m_size.set(newSize);
    }

    public KVec4 getColor() {
        return new KVec4(m_color);
    }

    public void setColor(KVec4 newColor) {
        m_color.set(newColor);
    }

    public KTexture getTexture() {
        return m_texture;
    }

    public void setTexture(KTexture newTexture) {
        m_texture = newTexture;
        setUV(m_spriteUV);
    }

    /**
     * 0 = TL
     * 1 = BR
     * 2 = BL
     * 3 = TR
     * */
    public void setUV(KVec2[] newUV) {
        for (int i = 0; i < 4; ++i) {
            KVec2 uv = newUV[i];
            if (uv == null) {
                Log.w("KittyLog", "setUV failed KVec2 object");
                return;
            }

            if (uv.x < -1.f || uv.x > 1.f || uv.y < -1.f || uv.y > 1.f) {
                Log.w("KittyLog", "setUV failed uv exceeded -1.f or 1.f");
                return;
            }
            m_spriteUV[i].set(uv);
        }

        if (m_texture != null) {
            KVec2 textureUV = m_texture.getUV();
            m_textureUV[0] = m_spriteUV[0].mul(textureUV);
            m_textureUV[1] = m_spriteUV[1].mul(textureUV);
            m_textureUV[2] = m_spriteUV[2].mul(textureUV);
            m_textureUV[3] = m_spriteUV[3].mul(textureUV);
        }
    }

    public KVec2[] getSpriteUV() {
        return m_spriteUV.clone();
    }

    KVec2[] getTextureUV() {
        return m_textureUV;
    }

    public int getLayer() {
        return m_layer;
    }

    public void setLayer(int newLayer) {
        // @TODO
        if (m_layer == newLayer) {
            return;
        }

        if (isUsingSparseGrid()) {
            removeFromSparseGrid();
            m_layer = newLayer;
            addToSparseGrid();
        }
        else {
            removeFromSpriteArray();
            m_layer = newLayer;
            addToSpriteArray();
        }
    }

    /**
     * @TODO: use dynamic tree rather than sparse grid?
     * Sparse grid will decrease the load of the culling process
     * The sparse grid can greatly improve performance
     * for very large amount(millions to billions) of sprites that are spread out across the map
     * But this may increase the overhead of updating(sprites that move) sprite's transform depending on its size,
     * depending on how many times bigger it is on SPARSE_GRID_SPRITE_NODE_SIZE
     * A few(~10) times bigger won't be a problem. Sprite size is rarely 2x the size anyway
     * This can also consume a lot of memory depending on the sprite position @see: PSparseGrid_Sprite header for explanation
     * This is a must for static sprites. For example: buildings, trees
     *
     * Sprite array will check every sprite when culling
     * @param bSparseGrid false to add this sprite to spriteArray
     */
    public void setContainer(boolean bSparseGrid) {
        if (isUsingSparseGrid() && bSparseGrid) {
            return;
        }

        if (bSparseGrid) {
            removeFromSpriteArray();
            addToSparseGrid();
        }
        else {
            removeFromSparseGrid();
            addToSpriteArray();
        }
    }

    public boolean isUsingSparseGrid() {
        return m_spriteSparseGrid != null;
    }

    private void addToSpriteArray() {
        KSpriteRenderer.SpriteContainer spriteContainer = m_spriteRenderer.m_spriteLayerMap.get(m_layer);
        if (spriteContainer == null) { // create new layer entry
            spriteContainer = new KSpriteRenderer.SpriteContainer();
            m_spriteRenderer.m_spriteLayerMap.put(m_layer, spriteContainer);
        }

        if (spriteContainer.sprites == null) { // create new sprite array
            spriteContainer.sprites = new KArrayList<>();
        }

        m_SpriteArrayIndex = spriteContainer.sprites.size();
        spriteContainer.sprites.add(this);
    }

    private void removeFromSpriteArray() {
        KSpriteRenderer.SpriteContainer spriteContainer = m_spriteRenderer.m_spriteLayerMap.get(m_layer);
        spriteContainer.sprites.removeSwap(m_SpriteArrayIndex);
        if (m_SpriteArrayIndex < spriteContainer.sprites.size()) {
            spriteContainer.sprites.get(m_SpriteArrayIndex).m_SpriteArrayIndex = m_SpriteArrayIndex;
        }
        else if (spriteContainer.spriteSparseGrid == null) { // nothing is in this layer anymore
           m_spriteRenderer.m_spriteLayerMap.remove(m_layer);
        }
    }

    private void addToSparseGrid() {

    }

    private void removeFromSparseGrid() {

    }

    public KAABB computeScreenAABB() {
        if (m_bInScreenSpace)
        {
            return computeWorldAABB();
        }

        KVec2 spritePosition = getPosition(); // world position
        float spriteAngle = getAngle(); // world angle
        KVec2 sizeHalf = m_size.mul(0.5f); // world size half

        KAABB outAABB = new KAABB();
        KCamera camera = m_spriteRenderer.m_renderer.getCamera();

        KVec2 spriteScreenPosition = camera.convertWorldToScreen(spritePosition);
        KVec2 sizeHalfScaled = sizeHalf.div(camera.getOrthoScale()); // sprite bounds get smaller when the camera zooms out vice versa
        if (spriteAngle == 0.f || spriteAngle == 180.f)
        {
            outAABB.lowerBound = sizeHalfScaled.neg().add(spriteScreenPosition);
            outAABB.upperBound = sizeHalfScaled.add(spriteScreenPosition);
        }
        else if (spriteAngle == 90.f || spriteAngle == 270.f)
        {
            KVec2 relativeUpperBound = new KVec2(sizeHalfScaled.y, sizeHalfScaled.x);
            outAABB.lowerBound = relativeUpperBound.neg().add(spriteScreenPosition);
            outAABB.upperBound = relativeUpperBound.add(spriteScreenPosition);
        }
        else
        {
            // set the lower and upper bound to the screen aabb center so that adding vector will expand the bounds outward
            outAABB.set(spriteScreenPosition);
            KVec2 relativeVertex_BR = sizeHalf.getRotated(spriteAngle);
            KVec2 worldVertex_BR = relativeVertex_BR.add(spritePosition);
            KVec2 vertex_BR_Screen = camera.convertWorldToScreen(worldVertex_BR);
            KVec2 relativeVertex_BR_Screen = vertex_BR_Screen.sub(spriteScreenPosition);

            outAABB.addSet(relativeVertex_BR_Screen.neg().add(spriteScreenPosition)); // top left
            outAABB.addSet(vertex_BR_Screen); // bottom right
            if (m_size.x == m_size.y) // square
            {
                outAABB.addSet(new KVec2(relativeVertex_BR_Screen.y, -relativeVertex_BR_Screen.x).add(spriteScreenPosition)); // top right
                outAABB.addSet(new KVec2(-relativeVertex_BR_Screen.y, relativeVertex_BR_Screen.x).add(spriteScreenPosition)); // bottom left
            }
            else
            {
                KVec2 relativeVertex_BL = new KVec2(-sizeHalf.x, sizeHalf.y).getRotated(spriteAngle);
                KVec2 worldVertex_BL = relativeVertex_BL.add(spritePosition);
                KVec2 vertex_BL_Screen = camera.convertWorldToScreen(worldVertex_BL);
                KVec2 relativeVertex_BL_Screen = vertex_BL_Screen.sub(spriteScreenPosition);

                outAABB.addSet(relativeVertex_BL_Screen.neg().add(spriteScreenPosition)); // top right
                outAABB.addSet(vertex_BL_Screen); // bottom left
            }
        }

        return outAABB;
    }

    public KAABB computeWorldAABB() {
        // @TODO
        return new KAABB();
    }

}
