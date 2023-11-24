package org.tbm.server.ninelives;

import java.util.*;
import java.util.stream.Collectors;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.world.ServerWorld;

public class LeaderboardData {
    private final String[] teamNames = new String[]{"bac_team_black", "bac_team_dark_blue", "bac_team_dark_green", "bac_team_dark_aqua", "bac_team_dark_red", "bac_team_dark_purple", "bac_team_gold", "bac_team_gray", "bac_team_dark_gray", "bac_team_blue", "bac_team_green", "bac_team_aqua", "bac_team_red", "bac_team_light_purple", "bac_team_yellow", "bac_team_white"};
    public static List<Team> getTeamsOrdered(ServerWorld world) {
        Scoreboard scoreboard = world.getScoreboard();
        if (scoreboard.getNullableObjective("bac_advancements_team") == null) {
            return null;
        } else {
            Collection<ScoreboardPlayerScore> scores = scoreboard.getAllPlayerScores(scoreboard.getNullableObjective("bac_advancements_team"));
            scores.forEach((score) -> {
                String playerName = String.valueOf(score.getPlayerName());
                NineLives.LOGGER.info("Team: " + playerName + " - " + score.getScore());
            });
            List<ScoreboardPlayerScore> scoresList = scores.stream().sorted(Comparator.comparing(ScoreboardPlayerScore::getScore).reversed()).collect(Collectors.toCollection(ArrayList::new));
            List<Team> orderedTeams = new ArrayList<>();
            Iterator<ScoreboardPlayerScore> scoreListIterator = scoresList.iterator();

            String playerName;
            while(scoreListIterator.hasNext()) {
                ScoreboardPlayerScore score = scoreListIterator.next();
                playerName = String.valueOf(score.getPlayerName());
                NineLives.LOGGER.info("Team: " + playerName + " - " + score.getScore());
                orderedTeams.add(scoreboard.getPlayerTeam(score.getPlayerName()));
            }

            Iterator<Team> teamsIterator = orderedTeams.iterator();

            while(scoreListIterator.hasNext()) {
                Team team = teamsIterator.next();
                NineLives.LOGGER.info(team.getDisplayName().getString() + ": " + team.getPlayerList().toString());
            }

            return orderedTeams;
        }
    }
}
