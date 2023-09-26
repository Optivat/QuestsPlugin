package com.titanborn.plugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class QuestLog {
    public String name;
    public String description;
    public int minLevel;
    public String length;
    public Map<String, Object> location;

    public QuestLog(String name, String description, int minLevel, String length, Location location) {
        this.name = name;
        this.description = description;
        this.minLevel = minLevel;
        this.length = length;
        this.location = location.serialize();
    }
    @SuppressWarnings("unused")
    public QuestLog(String name, String description, int minLevel, String length, Map<String, Object> location) {
        this.name = name;
        this.description = description;
        this.minLevel = minLevel;
        this.length = length;
        this.location = location;
    }

    public ItemStack itemize() {
        ItemStack questItem = new ItemStack(Material.WRITABLE_BOOK);
        ItemMeta itemMeta = questItem.getItemMeta();
        assert itemMeta != null;
        itemMeta.setDisplayName(ChatColor.YELLOW + name.replace("_", " ").trim());
        List<String> lore = new ArrayList<>();
        lore.add("");
        //This will change, to where it will actually check the level.
        lore.add(ChatColor.GRAY + "❌ Combat Lv. Min: " + minLevel);
        lore.add(ChatColor.GRAY + "- Length: " + ChatColor.BOLD + length);
        lore.add("");
        //Creating the description for the lore
        String[] descriptionSplit = description.replace("_", " ").split("\\s");
        StringBuilder loreline = new StringBuilder();
        for(String string : descriptionSplit) {
            loreline.append(" ").append(string);
            if(loreline.toString().length() > 25) {
                lore.add(ChatColor.GRAY + loreline.toString().trim());
                loreline = new StringBuilder();
            }
        }
        if(!loreline.toString().equalsIgnoreCase("")) {
            lore.add(ChatColor.GRAY + loreline.toString().trim());
        }
        lore.add("");
        Location loc = Location.deserialize(location);
        lore.add(ChatColor.GRAY + "(" + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ() + ")");
        itemMeta.setLore(lore);
        questItem.setItemMeta(itemMeta);
        return questItem;
    }

    public static QuestLog getQuestByMetaName(String name) {
        for(QuestLog questLog : Quests.totalQuestsMap.values()) {
            if(Objects.requireNonNull(questLog.itemize().getItemMeta()).getDisplayName().equalsIgnoreCase(name)) {
                return questLog;
            }
        }
        Bukkit.getLogger().info("NULL for: Method, getQuestByName()");
        return null;
    }

}
