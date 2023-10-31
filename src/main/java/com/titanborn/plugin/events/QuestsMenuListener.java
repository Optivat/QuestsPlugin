package com.titanborn.plugin.events;

import com.titanborn.plugin.QuestLog;
import com.titanborn.plugin.Quests;
import com.titanborn.plugin.commands.QuestsCommand;
import com.titanborn.plugin.events.custom.QuestStartEvent;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.Objects;

public class QuestsMenuListener implements Listener {

    @EventHandler
    public static void onInventoryClick(InventoryClickEvent e) {
        if (e.getView().getTitle().equalsIgnoreCase(ChatColor.DARK_AQUA.toString() + ChatColor.BOLD + "Main Quests")) {
            e.setCancelled(true);
            //To prevent null in console
            if(e.getRawSlot() > 54 || e.getRawSlot() < 0) {return;}
            ItemStack clickedItem = e.getInventory().getItem(e.getRawSlot());
            if (clickedItem == null) {return;}
            if (!clickedItem.hasItemMeta()) {return;}
            //If the user clicks on a quest
            Player player = (Player) e.getWhoClicked();
            removeBeacon(player);
            createBeacon(clickedItem, player, e.getView().getTitle(), e);
            if (Objects.requireNonNull(clickedItem.getItemMeta()).getDisplayName().equalsIgnoreCase(ChatColor.YELLOW + "Previous Page")) {
                int currentPlayerPage = Quests.playerPageMain.get(player);
                //This line of code should never happen, but if it does then this line of code should fix it.
                if(currentPlayerPage <= 0) {Quests.playerPageMain.put(player, 0);QuestsCommand.openMainQuestsGUI(player);player.playSound(player, Sound.BLOCK_NOTE_BLOCK_BASS, 3.0F, 0.5F);return;}
                Quests.playerPageMain.put(player, currentPlayerPage-1);
                QuestsCommand.openMainQuestsGUI(player);
            }
            if (Objects.requireNonNull(clickedItem.getItemMeta()).getDisplayName().equalsIgnoreCase(ChatColor.YELLOW + "Next Page")) {
                int currentPlayerPage = Quests.playerPageMain.get(player);
                //This line of code should never happen, but if it does then this line of code should fix it.
                if(currentPlayerPage < 0) {Quests.playerPageMain.put(player, 0);QuestsCommand.openMainQuestsGUI(player);player.playSound(player, Sound.BLOCK_NOTE_BLOCK_BASS, 3.0F, 0.5F);return;}
                Quests.playerPageMain.put(player, currentPlayerPage+1);
                QuestsCommand.openMainQuestsGUI(player);
            }
            if (Objects.requireNonNull(clickedItem.getItemMeta()).getDisplayName().equalsIgnoreCase(ChatColor.RED + "Close")) {
                e.getView().close();
            }
        }

        if (e.getView().getTitle().equalsIgnoreCase(ChatColor.DARK_AQUA.toString() + ChatColor.BOLD + "Side Quests")) {
            e.setCancelled(true);
            //To prevent null in console
            if(e.getRawSlot() > 54 || e.getRawSlot() < 0) {return;}
            if(Objects.isNull(e.getInventory().getItem(e.getRawSlot()))) {return;}
            ItemStack clickedItem = e.getInventory().getItem(e.getRawSlot());
            if (clickedItem == null) {return;}
            if (!clickedItem.hasItemMeta()) {return;}
            //If the user clicks on a quest
            Player player = (Player) e.getWhoClicked();
            removeBeacon(player);
            createBeacon(clickedItem, player, e.getView().getTitle(), e);
            if (Objects.requireNonNull(clickedItem.getItemMeta()).getDisplayName().equalsIgnoreCase(ChatColor.YELLOW + "Previous Page")) {
                int currentPlayerPage = Quests.playerPageSide.get(player);
                //This line of code should never happen, but if it does then this line of code should fix it.
                if(currentPlayerPage <= 0) {Quests.playerPageSide.put(player, 0);QuestsCommand.openSideQuestsGUI(player);player.playSound(player, Sound.BLOCK_NOTE_BLOCK_BASS, 3.0F, 0.5F);return;}
                Quests.playerPageSide.put(player, currentPlayerPage-1);
                QuestsCommand.openSideQuestsGUI(player);
            }
            if (Objects.requireNonNull(clickedItem.getItemMeta()).getDisplayName().equalsIgnoreCase(ChatColor.YELLOW + "Next Page")) {
                int currentPlayerPage = Quests.playerPageSide.get(player);
                //This line of code should never happen, but if it does then this line of code should fix it.
                if(currentPlayerPage < 0) {Quests.playerPageSide.put(player, 0);QuestsCommand.openSideQuestsGUI(player);player.playSound(player, Sound.BLOCK_NOTE_BLOCK_BASS, 3.0F, 0.5F);return;}
                Quests.playerPageSide.put(player, currentPlayerPage+1);
                QuestsCommand.openSideQuestsGUI(player);
            }
            if (Objects.requireNonNull(clickedItem.getItemMeta()).getDisplayName().equalsIgnoreCase(ChatColor.RED + "Close")) {
                e.getView().close();
            }
        }

        if (e.getView().getTitle().equalsIgnoreCase(ChatColor.DARK_AQUA.toString() + ChatColor.BOLD + "Quest Creation")) {
            e.setCancelled(true);
            //To prevent null in console
            if(e.getRawSlot() > 54 || e.getRawSlot() < 0) {return;}
            ItemStack clickedItem = e.getInventory().getItem(e.getRawSlot());
            if (clickedItem == null) {return;}
            if (!clickedItem.hasItemMeta()) {return;}
            //If the user clicks on a quest
            Player player = (Player) e.getWhoClicked();
            QuestLog questLog = Quests.currentQuestCreation.get(player);
            switch (clickedItem.getType()) {
                case GREEN_WOOL:
                    questLog.main = false;
                    QuestsCommand.openQuestsCreationGUI(player, questLog);
                    break;
                case RED_WOOL:
                    questLog.main = true;
                    QuestsCommand.openQuestsCreationGUI(player, questLog);
                    break;
                case OAK_SIGN:
                    new AnvilGUI.Builder()
                            .onClick((slot, stateSnapshot) -> {
                                if(slot == AnvilGUI.Slot.OUTPUT) {
                                    questLog.name = stateSnapshot.getText();
                                    return Collections.singletonList(AnvilGUI.ResponseAction.close());
                                } else {
                                    return Collections.singletonList(AnvilGUI.ResponseAction.replaceInputText("Try again"));
                                }
                            })
                            .text(questLog.name)
                            .title("Enter the name")
                            .plugin(Bukkit.getPluginManager().getPlugin("Quests"))
                            .onClose(stateSnapshot -> QuestsCommand.openQuestsCreationGUI(player, questLog))
                            .preventClose()
                            .open(player);
                    break;
                case DARK_OAK_SIGN:
                    new AnvilGUI.Builder()
                            .onClick((slot, stateSnapshot) -> {
                                if(slot == AnvilGUI.Slot.OUTPUT) {
                                    if(stateSnapshot.getText().startsWith("+")) {
                                        questLog.description+= stateSnapshot.getText().replace("+", "");
                                    } else if(stateSnapshot.getText().startsWith("-"))  {
                                        try {
                                            int temp = Integer.parseInt(stateSnapshot.getText());
                                            if(questLog.description.length()+temp < 0) {return Collections.singletonList(AnvilGUI.ResponseAction.replaceInputText("Error! Subtracted too much!"));}
                                            questLog.description = questLog.description.substring(0, questLog.description.length()+temp);
                                        } catch (NumberFormatException error) {
                                            return Collections.singletonList(AnvilGUI.ResponseAction.replaceInputText("Error! Invalid number!"));
                                        }
                                    } else {
                                        questLog.description = stateSnapshot.getText();
                                    }
                                    return Collections.singletonList(AnvilGUI.ResponseAction.close());
                                } else {
                                    return Collections.singletonList(AnvilGUI.ResponseAction.replaceInputText("Try again"));
                                }
                            })
                            .text(questLog.description)
                            .title("Enter the description")
                            .plugin(Bukkit.getPluginManager().getPlugin("Quests"))
                            .onClose(stateSnapshot -> QuestsCommand.openQuestsCreationGUI(player, questLog))
                            .preventClose()
                            .open(player);
                    break;
                case IRON_SWORD:
                    new AnvilGUI.Builder()
                            .onClick((slot, stateSnapshot) -> {
                                if(slot == AnvilGUI.Slot.OUTPUT) {
                                    try {
                                        questLog.minLevel = Integer.parseInt(stateSnapshot.getText());
                                        return Collections.singletonList(AnvilGUI.ResponseAction.close());
                                    } catch (NumberFormatException error) {
                                        return Collections.singletonList(AnvilGUI.ResponseAction.replaceInputText("Must be a number"));
                                    }
                                } else {
                                    return Collections.singletonList(AnvilGUI.ResponseAction.replaceInputText("Try again"));
                                }
                            })
                            .text(String.valueOf(questLog.minLevel))
                            .title("Enter the minimum combat level")
                            .plugin(Bukkit.getPluginManager().getPlugin("Quests"))
                            .onClose(stateSnapshot -> QuestsCommand.openQuestsCreationGUI(player, questLog))
                            .preventClose()
                            .open(player);
                    break;
                case GREEN_CONCRETE:
                    questLog.length = "MEDIUM";
                    QuestsCommand.openQuestsCreationGUI(player, questLog);
                    break;
                case YELLOW_CONCRETE:
                    questLog.length = "LONG";
                    QuestsCommand.openQuestsCreationGUI(player, questLog);
                    break;
                case RED_CONCRETE:
                case BLACK_CONCRETE:
                    questLog.length = "SHORT";
                    QuestsCommand.openQuestsCreationGUI(player, questLog);
                    break;
                case SKELETON_SKULL:
                    questLog.name = "_" + questLog.name;
                    e.getView().close();
                    player.sendMessage(ChatColor.GREEN + "Select an entity for the quest location!");
                    break;
                case BARRIER:
                    confirmationGUI(player, false);
                    break;
                case GREEN_DYE:
                    confirmationGUI(player, true);
                    break;
            }
        }
        if (e.getView().getTitle().equalsIgnoreCase(ChatColor.DARK_AQUA.toString() + ChatColor.BOLD + "Create quest?")) {
            e.setCancelled(true);
            //To prevent null in console
            if(e.getRawSlot() > 54 || e.getRawSlot() < 0) {return;}
            ItemStack clickedItem = e.getInventory().getItem(e.getRawSlot());
            if (clickedItem == null) {return;}
            if (!clickedItem.hasItemMeta()) {return;}
            //If the user clicks on a quest
            Player player = (Player) e.getWhoClicked();
            QuestLog questLog = Quests.currentQuestCreation.get(player);

            if(clickedItem.getType() == Material.GREEN_WOOL) {
                if(questLog.location == null) {questLog.location = player.getLocation().serialize();}
                if(questLog.main) {
                    Quests.totalMainQuestsMap.put(questLog.name, questLog);
                } else {
                    Quests.totalSideQuestsMap.put(questLog.name, questLog);
                }
                Quests.currentQuestCreation.remove(player);
                e.getView().close();
            } else if (clickedItem.getType() == Material.RED_WOOL) {
                QuestsCommand.openQuestsCreationGUI(player, questLog);
            }
        }
        if (e.getView().getTitle().equalsIgnoreCase(ChatColor.DARK_AQUA.toString() + ChatColor.BOLD + "Destroy quest?")) {
            e.setCancelled(true);
            //To prevent null in console
            if(e.getRawSlot() > 54 || e.getRawSlot() < 0) {return;}
            ItemStack clickedItem = e.getInventory().getItem(e.getRawSlot());
            if (clickedItem == null) {return;}
            if (!clickedItem.hasItemMeta()) {return;}
            //If the user clicks on a quest
            Player player = (Player) e.getWhoClicked();
            QuestLog questLog = Quests.currentQuestCreation.get(player);

            if(clickedItem.getType() == Material.GREEN_WOOL) {
                e.getView().close();
                Quests.currentQuestCreation.remove(player);
            } else if (clickedItem.getType() == Material.RED_WOOL) {
                QuestsCommand.openQuestsCreationGUI(player, questLog);
            }
        }
    }

