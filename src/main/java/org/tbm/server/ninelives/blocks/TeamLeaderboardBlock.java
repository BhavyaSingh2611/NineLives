package org.tbm.server.ninelives.blocks;

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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;
import org.tbm.server.ninelives.HologramManager;
import org.tbm.server.ninelives.mixinInterfaces.IDisplayEntityMixin;
import org.tbm.server.ninelives.mixinInterfaces.ITextDisplayEntityMixin;

import java.util.List;

public class TeamLeaderboardBlock extends Block {

    public TeamLeaderboardBlock(Settings settings) {
        super(settings);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        DisplayEntity.TextDisplayEntity entity = new DisplayEntity.TextDisplayEntity(EntityType.TEXT_DISPLAY, world);
        ITextDisplayEntityMixin textDisplayEntityMixin = (ITextDisplayEntityMixin) entity;
        IDisplayEntityMixin displayEntityMixin = (IDisplayEntityMixin) entity;

        Vec3d hologramPos = pos.toCenterPos().add(0,2,0);

        entity.setPosition(hologramPos);
        textDisplayEntityMixin.setHologramTextPlaceholder("""
                    Team Leaderboard
                    <color:#555555><strikethrough></strikethrough></color>
                    1st: <gold>★</gold> %ninelives:team_score 1% %ninelives:team_score_name 1%
                    2nd: <gold>★</gold> %ninelives:team_score 2% %ninelives:team_score_name 2%
                    3rd: <gold>★</gold> %ninelives:team_score 3% %ninelives:team_score_name 3%
                    4th: <gold>★</gold> %ninelives:team_score 4% %ninelives:team_score_name 4%
                    5th: <gold>★</gold> %ninelives:team_score 5% %ninelives:team_score_name 5%
                    6th: <gold>★</gold> %ninelives:team_score 6% %ninelives:team_score_name 6%
                    7th: <gold>★</gold> %ninelives:team_score 7% %ninelives:team_score_name 7%
                    8th: <gold>★</gold> %ninelives:team_score 8% %ninelives:team_score_name 8%
                    9th: <gold>★</gold> %ninelives:team_score 9% %ninelives:team_score_name 9%
                    10th: <gold>★</gold> %ninelives:team_score 10% %ninelives:team_score_name 10%
                    <color:#555555><strikethrough></strikethrough></color>
                    """);
        entity.getDataTracker().set(textDisplayEntityMixin.getBackgroundData(), 0);
        entity.getDataTracker().set(displayEntityMixin.getBillboardData(), (byte) 1);
        textDisplayEntityMixin.setIsHologram(true);
        textDisplayEntityMixin.setHologramName("TeamLeaderboard");
        world.spawnEntity(entity);
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        this.spawnBreakParticles(world, player, pos, state);
        if (state.isIn(BlockTags.GUARDED_BY_PIGLINS)) {
            PiglinBrain.onGuardedBlockInteracted(player, false);
        }

        world.emitGameEvent(GameEvent.BLOCK_DESTROY, pos, GameEvent.Emitter.of(player, state));

        Box box = new Box(pos.getX(), pos.getY(), pos.getZ(), pos.getX(),pos.getY() + 3, pos.getZ());

        List<DisplayEntity.TextDisplayEntity> dynamicHologramCaptures = world.getEntitiesByClass(DisplayEntity.TextDisplayEntity.class, box.expand(1), e -> true);
        for (DisplayEntity.TextDisplayEntity hologram: dynamicHologramCaptures) {
            ITextDisplayEntityMixin displayEntityMixin = (ITextDisplayEntityMixin) hologram;
            displayEntityMixin.setIsHologram(false);
            hologram.kill();
            HologramManager.removeHologram(displayEntityMixin.getHologramName());
            hologram.remove(Entity.RemovalReason.DISCARDED);
        }
    }
}
