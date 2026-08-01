package ru.example.airdrops;

import org.bukkit.Material;

public enum Rarity {
    PEACEFUL("Мирный", "§b", Material.BARREL, "BLUE"),
    EPIC("Эпический", "§a", Material.LODESTONE, "GREEN"),
    LEGENDARY("Легендарный", "§d", Material.RESPAWN_ANCHOR, "PURPLE"),
    MYTHIC("Мифический", "§c", Material.RED_SHULKER_BOX, "NETHER"),
    ULTRA_LEGENDARY("Ультра-легендарный", "§5", Material.PURPLE_SHULKER_BOX, "END");

    private final String display;
    private final String color;
    private final Material fakeMaterial;
    private final String theme;

    Rarity(String display, String color, Material fakeMaterial, String theme) {
        this.display = display;
        this.color = color;
        this.fakeMaterial = fakeMaterial;
        this.theme = theme;
    }

    public String display() { return display; }
    public String color() { return color; }
    public Material fakeMaterial() { return fakeMaterial; }
    public String theme() { return theme; }

    public static Rarity parse(String s) {
        return switch (s.toLowerCase()) {
            case "peaceful", "мирный", "normal" -> PEACEFUL;
            case "epic", "эпический" -> EPIC;
            case "legendary", "легендарный" -> LEGENDARY;
            case "mythic", "мифический" -> MYTHIC;
            case "ultra", "ultra_legendary", "ультра", "ультра-легендарный" -> ULTRA_LEGENDARY;
            default -> null;
        };
    }
}