    //This event is for selecting an entity when creating a quest
    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent e) {
        if(Quests.currentQuestCreation.get(e.getPlayer()) != null && Quests.currentQuestCreation.get(e.getPlayer()).name.startsWith("_")) {
            QuestLog questLog = Quests.currentQuestCreation.get(e.getPlayer());
            questLog.location = e.getRightClicked().getLocation().serialize();
            questLog.name = questLog.name.substring(1);
            QuestsCommand.openQuestsCreationGUI(e.getPlayer(), questLog);
            e.getPlayer().sendMessage(ChatColor.GREEN + "You have selected " + e.getRightClicked().getName() + " at (" +
                    e.getRightClicked().getLocation().getBlockX() + ", " + e.getRightClicked().getLocation().getBlockY() + ", " + e.getRightClicked().getLocation().getBlockZ() + ")");
        }
    }

    public static void createBeacon(ItemStack clickedItem, Player player, String title, InventoryClickEvent e) {
        if (clickedItem.getType() == Material.WRITABLE_BOOK || clickedItem.getType() == Material.WRITTEN_BOOK || clickedItem.getType() == Material.ENCHANTED_BOOK || clickedItem.getType() == Material.PAPER) {
            //This code creates a Beacon 30 blocks under the ground client side.
            ItemMeta questBookMeta = clickedItem.getItemMeta();
            assert questBookMeta != null;
            QuestLog questLog;
            QuestStartEvent event;
            if (title.equalsIgnoreCase(ChatColor.DARK_AQUA.toString() + ChatColor.BOLD + "Side Quests")) {
                questLog = QuestLog.getQuestByMetaName(questBookMeta.getDisplayName(), "side");
                assert questLog != null;
                event = new QuestStartEvent(player, questLog.name, questLog.main);
            } else if (title.equalsIgnoreCase(ChatColor.DARK_AQUA.toString() + ChatColor.BOLD + "Main Quests")) {
                questLog = QuestLog.getQuestByMetaName(questBookMeta.getDisplayName(), "main");
                assert questLog != null;
                event = new QuestStartEvent(player, questLog.name, questLog.main);
            } else {
                return;
            }
            Bukkit.getPluginManager().callEvent(event);
            Quests.currentQuestSelected.put(String.valueOf(player.getUniqueId()), questLog);
            Quests.saveJson();
            Location location = Location.deserialize(questLog.location);
            int x = location.getBlockX();
            int y = location.getBlockY() - 30;
            int z = location.getBlockZ();

            World world = location.getWorld();

            player.sendBlockChange(new Location(world, x, y, z), Material.BEACON.createBlockData());
            for (int i = 0; i <= 29; ++i) {
                assert world != null;
                if (world.getBlockAt(x, (y + 1) + i, z).getType() != Material.AIR) {
                    player.sendBlockChange(new Location(world, x, (y + 1) + i, z), Material.WHITE_STAINED_GLASS.createBlockData());
                }
            }
            for (int xPoint = x - 1; xPoint <= x + 1; xPoint++) {
                for (int zPoint = z - 1; zPoint <= z + 1; zPoint++) {
                    player.sendBlockChange(new Location(world, xPoint, y - 1, zPoint), Material.IRON_BLOCK.createBlockData());
                }
            }
            e.getView().close();
        }
    }
    public static void removeBeacon(Player player) {
        if (Quests.currentQuestSelected.get(String.valueOf(player.getUniqueId())) == null) {return;}
        Location location = Location.deserialize(Quests.currentQuestSelected.get(String.valueOf(player.getUniqueId())).location);
        int x = location.getBlockX();
        int y = location.getBlockY() - 30;
        int z = location.getBlockZ();

        World world = location.getWorld();
        if(world == null) {Bukkit.getLogger().severe("at removeBeacon(), the world of the log is somehow null???");return;}

        player.sendBlockChange(new Location(world, x, y, z), world.getBlockAt(new Location(world, x, y, z)).getBlockData());
        for (int i = 0; i <= 29; ++i) {
            if (world.getBlockAt(x, (y + 1) + i, z).getType() != Material.AIR) {
                player.sendBlockChange(new Location(world, x, (y + 1) + i, z), world.getBlockAt(new Location(world, x, (y + 1) + i, z)).getBlockData());
            }
        }
        for (int xPoint = x - 1; xPoint <= x + 1; xPoint++) {
            for (int zPoint = z - 1; zPoint <= z + 1; zPoint++) {
                player.sendBlockChange(new Location(world, xPoint, y - 1, zPoint), world.getBlockAt(new Location(world, xPoint, y - 1, zPoint)).getBlockData());
            }
        }
    }
    public static void confirmationGUI(Player player, boolean creation) {
        Inventory inventory;
        if(creation) {
            inventory  = Bukkit.createInventory(player, 27, ChatColor.DARK_AQUA.toString() + ChatColor.BOLD + "Create quest?");
        } else {
            inventory  = Bukkit.createInventory(player, 27, ChatColor.DARK_AQUA.toString() + ChatColor.BOLD + "Destroy quest?");
        }

        ItemStack bSG = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta bSGMeta = bSG.getItemMeta();
        assert bSGMeta != null;
        bSGMeta.setDisplayName(ChatColor.BLACK.toString());
        bSG.setItemMeta(bSGMeta);

        for(int x = 0; x < 27; x++) {
            inventory.setItem(x, bSG);
        }

        ItemStack gW = new ItemStack(Material.GREEN_WOOL);
        ItemMeta gWMeta = gW.getItemMeta();
        assert gWMeta != null;
        gWMeta.setDisplayName(ChatColor.GREEN + "Confirm");
        gW.setItemMeta(gWMeta);

        inventory.setItem(10, gW);

        ItemStack rW = new ItemStack(Material.RED_WOOL);
        ItemMeta rWMeta = rW.getItemMeta();
        assert rWMeta != null;
        rWMeta.setDisplayName(ChatColor.RED + "Deny");
        rW.setItemMeta(rWMeta);

        inventory.setItem(16, rW);

        player.openInventory(inventory);
    }
}
