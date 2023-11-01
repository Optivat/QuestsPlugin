package com.titanborn.plugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class QuestLog {
    public String name;
    public String description;
    public int minLevel;
    public String length;
    public Map<String, Object> location;
    public boolean main;
    public ArrayList<String> objectives;

    public QuestLog () {
        this.name = "null";
        this.description = "Change me!";
        this.minLevel = Integer.MAX_VALUE;
        this.length = ChatColor.BOLD.toString() + ChatColor.MAGIC + "Change me!";
        this.location = null;
        main = true;
        objectives = new ArrayList<>();
    }

    public ItemStack itemize() {
        ItemStack questItem = new ItemStack(Material.WRITABLE_BOOK);
        ItemMeta itemMeta = questItem.getItemMeta();
        assert itemMeta != null;
        itemMeta.setDisplayName(ChatColor.YELLOW + name);
        List<String> lore = new ArrayList<>();
        lore.add("");
        //This will change, to where it will actually check the level.
        lore.add(ChatColor.GRAY + "❌ Combat Lv. Min: " + minLevel);
        lore.add(ChatColor.GRAY + "- Length: " + ChatColor.BOLD + length);
        lore.add("");
        //Creating the description for the lore
        String[] descriptionSplit = description.split("\\s");
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
        if(!objectives.isEmpty()) {
            lore.add(ChatColor.GRAY + "Current Objective: ");

        }

        if (location == null) {
            lore.add(ChatColor.GRAY + "(Location is null!)");
        } else {
            Location loc = Location.deserialize(location);
            lore.add(ChatColor.GRAY + "(" + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ() + ")");
        }
        itemMeta.setLore(lore);
        questItem.setItemMeta(itemMeta);
        return questItem;
    }

    public void delete() {
        if(!Quests.totalMainQuestsMap.containsKey(this.name) && !Quests.totalSideQuestsMap.containsKey(this.name)) {
            Bukkit.getLogger().severe("Error removing quest, it does not exist inside maps! Contact Optivat to fix...");
            return;
        }
        if(main) {
            Quests.totalMainQuestsMap.remove(this.name);
        } else {
            Quests.totalSideQuestsMap.remove(this.name);
        }
    }

    public static QuestLog getQuestByMetaName(String name, String string) {
        if(string.equalsIgnoreCase("main")) {
            for(QuestLog questLog : Quests.totalMainQuestsMap.values()) {
                if(Objects.requireNonNull(questLog.itemize().getItemMeta()).getDisplayName().equalsIgnoreCase(name)) {
                    return questLog;
                }
            }
        } else if (string.equalsIgnoreCase("side")) {
            for(QuestLog questLog : Quests.totalSideQuestsMap.values()) {
                if(Objects.requireNonNull(questLog.itemize().getItemMeta()).getDisplayName().equalsIgnoreCase(name)) {
                    return questLog;
                }
            }
        }
        Bukkit.getLogger().info("NULL for: Method, getQuestByName()");
        return null;
    }

}
