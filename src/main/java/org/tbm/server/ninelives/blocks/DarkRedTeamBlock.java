package org.tbm.server.ninelives.blocks;

public class DarkRedTeamBlock extends TeamBlock {
    public DarkRedTeamBlock(Settings settings) {
        super(settings);
        colourName = "Dark_Red";
        positions = Direction.EAST.getPositions();
        rotation = Direction.EAST.getRotation();
    }
}
