package com.titanborn.plugin.events;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Objects;

public class GenericListener implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockClick(PlayerInteractEvent e){
        if(e.getAction() == Action.RIGHT_CLICK_BLOCK){
            if (Objects.requireNonNull(e.getClickedBlock()).getType() == Material.WHITE_STAINED_GLASS) {
                e.getPlayer().sendBlockChange(e.getClickedBlock().getLocation(), Material.WHITE_STAINED_GLASS.createBlockData());
            }
        }
    }
}
