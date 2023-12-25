package org.tbm.server.ninelives.blocks;

public class AquaTeamBlock extends TeamBlock {
    public AquaTeamBlock(Settings settings) {
        super(settings);
        colourName = "Aqua";
        positions = Direction.SOUTH.getPositions();
        rotation = Direction.SOUTH.getRotation();
    }
}
