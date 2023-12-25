package org.tbm.server.ninelives.blocks;

public class GrayTeamBlock extends TeamBlock {
    public GrayTeamBlock(Settings settings) {
        super(settings);
        colourName = "Gray";
        positions = Direction.EAST.getPositions();
        rotation = Direction.EAST.getRotation();
    }
}
