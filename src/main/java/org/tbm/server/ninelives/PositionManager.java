package org.tbm.server.ninelives;

import com.mojang.authlib.GameProfile;
import fuzs.strawstatues.world.entity.decoration.StrawStatue;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.tbm.server.ninelives.mixinInterfaces.IDisplayEntityMixin;
import org.tbm.server.ninelives.mixinInterfaces.ITextDisplayEntityMixin;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class PositionManager {
    public static StrawStatue[] team1stPodiums = new StrawStatue[3];
    public static StrawStatue[] team2ndPodiums = new StrawStatue[3];
    public static StrawStatue[] team3rdPodiums = new StrawStatue[3];
    private static final int[][] team1stPodiumsPos = {{0, 2, 0}, {0, 2, 0}, {0, 2, 0}};
    private static final int[][] team2ndPodiumsPos = {{0, 2, 0}, {0, 2, 0}, {0, 2, 0}};
    private static final int[][] team3rdPodiumsPos = {{0, 2, 0}, {0, 2, 0}, {0, 2, 0}};

    private static List<DisplayEntity.TextDisplayEntity> hologramList = new CopyOnWriteArrayList<>();
    private static long lastLeaderboardCheckTime = 0L;

    private static final Comparator<ScoreboardPlayerScore> HIGH_SCORE_COMPARATOR = (a, b) -> {
        if (a.getScore() < b.getScore()) {
            return 1;
        } else {
            return a.getScore() > b.getScore() ? -1 : a.getPlayerName().compareToIgnoreCase(b.getPlayerName());
        }
    };

    private static List<ScoreboardPlayerScore> getSortedScores(Scoreboard scoreboard) {
        ScoreboardObjective advancementsObjective = scoreboard.getNullableObjective("bac_advancements_team");
        if (advancementsObjective == null) {
            return null;
        } else {
            try {
                Collection<ScoreboardPlayerScore> scoreCollection = scoreboard.getAllPlayerScores(advancementsObjective);
                return scoreCollection.isEmpty() ? null : scoreboard.getAllPlayerScores(advancementsObjective).stream().sorted(HIGH_SCORE_COMPARATOR).toList();
            } catch (Exception e) {
                return null;
            }
        }
    }
    public static void onTick(ServerWorld world) {
        Map<String, String> teamNameMap = new HashMap<>();

        teamNameMap.put("bac_team_aqua", "Aqua_Team");
        teamNameMap.put("bac_team_black", "Black_Team");
        teamNameMap.put("bac_team_blue", "Blue_Team");
        teamNameMap.put("bac_team_dark_aqua", "Dark_Aqua_Team");
        teamNameMap.put("bac_team_dark_blue", "Dark_Blue_Team");
        teamNameMap.put("bac_team_dark_gray", "Dark_Gray_Team");
        teamNameMap.put("bac_team_dark_green", "Dark_Green_Team");
        teamNameMap.put("bac_team_dark_purple", "Dark_Purple_Team");
        teamNameMap.put("bac_team_dark_red", "Dark_Red_Team");
        teamNameMap.put("bac_team_gold", "Gold_Team");
        teamNameMap.put("bac_team_gray", "Gray_Team");
        teamNameMap.put("bac_team_green", "Green_Team");
        teamNameMap.put("bac_team_light_purple", "Light_Purple_Team");
        teamNameMap.put("bac_team_red", "Red_Team");
        teamNameMap.put("bac_team_white", "White_Team");
        teamNameMap.put("bac_team_yellow", "Yellow_Team");

        long leaderboardCheckInterval = 400L;
        if (world.equals(world.getServer().getOverworld()) && world.getTime() - lastLeaderboardCheckTime >= leaderboardCheckInterval) {
            lastLeaderboardCheckTime = world.getTime();
            Scoreboard scoreboard = world.getScoreboard();
            List<ScoreboardPlayerScore> scores = getSortedScores(scoreboard);

            for (DisplayEntity.TextDisplayEntity hologram: hologramList) {
                ITextDisplayEntityMixin displayEntity = (ITextDisplayEntityMixin) hologram;
                hologram.kill();
                HologramManager.removeHologram(displayEntity.getHologramName());

                displayEntity.setIsHologram(false);
                hologram.remove(Entity.RemovalReason.DISCARDED);
                hologramList.remove(hologram);
            }

            Box box = new Box(new BlockPos(20, 272, 295)).expand(3);
            Box box1 = new Box(new BlockPos(20, 272, 285)).expand(3);
            Box box2 = new Box(new BlockPos(26, 272, 290)).expand(3);

            List<DisplayEntity.TextDisplayEntity> dynamicHologramCaptures = world.getEntitiesByClass(DisplayEntity.TextDisplayEntity.class, box, e -> true);
            List<DisplayEntity.TextDisplayEntity> dynamicHologramCaptures1 = world.getEntitiesByClass(DisplayEntity.TextDisplayEntity.class, box1, e -> true);
            List<DisplayEntity.TextDisplayEntity> dynamicHologramCaptures2 = world.getEntitiesByClass(DisplayEntity.TextDisplayEntity.class, box2, e -> true);

            for (DisplayEntity.TextDisplayEntity hologram: dynamicHologramCaptures) {
                ITextDisplayEntityMixin displayEntityMixin = (ITextDisplayEntityMixin) hologram;
                hologram.kill();
                hologram.remove(Entity.RemovalReason.DISCARDED);

                displayEntityMixin.setIsHologram(false);
                HologramManager.removeHologram(displayEntityMixin.getHologramName());
            }
            for (DisplayEntity.TextDisplayEntity hologram: dynamicHologramCaptures1) {
                ITextDisplayEntityMixin displayEntityMixin = (ITextDisplayEntityMixin) hologram;
                hologram.kill();
                hologram.remove(Entity.RemovalReason.DISCARDED);

                displayEntityMixin.setIsHologram(false);
                HologramManager.removeHologram(displayEntityMixin.getHologramName());
            }
            for (DisplayEntity.TextDisplayEntity hologram: dynamicHologramCaptures2) {
                ITextDisplayEntityMixin displayEntityMixin = (ITextDisplayEntityMixin) hologram;
                hologram.kill();
                hologram.remove(Entity.RemovalReason.DISCARDED);

                displayEntityMixin.setIsHologram(false);
                HologramManager.removeHologram(displayEntityMixin.getHologramName());
            }


            if (scores != null && !scores.isEmpty()) {
                Team displayTeam = scoreboard.getPlayerTeam(scores.get(0).getPlayerName());

                List<String> playerNames = new ArrayList<>(displayTeam.getPlayerList().stream().toList());
                playerNames.remove(teamNameMap.get(displayTeam.getName()));

                for (int i = 0; i < playerNames.size() && i < 3; i++) {
                    if (team1stPodiums[i] != null) {
                        team1stPodiums[i].verifyAndSetOwner(new GameProfile(null, playerNames.get(i)));
                        DisplayEntity.TextDisplayEntity entity = new DisplayEntity.TextDisplayEntity(EntityType.TEXT_DISPLAY, world);
                        ITextDisplayEntityMixin textDisplayEntityMixin = (ITextDisplayEntityMixin) entity;
                        IDisplayEntityMixin displayEntityMixin = (IDisplayEntityMixin) entity;

                        int hologramX = team1stPodiumsPos[i][0];
                        int hologramY = team1stPodiumsPos[i][1];
                        int hologramZ = team1stPodiumsPos[i][2];
                        Vec3d hologramPos = team1stPodiums[i].getPos().add(hologramX, hologramY, hologramZ);

                        entity.setPosition(hologramPos);
                        textDisplayEntityMixin.setHologramTextPlaceholder(String.format("""
                        %s
                        Advancements: <gold>★</gold> %%ninelives:player_score %s%%
                        Deaths: <gold>☠</gold> %%ninelives:player_deaths %s%%""", playerNames.get(i), playerNames.get(i), playerNames.get(i)));
                        entity.getDataTracker().set(textDisplayEntityMixin.getBackgroundData(), 0);
                        entity.getDataTracker().set(displayEntityMixin.getBillboardData(), (byte) 3);
                        textDisplayEntityMixin.setIsHologram(true);
                        textDisplayEntityMixin.setHologramName(String.format("%s_%s", teamNameMap.get(displayTeam.getName()), playerNames.get(i)));
                        hologramList.add(entity);
                        world.spawnEntity(entity);

                        if (i == 0) {
                            DisplayEntity.TextDisplayEntity entity2 = new DisplayEntity.TextDisplayEntity(EntityType.TEXT_DISPLAY, world);
                            ITextDisplayEntityMixin textDisplayEntityMixin2 = (ITextDisplayEntityMixin) entity2;
                            IDisplayEntityMixin displayEntityMixin2 = (IDisplayEntityMixin) entity2;

                            double[] teamHologramPos = {0, 3.5, 0};
                            Vec3d hologramPos2 = team1stPodiums[i].getPos().add(teamHologramPos[0], teamHologramPos[1], teamHologramPos[2]);

                            entity2.setPosition(hologramPos2);
                            String formattedName = displayTeam.getName().replace("bac_team_", "").toLowerCase();
                            textDisplayEntityMixin2.setHologramTextPlaceholder(String.format("""
                    <%s><b><i>%%ninelives:team_display_name bac_team_%s%%</i></b></%s>
                    Advancements: <gold>★</gold> %%ninelives:team_score bac_team_%s%%
                    Lives: <gold>♥</gold> %%ninelives:team_lives bac_team_%s%%""", formattedName, formattedName, formattedName, formattedName, formattedName));
                            entity2.getDataTracker().set(textDisplayEntityMixin2.getBackgroundData(), 0);
                            entity2.getDataTracker().set(displayEntityMixin2.getBillboardData(), (byte) 3);
                            textDisplayEntityMixin2.setIsHologram(true);
                            textDisplayEntityMixin2.setHologramName(String.format("LeaderboardTeam_%s", formattedName));
                            hologramList.add(entity2);
                            world.spawnEntity(entity2);
                        }
                    }
                }
            }
            if (scores != null && scores.size() > 1) {
                Team displayTeam = scoreboard.getPlayerTeam(scores.get(1).getPlayerName());

                List<String> playerNames = new ArrayList<>(displayTeam.getPlayerList().stream().toList());
                playerNames.remove(teamNameMap.get(displayTeam.getName()));

                for (int i = 0; i < playerNames.size() && i < 3; i++) {
                    if (team2ndPodiums[i] != null) {
                        team2ndPodiums[i].verifyAndSetOwner(new GameProfile(null, playerNames.get(i)));
                        DisplayEntity.TextDisplayEntity entity = new DisplayEntity.TextDisplayEntity(EntityType.TEXT_DISPLAY, world);
                        ITextDisplayEntityMixin textDisplayEntityMixin = (ITextDisplayEntityMixin) entity;
                        IDisplayEntityMixin displayEntityMixin = (IDisplayEntityMixin) entity;

                        int hologramX = team2ndPodiumsPos[i][0];
                        int hologramY = team2ndPodiumsPos[i][1];
                        int hologramZ = team2ndPodiumsPos[i][2];
                        Vec3d hologramPos = team2ndPodiums[i].getPos().add(hologramX, hologramY, hologramZ);

                        entity.setPosition(hologramPos);
                        textDisplayEntityMixin.setHologramTextPlaceholder(String.format("""
                        %s
                        Advancements: <gold>★</gold> %%ninelives:player_score %s%%
                        Deaths: <gold>☠</gold> %%ninelives:player_deaths %s%%""", playerNames.get(i), playerNames.get(i), playerNames.get(i)));
                        entity.getDataTracker().set(textDisplayEntityMixin.getBackgroundData(), 0);
                        entity.getDataTracker().set(displayEntityMixin.getBillboardData(), (byte) 3);
                        textDisplayEntityMixin.setIsHologram(true);
                        textDisplayEntityMixin.setHologramName(String.format("%s_%s", teamNameMap.get(displayTeam.getName()), playerNames.get(i)));
                        hologramList.add(entity);
                        world.spawnEntity(entity);

                        if (i == 0) {
                            DisplayEntity.TextDisplayEntity entity2 = new DisplayEntity.TextDisplayEntity(EntityType.TEXT_DISPLAY, world);
                            ITextDisplayEntityMixin textDisplayEntityMixin2 = (ITextDisplayEntityMixin) entity2;
                            IDisplayEntityMixin displayEntityMixin2 = (IDisplayEntityMixin) entity2;

                            double[] teamHologramPos = {0, 3.5, 0};
                            Vec3d hologramPos2 = team2ndPodiums[i].getPos().add(teamHologramPos[0], teamHologramPos[1], teamHologramPos[2]);

                            entity2.setPosition(hologramPos2);
                            String formattedName = displayTeam.getName().replace("bac_team_", "").toLowerCase();
                            textDisplayEntityMixin2.setHologramTextPlaceholder(String.format("""
                    <%s><b><i>%%ninelives:team_display_name bac_team_%s%%</i></b></%s>
                    Advancements: <gold>★</gold> %%ninelives:team_score bac_team_%s%%
                    Lives: <gold>♥</gold> %%ninelives:team_lives bac_team_%s%%""", formattedName, formattedName, formattedName, formattedName, formattedName));
                            entity2.getDataTracker().set(textDisplayEntityMixin2.getBackgroundData(), 0);
                            entity2.getDataTracker().set(displayEntityMixin2.getBillboardData(), (byte) 3);
                            textDisplayEntityMixin2.setIsHologram(true);
                            textDisplayEntityMixin2.setHologramName(String.format("LeaderboardTeam_%s", formattedName));
                            hologramList.add(entity2);
                            world.spawnEntity(entity2);
                        }
                    }
                }
            }
            if (scores != null && scores.size() > 2) {
                Team displayTeam = scoreboard.getPlayerTeam(scores.get(2).getPlayerName());

                List<String> playerNames = new ArrayList<>(displayTeam.getPlayerList().stream().toList());
                playerNames.remove(teamNameMap.get(displayTeam.getName()));

                for (int i = 0; i < playerNames.size() && i < 3; i++) {
                    if (team3rdPodiums[i] != null) {
                        team3rdPodiums[i].verifyAndSetOwner(new GameProfile(null, playerNames.get(i)));
                        DisplayEntity.TextDisplayEntity entity = new DisplayEntity.TextDisplayEntity(EntityType.TEXT_DISPLAY, world);
                        ITextDisplayEntityMixin textDisplayEntityMixin = (ITextDisplayEntityMixin) entity;
                        IDisplayEntityMixin displayEntityMixin = (IDisplayEntityMixin) entity;

                        int hologramX = team3rdPodiumsPos[i][0];
                        int hologramY = team3rdPodiumsPos[i][1];
                        int hologramZ = team3rdPodiumsPos[i][2];
                        Vec3d hologramPos = team3rdPodiums[i].getPos().add(hologramX, hologramY, hologramZ);

                        entity.setPosition(hologramPos);
                        textDisplayEntityMixin.setHologramTextPlaceholder(String.format("""
                        %s
                        Advancements: <gold>★</gold> %%ninelives:player_score %s%%
                        Deaths: <gold>☠</gold> %%ninelives:player_deaths %s%%""", playerNames.get(i), playerNames.get(i), playerNames.get(i)));
                        entity.getDataTracker().set(textDisplayEntityMixin.getBackgroundData(), 0);
                        entity.getDataTracker().set(displayEntityMixin.getBillboardData(), (byte) 3);
                        textDisplayEntityMixin.setIsHologram(true);
                        textDisplayEntityMixin.setHologramName(String.format("%s_%s", teamNameMap.get(displayTeam.getName()), playerNames.get(i)));
                        hologramList.add(entity);
                        world.spawnEntity(entity);

                        if (i == 0) {
                            DisplayEntity.TextDisplayEntity entity2 = new DisplayEntity.TextDisplayEntity(EntityType.TEXT_DISPLAY, world);
                            ITextDisplayEntityMixin textDisplayEntityMixin2 = (ITextDisplayEntityMixin) entity2;
                            IDisplayEntityMixin displayEntityMixin2 = (IDisplayEntityMixin) entity2;

                            double[] teamHologramPos = {0, 3.5, 0};
                            Vec3d hologramPos2 = team3rdPodiums[i].getPos().add(teamHologramPos[0], teamHologramPos[1], teamHologramPos[2]);

                            entity2.setPosition(hologramPos2);
                            String formattedName = displayTeam.getName().replace("bac_team_", "").toLowerCase();
                            textDisplayEntityMixin2.setHologramTextPlaceholder(String.format("""
                    <%s><b><i>%%ninelives:team_display_name bac_team_%s%%</i></b></%s>
                    Advancements: <gold>★</gold> %%ninelives:team_score bac_team_%s%%
                    Lives: <gold>♥</gold> %%ninelives:team_lives bac_team_%s%%""", formattedName, formattedName, formattedName, formattedName, formattedName));
                            entity2.getDataTracker().set(textDisplayEntityMixin2.getBackgroundData(), 0);
                            entity2.getDataTracker().set(displayEntityMixin2.getBillboardData(), (byte) 3);
                            textDisplayEntityMixin2.setIsHologram(true);
                            textDisplayEntityMixin2.setHologramName(String.format("LeaderboardTeam_%s", formattedName));
                            hologramList.add(entity2);
                            world.spawnEntity(entity2);
                        }
                    }
                }
            }
        }
    }

    public static void onKill(ServerWorld world) {
        for (DisplayEntity.TextDisplayEntity hologram: hologramList) {
            ITextDisplayEntityMixin displayEntity = (ITextDisplayEntityMixin) hologram;
            hologram.kill();
            HologramManager.removeHologram(displayEntity.getHologramName());

            displayEntity.setIsHologram(false);
            hologram.remove(Entity.RemovalReason.DISCARDED);
            hologramList.remove(hologram);
        }
    }
}
