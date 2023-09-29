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
                        if(args.length == 5) {
                            Quests.totalQuestsMap.put(args[1], new QuestLog(args[1], args[4], Integer.parseInt(args[2]), args[3], player.getLocation()));
                            Quests.saveJson();
                        } else {
                            player.sendMessage(ChatColor.RED + "Invalid argument for create! /quests create (name) (min level) (length) (description)");
                        }
                        break;
                    case "remove":
                        if(args.length == 2) {
                            if(Quests.totalQuestsMap.containsKey(args[1])) {
                                Quests.totalQuestsMap.remove(args[1]);
                                Quests.saveJson();
                            } else {
                                player.sendMessage(ChatColor.RED + "Invalid arguments for remove! The name is not a quest.");
                            }
                        } else {
                            player.sendMessage(ChatColor.RED + "Invalid arguments for remove! /quests remove (name)");
                        }
                        break;
                    case "open":
                        if(!Quests.playerPage.containsKey(player)) {
                            Quests.playerPage.put(player, 0);
                        }
                        //To open the GUI
                        openQuestsGUI(player);
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
    public static void openQuestsGUI(Player player) {
        int playerPage = Quests.playerPage.get(player);
        Inventory inventory = Bukkit.createInventory(player, 54, ChatColor.DARK_AQUA.toString() + ChatColor.BOLD + "Quests");
        ItemStack bSG = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta bSGMeta = bSG.getItemMeta();
        assert bSGMeta != null;
        bSGMeta.setDisplayName(ChatColor.BLACK.toString());
        bSG.setItemMeta(bSGMeta);
        int slots = 0;
        /*for(QuestLog quest : Quests.totalQuestsMap.values()) {
            inventory.setItem(slots, quest.itemize());
            slots+=1;
        }*/
        List<QuestLog> quests = new ArrayList<>(Quests.totalQuestsMap.values());
        for (int x = 0; x < Quests.totalQuestsMap.values().size()-1; x++) {
            if(slots == 44) {break;}
            if (x > playerPage*44) {
                inventory.setItem(x, quests.get(x).itemize());
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
        try {
            textures.setSkin(new URL("https://textures.minecraft.net/texture/d34ef0638537222b20f480694dadc0f85fbe0759d581aa7fcdf2e43139377158"));
        } catch (MalformedURLException e) {throw new RuntimeException(e);}
        skullMeta.setOwnerProfile(profile);
        skullMeta.setDisplayName(ChatColor.YELLOW + "Next Page");
        skull.setItemMeta(skullMeta);
        inventory.setItem(53, skull);
        player.openInventory(inventory);
    }
}
