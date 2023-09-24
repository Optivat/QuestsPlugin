package com.titanborn.plugin.events;

import com.titanborn.plugin.QuestLog;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Objects;

public class QuestsMenuListener implements Listener {

    @EventHandler
    public static void onInventoryClick(InventoryClickEvent e) {
        if(e.getView().getTitle().equalsIgnoreCase(ChatColor.DARK_AQUA  + "" + ChatColor.BOLD + "Quests")) {
            e.setCancelled(true);
            //To prevent null in console
            if(Objects.requireNonNull(e.getInventory().getItem(e.getRawSlot())).getItemMeta() == null) {return;}
            if(!Objects.requireNonNull(e.getInventory().getItem(e.getRawSlot())).hasItemMeta()) {return;}
            //If the user clicks on a quest
            if(Objects.requireNonNull(e.getInventory().getItem(e.getRawSlot())).getType() == Material.WRITABLE_BOOK) {

                //This code creates a Beacon 30 blocks under the ground client side.

                ItemMeta questBookMeta = Objects.requireNonNull(e.getInventory().getItem(e.getRawSlot())).getItemMeta();
                QuestLog questLog = QuestLog.getQuestByMetaName(questBookMeta.getDisplayName());
                Player player = (Player) e.getWhoClicked();
                assert questLog != null;
                Location location = Location.deserialize(questLog.location);
                int x = location.getBlockX();
                int y = location.getBlockY() - 30;
                int z = location.getBlockZ();

                World world = location.getWorld();

                player.sendBlockChange(new Location(world, x, y, z), Material.BEACON.createBlockData());
                for (int i = 0; i <= 29; ++i) {
                    assert world != null;
                    if(world.getBlockAt(x, (y + 1) + i, z).getType() != Material.AIR) {
                        player.sendBlockChange(new Location(world, x, (y + 1) + i, z), Material.WHITE_STAINED_GLASS.createBlockData());
                    }
                }
                for (int xPoint = x-1; xPoint <= x+1 ; xPoint++) {
                    for (int zPoint = z-1 ; zPoint <= z+1; zPoint++) {
                        player.sendBlockChange(new Location(world, xPoint, y-1, zPoint), Material.IRON_BLOCK.createBlockData());
                    }
                }

            }
            e.getView().close();
        }
    }
}
