package org.tbm.server.ninelives;

import fuzs.strawstatues.world.entity.decoration.StrawStatue;
import org.tbm.server.ninelives.blocks.ModBlocks;
import org.tbm.server.ninelives.commands.HologramCommand;
import org.tbm.server.ninelives.event.PlayerDeathCallback;
import org.tbm.server.ninelives.mixinInterfaces.ITextDisplayEntityMixin;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.mixin.command.ArgumentTypesAccessor;
import net.minecraft.world.GameMode;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.entity.decoration.DisplayEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class NineLives implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("ninelives");

    public static APIData dataThread;

    public void onInitialize() {
        API.main(null);
        PlayerDeathCallback.EVENT.register((player, source) -> this.deathEvent(player));
        ServerTickEvents.START_WORLD_TICK.register(this::onWorldTick);
        CustomPlaceholders.registerCustomPlaceholders();
        ArgumentTypesAccessor.fabric_getClassMap().put(HologramCommand.HologramArgumentType.class, ConstantArgumentSerializer.of(HologramCommand.HologramArgumentType::hologram));
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> HologramCommand.register(dispatcher, registryAccess, environment));

        ServerLifecycleEvents.SERVER_STARTING.register((server) -> HologramManager.clear());

        ServerLifecycleEvents.SERVER_STARTED.register((server) -> {
            dataThread = new APIData(server.getOverworld());
            dataThread.setName("9lives-data");
            dataThread.start();
        });

        ServerLifecycleEvents.SERVER_STOPPING.register((server) -> {
            PositionManager.onKill(server.getOverworld());
            API.stopSpark();
        });

        ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
            if (entity instanceof DisplayEntity.TextDisplayEntity textEntity) {
                ITextDisplayEntityMixin textDisplayEntityMixin = (ITextDisplayEntityMixin)textEntity;
                if (textDisplayEntityMixin.isHologram()) {
                    HologramManager.addHologram(textDisplayEntityMixin.getHologramName(), entity);
                }
            }

            if (entity instanceof StrawStatue statue) {
                if (statue.hasCustomName()) {
                    String name = statue.getCustomName().getString();
                    if (name.startsWith("1st_")) {
                        PositionManager.team1stPodiums[Integer.parseInt(name.substring(4, 5)) - 1] = statue;
                    } else if (name.startsWith("2nd_")) {
                        PositionManager.team2ndPodiums[Integer.parseInt(name.substring(4, 5)) - 1] = statue;
                    } else if (name.startsWith("3rd_")) {
                        PositionManager.team3rdPodiums[Integer.parseInt(name.substring(4, 5)) - 1] = statue;
                    }
                }

            }
        });
        ModBlocks.registerModBlocks();
    }

    private void onWorldTick(ServerWorld serverWorld) {
        HologramManager.onTick(serverWorld);
        PositionManager.onTick(serverWorld);
    }

    public void deathEvent(ServerPlayerEntity player) {
        if (player.getScoreboardTeam() == null) {
            LOGGER.warn("Player is not on a team!");
        } else {
            Scoreboard scoreboard = player.getScoreboard();
            ScoreboardObjective livesObjective = scoreboard.getNullableObjective("9lives_lives");
            if (livesObjective == null) {
                LOGGER.warn("Scoreboard objective is not created!");
            } else {
                String teamName = player.getScoreboardTeam().getName();
                if (scoreboard.playerHasObjective(teamName, livesObjective)) {
                    ScoreboardPlayerScore lives = scoreboard.getPlayerScore(teamName, livesObjective);
                    if (lives.getScore() > 0) {
                        lives.incrementScore(-1);
                    } else {
                        player.changeGameMode(GameMode.SPECTATOR);
                    }
                } else {
                    LOGGER.warn("Team doesn't have lives score set!");
                }
            }
        }
    }
}
