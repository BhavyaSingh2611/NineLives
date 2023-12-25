package org.tbm.server.ninelives.blocks;


public enum Direction {
    NORTH,
    EAST,
    SOUTH,
    WEST;

    public double[][] getPositions() {
        return switch (this) {
            case NORTH -> new double[][]{{2, 1, 0}, {0, 1.5, 0}, {-2, 1, 0}};

            case EAST -> new double[][]{{0, 1, 2}, {0, 1.5, 0}, {0, 1, -2}};

            case SOUTH -> new double[][]{{-2, 1, 0}, {0, 1.5, 0}, {2, 1, 0}};

            case WEST -> new double[][]{{0, 1, -2}, {0, 1.5, 0}, {0, 1, 2}};
        };
    }

    public float getRotation() {
        return switch (this) {
            case SOUTH -> 0;
            case WEST -> 90;
            case NORTH -> 180;
            case EAST -> -90;

        };
    }

}
