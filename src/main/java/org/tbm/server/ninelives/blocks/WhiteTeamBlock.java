package org.tbm.server.ninelives.blocks;

public class WhiteTeamBlock extends TeamBlock {
    public WhiteTeamBlock(Settings settings) {
        super(settings);
        colourName = "White";
        positions = Direction.EAST.getPositions();
        rotation = Direction.EAST.getRotation();
    }
}
