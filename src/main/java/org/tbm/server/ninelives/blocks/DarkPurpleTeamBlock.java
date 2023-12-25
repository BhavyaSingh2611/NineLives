package org.tbm.server.ninelives.blocks;

public class DarkPurpleTeamBlock extends TeamBlock {
    public DarkPurpleTeamBlock(Settings settings) {
        super(settings);
        colourName = "Dark_Purple";
        positions = Direction.WEST.getPositions();
        rotation = Direction.WEST.getRotation();
    }
}
