package org.tbm.server.ninelives.blocks;

public class DarkAquaTeamBlock extends TeamBlock {
    public DarkAquaTeamBlock(Settings settings) {
        super(settings);
        colourName = "Dark_Aqua";
        positions = Direction.SOUTH.getPositions();
        rotation = Direction.SOUTH.getRotation();
    }
}
