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
        if (e.getView().getTitle().equalsIgnoreCase(ChatColor.DARK_AQUA.toString() + ChatColor.BOLD + "Quests")) {
            e.setCancelled(true);
            //To prevent null in console
            ItemStack clickedItem = e.getInventory().getItem(e.getRawSlot());
            if (clickedItem != null && clickedItem.hasItemMeta()) {return;}
            //If the user clicks on a quest
            assert clickedItem != null;
            Player player = (Player) e.getWhoClicked();
            if (clickedItem.getType() == Material.WRITABLE_BOOK) {

                //This code creates a Beacon 30 blocks under the ground client side.

                ItemMeta questBookMeta = clickedItem.getItemMeta();
                assert questBookMeta != null;
                QuestLog questLog = QuestLog.getQuestByMetaName(questBookMeta.getDisplayName());
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
            if (Objects.requireNonNull(clickedItem.getItemMeta()).getDisplayName().equalsIgnoreCase(ChatColor.YELLOW + "Previous Page")) {
                int currentPlayerPage = Quests.playerPage.get(player);
                //This line of code should never happen, but if it does then this line of code should fix it.
                if(currentPlayerPage <= 0) {Quests.playerPage.put(player, 0);QuestsCommand.openQuestsGUI(player);player.playSound(player, Sound.BLOCK_NOTE_BLOCK_BASS, 3.0F, 0.5F);return;}
                Quests.playerPage.put(player, currentPlayerPage-1);
                QuestsCommand.openQuestsGUI(player);
            }
            if (Objects.requireNonNull(clickedItem.getItemMeta()).getDisplayName().equalsIgnoreCase(ChatColor.YELLOW + "Next Page")) {
                int currentPlayerPage = Quests.playerPage.get(player);
                //This line of code should never happen, but if it does then this line of code should fix it.
                if(currentPlayerPage < 0) {Quests.playerPage.put(player, 0);QuestsCommand.openQuestsGUI(player);player.playSound(player, Sound.BLOCK_NOTE_BLOCK_BASS, 3.0F, 0.5F);return;}
                Quests.playerPage.put(player, currentPlayerPage+1);
                QuestsCommand.openQuestsGUI(player);
            }
        }
    }
}
