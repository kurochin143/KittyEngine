package KittyEngine.Graphics;

import KittyEngine.Container.KArrayList;

// @TODO resize the grid in the sprite renderer or in remove?

public class KSpriteSparseGrid {

    public class Grid {

        public class Node {
            public KArrayList<KSprite> sprites;
        }

        KArrayList<KArrayList<Node>> grid;
    }

    private Grid[] m_grids = new Grid[4];
    private int m_spriteNum;

    void add(KSprite sprite) {

    }

    void remove(KSprite sprite) {

    }

}
