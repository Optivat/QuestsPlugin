package com.titanborn.plugin.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class QuestsCommandTabCompleter implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if(args.length == 1) {
            List<String> strings = new ArrayList<>();
            strings.add("update");
            strings.add("create");
            strings.add("open");
            strings.add("remove");
            return strings;
        }
        return null;
    }
}
