package com.titanborn.plugin.events;

import com.titanborn.plugin.QuestLog;
import com.titanborn.plugin.Quests;
import com.titanborn.plugin.commands.QuestsCommand;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Objects;

public class QuestsMenuListener implements Listener {

    @EventHandler
    public static void onInventoryClick(InventoryClickEvent e) {
        if (e.getView().getTitle().equalsIgnoreCase(ChatColor.DARK_AQUA.toString() + ChatColor.BOLD + "Main Quests")) {
            e.setCancelled(true);
            //To prevent null in console
            if(e.getRawSlot() > 54) {return;}
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
            if(e.getRawSlot() > 54) {return;}
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
    }
    public static void createBeacon(ItemStack clickedItem, Player player, String title, InventoryClickEvent e) {
        if (clickedItem.getType() == Material.WRITABLE_BOOK) {
            //This code creates a Beacon 30 blocks under the ground client side.
            ItemMeta questBookMeta = clickedItem.getItemMeta();
            assert questBookMeta != null;
            QuestLog questLog;
            if (title.equalsIgnoreCase(ChatColor.DARK_AQUA.toString() + ChatColor.BOLD + "Side Quests")) {
                questLog = QuestLog.getQuestByMetaName(questBookMeta.getDisplayName(), "side");
            } else if (title.equalsIgnoreCase(ChatColor.DARK_AQUA.toString() + ChatColor.BOLD + "Main Quests")) {
                questLog = QuestLog.getQuestByMetaName(questBookMeta.getDisplayName(), "main");
            } else {
                return;
            }
            Quests.currentQuestSelected.put(String.valueOf(player.getUniqueId()), questLog);
            Quests.saveJson();
            assert questLog != null;
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
}
