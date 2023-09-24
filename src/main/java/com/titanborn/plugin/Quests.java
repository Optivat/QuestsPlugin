package com.titanborn.plugin;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.titanborn.plugin.commands.QuestsCommand;
import com.titanborn.plugin.commands.QuestsCommandTabCompleter;
import com.titanborn.plugin.events.QuestsMenuListener;
import org.bukkit.Bukkit;
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

    @Override
    public void onEnable() {
        totalQuestsFile = new File(getDataFolder().getAbsolutePath() + "/quests.json");
        //Initialization of JSON, might put in a separate class if needed.

        //First boot up of the plugin, it will create a empty json.
        if (!totalQuestsFile.exists()) {
            try {
                //Creates folder, then creates the file.
                new File(getDataFolder().getAbsolutePath()).mkdirs();
                FileWriter fileWriter = new FileWriter(totalQuestsFile);
                fileWriter.write(totalQuestsMap.toString());
                fileWriter.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            //After first boot up, it will always go to this line of code.
            try {
                //Possible optimization for later.
                Type playerQuestType = new TypeToken<Map<String, QuestLog>>(){}.getType();
                totalQuestsMap = gson.fromJson(new FileReader(totalQuestsFile), playerQuestType);
                if(totalQuestsFile == null) {
                    totalQuestsMap = new HashMap<>();
                }
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        getCommand("quests").setExecutor(new QuestsCommand());
        getCommand("quests").setTabCompleter(new QuestsCommandTabCompleter());

        Bukkit.getPluginManager().registerEvents(new QuestsMenuListener(), this);
    }

    public static void saveJson() {
        final String jsonQuests = gson.toJson(totalQuestsMap);
        totalQuestsFile.delete();
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
