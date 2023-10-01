package com.titanborn.plugin.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class QuestsCommandTabCompleter implements TabCompleter {
    //I am going to pray that this doesn't cause any problems in the foreseeable future.
    @SuppressWarnings("NullableProblems")
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> strings = new ArrayList<>();
        if(args.length == 1) {
            strings.add("update");
            strings.add("create");
            strings.add("open");
            strings.add("remove");
            return strings;
        }
        if(args.length == 2 && (args[0].equalsIgnoreCase("open") || args[0].equalsIgnoreCase("create") || args[0].equalsIgnoreCase("remove"))) {
            strings.add("main");
            strings.add("side");
            return strings;
        }
        return strings;
    }
}
