package org.tbm.server.ninelives.blocks;

public class GoldTeamBlock extends TeamBlock {
    public GoldTeamBlock(Settings settings) {
        super(settings);
        colourName = "Gold";
        positions = Direction.NORTH.getPositions();
        rotation = Direction.NORTH.getRotation();
    }
}
