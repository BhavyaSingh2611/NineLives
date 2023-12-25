package org.tbm.server.ninelives.blocks;

public class RedTeamBlock extends TeamBlock {
    public RedTeamBlock(Settings settings) {
        super(settings);
        this.colourName = "Red";
        positions = Direction.NORTH.getPositions();
        rotation = Direction.NORTH.getRotation();
    }
}

