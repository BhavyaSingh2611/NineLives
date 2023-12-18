package org.tbm.server.ninelives;

import net.minecraft.block.Block;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.tbm.server.ninelives.blocks.ModBlocks;

import java.util.*;

public class PositionManager {
    private static final int[][] positions = {{0, 90, 0}, {10, 90, 0}, {-10, 90, 0}};
    private static String position1st;
    private static String position2nd;
    private static String position3rd;
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
        long leaderboardCheckInterval = 400L;
        if (world.equals(world.getServer().getOverworld()) && world.getTime() - lastLeaderboardCheckTime >= leaderboardCheckInterval) {
            lastLeaderboardCheckTime = world.getTime();
            Scoreboard scoreboard = world.getScoreboard();
            List<ScoreboardPlayerScore> scores = getSortedScores(scoreboard);
            if (scores != null && scores.size() > 0) {
                Team displayTeam = scoreboard.getPlayerTeam(scores.get(0).getPlayerName());
                position1st = displayTeam.getName();
            }
            if (scores != null && scores.size() > 1) {
                Team displayTeam = scoreboard.getPlayerTeam(scores.get(1).getPlayerName());
                position2nd = displayTeam.getName();
            }
            if (scores != null && scores.size() > 2) {
                Team displayTeam = scoreboard.getPlayerTeam(scores.get(2).getPlayerName());
                position3rd = displayTeam.getName();
            }
            updatePodiums(world);
        }
    }

    private static void updatePodiums(ServerWorld world) {
        Map<String, Block> teamNameMap = new HashMap<>();

        teamNameMap.put("bac_team_aqua", ModBlocks.AQUA_TEAM_BLOCK);
        teamNameMap.put("bac_team_black", ModBlocks.BLACK_TEAM_BLOCK);
        teamNameMap.put("bac_team_blue", ModBlocks.BLUE_TEAM_BLOCK);
        teamNameMap.put("bac_team_dark_aqua", ModBlocks.DARK_AQUA_TEAM_BLOCK);
        teamNameMap.put("bac_team_dark_blue", ModBlocks.DARK_BLUE_TEAM_BLOCK);
        teamNameMap.put("bac_team_dark_gray", ModBlocks.DARK_GRAY_TEAM_BLOCK);
        teamNameMap.put("bac_team_dark_green", ModBlocks.DARK_GREEN_TEAM_BLOCK);
        teamNameMap.put("bac_team_dark_purple", ModBlocks.DARK_PURPLE_TEAM_BLOCK);
        teamNameMap.put("bac_team_dark_red", ModBlocks.DARK_RED_TEAM_BLOCK);
        teamNameMap.put("bac_team_gold", ModBlocks.GOLD_TEAM_BLOCK);
        teamNameMap.put("bac_team_gray", ModBlocks.GRAY_TEAM_BLOCK);
        teamNameMap.put("bac_team_green", ModBlocks.GREEN_TEAM_BLOCK);
        teamNameMap.put("bac_team_light_purple", ModBlocks.LIGHT_PURPLE_TEAM_BLOCK);
        teamNameMap.put("bac_team_red", ModBlocks.RED_TEAM_BLOCK);
        teamNameMap.put("bac_team_white", ModBlocks.WHITE_TEAM_BLOCK);
        teamNameMap.put("bac_team_yellow", ModBlocks.YELLOW_TEAM_BLOCK);

        BlockPos pos1st = new BlockPos(positions[0][0], positions[0][1], positions[0][2]);
        BlockPos pos2nd = new BlockPos(positions[1][0], positions[1][1], positions[1][2]);
        BlockPos pos3rd = new BlockPos(positions[2][0], positions[2][1], positions[2][2]);

        if (position1st != null && position2nd != null && position3rd != null) {
            world.removeBlock(pos1st, false);
            teamNameMap.get(position1st).onBreak(world, pos1st, teamNameMap.get(position1st).getDefaultState(), null);

            world.removeBlock(pos2nd, false);
            teamNameMap.get(position2nd).onBreak(world, pos2nd, teamNameMap.get(position2nd).getDefaultState(), null);

            world.removeBlock(pos3rd, false);
            teamNameMap.get(position3rd).onBreak(world, pos3rd, teamNameMap.get(position3rd).getDefaultState(), null);

            world.setBlockState(pos1st, teamNameMap.get(position1st).getDefaultState(), 3);
            teamNameMap.get(position1st).onPlaced(world, pos1st, teamNameMap.get(position1st).getDefaultState(), null, null);

            world.setBlockState(pos2nd, teamNameMap.get(position2nd).getDefaultState(), 3);
            teamNameMap.get(position2nd).onPlaced(world, pos2nd, teamNameMap.get(position2nd).getDefaultState(), null, null);

            world.setBlockState(pos3rd, teamNameMap.get(position3rd).getDefaultState(), 3);
            teamNameMap.get(position3rd).onPlaced(world, pos3rd, teamNameMap.get(position3rd).getDefaultState(), null, null);
        }
    }
}
