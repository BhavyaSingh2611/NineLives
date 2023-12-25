package org.tbm.server.ninelives.blocks;

public class BlackTeamBlock extends TeamBlock {
    public BlackTeamBlock(Settings settings) {
        super(settings);
        this.colourName = "Black";
        positions = Direction.EAST.getPositions();
        rotation = Direction.EAST.getRotation();
    }
}