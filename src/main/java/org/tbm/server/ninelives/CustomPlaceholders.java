package org.tbm.server.ninelives;

import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.PlaceholderResult;
import eu.pb4.placeholders.api.Placeholders;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.scoreboard.Team;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.Identifier;
import net.minecraft.server.network.ServerPlayerEntity;

public class CustomPlaceholders {
    public static final List<String> teams = new ArrayList<>();
    public static final Comparator<ScoreboardPlayerScore> HIGH_SCORE_COMPARATOR = (a, b) -> {
        if (a.getScore() < b.getScore()) {
            return 1;
        } else {
            return a.getScore() > b.getScore() ? -1 : a.getPlayerName().compareToIgnoreCase(b.getPlayerName());
        }
    };

    public static void registerCustomPlaceholders() {

        Placeholders.register(new Identifier("ninelives", "timer"), (ctx, arg) -> {
            LocalDateTime dateTime = LocalDateTime.parse("2024-01-04T18:00:00");

            long time = dateTime.toEpochSecond(ZoneOffset.ofHours(0));
            long currentTime = System.currentTimeMillis() / 1000L;
            long timeLeft = time - currentTime;

            long hours = timeLeft / 3600;
            long minutes = (timeLeft % 3600) / 60;
            long seconds = timeLeft % 60;

            if (timeLeft < 0) {
                return PlaceholderResult.value("00:00:00");
            }

            return PlaceholderResult.value("%02d:%02d:%02d".formatted(hours, minutes, seconds));

        });

        Placeholders.register(new Identifier("ninelives", "team_lives"), (ctx, arg) -> {
            if (arg == null) {
                return !ctx.hasPlayer() ? PlaceholderResult.invalid("No player!") : PlaceholderResult.value(getLives(ctx));
            } else {
                return PlaceholderResult.value(getLives(ctx, arg));
            }
        });
        Placeholders.register(new Identifier("ninelives", "team_score"), (ctx, arg) -> {
            if (arg == null) {
                return !ctx.hasPlayer() ? PlaceholderResult.invalid("No player!") : PlaceholderResult.value(getTeamAdvancements(ctx));
            } else {
                return PlaceholderResult.value(getTeamAdvancements(ctx, arg));
            }
        });
        Placeholders.register(new Identifier("ninelives", "player_score"), (ctx, arg) -> {
            if (arg == null) {
                return !ctx.hasPlayer() ? PlaceholderResult.invalid("No player!") : PlaceholderResult.value(getAdvancements(ctx));
            } else {
                return PlaceholderResult.value(getAdvancements(ctx, arg));
            }
        });
        Placeholders.register(new Identifier("ninelives", "player_score_name"), (ctx, arg) -> arg == null ? PlaceholderResult.invalid("No argument!") : PlaceholderResult.value(getAdvancementsName(ctx, arg)));
        Placeholders.register(new Identifier("ninelives", "player_deaths"), (ctx, arg) -> {
            if (arg == null) {
                return !ctx.hasPlayer() ? PlaceholderResult.invalid("No player!") : PlaceholderResult.value(getDeaths(ctx));
            } else {
                return PlaceholderResult.value(getDeaths(ctx, arg));
            }
        });
        Placeholders.register(new Identifier("ninelives", "player_deaths_name"), (ctx, arg) -> arg == null ? PlaceholderResult.invalid("No argument!") : PlaceholderResult.value(getDeathsName(ctx, arg)));
        Placeholders.register(new Identifier("ninelives", "team"), (ctx, arg) -> {
            if (arg == null) {
                return !ctx.hasPlayer() ? PlaceholderResult.invalid("No player!") : PlaceholderResult.value(getTeam(ctx));
            } else {
                ServerPlayerEntity player = ctx.server().getPlayerManager().getPlayer(arg);
                return player == null ? PlaceholderResult.invalid("No player with that name found!") : PlaceholderResult.value(getTeam(player));
            }
        });
        Placeholders.register(new Identifier("ninelives", "team_display_name"), (ctx, arg) -> {
            if (arg == null) {
                return !ctx.hasPlayer() ? PlaceholderResult.invalid("No player!") : PlaceholderResult.value(getTeamDisplayName(ctx));
            } else {
                ServerPlayerEntity player = ctx.server().getPlayerManager().getPlayer(arg);
                if (player == null) {
                    Team team = ctx.server().getScoreboard().getTeam(arg);
                    return team == null ? PlaceholderResult.value("No player or team found with that name") : PlaceholderResult.value(team.getDisplayName());
                } else {
                    return PlaceholderResult.value(getTeamDisplayName(player));
                }
            }
        });
        Placeholders.register(new Identifier("ninelives", "team_score_name"), (ctx, arg) -> {
            if (arg == null) {
                return PlaceholderResult.invalid("argument required");
            } else {
                int teamPlace;
                try {
                    teamPlace = Integer.parseInt(arg);
                } catch (Exception var4) {
                    return PlaceholderResult.invalid("number between 1 and 16 required");
                }

                return teamPlace >= 1 && teamPlace <= 16 ? PlaceholderResult.value(getTeamPlaceDisplayName(ctx, teamPlace - 1)) : PlaceholderResult.invalid("number between 1 and 16 required");
            }
        });
        Placeholders.register(new Identifier("ninelives", "cm_to_score"), (ctx, arg) -> {
            double height;

            try {
                height = Double.parseDouble(arg);
            } catch (Exception err) {
                return PlaceholderResult.value("height not a number");
            }

            return PlaceholderResult.value(cmToScale(height));
        });
    }

