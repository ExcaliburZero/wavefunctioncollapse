package wavefunctioncollapse;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Random;

public class Board {
    /**
     * The wave function states of all of the tiles on the board. Boolean
     * values of true for a given (row, column, state) position indicate
     * that the state is possible for that given tile.
     */
    final boolean[][][] tileStates;

    final Random rand;
    final WaveFunctionDefinition waveDefinition;

    public Board(final int width, final int height,
                 final WaveFunctionDefinition waveDefinition, long seed) {
        final int numTiles = waveDefinition.getTiles().length;

        rand = new Random(seed);
        tileStates = new boolean[width][height][numTiles];
        this.waveDefinition = waveDefinition;

        initializeTileStates();
    }

    private static class Point {
        final int row;
        final int column;

        Point(final int row, final int column) {
            this.row = row;
            this.column = column;
        }
    }

    /**
     * Initializes all of the tiles to be able to be any possible tile, as no
     * tiles have yet been actualized, so all configurations are applicable.
     */
    private void initializeTileStates() {
        final int width = tileStates.length;
        final int height = tileStates[0].length;
        final int numTiles = tileStates[0][0].length;

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                for (int k = 0; k < numTiles; k++) {
                    tileStates[i][j][k] = true;
                }
            }
        }
    }

    public boolean actualizeNextTile() {
        final int width = tileStates.length;
        final int height = tileStates[0].length;
        final int numTiles = tileStates[0][0].length;

        boolean anyNonActualized = false;
        int minNumStatesRemaining = numTiles;
        ArrayList<Point> minStateTiles = new ArrayList<>();

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                // Count how many possible states the tile has
                int numRemaining = 0;
                for (int k = 0; k < numTiles; k++) {
                    if (tileStates[i][j][k]) {
                        numRemaining++;
                    }
                }

                final boolean isActualized = numRemaining == 1;
                if (!isActualized) {
                    anyNonActualized = true;

                    if (numRemaining < minNumStatesRemaining) {
                        minNumStatesRemaining = numRemaining;
                        minStateTiles = new ArrayList<>();
                        minStateTiles.add(new Point(i, j));
                    } else if (numRemaining == minNumStatesRemaining) {
                        minStateTiles.add(new Point(i, j));
                    }
                }
            }
        }

        if (anyNonActualized) {
            // Pick a tile to actualize
            final Point minStateTile = minStateTiles.get(
                    rand.nextInt(minStateTiles.size())
            );
            final int i = minStateTile.row;
            final int j = minStateTile.column;

            actualizeTile(i, j);
            propagate(i, j);

            return true;
        } else {
            return false;
        }
    }

    private void actualizeTile(final int i, final int j) {
        final int numTiles = tileStates[0][0].length;

        // Find what possible states the tile can actualize to
        ArrayList<Integer> possibleStates = new ArrayList<>();
        for (int k = 0; k < numTiles; k++) {
            if (tileStates[i][j][k]) {
                possibleStates.add(k);
            }
        }

        // Pick a random possible state to actualize the tile to
        final int randomState = possibleStates.get(
                rand.nextInt(possibleStates.size())
        );

        // Actualize the tile to the chosen state
        for (int k = 0; k < numTiles; k++) {
            if (k != randomState) {
                tileStates[i][j][k] = false;
            }
        }
    }

    /**
     * Recalculate all of the possible states for the neighbors of the
     * actualized tile.
     */
    private void propagate(final int row, final int column) {
        final int lastRow = tileStates.length - 1;
        final int lastColumn = tileStates[0].length - 1;

        final int startRow    = Math.max(0         , row    - waveDefinition.getN());
        final int endRow      = Math.min(lastRow   , row    + waveDefinition.getN());
        final int startColumn = Math.max(0         , column - waveDefinition.getM());
        final int endColumn   = Math.min(lastColumn, column + waveDefinition.getM());

        for (int i = startRow; i <= endRow; i++) {
            for (int j = startColumn; j <= endColumn; j++) {
                boolean atLeastOneState = false;
                for (TileConfiguration config : waveDefinition.getTileConfigurations()) {
                    if (tileStates[i][j][config.tileType] && config.fitsConfiguration(this, i, j)) {
                        tileStates[i][j][config.tileType] = true;
                        atLeastOneState = true;
                    }
                }

                if (!atLeastOneState) {
                    throw new ContradictoryBoardStateException(
                            "Contradiction created at (" + i + ", " + j + ")"
                    );
                }
            }
        }
    }

    public Optional<Integer> getTileState(final int row, final int column) {
        final int numTiles = tileStates[0][0].length;

        int numRemaining = 0;
        Optional<Integer> lastState = Optional.empty();
        for (int k = 0; k < numTiles; k++) {
            if (tileStates[row][column][k]) {
                numRemaining++;
                lastState = Optional.of(k);
            }
        }

        final boolean isActualized = numRemaining == 1;

        if (isActualized) {
            return Optional.of(lastState.get());
        } else {
            return Optional.empty();
        }
    }

    public int getNumActualized() {
        final int width = tileStates.length;
        final int height = tileStates[0].length;
        final int numTiles = tileStates[0][0].length;

        int numActualized = 0;
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                // Count how many possible states the tile has
                int numRemaining = 0;
                for (int k = 0; k < numTiles; k++) {
                    if (tileStates[i][j][k]) {
                        numRemaining++;
                    }
                }

                final boolean isActualized = numRemaining == 1;
                if (isActualized) {
                    numActualized++;
                }
            }
        }

        return numActualized;
    }

    public int getNumRows() {
        return tileStates.length;
    }

    public int getNumColumns() {
        return tileStates[0].length;
    }

    public BufferedImage getImage() {
        final int width = tileStates.length;
        final int height = tileStates[0].length;

        // TODO(chris): Move this section to the waveDefinition and add validation in that constructor
        final int tileWidth = waveDefinition.getTiles()[0].getBitMap().getWidth();
        final int tileHeight = waveDefinition.getTiles()[0].getBitMap().getHeight();

        final int imageWidth = tileWidth * width;
        final int imageHeight = tileHeight * height;

        BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                final Optional<Integer> state = getTileState(i, j);
                final Optional<Tile> tile = state.map((Integer a) -> waveDefinition.getTiles()[a]);

                final BufferedImage tileImage = tile.get().getBitMap();

                if (tile.isPresent()) {
                    for (int r = 0; r < tileWidth; r++) {
                        for (int c = 0; c < tileHeight; c++) {
                            image.setRGB(i * tileWidth + r, j * tileHeight + c, tileImage.getRGB(r, c));
                        }
                    }
                }
            }
        }

        return image;
    }
}
