package com.titanborn.plugin.commands;

import com.titanborn.plugin.QuestLog;
import com.titanborn.plugin.Quests;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.UUID;

public class QuestsCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player) {
            Player player = (Player) sender;
            if(args.length >= 1) {
                switch(args[0]) {
                    case "update":
                        //To update the JSON in game, will be removed when JSON goes online.
                        Quests.saveJson();
                        break;
                    case "create":
                        //To create quests in game, will be removed when JSON goes online.
                        if(args.length == 5) {
                            Quests.totalQuestsMap.put(args[1], new QuestLog(args[1], args[4], Integer.parseInt(args[2]), args[3], player.getLocation()));
                            Quests.saveJson();
                        } else {
                            player.sendMessage(ChatColor.RED + "Invalid arguement for create! /quests create (name) (min level) (length) (description)");
                        }
                        break;
                    case "remove":
                        if(args.length == 2) {
                            if(Quests.totalQuestsMap.containsKey(args[1])) {
                                Quests.totalQuestsMap.remove(args[1]);
                                Quests.saveJson();
                            } else {
                                player.sendMessage(ChatColor.RED + "Invalid arguements for remove! The name is not a quest.");
                            }
                        } else {
                            player.sendMessage(ChatColor.RED + "Invalid arguements for remove! /quests remove (name)");
                        }
                        break;
                    case "open":
                        //To open the GUI
                        Inventory inventory = Bukkit.createInventory(player, 54, ChatColor.DARK_AQUA  + "" + ChatColor.BOLD + "Quests");
                        ItemStack bSG = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
                        ItemMeta bSGMeta = bSG.getItemMeta();
                        assert bSGMeta != null;
                        bSGMeta.setDisplayName(ChatColor.BLACK + "");
                        bSG.setItemMeta(bSGMeta);
                        int slots = 0;
                        for(QuestLog quest : Quests.totalQuestsMap.values()) {
                            inventory.setItem(slots, quest.itemize());
                            slots+=1;
                        }
                        for(int x = slots; x < 45; x++) {
                            inventory.setItem(x, bSG);
                        }
                        ItemStack skull = new ItemStack(Material.PLAYER_HEAD, 1);
                        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
                        assert skullMeta != null;
                        skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(UUID.fromString("a68f0b64-8d14-4000-a95f-4b9ba14f8df9")));
                        skull.setItemMeta(skullMeta);
                        inventory.setItem(45, skull);
                        skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(UUID.fromString("50c8510b-5ea0-4d60-be9a-7d542d6cd156")));
                        skull.setItemMeta(skullMeta);
                        inventory.setItem(53, skull);
                        player.openInventory(inventory);
                        break;
                    default:
                        return false;
                }
            } else {
                //Note to self, remove (1) on release.
                player.sendMessage(ChatColor.RED + "Invalid arguement (1)!");
            }
        }
        return false;
    }
}