    public static Text getLives(PlaceholderContext ctx) {
        if (ctx.player().getScoreboardTeam() == null) {
            return Text.literal("∞").formatted(Formatting.OBFUSCATED);
        } else {
            String teamName = ctx.player().getScoreboardTeam().getName();
            Scoreboard scoreboard = ctx.player().getScoreboard();
            ScoreboardObjective livesObjective = scoreboard.getNullableObjective("9lives_lives");
            if (livesObjective == null) {
                return Text.literal("∞").formatted(Formatting.OBFUSCATED);
            } else if (scoreboard.playerHasObjective(teamName, livesObjective)) {
                ScoreboardPlayerScore lives = scoreboard.getPlayerScore(teamName, livesObjective);
                return Text.of(String.valueOf(lives.getScore()));
            } else {
                return Text.literal("∞").formatted(Formatting.OBFUSCATED);
            }
        }
    }

    public static Text getLives(PlaceholderContext ctx, String arg) {
        Scoreboard scoreboard = ctx.server().getScoreboard();
        ScoreboardObjective livesObjective = scoreboard.getNullableObjective("9lives_lives");
        if (livesObjective == null) {
            return Text.of("Lives objective does not exist");
        } else {
            int teamPlace;

            try {
                teamPlace = Integer.parseInt(arg);
                --teamPlace;
                List<ScoreboardPlayerScore> scores = getSortedScores(scoreboard, "bac_advancements_team");
                if (scores != null && teamPlace < scores.size()) {
                    ScoreboardPlayerScore livesScore = getSiblingScore(scores.get(teamPlace), livesObjective);
                    return Text.of(String.valueOf(livesScore.getScore()));
                } else {
                    return Text.literal("∞").formatted(Formatting.OBFUSCATED);
                }
            } catch (Exception var7) {
                if (!scoreboard.playerHasObjective(arg, livesObjective)) {
                    Team playerTeam = scoreboard.getPlayerTeam(arg);
                    if (playerTeam == null) {
                        return Text.literal("∞").formatted(Formatting.OBFUSCATED);
                    } else {
                        return scoreboard.playerHasObjective(playerTeam.getName(), livesObjective) ? Text.of(String.valueOf(scoreboard.getPlayerScore(playerTeam.getName(), livesObjective).getScore())) : Text.literal("∞").formatted(Formatting.OBFUSCATED);
                    }
                } else {
                    return Text.of(String.valueOf(scoreboard.getPlayerScore(arg, livesObjective).getScore()));
                }
            }
        }
    }

    public static Text getTeamAdvancements(PlaceholderContext ctx) {
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

        if (ctx.player().getScoreboardTeam() == null) {
            return Text.literal("00").formatted(Formatting.OBFUSCATED);
        } else {
            String teamName = teamNameMap.get(ctx.player().getScoreboardTeam().getName());
            Scoreboard scoreboard = ctx.player().getScoreboard();
            ScoreboardObjective scoreObjective = scoreboard.getNullableObjective("bac_advancements_team");
            if (scoreObjective == null) {
                return Text.literal("00").formatted(Formatting.OBFUSCATED);
            } else if (scoreboard.playerHasObjective(teamName, scoreObjective)) {
                ScoreboardPlayerScore advancements = scoreboard.getPlayerScore(teamName, scoreObjective);
                return Text.of(String.valueOf(advancements.getScore()));
            } else {
                return Text.literal("00").formatted(Formatting.OBFUSCATED);
            }
        }
    }

