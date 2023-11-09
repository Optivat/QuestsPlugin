package com.titanborn.plugin.commands;

import com.titanborn.plugin.PlayerQuestInfo;
import com.titanborn.plugin.QuestLog;
import com.titanborn.plugin.Quests;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

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
                        if(!Quests.currentQuestCreation.containsKey(player)) {
                            Quests.currentQuestCreation.put(player, new QuestLog());
                        }
                        QuestsCommand.openQuestsCreationGUI(player, Quests.currentQuestCreation.get(player));
                        //This comment should be temporary and be removed once the new quest creation is done.
                        /*if(args.length == 6) {
                            if(args[1].equalsIgnoreCase("main")) {
                                Quests.totalMainQuestsMap.put(args[2], new QuestLog(true, args[2].replace("_", " ").trim(), args[5].replace("\\_", "{]-+-oj90pojsnuf9").replace("_", " ").replace("{]-+-oj90pojsnuf9", "_"), Integer.parseInt(args[3]), args[4], player.getLocation()));
                                Quests.saveJson();
                            } else if (args[1].equalsIgnoreCase("side")) {
                                QuestsCommand.openQuestsCreationGUI(player, new QuestLog());
                                Quests.saveJson();
                            } else {
                                player.sendMessage(ChatColor.RED + "Invalid argument for create! The first argument should be \"main\" or \"side\".");
                            }
                        } else {
                            player.sendMessage(ChatColor.RED + "Invalid argument for create! /quests create (main/side) (name) (min level) (length) (description)");
                        }*/
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
                                player.sendMessage(ChatColor.RED + "Invalid argument for create! The first argument should be \"main\" or \"side\".");
                            }
                        } else {
                            player.sendMessage(ChatColor.RED + "Invalid arguments for remove! /quests remove (main/side) (name)");
                        }
                        break;
                    case "open":
                        if(!Quests.playerQuestInfo.containsKey(player.getUniqueId())) {
                            Quests.playerQuestInfo.put(player.getUniqueId(), new PlayerQuestInfo(player));
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
        PlayerQuestInfo playerInfo = Quests.playerQuestInfo.get(player.getUniqueId());
        //Creating inventory
        inventory = Bukkit.createInventory(player, 54, ChatColor.DARK_AQUA.toString() + ChatColor.BOLD + "Side Quests");
        //Gets all side quests
        quests = new ArrayList<>(Quests.totalSideQuestsMap.values());
        //Gets the player's current page, if it is opened for the first time in the command it will be assigned to 0, ensuring this won't return null.
        playerPage = playerInfo.playerPageSide;
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
                inventory.setItem(slots, quests.get(x).itemize(player));
                slots++;
            }
        }
        //If slots != 45, this will fill in the gaps of the quests
        for(int x = slots; x < 45; x++) {
            inventory.setItem(x, bSG);
        }
        ItemStack skull = new ItemStack(Material.ARROW, 1);
        ItemMeta skullMeta = skull.getItemMeta();
        //In 1.20 this doesn't work, I haven't explored why, but it will be commented until the server updates to 1.20
        /*ItemStack skull = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
        */assert skullMeta != null;/*
        PlayerProfile profile = Bukkit.createPlayerProfile(UUID.randomUUID());
        PlayerTextures textures = profile.getTextures();
        */
        //Close block
        ItemStack border = new ItemStack(Material.BARRIER);
        ItemMeta borderMeta = border.getItemMeta();
        if(borderMeta == null) {Bukkit.getLogger().severe(Quests.prefix +"Close Barrier Meta is null? Ask Optivat to fix this, send him the log of the console."); return;}
        borderMeta.setDisplayName(ChatColor.RED + "Close");
        border.setItemMeta(borderMeta);
        inventory.setItem(49, border);

        if (playerPage > 0) {
            //In 1.20 this doesn't work, I haven't explored why, but it will be commented until the server updates to 1.20
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

        if (quests.size() > (playerPage+1)*44) {
            //In 1.20 this doesn't work, I haven't explored why, but it will be commented until the server updates to 1.20
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

        //If (Player has quest Selected) show next OBjective in lore under description.


        //Variable initialization
        int playerPage;
        Inventory inventory;
        List<QuestLog> quests;
        PlayerQuestInfo playerInfo = Quests.playerQuestInfo.get(player.getUniqueId());
        //Creates inventory
        inventory = Bukkit.createInventory(player, 54, ChatColor.DARK_AQUA.toString() + ChatColor.BOLD + "Main Quests");
        //Gets all Main Quests into an array from first to last in order
        quests = new ArrayList<>(Quests.totalMainQuestsMap.values());
        //Gets the playerPage, if the player hasn't opened this menu before it is assigned prior as to avoid null
        playerPage = playerInfo.playerPageMain;

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

        //It may be argued that an integer can be used here, but I feel that assigning ItemStacks in order from first to last will be used in the future.
        ArrayList<ItemStack> tempOrganization = new ArrayList<>();
        //This for loop is to shift the snake by 2
        for (int i = 0; i < 8; i += 2) {
            //Goes down 4
            for (int x = i; x <= 27 + i; x += 9) {
                int value = (tempOrganization.size());
                setItemInMainQuests(playerPage, inventory, quests, rG, tempOrganization, x, value, player);
                //After it goes down 4 it goes 1 right
                if (x == (27 + i)) {
                    value = (tempOrganization.size());
                    //This is why I did I+=2 later instead of i+=1;
                    x++;
                    setItemInMainQuests(playerPage, inventory, quests, rG, tempOrganization, x, value, player);
                }
            }
            //Shifts so it will continue the snake up.
            i += 2;
            //Goes up 4
            for (int x = 27 + i; x >= i; x -= 9) {
                int value = (tempOrganization.size());
                setItemInMainQuests(playerPage, inventory, quests, rG, tempOrganization, x, value, player);
                //After it reaches the top, it will go right 1
                if (x == i) {
                    value = (tempOrganization.size());
                    x++;
                    setItemInMainQuests(playerPage, inventory, quests, rG, tempOrganization, x, value, player);
                }
            }
        }
        //This fills out the last column with quests.
        for (int x = 8; x <= 53; x += 9) {
            setItemInMainQuests(playerPage, inventory, quests, rG, tempOrganization, x, tempOrganization.size(), player);
        }

        for (int x = 0; x < 54; x++) {
            //Very lazy
            //There is a lot of commented code that does not work in 1.20.2 but works in 1.19, and I need to investigate into why or find a different solution
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
                if(borderMeta == null) {Bukkit.getLogger().severe(Quests.prefix +"Close Barrier Meta is null? Ask Optivat to fix this, send him the log of the console."); return;}
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

    private static void setItemInMainQuests(int playerPage, Inventory inventory, List<QuestLog> quests, ItemStack rG, ArrayList<ItemStack> tempOrganization, int x, int value, Player player) {
        //The amount, as in the stack, # of items, up to 64... Will mention what will happen if it is >50 later.
        int amount = (value + (playerPage*25));
        //1. Insures that it doesn't get an invalid quest. 2. Ensures that it won't iterate beyond the quests array size.
        if (amount < quests.size()) {
            ItemStack questBook = quests.get(amount).itemize(player);
            ItemMeta questBookMeta = questBook.getItemMeta();
            if(questBookMeta == null) {Bukkit.getLogger().severe(Quests.prefix +"Quest Book Meta is null? Ask Optivat to fix this, send him the log of the console."); return;}
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
            questBook.setItemMeta(questBookMeta);
            inventory.setItem(x, questBook);
        } else {
            tempOrganization.add(rG);
            inventory.setItem(x, rG);
        }
    }
    public static void openQuestsCreationGUI(Player player, QuestLog questLog) {
        Inventory inventory = Bukkit.createInventory(player, 54, ChatColor.DARK_AQUA.toString() + ChatColor.BOLD + "Quest Creation");

        ItemStack bSG = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta bSGMeta = bSG.getItemMeta();
        assert bSGMeta != null;
        bSGMeta.setDisplayName(ChatColor.BLACK.toString());
        bSG.setItemMeta(bSGMeta);

        for(int x = 0; x < 54; x++) {
            inventory.setItem(x, bSG);
        }

        ItemStack wool;
        ItemMeta woolMeta;
        if(questLog.main) {
            wool = new ItemStack(Material.GREEN_WOOL);
            woolMeta = wool.getItemMeta();
            assert woolMeta != null;
            woolMeta.setDisplayName(ChatColor.GREEN + "Click on me to change quest type, currently: " + ChatColor.BOLD + "Main");
            wool.setItemMeta(woolMeta);
        } else {
            wool = new ItemStack(Material.RED_WOOL);
            woolMeta = wool.getItemMeta();
            assert woolMeta != null;
            woolMeta.setDisplayName(ChatColor.RED + "Click on me to change quest type, currently: " + ChatColor.BOLD + "Side");
            wool.setItemMeta(woolMeta);
        }
        inventory.setItem(10, wool);

        ItemStack sign = new ItemStack(Material.OAK_SIGN);
        ItemMeta signMeta = sign.getItemMeta();
        assert signMeta != null;
        signMeta.setDisplayName("Click on me to change name, currently: " + questLog.name);
        sign.setItemMeta(signMeta);

        inventory.setItem(12, sign);

        ItemStack darkOakSign = new ItemStack(Material.DARK_OAK_SIGN);
        ItemMeta darkOakSignMeta = darkOakSign.getItemMeta();
        assert darkOakSignMeta != null;
        darkOakSignMeta.setDisplayName("Click on me to change description, currently:");
        List<String> lore = new ArrayList<>();
        lore.add("");
        //Creating the description for the lore
        String[] descriptionSplit = questLog.description.split("\\s");
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
        darkOakSignMeta.setLore(lore);
        darkOakSign.setItemMeta(darkOakSignMeta);

        inventory.setItem(14, darkOakSign);

        ItemStack ironSword = new ItemStack(Material.IRON_SWORD);
        ItemMeta ironSwordMeta = ironSword.getItemMeta();
        assert ironSwordMeta != null;
        ironSwordMeta.setDisplayName("Click on me to change combat level requirement, currently: " + questLog.minLevel);
        ironSword.setItemMeta(ironSwordMeta);

        inventory.setItem(16, ironSword);

        ItemStack concrete;
        ItemMeta concreteMeta;
        if(questLog.length.equalsIgnoreCase("SHORT")) {
            concrete = new ItemStack(Material.GREEN_CONCRETE);
            concreteMeta = concrete.getItemMeta();
            assert concreteMeta != null;
            concreteMeta.setDisplayName("Click on me to change length, currently: " + questLog.length);
            concrete.setItemMeta(concreteMeta);
        } else if (questLog.length.equalsIgnoreCase("MEDIUM")) {
            concrete = new ItemStack(Material.YELLOW_CONCRETE);
            concreteMeta = concrete.getItemMeta();
            assert concreteMeta != null;
            concreteMeta.setDisplayName("Click on me to change length, currently: " + questLog.length);
            concrete.setItemMeta(concreteMeta);
        } else if(questLog.length.equalsIgnoreCase("LONG")) {
            concrete = new ItemStack(Material.RED_CONCRETE);
            concreteMeta = concrete.getItemMeta();
            assert concreteMeta != null;
            concreteMeta.setDisplayName("Click on me to change length, currently: " + questLog.length);
            concrete.setItemMeta(concreteMeta);
        } else {
            concrete = new ItemStack(Material.BLACK_CONCRETE);
            concreteMeta = concrete.getItemMeta();
            assert concreteMeta != null;
            concreteMeta.setDisplayName("Click on me to change length, currently: " + questLog.length);
            concrete.setItemMeta(concreteMeta);
        }

        inventory.setItem(37, concrete);

        ItemStack skull = new ItemStack(Material.SKELETON_SKULL);
        ItemMeta skullMeta = skull.getItemMeta();
        assert skullMeta != null;
        skullMeta.setDisplayName("Click to select entity for location, default is your location.");
        Location loc;
        if(questLog.location == null) {
            loc = player.getLocation();
            skullMeta.setLore(Arrays.asList("", ChatColor.GRAY + "Currently selected location: ",
                    ChatColor.GOLD + "(" + Objects.requireNonNull(loc.getWorld()).getName() + " , " + loc.getBlockX() + " , " + loc.getBlockY() + " , " + loc.getBlockZ() + ")"));
        } else {
            loc = Location.deserialize(questLog.location);
            skullMeta.setLore(Arrays.asList("", ChatColor.GRAY + "Currently selected location: ",
                    ChatColor.GOLD + "(" + Objects.requireNonNull(loc.getWorld()).getName() + " , " + loc.getBlockX() + " , " + loc.getBlockY() + " , " + loc.getBlockZ() + ")"));
        }
        skull.setItemMeta(skullMeta);

        inventory.setItem(39, skull);

        ItemStack armorStand = new ItemStack(Material.ARMOR_STAND);
        ItemMeta asMeta = armorStand.getItemMeta();
        assert asMeta != null;
        asMeta.setDisplayName("Click to add objectives!");
        lore = new ArrayList<>();
        lore.add("");
        if (!questLog.objectives.isEmpty()) {
            int objectiveIncrement = 1;
            for(String objective : questLog.objectives) {
                lore.add("Objective #" + objectiveIncrement + ": ");
                String[] objectiveSplit = objective.split("\\s");
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
                lore.add("");
            }
        }

        asMeta.setLore(lore);
        armorStand.setItemMeta(asMeta);

        inventory.setItem(41, armorStand);

        ItemStack border = new ItemStack(Material.BARRIER);
        ItemMeta borderMeta = border.getItemMeta();
        assert borderMeta != null;
        borderMeta.setDisplayName(ChatColor.RED + "Close");
        border.setItemMeta(borderMeta);
        inventory.setItem(49, border);

        ItemStack greenDye = new ItemStack(Material.GREEN_DYE);
        ItemMeta greenDyeMeta = greenDye.getItemMeta();
        assert greenDyeMeta != null;
        greenDyeMeta.setDisplayName(ChatColor.GREEN + "Create quest");
        greenDye.setItemMeta(greenDyeMeta);
        inventory.setItem(4, greenDye);

        player.openInventory(inventory);

    }
}
