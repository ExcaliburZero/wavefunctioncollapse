package wavefunctioncollapse;

public class WaveFunctionDefinition {
    /**
     * The different types of possible tiles.
     */
    private final Tile[] tiles;

    /**
     * All of the possible configurations for tiles.
     */
    private final TileConfiguration[] tileConfigurations;

    private final int n;
    private final int m;

    public WaveFunctionDefinition(final int n, final int m, final Tile[] tiles, final TileConfiguration[] tileConfigurations) {
        this.n = n;
        this.m = m;
        this.tiles = tiles;
        this.tileConfigurations = tileConfigurations;
    }

    public Tile[] getTiles() {
        return tiles;
    }

    public TileConfiguration[] getTileConfigurations() {
        return tileConfigurations;
    }

    public int getN() {
        return n;
    }

    public int getM() {
        return m;
    }
}
