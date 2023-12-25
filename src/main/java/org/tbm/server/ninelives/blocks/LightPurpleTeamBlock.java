package org.tbm.server.ninelives.blocks;

public class LightPurpleTeamBlock extends TeamBlock {
    public LightPurpleTeamBlock(Settings settings) {
        super(settings);
        colourName = "Light_Purple";
        positions = Direction.WEST.getPositions();
        rotation = Direction.WEST.getRotation();
    }
}
