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
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class QuestsCommand implements CommandExecutor {

    // “I'll keep all my emotions right here, and then one day I'll die” - John Malaney
    @SuppressWarnings("NullableProblems")
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
                        if(args.length == 6) {
                            if(args[1].equalsIgnoreCase("main")) {
                                Quests.totalMainQuestsMap.put(args[2], new QuestLog(args[2], args[5], Integer.parseInt(args[3]), args[4], player.getLocation()));
                                Quests.saveJson();
                            } else if (args[1].equalsIgnoreCase("side")) {
                                Quests.totalSideQuestsMap.put(args[2], new QuestLog(args[2], args[5], Integer.parseInt(args[3]), args[4], player.getLocation()));
                                Quests.saveJson();
                            } else {
                                player.sendMessage(ChatColor.RED + "Invalid argument for create! The first arguement should be \"main\" or \"side\".");
                            }
                        } else {
                            player.sendMessage(ChatColor.RED + "Invalid argument for create! /quests create (main/side) (name) (min level) (length) (description)");
                        }
                        break;
                    case "remove":
                        if(args.length == 3) {
                            if(args[1].equalsIgnoreCase("main")) {
                                if(Quests.totalMainQuestsMap.containsKey(args[1])) {
                                    Quests.totalMainQuestsMap.remove(args[1]);
                                    Quests.saveJson();
                                } else {
                                    player.sendMessage(ChatColor.RED + "Invalid arguments for remove! The name is not a main quest.");
                                }
                            } else if (args[1].equalsIgnoreCase("side")) {
                                if(Quests.totalSideQuestsMap.containsKey(args[1])) {
                                    Quests.totalSideQuestsMap.remove(args[1]);
                                    Quests.saveJson();
                                } else {
                                    player.sendMessage(ChatColor.RED + "Invalid arguments for remove! The name is not a side quest.");
                                }
                            } else {
                                player.sendMessage(ChatColor.RED + "Invalid argument for create! The first arguement should be \"main\" or \"side\".");
                            }
                        } else {
                            player.sendMessage(ChatColor.RED + "Invalid arguments for remove! /quests remove (main/side) (name)");
                        }
                        break;
                    case "open":
                        if(!Quests.playerPageMain.containsKey(player)) {
                            Quests.playerPageMain.put(player, 0);
                        }
                        if(!Quests.playerPageSide.containsKey(player)) {
                            Quests.playerPageSide.put(player, 0);
                        }
                        if(args.length == 2) {
                            if(args[1].equalsIgnoreCase("main")) {
                                openQuestsGUI(player, "main");
                            } else if (args[1].equalsIgnoreCase("side")) {
                                openQuestsGUI(player, "side");
                            } else {
                                player.sendMessage(ChatColor.RED + "Invalid arguments for open! /quests open (main/side)");
                            }
                        } else {
                            player.sendMessage(ChatColor.RED + "Invalid arguments for open! /quests open (main/side)");
                        }
                        //To open the GUI
                        break;
                    default:
                        return false;
                }
            } else {
                //Note to self, remove (1) on release.
                player.sendMessage(ChatColor.RED + "Invalid argument (1)!");
            }
        }
        return false;
    }
    public static void openQuestsGUI(Player player, String string) {
        int playerPage;
        Inventory inventory;
        List<QuestLog> quests;
        if(string.equalsIgnoreCase("main")) {
            inventory = Bukkit.createInventory(player, 54, ChatColor.DARK_AQUA.toString() + ChatColor.BOLD + "Main Quests");
            quests = new ArrayList<>(Quests.totalMainQuestsMap.values());
            playerPage = Quests.playerPageMain.get(player);
        } else if (string.equalsIgnoreCase("side")){
            inventory = Bukkit.createInventory(player, 54, ChatColor.DARK_AQUA.toString() + ChatColor.BOLD + "Side Quests");
            quests = new ArrayList<>(Quests.totalSideQuestsMap.values());
            playerPage = Quests.playerPageSide.get(player);
        } else {
            Bukkit.getLogger().severe("Error! Failed to open inventory at openQuestsGUI() in Quests plugin!");
            return;
        }
        ItemStack bSG = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta bSGMeta = bSG.getItemMeta();
        assert bSGMeta != null;
        bSGMeta.setDisplayName(ChatColor.BLACK.toString());
        bSG.setItemMeta(bSGMeta);
        int slots = 0;
        for (int x = 0; x < quests.size(); x++) {
            if(slots == 45) {break;}
            if (x >= playerPage*45) {
                inventory.setItem(slots, quests.get(x).itemize());
                slots++;
            }
        }
        for(int x = slots; x < 45; x++) {
            inventory.setItem(x, bSG);
        }
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
        assert skullMeta != null;
        PlayerProfile profile = Bukkit.createPlayerProfile(UUID.randomUUID());
        PlayerTextures textures = profile.getTextures();
        if(playerPage > 0) {
            try {
                textures.setSkin(new URL("https://textures.minecraft.net/texture/f7aacad193e2226971ed95302dba433438be4644fbab5ebf818054061667fbe2"));
            } catch (MalformedURLException e) {throw new RuntimeException(e);}
            skullMeta.setOwnerProfile(profile);
            skullMeta.setDisplayName(ChatColor.YELLOW + "Previous Page");
            skull.setItemMeta(skullMeta);
            inventory.setItem(45, skull);
        }
        if (quests.size() > (playerPage+1)*44) {
            try {
                textures.setSkin(new URL("https://textures.minecraft.net/texture/d34ef0638537222b20f480694dadc0f85fbe0759d581aa7fcdf2e43139377158"));
            } catch (MalformedURLException e) {throw new RuntimeException(e);}
            skullMeta.setOwnerProfile(profile);
            skullMeta.setDisplayName(ChatColor.YELLOW + "Next Page");
            skull.setItemMeta(skullMeta);
            inventory.setItem(53, skull);
        }
        player.openInventory(inventory);
    }
}
