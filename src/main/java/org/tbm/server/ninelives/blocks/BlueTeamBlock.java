package org.tbm.server.ninelives.blocks;

public class BlueTeamBlock extends TeamBlock {
    public BlueTeamBlock(Settings settings) {
        super(settings);
        colourName = "Blue";
        positions = Direction.SOUTH.getPositions();
        rotation = Direction.SOUTH.getRotation();
    }
}
