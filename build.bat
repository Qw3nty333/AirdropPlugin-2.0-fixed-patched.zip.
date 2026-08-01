package ru.example.airdrops;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public final class AirdropGUI {
    private final AirdropPlugin plugin;

    public AirdropGUI(AirdropPlugin plugin) {
        this.plugin = plugin;
    }

    public void open(Player player) {
        if (!plugin.getConfig().getBoolean("gui.enabled", true)) return;

        int rows = Math.max(1, Math.min(6, plugin.getConfig().getInt("gui.rows", 3)));
        String title = plugin.getConfig().getString("gui.title", "§8Аирдропы §7• §fактивные");
        Inventory inv = Bukkit.createInventory(null, rows * 9, title);

        int slot = 0;
        for (Airdrop drop : plugin.getManager().all()) {
            if (slot >= inv.getSize()) break;

            Material mat = drop.rarity().fakeMaterial();
            ItemStack item = new ItemStack(mat);
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(drop.rarity().color() + drop.rarity().display());
                List<String> lore = new ArrayList<>();
                lore.add("§7ID: §f" + drop.id().toString().substring(0, 8));
                lore.add("§7Мир: §f" + drop.center().getWorld().getName());
                lore.add("§7X: §f" + drop.center().getBlockX() + " §7Y: §f" + drop.center().getBlockY() + " §7Z: §f" + drop.center().getBlockZ());
                lore.add("§7Статус: " + (drop.isDomeLocked() ? "§5Купол" : (drop.isOpened() ? "§aОткрыт" : "§eОжидание")));
                lore.add("");
                lore.add("§aЛКМ §7— телепорт");
                meta.setLore(lore);
                item.setItemMeta(meta);
            }
            inv.setItem(slot++, item);
        }

        if (slot == 0) {
            ItemStack empty = new ItemStack(Material.BARRIER);
            ItemMeta meta = empty.getItemMeta();
            if (meta != null) {
                meta.setDisplayName("§cАктивных аирдропов нет");
                empty.setItemMeta(meta);
            }
            inv.setItem(13, empty);
        }

        player.openInventory(inv);
    }

    public boolean handle(InventoryClickEvent event) {
        String title = event.getView().getTitle();
        String configured = plugin.getConfig().getString("gui.title", "§8Аирдропы §7• §fактивные");
        if (!title.equals(configured)) return false;

        event.setCancelled(true);
        if (!(event.getWhoClicked() instanceof Player player)) return true;

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType().isAir()) return true;

        int index = event.getSlot();
        List<Airdrop> drops = new ArrayList<>(plugin.getManager().all());
        if (index < 0 || index >= drops.size()) return true;

        Airdrop drop = drops.get(index);
        player.closeInventory();
        player.teleport(drop.center().clone().add(0, 1, 0));
        player.sendMessage("§aТелепорт к аирдропу §f" + drop.rarity().display() + "§a.");
        return true;
    }
}
