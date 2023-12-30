package com.titanborn.plugin.events.custom;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class QuestEndEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Player player;
    private final String name;
    private final boolean type;

    public QuestEndEvent(Player player, String name, boolean type) {
        this.player = player;
        this.name = name;
        this.type = type;
    }

    public Player getPlayer() {
        return player;
    }

    public String getQuestName() {
        return name;
    }

    public boolean getQuestType() {
        return type;
    }

    //I see no problems with this.
    @SuppressWarnings("NullableProblems")
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
