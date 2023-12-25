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

    public static void onTick(ServerWorld world) {
        long leaderboardCheckInterval = 20L;
        if (world.equals(world.getServer().getOverworld()) && world.getTime() - lastLeaderboardCheckTime >= leaderboardCheckInterval) {
            lastLeaderboardCheckTime = world.getTime();
            holograms.forEach((name, hologram) -> {
                ITextDisplayEntityMixin textDisplayEntityMixin = (ITextDisplayEntityMixin)hologram;
                hologram.getDataTracker().set(textDisplayEntityMixin.getTextData(), Placeholders.parseText(TextParserUtils.formatText(textDisplayEntityMixin.getHologramTextPlaceholder()), PlaceholderContext.of(world.getServer())));
            });
        }
    }
}
