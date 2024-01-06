package org.tbm.server.ninelives;

import org.tbm.server.ninelives.mixinInterfaces.ITextDisplayEntityMixin;
import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.Placeholders;
import eu.pb4.placeholders.api.TextParserUtils;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;

public class HologramManager {
    public static String teamPlacements = "";
    public static String playerPlacements = "";
    public static String deathPlacements = "";
    public static String teamLivesPlacements = "";
    private static final Map<String, Entity> holograms = new HashMap<>();
    private static long lastLeaderboardCheckTime = 0L;

    public static void addHologram(String name, Entity entity) {
        holograms.put(name, entity);
    }

    public static void removeHologram(String name) {
        holograms.remove(name);
    }

    public static Entity getHologram(String name) {
        return holograms.getOrDefault(name, null);
    }

    public static Set<String> getAllNames() {
        return holograms.keySet();
    }

    public static Map<String, Entity> getHolograms() {
        return holograms;
    }

    public static void clear() {
        holograms.clear();
    }

    public static void teamPlacements(ServerWorld world) {
        StringBuilder teamPlacementsBuilder = new StringBuilder();
        for (int i = 1; i <= 16; i++) {
            teamPlacementsBuilder.append(String.format("%%ninelives:team_score %d%%>%%ninelives:team_score_name %d%%;",i, i));
        }
        teamPlacements = Placeholders.parseText(TextParserUtils.formatText(teamPlacementsBuilder.toString()), PlaceholderContext.of(world.getServer())).getString();
    }

    public static void playerPlacements(ServerWorld world) {
        StringBuilder playerPlacementsBuilder = new StringBuilder();
        for (int i = 1; i <= 48; i++) {
            playerPlacementsBuilder.append(String.format("%%ninelives:player_score %d%%>%%ninelives:player_score_name %d%%;",i, i));
        }
        playerPlacements = Placeholders.parseText(TextParserUtils.formatText(playerPlacementsBuilder.toString()), PlaceholderContext.of(world.getServer())).getString();
    }

    public static void deathPlacements(ServerWorld world) {
        StringBuilder deathPlacementsBuilder = new StringBuilder();
        for (int i = 1; i <= 48; i++) {
            deathPlacementsBuilder.append(String.format("%%ninelives:player_deaths %d%%>%%ninelives:player_deaths_name %d%%;",i, i));
        }
        deathPlacements = Placeholders.parseText(TextParserUtils.formatText(deathPlacementsBuilder.toString()), PlaceholderContext.of(world.getServer())).getString();
    }

    public static void teamLivesPlacements(ServerWorld world) {
        String[] teams = {"bac_team_aqua", "bac_team_black", "bac_team_blue", "bac_team_dark_aqua", "bac_team_dark_blue", "bac_team_dark_gray", "bac_team_dark_green", "bac_team_dark_purple", "bac_team_dark_red", "bac_team_gold", "bac_team_gray", "bac_team_green", "bac_team_light_purple", "bac_team_red", "bac_team_white", "bac_team_yellow"};
        StringBuilder teamLivesPlacementBuilder = new StringBuilder();
        for (String team : teams) {
            teamLivesPlacementBuilder.append(String.format("%%ninelives:team_lives %s%%>%s;", team, world.getScoreboard().getTeam(team).getDisplayName().getString()));
        }
        teamLivesPlacements = Placeholders.parseText(TextParserUtils.formatText(teamLivesPlacementBuilder.toString()), PlaceholderContext.of(world.getServer())).getString();
    }

    public static void onTick(ServerWorld world) {
        long leaderboardCheckInterval = 20L;
        if (world.equals(world.getServer().getOverworld()) && world.getTime() % 23L == 0L) {
            teamPlacements(world);
        }
        if (world.equals(world.getServer().getOverworld()) && world.getTime() % 29L == 0L) {
            playerPlacements(world);
        }
        if (world.equals(world.getServer().getOverworld()) && world.getTime() % 31L == 0L) {
            deathPlacements(world);
        }
        if (world.equals(world.getServer().getOverworld()) && world.getTime() % 37L == 0L) {
            teamLivesPlacements(world);
        }
        if (world.equals(world.getServer().getOverworld()) && world.getTime() - lastLeaderboardCheckTime >= leaderboardCheckInterval) {
            lastLeaderboardCheckTime = world.getTime();
            holograms.forEach((name, hologram) -> {
                ITextDisplayEntityMixin textDisplayEntityMixin = (ITextDisplayEntityMixin)hologram;
                hologram.getDataTracker().set(textDisplayEntityMixin.getTextData(), Placeholders.parseText(TextParserUtils.formatText(textDisplayEntityMixin.getHologramTextPlaceholder()), PlaceholderContext.of(world.getServer())));
            });
        }
    }
}
