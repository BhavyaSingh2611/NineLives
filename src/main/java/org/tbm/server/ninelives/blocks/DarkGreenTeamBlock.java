package org.tbm.server.ninelives.blocks;

public class DarkGreenTeamBlock extends TeamBlock {
    public DarkGreenTeamBlock(Settings settings) {
        super(settings);
        this.colourName = "Dark_Green";
        positions = Direction.EAST.getPositions();
        rotation = Direction.EAST.getRotation();
    }
}
