package com.titanborn.plugin;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerQuestInfo {
    public ArrayList<UUID> completedQuests;
    public Map<UUID, Integer> currentObjective;
    public UUID currentQuestSelected;
    public int playerPageMain;
    public int playerPageSide;
    public UUID id;

    public PlayerQuestInfo(Player player) {
        completedQuests = new ArrayList<>();
        currentObjective = new HashMap<>();
        currentQuestSelected = null;
        playerPageMain = 0;
        playerPageSide = 0;
        id = player.getUniqueId();
    }

}
