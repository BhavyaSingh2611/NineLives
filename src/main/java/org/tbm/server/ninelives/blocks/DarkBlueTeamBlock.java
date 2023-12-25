package org.tbm.server.ninelives.blocks;

public class DarkBlueTeamBlock extends TeamBlock {
    public DarkBlueTeamBlock(Settings settings) {
        super(settings);
        colourName = "Dark_Blue";
        positions = Direction.WEST.getPositions();
        rotation = Direction.WEST.getRotation();
    }
}
