package org.tbm.server.ninelives;

import net.minecraft.server.world.ServerWorld;

public class APIData extends Thread {
    private long lastLeaderboardCheckTime = 0L;
    private final ServerWorld world;

    public APIData(ServerWorld world) {
        this.world = world;
    }

    public void run() {
        long leaderboardCheckInterval = 20L;

        while (true) {
            if (world.equals(world.getServer().getOverworld()) && world.getTime() - lastLeaderboardCheckTime >= leaderboardCheckInterval) {
                lastLeaderboardCheckTime = world.getTime();
                HologramManager.teamPlacements(world);
                HologramManager.playerPlacements(world);
                HologramManager.deathPlacements(world);
                HologramManager.teamLivesPlacements(world);
            }
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException ignored) {}
        }

    }
}