    public static Text getTeamAdvancements(PlaceholderContext ctx, String arg) {
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

        Scoreboard scoreboard = ctx.server().getScoreboard();
        ScoreboardObjective scoreObjective = scoreboard.getNullableObjective("bac_advancements_team");
        if (scoreObjective == null) {
            return Text.of("Objective does not exist");
        } else {
            int teamPlace;

            try {
                teamPlace = Integer.parseInt(arg);
                --teamPlace;
                List<ScoreboardPlayerScore> scores = getSortedScores(scoreboard, "bac_advancements_team");
                return scores != null && teamPlace < scores.size() ? Text.of(String.valueOf(scores.get(teamPlace).getScore())) : Text.literal("00").formatted(Formatting.OBFUSCATED);
            } catch (Exception var6) {
                if (!scoreboard.playerHasObjective(arg, scoreObjective)) {
                    Team playerTeam = scoreboard.getTeam(arg);
                    String search = teamNameMap.get(arg);
                    if (playerTeam == null) {
                        return Text.literal("00").formatted(Formatting.OBFUSCATED);
                    } else {
                        return scoreboard.playerHasObjective(search, scoreObjective) ? Text.of(String.valueOf(scoreboard.getPlayerScore(search, scoreObjective).getScore())) : Text.literal("00").formatted(Formatting.OBFUSCATED);
                    }
                } else {
                    return Text.of(String.valueOf(scoreboard.getPlayerScore(arg, scoreObjective).getScore()));
                }
            }
        }
    }

    public static Text getAdvancements(PlaceholderContext ctx) {
        Scoreboard scoreboard = ctx.player().getScoreboard();
        ctx.player().getScore();
        ScoreboardObjective scoreObjective = scoreboard.getNullableObjective("bac_advancements");
        if (scoreObjective == null) {
            return Text.literal("00").formatted(Formatting.OBFUSCATED);
        } else if (scoreboard.playerHasObjective(ctx.player().getName().getString(), scoreObjective)) {
            ScoreboardPlayerScore advancements = scoreboard.getPlayerScore(ctx.player().getName().getString(), scoreObjective);
            return Text.of(String.valueOf(advancements.getScore()));
        } else {
            return Text.literal("00").formatted(Formatting.OBFUSCATED);
        }
    }

    public static Text getAdvancements(PlaceholderContext ctx, String arg) {
        Scoreboard scoreboard = ctx.server().getScoreboard();
        ScoreboardObjective scoreObjective = scoreboard.getNullableObjective("bac_advancements");
        if (scoreObjective == null) {
            return Text.of("Objective does not exist");
        } else {
            int playerPlace;

            try {
                playerPlace = Integer.parseInt(arg);
                --playerPlace;
                List<ScoreboardPlayerScore> scores = getSortedScores(scoreboard, "bac_advancements");
                return scores != null && playerPlace < scores.size() ? Text.of(String.valueOf(scores.get(playerPlace).getScore())) : Text.literal("00").formatted(Formatting.OBFUSCATED);
            } catch (Exception var6) {
                return !scoreboard.playerHasObjective(arg, scoreObjective) ? Text.literal("0") : Text.of(String.valueOf(scoreboard.getPlayerScore(arg, scoreObjective).getScore()));
            }
        }
    }

    public static Text getAdvancementsName(PlaceholderContext ctx, String arg) {
        Scoreboard scoreboard = ctx.server().getScoreboard();
        ScoreboardObjective scoreObjective = scoreboard.getNullableObjective("bac_advancements");
        if (scoreObjective == null) {
            return Text.of("Objective does not exist");
        } else {
            int playerPlace;

            try {
                playerPlace = Integer.parseInt(arg);
                --playerPlace;
                List<ScoreboardPlayerScore> scores = getSortedScores(scoreboard, "bac_advancements");
                if (scores != null && playerPlace < scores.size()) {
                    Text playerName = Text.literal(scores.get(playerPlace).getPlayerName());
                    Team playerTeam = scoreboard.getPlayerTeam(scores.get(playerPlace).getPlayerName());
                    return playerTeam == null ? playerName : playerTeam.decorateName(playerName);
                } else {
                    return Text.literal("12345678").formatted(Formatting.OBFUSCATED);
                }
            } catch (Exception var8) {
                return Text.literal("12345678").formatted(Formatting.OBFUSCATED);
            }
        }
    }

    public static Text getDeaths(PlaceholderContext ctx) {
        Scoreboard scoreboard = ctx.player().getScoreboard();
        ctx.player().getScore();
        ScoreboardObjective scoreObjective = scoreboard.getNullableObjective("ninelives_deaths");
        if (scoreObjective == null) {
            return Text.literal("0").formatted(Formatting.OBFUSCATED);
        } else if (scoreboard.playerHasObjective(ctx.player().getName().getString(), scoreObjective)) {
            ScoreboardPlayerScore advancements = scoreboard.getPlayerScore(ctx.player().getName().getString(), scoreObjective);
            return Text.of(String.valueOf(advancements.getScore()));
        } else {
            return Text.literal("0").formatted(Formatting.OBFUSCATED);
        }
    }

