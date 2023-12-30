package com.titanborn.plugin;

import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.MMOCoreAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class QuestLog {
    public String name;
    public String description;
    public int minLevel;
    public String length;
    public Map<String, Object> location;
    public boolean main;
    public ArrayList<String> objectives;
    public UUID uuid;

    public QuestLog () {
        this.name = "null";
        this.description = "Change me!";
        this.minLevel = Integer.MAX_VALUE;
        this.length = ChatColor.BOLD.toString() + ChatColor.MAGIC + "Change me!";
        this.location = null;
        main = true;
        objectives = new ArrayList<>();
        uuid = UUID.randomUUID();
    }

    public ItemStack itemize(Player player) {
        PlayerQuestInfo playerInfo = Quests.playerQuestInfo.get(player.getUniqueId());
        ItemStack questItem = new ItemStack(Material.WRITABLE_BOOK);
        ItemMeta itemMeta = questItem.getItemMeta();
        assert itemMeta != null;
        itemMeta.setDisplayName(ChatColor.YELLOW + name);
        List<String> lore = new ArrayList<>();
        lore.add("");
        //This will change, to where it will actually check the level.
        MMOCoreAPI api = new MMOCoreAPI((JavaPlugin) Bukkit.getPluginManager().getPlugin("Quests"));
        int level = api.getPlayerData(player).getLevel();
        if(level > minLevel) {
            lore.add(ChatColor.GRAY + "✔ Combat Lv. Min: " + minLevel);
        } else {
            lore.add(ChatColor.GRAY + "❌ Combat Lv. Min: " + minLevel);

        }
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
        if(!objectives.isEmpty() && playerInfo.currentQuestSelected != null && playerInfo.currentQuestSelected == uuid && QuestLog.getQuestByUUID(playerInfo.currentQuestSelected).main == main) {
            lore.add(ChatColor.GOLD + "Current Objective: ");
            String[] objectiveSplit = objectives.get(playerInfo.currentObjective.get(uuid)).split("\\s");
            loreline = new StringBuilder();
            for(String string : objectiveSplit) {
                loreline.append(" ").append(string);
                if(loreline.toString().length() > 25) {
                    lore.add(ChatColor.GRAY + loreline.toString().trim());
                    loreline = new StringBuilder();
                }
            }
            if(!loreline.toString().equalsIgnoreCase("")) {
                lore.add(ChatColor.GRAY + loreline.toString().trim());
            }
        }
        lore.add("");
        if (Objects.isNull(location)) {
            lore.add(ChatColor.GRAY + "(Location is null!)");
        } else {
            Location loc = Location.deserialize(location);
            lore.add(ChatColor.GRAY + "(" + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ() + ")");
        }

        if(playerInfo.completedQuests.contains(uuid)) {
            lore.add("");
            lore.add(ChatColor.GRAY + "Completed Quest");
            itemMeta.addEnchant(Enchantment.DURABILITY, 1, true);
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        itemMeta.setLore(lore);
        questItem.setItemMeta(itemMeta);
        return questItem;
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

        if (Objects.isNull(location)) {
            lore.add(ChatColor.GRAY + "(Location is null!)");
        } else {
            Location loc = Location.deserialize(location);
            lore.add(ChatColor.GRAY + "(" + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ() + ")");
        }
        itemMeta.setLore(lore);
        questItem.setItemMeta(itemMeta);
        return questItem;
    }

    @Deprecated
    public void delete() {
        if(!Quests.totalMainQuestsMap.containsKey(this.name) && !Quests.totalSideQuestsMap.containsKey(this.name)) {
            Bukkit.getLogger().severe(Quests.prefix +"Error removing quest, it does not exist inside maps! Contact Optivat to fix...");
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
    public static QuestLog getQuestByUUID(UUID uuid) {
        QuestLog questLogUUID = null;
        for(QuestLog questLog : Quests.totalMainQuestsMap.values()) {
            if(uuid.equals(questLog.uuid)) {
                questLogUUID = questLog;
            }
        }
        for(QuestLog questLog : Quests.totalSideQuestsMap.values()) {
            if(uuid.equals(questLog.uuid)) {
                questLogUUID = questLog;
            }
        }
        if(questLogUUID == null) {Bukkit.getLogger().severe(Quests.prefix + "THERE ARE NO QUESTS THAT GOES BY THIS ID, CONTACT OPTIVAT FOR FIX.");}
        return questLogUUID;
    }
}
