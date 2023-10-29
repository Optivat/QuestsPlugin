package com.titanborn.plugin.commands;

import com.titanborn.plugin.QuestLog;
import com.titanborn.plugin.Quests;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class QuestsCommand implements CommandExecutor {

    // Suppressing this problems like how I supress stress in real life...
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
                                Quests.totalMainQuestsMap.put(args[2], new QuestLog(args[2].replace("_", " ").trim(), args[5].replace("\\_", "{]-+-oj90pojsnuf9").replace("_", " ").replace("{]-+-oj90pojsnuf9", "_"), Integer.parseInt(args[3]), args[4], player.getLocation()));
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
                                openMainQuestsGUI(player);
                            } else if (args[1].equalsIgnoreCase("side")) {
                                openSideQuestsGUI(player);
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
    public static void openSideQuestsGUI(Player player) {
        //Variable initialization
        int playerPage;
        Inventory inventory;
        List<QuestLog> quests;
        //Creating inventory
        inventory = Bukkit.createInventory(player, 54, ChatColor.DARK_AQUA.toString() + ChatColor.BOLD + "Side Quests");
        //Gets all side quests
        quests = new ArrayList<>(Quests.totalSideQuestsMap.values());
        //Gets the player's current page, if it is opened for the first time in the command it will be assigned to 0, ensuring this won't return null.
        playerPage = Quests.playerPageSide.get(player);
        //Empty space block
        ItemStack bSG = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta bSGMeta = bSG.getItemMeta();
        assert bSGMeta != null;
        bSGMeta.setDisplayName(ChatColor.BLACK.toString());
        bSG.setItemMeta(bSGMeta);
        int slots = 0;
        //iterates through each side quest and puts it in
        for (int x = 0; x < quests.size(); x++) {
            if(slots == 45) {break;}
            //playerPage is used to ensure that the quests aren't the same each page
            if (x >= playerPage*45) {
                inventory.setItem(slots, quests.get(x).itemize());
                slots++;
            }
        }
        //If slots != 45, this will fill in the gaps of the quests
        for(int x = slots; x < 45; x++) {
            inventory.setItem(x, bSG);
        }
        ItemStack skull = new ItemStack(Material.ARROW, 1);
        ItemMeta skullMeta = skull.getItemMeta();
        //In 1.20 this doesn't work, I haven't explored why but it will be commented until the server updates to 1.20
        /*ItemStack skull = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
        */assert skullMeta != null;/*
        PlayerProfile profile = Bukkit.createPlayerProfile(UUID.randomUUID());
        PlayerTextures textures = profile.getTextures();
        */
        //Close block
        ItemStack border = new ItemStack(Material.BARRIER);
        ItemMeta borderMeta = border.getItemMeta();
        if(borderMeta == null) {Bukkit.getLogger().info("Close Barrier Meta is null? Ask Optivat to fix this, send him the log of the console."); return;}
        borderMeta.setDisplayName(ChatColor.RED + "Close");
        border.setItemMeta(borderMeta);
        inventory.setItem(49, border);

        if (playerPage > 0) {
            //In 1.20 this doesn't work, I haven't explored why but it will be commented until the server updates to 1.20
            /*
            try {
                textures.setSkin(new URL("https://textures.minecraft.net/texture/f7aacad193e2226971ed95302dba433438be4644fbab5ebf818054061667fbe2"));
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
            skullMeta.setOwnerProfile(profile);*/
            skullMeta.setDisplayName(ChatColor.YELLOW + "Previous Page");
            skull.setItemMeta(skullMeta);
            inventory.setItem(48, skull);
        }

        if (playerPage < (playerPage+1)*44) {
            //In 1.20 this doesn't work, I haven't explored why but it will be commented until the server updates to 1.20
            /*try {
                textures.setSkin(new URL("https://textures.minecraft.net/texture/d34ef0638537222b20f480694dadc0f85fbe0759d581aa7fcdf2e43139377158"));
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
            skullMeta.setOwnerProfile(profile);*/
            skullMeta.setDisplayName(ChatColor.YELLOW + "Next Page");
            skull.setItemMeta(skullMeta);
            inventory.setItem(50, skull);
        }

        player.openInventory(inventory);
    }
    public static void openMainQuestsGUI(Player player) {
        //Variable initialization
        int playerPage;
        Inventory inventory;
        List<QuestLog> quests;
        //Creates inventory
        inventory = Bukkit.createInventory(player, 54, ChatColor.DARK_AQUA.toString() + ChatColor.BOLD + "Main Quests");
        //Gets all Main Quests into a array from first to last in order
        quests = new ArrayList<>(Quests.totalMainQuestsMap.values());
        //Gets the playerPage, if the player hasn't opened this menu before it is assigned prior as to avoid null
        playerPage = Quests.playerPageMain.get(player);

        //This hasn't been used in the code, I don't know if it will be used, safe to remove for the time being.
        ItemStack bSG = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta bSGMeta = bSG.getItemMeta();
        assert bSGMeta != null;
        bSGMeta.setDisplayName(ChatColor.BLACK.toString());
        bSG.setItemMeta(bSGMeta);

        ItemStack rG = new ItemStack(Material.RED_STAINED_GLASS);
        ItemMeta rGMeta = rG.getItemMeta();
        assert rGMeta != null;
        rGMeta.setDisplayName(ChatColor.RED + "Coming soon...");
        rG.setItemMeta(rGMeta);

        //This will be used when I implement quest completions, these will be hiding the quests until then this is unused
        ItemStack gG = new ItemStack(Material.GRAY_STAINED_GLASS);
        ItemMeta gGMeta = gG.getItemMeta();
        assert gGMeta != null;
        gGMeta.setDisplayName(ChatColor.MAGIC + "L bozo");
        gG.setItemMeta(gGMeta);

        //It may be argued that an integer can be used here but I feel that assigning ItemStacks in order from first to last will be used in the future.
        ArrayList<ItemStack> tempOrganization = new ArrayList<>();
        //This for loop is to shift the snake by 2
        for (int i = 0; i < 8; i += 2) {
            //Goes down 4
            for (int x = i; x <= 27 + i; x += 9) {
                int value = (tempOrganization.size());
                setItemInMainQuests(playerPage, inventory, quests, rG, tempOrganization, x, value);
                //After it goes down 4 it goes 1 right
                if (x == (27 + i)) {
                    value = (tempOrganization.size());
                    //This is why I did i+=2 later instead of i+=1;
                    x++;
                    setItemInMainQuests(playerPage, inventory, quests, rG, tempOrganization, x, value);
                }
            }
            //Shifts so it will continue the snake up.
            i += 2;
            //Goes up 4
            for (int x = 27 + i; x >= i; x -= 9) {
                int value = (tempOrganization.size());
                setItemInMainQuests(playerPage, inventory, quests, rG, tempOrganization, x, value);
                //After it reaches the top, it will go right 1
                if (x == i) {
                    value = (tempOrganization.size());
                    x++;
                    setItemInMainQuests(playerPage, inventory, quests, rG, tempOrganization, x, value);
                }
            }
        }
        //This fills out the last column with quests.
        for (int x = 8; x <= 53; x += 9) {
            setItemInMainQuests(playerPage, inventory, quests, rG, tempOrganization, x, tempOrganization.size());
        }

        for (int x = 0; x < 54; x++) {
            //Very lazy
            //There is a lot of commented code that does not work in 1.20.2 but works in 1.19 and I need to investigate into why or find a different solution
            if (!(x == 0 || (x >= 2 && x <= 4) || (x >= 6 && x <= 8) || x == 9 || x == 11 || x == 13 || x == 15 || x == 17 || x == 18 || x == 20 || x == 22 || x == 24 || x == 26 || (x >= 27 && x <= 29) || (x >= 31 && x <= 33) || x == 35 || x == 44 || x == 53) && !(x == 48 || x == 49 || x == 50)) {
                inventory.setItem(x, bSG);
            } else if (x == 48 || x == 49 || x == 50) {
                ItemStack skull = new ItemStack(Material.ARROW, 1);
                ItemMeta skullMeta = skull.getItemMeta();
                /*ItemStack skull = new ItemStack(Material.PLAYER_HEAD, 1);
                SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
                */assert skullMeta != null;/*
                PlayerProfile profile = Bukkit.createPlayerProfile(UUID.randomUUID());
                PlayerTextures textures = profile.getTextures();
                */
                ItemStack border = new ItemStack(Material.BARRIER);
                ItemMeta borderMeta = border.getItemMeta();
                if(borderMeta == null) {Bukkit.getLogger().info("Close Barrier Meta is null? Ask Optivat to fix this, send him the log of the console."); return;}
                    borderMeta.setDisplayName(ChatColor.RED + "Close");
                border.setItemMeta(borderMeta);
                inventory.setItem(49, border);
                if (playerPage > 0) {
                    /*
                    try {
                        textures.setSkin(new URL("https://textures.minecraft.net/texture/f7aacad193e2226971ed95302dba433438be4644fbab5ebf818054061667fbe2"));
                    } catch (MalformedURLException e) {
                        throw new RuntimeException(e);
                    }
                    skullMeta.setOwnerProfile(profile);*/
                    skullMeta.setDisplayName(ChatColor.YELLOW + "Previous Page");
                    skull.setItemMeta(skullMeta);
                    inventory.setItem(48, skull);
                } else {
                    inventory.setItem(x, bSG);
                }
                if (playerPage < quests.size()/25) {
                    /*try {
                        textures.setSkin(new URL("https://textures.minecraft.net/texture/d34ef0638537222b20f480694dadc0f85fbe0759d581aa7fcdf2e43139377158"));
                    } catch (MalformedURLException e) {
                        throw new RuntimeException(e);
                    }
                    skullMeta.setOwnerProfile(profile);*/
                    skullMeta.setDisplayName(ChatColor.YELLOW + "Next Page");
                    skull.setItemMeta(skullMeta);
                    inventory.setItem(50, skull);
                } else {
                    inventory.setItem(x, bSG);
                }
            }
        }
        player.openInventory(inventory);
    }

    private static void setItemInMainQuests(int playerPage, Inventory inventory, List<QuestLog> quests, ItemStack rG, ArrayList<ItemStack> tempOrganization, int x, int value) {
        //The amount, as in the stack, # of items, up to 64... Will mention what will happen if it is >50 later.
        int amount = (value + (playerPage*25));
        //1. Insures that it doesn't get a invalid quest. 2. Ensures that it won't iterate beyond the quests array size.
        if (quests.get(value) != null && amount < quests.size()) {
            ItemStack questBook = quests.get(amount).itemize();
            ItemMeta questBookMeta = questBook.getItemMeta();
            if(questBookMeta == null) {Bukkit.getLogger().info("Quest Book Meta is null? Ask Optivat to fix this, send him the log of the console."); return;}
            questBook.setAmount(amount);
            //Unique case, to ensure that the amount is nicely organized.
            if (amount == 0) {
                questBook.setType(Material.PAPER);
                questBook.setAmount(1);
            }
            //At 25  Which is at the beginning of each gui, is to ensure that it nicely ends the snake with 50.
            if(playerPage != 0 && amount%((playerPage*25)) == 0 && amount%((playerPage*50)) != 0) {
                questBook.setType(Material.PAPER);
                questBook.setAmount(1);
                tempOrganization.add(questBook);
            } else {
                tempOrganization.add(questBook);
            }
            //Addresses the aforementioned, where if the amount is >50 it switches the item and resets the count back to 1, visually, but continues to increment past 50.
            int change = 50;
            if(amount/change >= 1 && amount%change != 0) {
                questBook.setType(Material.WRITTEN_BOOK);
                questBook.setAmount((amount) - change);
                questBookMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES);
                for(Enchantment echant : questBookMeta.getEnchants().keySet()) {
                    questBookMeta.removeEnchant(echant);
                }
            }
            //Every five it enchants it, why idk I thought it was cool.
            if(amount%5 == 0) {
                questBookMeta.addEnchant(Enchantment.DURABILITY, 1, true);
                questBookMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
            questBook.setItemMeta(questBookMeta);
            inventory.setItem(x, questBook);
        } else {
            tempOrganization.add(rG);
            inventory.setItem(x, rG);
        }
    }
}
