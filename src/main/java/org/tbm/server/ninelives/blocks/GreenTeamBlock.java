package org.tbm.server.ninelives.blocks;

public class GreenTeamBlock extends TeamBlock {
    public GreenTeamBlock(Settings settings) {
        super(settings);
        colourName = "Green";
        positions = Direction.EAST.getPositions();
        rotation = Direction.EAST.getRotation();
    }
}