    public static Text getDeaths(PlaceholderContext ctx, String arg) {
        Scoreboard scoreboard = ctx.server().getScoreboard();
        ScoreboardObjective scoreObjective = scoreboard.getNullableObjective("ninelives_deaths");
        if (scoreObjective == null) {
            return Text.of("Objective does not exist");
        } else {
            int playerPlace;

            try {
                playerPlace = Integer.parseInt(arg);
                --playerPlace;
                List<ScoreboardPlayerScore> scores = getSortedScores(scoreboard, "ninelives_deaths");
                return scores != null && playerPlace < scores.size() ? Text.of(String.valueOf(scores.get(playerPlace).getScore())) : Text.literal("0").formatted(Formatting.OBFUSCATED);
            } catch (Exception var6) {
                return !scoreboard.playerHasObjective(arg, scoreObjective) ? Text.literal("0") : Text.of(String.valueOf(scoreboard.getPlayerScore(arg, scoreObjective).getScore()));
            }
        }
    }

    public static Text getDeathsName(PlaceholderContext ctx, String arg) {
        Scoreboard scoreboard = ctx.server().getScoreboard();
        ScoreboardObjective scoreObjective = scoreboard.getNullableObjective("ninelives_deaths");
        if (scoreObjective == null) {
            return Text.of("Objective does not exist");
        } else {
            int playerPlace;

            try {
                playerPlace = Integer.parseInt(arg);
                --playerPlace;
                List<ScoreboardPlayerScore> scores = getSortedScores(scoreboard, "ninelives_deaths");
                if (scores != null && playerPlace < scores.size()) {
                    Text playerName = Text.literal(scores.get(playerPlace).getPlayerName());
                    Team playerTeam = scoreboard.getPlayerTeam(scores.get(playerPlace).getPlayerName());
                    return playerTeam == null ? playerName : playerTeam.decorateName(playerName);
                } else {
                    return Text.literal("12345678").formatted(Formatting.OBFUSCATED);
                }
            } catch (Exception var8) {
                return Text.literal("12345678").formatted(Formatting.OBFUSCATED);
            }
        }
    }

    public static String getTeam(PlaceholderContext ctx) {
        if (ctx.player().getScoreboardTeam() == null) {
            return "0";
        } else {
            Team team = (Team)ctx.player().getScoreboardTeam();
            return team.getName().substring(9);
        }
    }

    public static String getTeam(ServerPlayerEntity player) {
        if (player.getScoreboardTeam() == null) {
            return "0";
        } else {
            Team team = (Team)player.getScoreboardTeam();
            return team.getName().substring(9);
        }
    }

    public static Text getTeamDisplayName(PlaceholderContext ctx) {
        if (ctx.player().getScoreboardTeam() == null) {
            return Text.literal("None");
        } else {
            Team team = (Team)ctx.player().getScoreboardTeam();
            return team.getDisplayName();
        }
    }

    public static Text getTeamDisplayName(ServerPlayerEntity player) {
        if (player.getScoreboardTeam() == null) {
            return Text.literal("None");
        } else {
            Team team = (Team)player.getScoreboardTeam();
            return team.getDisplayName();
        }
    }

    public static String cmToScale(double height) {
        double clampedHeight = Math.max(Math.min(height, 250.0), 100.0);
        double scale = clampedHeight / 187.5;
        return Double.toString(scale);
    }

    public static Text getTeamPlaceDisplayName(PlaceholderContext ctx, int place) {
        Scoreboard scoreboard = ctx.server().getScoreboard();
        List<ScoreboardPlayerScore> scores = getSortedScores(scoreboard, "bac_advancements_team");
        if (scores != null && place < scores.size()) {
            Team displayTeam = scoreboard.getPlayerTeam(scores.get(place).getPlayerName());
            return displayTeam.getDisplayName().copy().formatted(displayTeam.getColor());
        } else {
            return Text.literal("12345678").formatted(Formatting.OBFUSCATED);
        }
    }

    public static List<ScoreboardPlayerScore> getSortedScores(Scoreboard scoreboard, String objective) {
        ScoreboardObjective advancementsObjective = scoreboard.getNullableObjective(objective);
        if (advancementsObjective == null) {
            return null;
        } else {
            try {
                Collection<ScoreboardPlayerScore> scoreCollection = scoreboard.getAllPlayerScores(advancementsObjective);
                return scoreCollection.isEmpty() ? null : scoreboard.getAllPlayerScores(advancementsObjective).stream().sorted(HIGH_SCORE_COMPARATOR).toList();
            } catch (Exception var4) {
                return null;
            }
        }
    }

    public static ScoreboardPlayerScore getSiblingScore(ScoreboardPlayerScore score, ScoreboardObjective siblingScore) {
        return !score.getScoreboard().playerHasObjective(score.getPlayerName(), siblingScore) ? null : score.getScoreboard().getPlayerScore(score.getPlayerName(), siblingScore);
    }
}
