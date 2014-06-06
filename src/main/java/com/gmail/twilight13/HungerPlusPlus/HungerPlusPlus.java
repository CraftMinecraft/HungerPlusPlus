package com.gmail.twilight13.HungerPlusPlus;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Server;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class HungerPlusPlus
        extends JavaPlugin {

    public final Logger log = Logger.getLogger("Minecraft");
    ServerPlayerListener PlayerListener = new ServerPlayerListener(this);
    FileConfiguration config;

    public void onEnable() {
        Metrics metrics;
        try {
            metrics = new Metrics(this);
            metrics.start();
        } catch (IOException ex) {
            // Oh well
        }
        this.config = getConfig();
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(this.PlayerListener, this);
        this.log.info("[HungerPlusPlus] " + getDescription().getVersion() + " enabled!");
    }

    public void onDisable() {
        this.config = null;
    }
}
