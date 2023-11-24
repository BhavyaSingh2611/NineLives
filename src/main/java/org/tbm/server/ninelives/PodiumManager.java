package org.tbm.server.ninelives;

import java.util.*;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.entity.decoration.DisplayEntity;

public class PodiumManager {
    private static final Map<String, ArrayList<DisplayEntity.ItemDisplayEntity>> statues = new HashMap<>();
    private static long lastLeaderboardCheckTime = 0L;
    private static final boolean checkSilver = false;
    private static final boolean checkBronze = false;
    private static String[] lastTeams = new String[]{"first", "second", "third"};
    private static final BlockPos[] goldPodiums = new BlockPos[]{new BlockPos(-309, 72, -150), new BlockPos(-307, 72, -152), new BlockPos(-305, 72, -154)};
    private static final float goldRotation = 135.0F;
    private static final BlockPos[] bronzePodiums = new BlockPos[]{new BlockPos(-300, 72, -160), new BlockPos(-298, 72, -160), new BlockPos(-296, 72, -160)};
    private static final float bronzeRotation = 180.0F;
    private static final BlockPos[] silverPodiums = new BlockPos[]{new BlockPos(-315, 72, -145), new BlockPos(-315, 72, -143), new BlockPos(-315, 72, -141)};
    private static final float silverRotation = 90.0F;
    private static final Vec3d inactivePos = new Vec3d(-308.0, 62.0, -152.0);

    public static void addStatue(DisplayEntity.ItemDisplayEntity entity) {
        entity.setPosition(inactivePos);
        String teamName = "none";
        if (entity.getCommandTags().contains("blue")) {
            teamName = "bac_team_blue";
        }

        if (entity.getCommandTags().contains("dark_aqua")) {
            teamName = "bac_team_dark_aqua";
        }

        if (entity.getCommandTags().contains("dark_gray")) {
            teamName = "bac_team_dark_gray";
        }

        if (entity.getCommandTags().contains("dark_green")) {
            teamName = "bac_team_dark_green";
        }

        if (entity.getCommandTags().contains("aqua")) {
            teamName = "bac_team_aqua";
        }

        if (entity.getCommandTags().contains("green")) {
            teamName = "bac_team_green";
        }

        if (entity.getCommandTags().contains("light_purple")) {
            teamName = "bac_team_light_purple";
        }

        if (entity.getCommandTags().contains("gold")) {
            teamName = "bac_team_gold";
        }

        if (entity.getCommandTags().contains("red")) {
            teamName = "bac_team_red";
        }

        if (entity.getCommandTags().contains("dark_purple")) {
            teamName = "bac_team_dark_purple";
        }

        if (entity.getCommandTags().contains("dark_red")) {
            teamName = "bac_team_dark_red";
        }

        if (entity.getCommandTags().contains("yellow")) {
            teamName = "bac_team_yellow";
        }

        if (!teamName.equals("none")) {
            ArrayList<DisplayEntity.ItemDisplayEntity> newStatues = statues.getOrDefault(teamName, new ArrayList<>());
            if (!newStatues.contains(entity)) {
                newStatues.add(entity);
            }

            statues.put(teamName, newStatues);
            NineLives.LOGGER.info(teamName + " " + statues.get(teamName).size());
        }

    }

    public static void clear() {
        statues.clear();
    }

    public static void onTick(ServerWorld world) {
        long leaderboardCheckInterval = 20L;
        if (world.equals(world.getServer().getOverworld()) && world.getTime() - lastLeaderboardCheckTime >= leaderboardCheckInterval) {
            lastLeaderboardCheckTime = world.getTime();
            sortStatues2(world.getScoreboard(), "bac_advancements_team");
        }

    }

    public static void resetStatues() {
        if (!statues.isEmpty()) {
            statues.forEach((key, value) -> {
            });
        }

    }

