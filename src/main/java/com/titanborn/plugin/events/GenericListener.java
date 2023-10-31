package com.titanborn.plugin.events;

import com.titanborn.plugin.QuestLog;
import com.titanborn.plugin.Quests;
import com.titanborn.plugin.events.custom.QuestStartEvent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.Objects;

/**
 * To listen for generic events that I decided aren't fit for their own separate classes.
 */
public class GenericListener implements Listener {
    //To maintain ghost blocks, it does NOT work... This is depresso expresso
    /**
     * To listen for generic events that I decided aren't fit for their own separate classes.
     */
    @SuppressWarnings("checkstyle:DesignForExtension")
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockClick(PlayerInteractEvent e){
        if(e.getAction() == Action.RIGHT_CLICK_BLOCK){
            if (e.getPlayer().getWorld().getBlockAt(Objects.requireNonNull(e.getClickedBlock()).getLocation()).getType() == Material.WHITE_STAINED_GLASS) {
                e.getPlayer().sendBlockChange(e.getClickedBlock().getLocation(), Material.WHITE_STAINED_GLASS.createBlockData());
            }
        }
    }

    //TEMPORARY EVENT REMOVE LATER
    @EventHandler
    public void onQuestStart(QuestStartEvent e) {
        e.getPlayer().sendMessage(e.getQuestName());
    }

    //To maintain current quest
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        if (Quests.currentQuestSelected.containsKey(String.valueOf(e.getPlayer().getUniqueId()))) {
            Player player = e.getPlayer();
            QuestLog questLog = Quests.currentQuestSelected.get(String.valueOf(e.getPlayer().getUniqueId()));
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
        }
    }
}
