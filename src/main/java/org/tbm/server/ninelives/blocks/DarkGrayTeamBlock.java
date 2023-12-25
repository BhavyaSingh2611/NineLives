package org.tbm.server.ninelives.blocks;

public class DarkGrayTeamBlock extends TeamBlock {
    public DarkGrayTeamBlock(Settings settings) {
        super(settings);
        colourName = "Dark_Gray";
        positions = Direction.EAST.getPositions();
        rotation = Direction.EAST.getRotation();
    }
}
