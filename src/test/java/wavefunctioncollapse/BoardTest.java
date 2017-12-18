package wavefunctioncollapse;

import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


class BoardTest {
    /**
     * Returns a small dummy image to use in tests.
     *
     * @return A dummy BufferedImage.
     */
    private BufferedImage getDummyImage() {
        final BufferedImage image = new BufferedImage(5, 5, BufferedImage.TYPE_INT_RGB);

        image.setRGB(0, 0, 200);
        image.setRGB(1, 2, 100);
        image.setRGB(4, 3, 500);

        return image;
    }

    /**
     * Attempting to actualize all the tiles in a non actualized board without
     * contradictions should return true for all the tiles and then return
     * false once all of the tiles have been actualized.
     */
    @Test
    void actualizeNextTileNoneActualized() {
        final int width = 10;
        final int height = 10;
        final int seed = 42;

        final int n = 1;
        final int m = 1;

        final Tile[] tiles = new Tile[] {
                new Tile(getDummyImage()),
                new Tile(getDummyImage())
        };

        final TileConfiguration[] tileConfigurations = new TileConfiguration[] {
                new TileConfiguration(0, (bp) -> true),
                new TileConfiguration(1, (bp) -> true)
        };

        final WaveFunctionDefinition waveDefinition =
                new WaveFunctionDefinition(n, m, tiles, tileConfigurations);

        final Board board = new Board(width, height, waveDefinition, seed);

        final int numTotalTiles = width * height;
        for (int i = 0; i < numTotalTiles; i++) {
            assertEquals(i, board.getNumActualized());
            assertTrue(board.actualizeNextTile());
        }
        assertEquals(numTotalTiles, board.getNumActualized());
        assertFalse(board.actualizeNextTile());
    }

    /**
     * Attempting to actualize a tile in a board with no actualized tiles and
     * only unsatisfiable tile configurations should raise a
     * ContradictoryBoardStateException.
     */
    @Test
    void actualizeNextTileContradiction() {
        final int width = 10;
        final int height = 10;
        final int seed = 42;

        final int n = 1;
        final int m = 1;

        final Tile[] tiles = new Tile[] {
                new Tile(getDummyImage()),
                new Tile(getDummyImage())
        };

        final TileConfiguration[] tileConfigurations = new TileConfiguration[] {
                new TileConfiguration(0, (bp) -> false),
                new TileConfiguration(1, (bp) -> false)
        };

        final WaveFunctionDefinition waveDefinition =
                new WaveFunctionDefinition(n, m, tiles, tileConfigurations);

        final Board board = new Board(width, height, waveDefinition, seed);

        assertThrows(ContradictoryBoardStateException.class, board::actualizeNextTile);
    }

    /**
     * Attempting to actualize a tile in a fully actualized board should return
     * false to indicate that there are no tiles left to actualize.
     */
    @Test
    void actualizeNextTileAllActualized() {
        final int width = 10;
        final int height = 10;
        final int seed = 42;

        final int n = 1;
        final int m = 1;

        final Tile[] tiles = new Tile[] {
                new Tile(getDummyImage())
        };

        final TileConfiguration[] tileConfigurations = new TileConfiguration[] {
                new TileConfiguration(0, (bp) -> true)
        };

        final WaveFunctionDefinition waveDefinition =
                new WaveFunctionDefinition(n, m, tiles, tileConfigurations);

        final Board board = new Board(width, height, waveDefinition, seed);

        final int numTotalTiles = width * height;
        assertEquals(numTotalTiles, board.getNumActualized());
        assertFalse(board.actualizeNextTile());
    }

    /**
     * Attempting to get the state of an initial board with no tiles actualized
     * should always report that the tiles have no state.
     */
    @Test
    void getTileStateNoneActualized() {
        final int width = 10;
        final int height = 10;
        final int seed = 42;

        final int n = 1;
        final int m = 1;

        final Tile[] tiles = new Tile[] {
                new Tile(getDummyImage()),
                new Tile(getDummyImage())
        };

        final TileConfiguration[] tileConfigurations = new TileConfiguration[] {
                new TileConfiguration(0, (bp) -> true),
                new TileConfiguration(1, (bp) -> true)
        };

        final WaveFunctionDefinition waveDefinition =
                new WaveFunctionDefinition(n, m, tiles, tileConfigurations);

        final Board board = new Board(width, height, waveDefinition, seed);

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                assertEquals(Optional.empty(), board.getTileState(i, j));
            }
        }
    }

    /**
     * Attempting to get the state of an initial board with all tiles
     * actualized should always report that the tiles have the only
     * possible state.
     */
    @Test
    void getTileStateAllActualized() {
        final int width = 10;
        final int height = 10;
        final int seed = 42;

        final int n = 1;
        final int m = 1;

        final Tile[] tiles = new Tile[] {
                new Tile(getDummyImage())
        };

        final TileConfiguration[] tileConfigurations = new TileConfiguration[] {
                new TileConfiguration(0, (bp) -> true)
        };

        final WaveFunctionDefinition waveDefinition =
                new WaveFunctionDefinition(n, m, tiles, tileConfigurations);

        final Board board = new Board(width, height, waveDefinition, seed);

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                assertEquals(Optional.of(0), board.getTileState(i, j));
            }
        }
    }

    @Test
    void getImageActualized() {
        final int width = 4;
        final int height = 4;
        final int seed = 42;

        final int n = 1;
        final int m = 1;

        final BufferedImage dummy = getDummyImage();
        final Tile[] tiles = new Tile[] {
                new Tile(dummy)
        };

        final TileConfiguration[] tileConfigurations = new TileConfiguration[] {
                new TileConfiguration(0, (bp) -> true)
        };

        final WaveFunctionDefinition waveDefinition =
                new WaveFunctionDefinition(n, m, tiles, tileConfigurations);

        final Board board = new Board(width, height, waveDefinition, seed);

        final BufferedImage image = board.getImage();

        final int tileWidth = dummy.getWidth();
        final int tileHeight = dummy.getHeight();
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {

                for (int k = 0; k < tileWidth; k++) {
                    for (int l = 0; l < tileHeight; l++) {
                        final int row = i * tileHeight + k;
                        final int column = j * tileWidth + l;

                        final int actual = image.getRGB(row, column);
                        final int expected = dummy.getRGB(k, l);

                        assertEquals(expected, actual);
                    }
                }
            }
        }
    }
}
