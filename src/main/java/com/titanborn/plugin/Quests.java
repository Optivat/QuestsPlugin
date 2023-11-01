package com.titanborn.plugin;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.titanborn.plugin.commands.QuestsCommand;
import com.titanborn.plugin.commands.QuestsCommandTabCompleter;
import com.titanborn.plugin.events.GenericListener;
import com.titanborn.plugin.events.QuestsMenuListener;
import com.titanborn.plugin.events.custom.QuestStartEvent;
import org.bukkit.*;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public final class Quests extends JavaPlugin {

    public static File totalMainQuestsFile;
    public static File totalSideQuestsFile;
    public static File currentQuestSelectedFile;
    public static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    public static Map<String, QuestLog> totalMainQuestsMap = new HashMap<>();
    public static Map<String, QuestLog> totalSideQuestsMap = new HashMap<>();
    public static Map<String, QuestLog> currentQuestSelected = new HashMap<>();
    public static Map<Player, Integer> playerPageMain = new HashMap<>();
    public static Map<Player, Integer> playerPageSide = new HashMap<>();
    public static Map<Player, QuestLog> currentQuestCreation = new HashMap<>();

    @Override
    public void onEnable() {
        totalMainQuestsFile = new File(getDataFolder().getAbsolutePath() + "/main_quests.json");
        totalSideQuestsFile = new File(getDataFolder().getAbsolutePath() + "/side_quests.json");
        currentQuestSelectedFile = new File(getDataFolder().getAbsolutePath() + "/player_selected_quests.json");
        //Initialization of JSON, might put in a separate class if needed.

        //First boot up of the plugin, it will create an empty json.
        if (!totalMainQuestsFile.exists()) {
            try {
                //Creates folder, then creates the file.
                boolean mkdirs = new File(getDataFolder().getAbsolutePath()).mkdirs();
                if(!mkdirs) {
                    this.getLogger().severe("Failed to make config folder in plugins folder!");
                }
                FileWriter fileWriter = new FileWriter(totalMainQuestsFile);
                fileWriter.write(totalMainQuestsMap.toString());
                fileWriter.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            //After first boot up, it will always go to this line of code.
            try {
                //Using an experimental feature, may cause errors in the future
                @SuppressWarnings("UnstableApiUsage") Type playerQuestType = new TypeToken<Map<String, QuestLog>>(){}.getType();
                totalMainQuestsMap = gson.fromJson(new FileReader(totalMainQuestsFile), playerQuestType);
                if(totalMainQuestsFile == null) {
                    totalMainQuestsMap = new HashMap<>();
                }
            } catch (FileNotFoundException e) {
                this.getLogger().severe("Failed to find \"quests.json\" in the config directory of the plugin.");
                throw new RuntimeException(e);
            }
        }

        if (!totalSideQuestsFile.exists()) {
            try {
                //Creates folder, then creates the file.
                FileWriter fileWriter = new FileWriter(totalSideQuestsFile);
                fileWriter.write(totalSideQuestsMap.toString());
                fileWriter.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            //After first boot up, it will always go to this line of code.
            try {
                //Using an experimental feature, may cause errors in the future
                @SuppressWarnings("UnstableApiUsage") Type playerQuestType = new TypeToken<Map<String, QuestLog>>(){}.getType();
                totalSideQuestsMap = gson.fromJson(new FileReader(totalSideQuestsFile), playerQuestType);
                if(totalSideQuestsFile == null) {
                    totalSideQuestsMap = new HashMap<>();
                }
            } catch (FileNotFoundException e) {
                this.getLogger().severe("Failed to find \"side_quests.json\" in the config directory of the plugin.");
                throw new RuntimeException(e);
            }
        }

        if (!currentQuestSelectedFile.exists()) {
            try {
                //Creates folder, then creates the file.
                FileWriter fileWriter = new FileWriter(currentQuestSelectedFile);
                fileWriter.write(currentQuestSelectedFile.toString());
                fileWriter.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            //After first boot up, it will always go to this line of code.
            try {
                //Using an experimental feature, may cause errors in the future
                @SuppressWarnings("UnstableApiUsage") Type playerQuestType = new TypeToken<Map<String, QuestLog>>(){}.getType();
                currentQuestSelected = gson.fromJson(new FileReader(currentQuestSelectedFile), playerQuestType);
                if(currentQuestSelectedFile == null) {
                    currentQuestSelected = new HashMap<>();
                }
            } catch (FileNotFoundException e) {
                this.getLogger().severe("Failed to find \"side_quests.json\" in the config directory of the plugin.");
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
        final String jsonMainQuests = gson.toJson(totalMainQuestsMap);
        final String jsonSideQuests = gson.toJson(totalSideQuestsMap);
        final String jsonCurrentSelectedQuests = gson.toJson(currentQuestSelected);
        // "Goofy ahhh coding."
        boolean fileDeletion = totalMainQuestsFile.delete();
        if(!fileDeletion) {
            Bukkit.getLogger().severe("Failed to delete file \"" + totalMainQuestsFile.getName() + "\" in method saveJson()");
            return;
        }
        fileDeletion = totalSideQuestsFile.delete();
        if(!fileDeletion) {
            Bukkit.getLogger().severe("Failed to delete file \"" + totalSideQuestsFile.getName() + "\" in method saveJson()");
            return;
        }
        fileDeletion = currentQuestSelectedFile.delete();
        if(!fileDeletion) {
            Bukkit.getLogger().severe("Failed to delete file \"" + currentQuestSelectedFile.getName() + "\" in method saveJson()");
            return;
        }
        try {
            Files.write(totalMainQuestsFile.toPath(), jsonMainQuests.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
            Files.write(totalSideQuestsFile.toPath(), jsonSideQuests.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
            Files.write(currentQuestSelectedFile.toPath(), jsonCurrentSelectedQuests.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //Used in QusetAdapter don't remove
    public static void completeQuest(Player player) {
        if(currentQuestSelected.containsKey(String.valueOf(player.getUniqueId()))) {
            QuestLog completedQuest = currentQuestSelected.get(String.valueOf(player.getUniqueId()));
            ArrayList<QuestLog> quests;
            if(completedQuest.main) {
                quests = new ArrayList<>(totalMainQuestsMap.values());
            } else {
                quests = new ArrayList<>(totalSideQuestsMap.values());
            }
            boolean nextValueIsNextQuest = false;
            for(QuestLog questLog : quests) {
                if (!nextValueIsNextQuest) {
                    if(completedQuest.name.equalsIgnoreCase(questLog.name)) {
                        nextValueIsNextQuest = true;
                    }
                } else {
                    QuestsMenuListener.removeBeacon(player);
                    currentQuestSelected.put(String.valueOf(player.getUniqueId()), questLog);
                    Quests.saveJson();

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
                    QuestStartEvent event = new QuestStartEvent(player, questLog.name, questLog.main);
                    Bukkit.getPluginManager().callEvent(event);
                    break;
                }
            }
        }
    }

    @Override
    public void onDisable() {
        saveJson();
    }
}
