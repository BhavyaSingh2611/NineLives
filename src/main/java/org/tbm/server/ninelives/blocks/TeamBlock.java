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
    private final int[][] positions = {{0, 2, 0}, {3, 2, 0}, {-3, 2, 0}};
    private final int[][] hologramPositions = {{0, 1, 1}, {3, 1, 1}, {-3, 1, 1}};
    private final int[][] teamHologramPosition = {{0,0,1}};

    public String colourName;

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        Scoreboard scoreboard = world.getScoreboard();

        Team team = scoreboard.getTeam("bac_team_" + colourName.toLowerCase());
        List<String> playerNames = new ArrayList<>(team.getPlayerList().stream().toList());
        playerNames.remove(colourName + "_Team");

        for (int i = 0; i < playerNames.size(); i++) {
            String playerName = playerNames.get(i);
            int x = positions[i][0];
            int y = positions[i][1];
            int z = positions[i][2];
            Vec3d statuePos = pos.toCenterPos().add(x, y, z);

            StrawStatue straw = new StrawStatue(world, statuePos.x, statuePos.y, statuePos.z);
            straw.verifyAndSetOwner(new GameProfile(null, playerName));
            straw.setNoGravity(true);
            straw.setInvulnerable(true);
            world.spawnEntity(straw);

            DisplayEntity.TextDisplayEntity entity = new DisplayEntity.TextDisplayEntity(EntityType.TEXT_DISPLAY, world);
            ITextDisplayEntityMixin textDisplayEntityMixin = (ITextDisplayEntityMixin) entity;
            IDisplayEntityMixin displayEntityMixin = (IDisplayEntityMixin) entity;

            int hologramX = hologramPositions[i][0];
            int hologramY = hologramPositions[i][1];
            int hologramZ = hologramPositions[i][2];
            Vec3d hologramPos = pos.toCenterPos().add(hologramX, hologramY, hologramZ);

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

        int[] teamHologramPos = this.teamHologramPosition[0];
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

        Box box = new Box(pos.getX() - 3, pos.getY(), pos.getZ() - 1, pos.getX() + 4,pos.getY() + 4, pos.getZ() + 1);
        List<StrawStatue> dynamicStatueCaptures = world.getEntitiesByClass(StrawStatue.class, box, e -> e.hasNoGravity() && e.isInvulnerable() && box.contains(e.getPos()));
        for (StrawStatue statue: dynamicStatueCaptures) {
            statue.remove(Entity.RemovalReason.DISCARDED);
        }


        List<DisplayEntity.TextDisplayEntity> dynamicHologramCaptures = world.getEntitiesByClass(DisplayEntity.TextDisplayEntity.class, box.expand(2), e -> true);
        for (DisplayEntity.TextDisplayEntity hologram: dynamicHologramCaptures) {
            ITextDisplayEntityMixin displayEntityMixin = (ITextDisplayEntityMixin) hologram;
            displayEntityMixin.setIsHologram(false);
            hologram.kill();
            HologramManager.removeHologram(displayEntityMixin.getHologramName());
            hologram.remove(Entity.RemovalReason.DISCARDED);
        }
    }
}
