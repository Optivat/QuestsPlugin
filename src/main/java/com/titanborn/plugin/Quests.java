package com.titanborn.plugin;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.titanborn.plugin.commands.QuestsCommand;
import com.titanborn.plugin.commands.QuestsCommandTabCompleter;
import com.titanborn.plugin.events.GenericListener;
import com.titanborn.plugin.events.QuestsMenuListener;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;

public final class Quests extends JavaPlugin {

    public static File totalQuestsFile;
    public static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    public static Map<String, QuestLog> totalQuestsMap = new HashMap<>();
    public static Map<Player, Integer> playerPage = new HashMap<>();

    @Override
    public void onEnable() {
        totalQuestsFile = new File(getDataFolder().getAbsolutePath() + "/quests.json");
        //Initialization of JSON, might put in a separate class if needed.

        //First boot up of the plugin, it will create an empty json.
        if (!totalQuestsFile.exists()) {
            try {
                //Creates folder, then creates the file.
                boolean mkdirs = new File(getDataFolder().getAbsolutePath()).mkdirs();
                if(!mkdirs) {
                    this.getLogger().severe("Failed to make config folder in plugins folder!");
                    this.getLogger().severe("DISABLING LOG PLUGIN...");
                }
                FileWriter fileWriter = new FileWriter(totalQuestsFile);
                fileWriter.write(totalQuestsMap.toString());
                fileWriter.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            //After first boot up, it will always go to this line of code.
            try {
                //Using an experimental feature, may cause errors in the future
                @SuppressWarnings("UnstableApiUsage") Type playerQuestType = new TypeToken<Map<String, QuestLog>>(){}.getType();
                totalQuestsMap = gson.fromJson(new FileReader(totalQuestsFile), playerQuestType);
                if(totalQuestsFile == null) {
                    totalQuestsMap = new HashMap<>();
                }
            } catch (FileNotFoundException e) {
                this.getLogger().severe("Failed to find \"quests.json\" in the config directory of the plugin.");
                throw new RuntimeException(e);
            }
        }
        PluginCommand questsCommand = getCommand("quests");
        if(questsCommand != null) {
            questsCommand.setExecutor(new QuestsCommand());
            questsCommand.setTabCompleter(new QuestsCommandTabCompleter());
        }

        Bukkit.getPluginManager().registerEvents(new QuestsMenuListener(), this);
        Bukkit.getPluginManager().registerEvents(new GenericListener(), this);
    }

    public static void saveJson() {
        final String jsonQuests = gson.toJson(totalQuestsMap);
        boolean fileDeletion = totalQuestsFile.delete();
        if(!fileDeletion) {
            Bukkit.getLogger().severe("Failed to delete file \"" + totalQuestsFile.getName() + "\" in method saveJson()");
            return;
        }
        try {
            Files.write(totalQuestsFile.toPath(), jsonQuests.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onDisable() {
        saveJson();
    }
}
