package org.tbm.server.ninelives.blocks;

public class YellowTeamBlock extends TeamBlock {
    public YellowTeamBlock(Settings settings) {
        super(settings);
        colourName = "Yellow";
        positions = Direction.NORTH.getPositions();
        rotation = Direction.NORTH.getRotation();
    }
}
