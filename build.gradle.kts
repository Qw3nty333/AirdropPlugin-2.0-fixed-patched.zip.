package ru.example.airdrops;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public final class AirdropManager {
    private final AirdropPlugin plugin;
    private final Map<UUID, Airdrop> active = new LinkedHashMap<>();
    private BukkitTask autoTask;

    public AirdropManager(AirdropPlugin plugin) {
        this.plugin = plugin;
    }

    public Collection<Airdrop> all() {
        return Collections.unmodifiableCollection(active.values());
    }

    public void start() {
        stopAuto();
        if (!plugin.getConfig().getBoolean("auto-spawn.enabled", true)) return;

        long interval = Math.max(1, plugin.getConfig().getLong("auto-spawn.interval-minutes", 20)) * 60L * 20L;
        autoTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            int max = plugin.getConfig().getInt("auto-spawn.max-active", 3);
            if (active.size() >= max) return;
            spawnRandom(null);
        }, 40L, interval);
    }

    public void stopAuto() {
        if (autoTask != null) autoTask.cancel();
        autoTask = null;
    }

    public Airdrop spawnRandom(Rarity forced) {
        World world = getWorld();
        if (world == null) return null;

        Rarity rarity = forced != null ? forced : randomRarity();
        Location loc = randomLocation(world);
        if (loc == null) return null;

        int minDistance = plugin.getConfig().getInt("auto-spawn.min-distance-between-drops", 120);
        for (Airdrop drop : active.values()) {
            if (drop.center().getWorld().equals(world) && drop.center().distanceSquared(loc) < minDistance * minDistance) {
                return null;
            }
        }

        Airdrop drop = new Airdrop(plugin, loc, rarity);
        active.put(drop.id(), drop);
        drop.start();

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (drop.isOpened()) {
                // Открытый аирдроп остаётся ещё немного, затем убирается.
                long delay = Math.max(1, plugin.getConfig().getLong("cleanup-after-open-seconds", 300)) * 20L;
                Bukkit.getScheduler().runTaskLater(plugin, () -> remove(drop.id()), delay);
            }
        }, Math.max(20L, 5L * 60L * 20L));

        return drop;
    }

    public void remove(UUID id) {
        Airdrop d = active.remove(id);
        if (d != null) d.remove();
    }

    public void removeAll() {
        for (Airdrop d : new ArrayList<>(active.values())) d.remove();
        active.clear();
    }

    public Airdrop find(Location location) {
        for (Airdrop d : active.values()) {
            if (d.isInside(location)) return d;
        }
        return null;
    }

    public Airdrop findById(String prefix) {
        for (Airdrop d : active.values()) {
            if (d.id().toString().startsWith(prefix)) return d;
        }
        return null;
    }

    private World getWorld() {
        String name = plugin.getConfig().getString("auto-spawn.world", "world");
        World world = Bukkit.getWorld(name);
        if (world != null) return world;
        return Bukkit.getWorlds().isEmpty() ? null : Bukkit.getWorlds().get(0);
    }

    private Location randomLocation(World world) {
        int radius = plugin.getConfig().getInt("auto-spawn.radius", 600);
        double cx = plugin.getConfig().getDouble("auto-spawn.center-x", world.getSpawnLocation().getX());
        double cz = plugin.getConfig().getDouble("auto-spawn.center-z", world.getSpawnLocation().getZ());

        for (int attempt = 0; attempt < 40; attempt++) {
            double angle = ThreadLocalRandom.current().nextDouble(Math.PI * 2);
            double distance = ThreadLocalRandom.current().nextDouble(80, Math.max(81, radius));
            int x = (int) Math.round(cx + Math.cos(angle) * distance);
            int z = (int) Math.round(cz + Math.sin(angle) * distance);
            int y = world.getHighestBlockYAt(x, z) + plugin.getConfig().getInt("spawn-height-offset", 1);

            Block ground = world.getBlockAt(x, y - 1, z);
            if (!ground.getType().isSolid()) continue;
            if (y < world.getMinHeight() + 5 || y > world.getMaxHeight() - 20) continue;

            // Не ставим аирдроп прямо в жидкость.
            if (ground.isLiquid()) continue;

            return new Location(world, x, y, z);
        }
        return null;
    }

    private Rarity randomRarity() {
        Map<Rarity, Integer> weights = new LinkedHashMap<>();
        weights.put(Rarity.PEACEFUL, plugin.getConfig().getInt("auto-spawn.rarity-weights.peaceful", 45));
        weights.put(Rarity.EPIC, plugin.getConfig().getInt("auto-spawn.rarity-weights.epic", 28));
        weights.put(Rarity.LEGENDARY, plugin.getConfig().getInt("auto-spawn.rarity-weights.legendary", 16));
        weights.put(Rarity.MYTHIC, plugin.getConfig().getInt("auto-spawn.rarity-weights.mythic", 8));
        weights.put(Rarity.ULTRA_LEGENDARY, plugin.getConfig().getInt("auto-spawn.rarity-weights.ultra", 3));

        int total = weights.values().stream().mapToInt(Integer::intValue).sum();
        int roll = ThreadLocalRandom.current().nextInt(Math.max(1, total));
        for (var e : weights.entrySet()) {
            roll -= e.getValue();
            if (roll < 0) return e.getKey();
        }
        return Rarity.PEACEFUL;
    }
}