    public static void sortStatues2(Scoreboard scoreboard, String objective) {
        List<ScoreboardPlayerScore> scores = CustomPlaceholders.getSortedScores(scoreboard, objective);
        String[] newTeams = new String[]{"first", "second", "third"};
        if (scores != null && !scores.isEmpty()) {
            if (scores.size() > 2) {
                newTeams[2] = scores.get(2).getPlayerName();
            }

            if (scores.size() > 1) {
                newTeams[1] = scores.get(1).getPlayerName();
            }

            if (!scores.isEmpty()) {
                newTeams[0] = scores.get(0).getPlayerName();
            }
        }

        if (!Arrays.equals(newTeams, lastTeams)) {
            String[] var4 = lastTeams;
            for (String team : var4) {
                if (statues.containsKey(team)) {

                    for (DisplayEntity.ItemDisplayEntity o : statues.get(team)) {
                        o.setPosition(inactivePos);
                    }
                }
            }

            placeStatues(scores);
            lastTeams = newTeams;
        }

    }

    public static void placeStatues(List<ScoreboardPlayerScore> scores) {
        if (scores != null && !scores.isEmpty()) {
            for(int i = 0; i < scores.size() && i < 3; ++i) {
                if (statues.containsKey(scores.get(i).getPlayerName())) {
                    ArrayList<DisplayEntity.ItemDisplayEntity> newStatues = statues.get(scores.get(i).getPlayerName());

                    for(int i2 = 0; i2 < newStatues.size(); ++i2) {
                        switch (i) {
                            case 0:
                                newStatues.get(i2).setPosition(goldPodiums[i2].toCenterPos());
                                newStatues.get(i2).setYaw(goldRotation);
                                break;
                            case 1:
                                newStatues.get(i2).setPosition(silverPodiums[i2].toCenterPos());
                                newStatues.get(i2).setYaw(silverRotation);
                                break;
                            case 2:
                                newStatues.get(i2).setPosition(bronzePodiums[i2].toCenterPos());
                                newStatues.get(i2).setYaw(bronzeRotation);
                        }
                    }
                }
            }
        }
    }

    public static void sortStatues(Scoreboard scoreboard, int place, String objective) {
        List<ScoreboardPlayerScore> scores = CustomPlaceholders.getSortedScores(scoreboard, objective);
        if (scores == null || scores.isEmpty() || scores.size() <= place) {
            if (place >= 3) {
                return;
            }

            if (statues.containsKey(lastTeams[place])) {
                for (DisplayEntity.ItemDisplayEntity o : statues.get(lastTeams[place])) {
                    o.setPosition(inactivePos);
                }
            }

            sortStatues(scoreboard, place + 1, "bac_advancements_team");
        }

        String newTeam = scores.get(place).getPlayerName();
        if (!Objects.equals(newTeam, lastTeams[place])) {
            if (statues.containsKey(newTeam)) {
                ArrayList<String> scoreboardNames = new ArrayList<>();

                for (ScoreboardPlayerScore score : scores) {
                    scoreboardNames.add(score.getPlayerName());
                }

                if (statues.containsKey(lastTeams[place]) && (!scoreboardNames.contains(lastTeams[place]) || scoreboardNames.indexOf(lastTeams[place]) > 2)) {

                    for (DisplayEntity.ItemDisplayEntity o : statues.get(lastTeams[place])) {
                        o.setPosition(inactivePos);
                    }
                }

                if (place < 2) {
                    sortStatues(scoreboard, place + 1, "bac_advancements_team");
                }

                ArrayList<DisplayEntity.ItemDisplayEntity> newStatues = statues.get(newTeam);

                for (int i = 0; i < newStatues.size(); ++i) {
                    switch (place) {
                        case 0:
                            newStatues.get(i).setPosition(goldPodiums[i].toCenterPos());
                            newStatues.get(i).setYaw(goldRotation);
                            break;
                        case 1:
                            newStatues.get(i).setPosition(silverPodiums[i].toCenterPos());
                            newStatues.get(i).setYaw(silverRotation);
                            break;
                        case 2:
                            newStatues.get(i).setPosition(bronzePodiums[i].toCenterPos());
                            newStatues.get(i).setYaw(bronzeRotation);
                    }
                }

                lastTeams[place] = newTeam;
            }
        }
    }
}
