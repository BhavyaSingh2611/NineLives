package org.tbm.server.ninelives.blocks;

import com.mojang.authlib.GameProfile;
import fuzs.strawstatues.world.entity.decoration.StrawStatue;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;
import org.tbm.server.ninelives.HologramManager;
import org.tbm.server.ninelives.mixinInterfaces.IDisplayEntityMixin;
import org.tbm.server.ninelives.mixinInterfaces.ITextDisplayEntityMixin;

import java.util.ArrayList;
import java.util.List;

public class TeamBlock extends Block {
    public TeamBlock(Settings settings) {
        super(settings);
    }
    public double[][] positions = {{0, 1, -2}, {0, 1.5, 0}, {0, 1, 2}};
    public float rotation = 90;
    private final double[][] teamHologramPosition = {{0, 4.5, 0}};

    public String colourName;

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        Scoreboard scoreboard = world.getScoreboard();
        Team team = scoreboard.getPlayerTeam(colourName + "_Team");

        List<String> playerNames = new ArrayList<>(team.getPlayerList().stream().toList());
        playerNames.remove(colourName + "_Team");

        for (int i = 0; i < playerNames.size(); i++) {
            String playerName = playerNames.get(i);
            double x = positions[i][0];
            double y = positions[i][1];
            double z = positions[i][2];
            Vec3d statuePos = pos.toCenterPos().add(x, y, z);

            StrawStatue straw = new StrawStatue(world, statuePos.x, statuePos.y, statuePos.z);
            straw.verifyAndSetOwner(new GameProfile(null, playerName));
            straw.setYaw(rotation);
            straw.setNoGravity(true);
            straw.setInvulnerable(true);
            world.spawnEntity(straw);

            DisplayEntity.TextDisplayEntity entity = new DisplayEntity.TextDisplayEntity(EntityType.TEXT_DISPLAY, world);
            ITextDisplayEntityMixin textDisplayEntityMixin = (ITextDisplayEntityMixin) entity;
            IDisplayEntityMixin displayEntityMixin = (IDisplayEntityMixin) entity;

            Vec3d hologramPos = pos.toCenterPos().add(x, y + 2, z);

            entity.setPosition(hologramPos);
            textDisplayEntityMixin.setHologramTextPlaceholder(String.format("""
                    %s
                    Advancements: <gold>★</gold> %%ninelives:player_score %s%%
                    Deaths: <gold>☠</gold> %%ninelives:player_deaths %s%%""", playerName, playerName, playerName));
            entity.getDataTracker().set(textDisplayEntityMixin.getBackgroundData(), 0);
            entity.getDataTracker().set(displayEntityMixin.getBillboardData(), (byte) 3);
            textDisplayEntityMixin.setIsHologram(true);
            textDisplayEntityMixin.setHologramName(String.format("%s_%s", colourName, playerName));
            world.spawnEntity(entity);
        }

        DisplayEntity.TextDisplayEntity entity = new DisplayEntity.TextDisplayEntity(EntityType.TEXT_DISPLAY, world);
        ITextDisplayEntityMixin textDisplayEntityMixin = (ITextDisplayEntityMixin) entity;
        IDisplayEntityMixin displayEntityMixin = (IDisplayEntityMixin) entity;

        double[] teamHologramPos = this.teamHologramPosition[0];
        Vec3d hologramPos = pos.toCenterPos().add(teamHologramPos[0], teamHologramPos[1], teamHologramPos[2]);

        entity.setPosition(hologramPos);
        String formattedName = colourName.toLowerCase();
        textDisplayEntityMixin.setHologramTextPlaceholder(String.format("""
                    <%s><b><i>%%ninelives:team_display_name bac_team_%s%%</i></b></%s>
                    Advancements: <gold>★</gold> %%ninelives:team_score bac_team_%s%%
                    Lives: <gold>♥</gold> %%ninelives:team_lives bac_team_%s%%""", formattedName, formattedName, formattedName, formattedName, formattedName));
        entity.getDataTracker().set(textDisplayEntityMixin.getBackgroundData(), 0);
        entity.getDataTracker().set(displayEntityMixin.getBillboardData(), (byte) 3);
        textDisplayEntityMixin.setIsHologram(true);
        textDisplayEntityMixin.setHologramName(String.format("Main_%s", colourName));
        world.spawnEntity(entity);
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        this.spawnBreakParticles(world, player, pos, state);
        if (state.isIn(BlockTags.GUARDED_BY_PIGLINS)) {
            PiglinBrain.onGuardedBlockInteracted(player, false);
        }

        world.emitGameEvent(GameEvent.BLOCK_DESTROY, pos, GameEvent.Emitter.of(player, state));

        double x1 = pos.getX() + positions[0][0];
        double y1 = pos.getY() + positions[0][1];
        double z1 = pos.getZ() + positions[0][2];

        double x2 = pos.getX() + positions[2][0];
        double y2 = pos.getY() + positions[2][1];
        double z2 = pos.getZ() + positions[2][2];
        Box box = new Box(x1, y1, z1 , x2 , y2, z2);
        List<StrawStatue> dynamicStatueCaptures = world.getEntitiesByClass(StrawStatue.class, box.expand(2,6,2), e -> e.hasNoGravity() && e.isInvulnerable() && box.expand(2,6,2).contains(e.getPos()));
        for (StrawStatue statue: dynamicStatueCaptures) {
            statue.remove(Entity.RemovalReason.DISCARDED);
        }


        List<DisplayEntity.TextDisplayEntity> dynamicHologramCaptures = world.getEntitiesByClass(DisplayEntity.TextDisplayEntity.class, box.expand(2,6,2), e -> true);
        for (DisplayEntity.TextDisplayEntity hologram: dynamicHologramCaptures) {
            ITextDisplayEntityMixin displayEntityMixin = (ITextDisplayEntityMixin) hologram;
            displayEntityMixin.setIsHologram(false);
            hologram.kill();
            HologramManager.removeHologram(displayEntityMixin.getHologramName());
            hologram.remove(Entity.RemovalReason.DISCARDED);
        }
    }
}
