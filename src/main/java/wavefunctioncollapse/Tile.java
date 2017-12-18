package wavefunctioncollapse;

import java.awt.image.BufferedImage;

public class Tile {
    /**
     * The bitmap that defines how the tile appears visually.
     */
    private final BufferedImage bitMap;

    public Tile(final BufferedImage bitMap) {
        this.bitMap = bitMap;
    }

    public BufferedImage getBitMap() {
        return bitMap;
    }
}
