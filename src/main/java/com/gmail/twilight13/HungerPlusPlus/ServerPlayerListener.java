package com.gmail.twilight13.HungerPlusPlus;

import java.util.logging.Logger;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class ServerPlayerListener
        implements Listener {

    public final Logger log = Logger.getLogger("Minecraft");
    public static HungerPlusPlus plugin;

    public ServerPlayerListener(HungerPlusPlus instance) {
        plugin = instance;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        float hunger_config_setting = (float) plugin.config.getDouble("Hunger.hunger_rate");
        float realistic_metabolism_setting = (float) plugin.config.getDouble("Hunger.basal_metabolic_rate");

        final Player p = e.getPlayer();
        float new_exhaust_start_level = 0.0F;

        final int debug_mode = plugin.config.getInt("Hunger.debug_mode");

        if (hunger_config_setting == 0.0F) {
            hunger_config_setting = 1.0F;
        }
        if (hunger_config_setting > 0.0F) {
            new_exhaust_start_level = 4.0F / hunger_config_setting * (hunger_config_setting - 1.0F);
        }
        if (hunger_config_setting < 0.0F) {
            new_exhaust_start_level = hunger_config_setting * 4.0F - 1.0F;
        }
        if (realistic_metabolism_setting != 0.0F) {
            realistic_metabolism_setting = 0.0041666F / realistic_metabolism_setting / hunger_config_setting;
        } else {
            realistic_metabolism_setting = 0.0F;
        }
        p.setExhaustion(new_exhaust_start_level);
        if (debug_mode == 1) {
            this.log.info("new_exhaust_start_level is now " + new_exhaust_start_level);
        }
        plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            float hunger_rate, realistic_metabolism_setting, new_exhaust_start_level;
            public Runnable init(float hungerConfigSetting, float realisticMetabolismSetting, float newExhaustStartLevel) {
                this.hunger_rate = hungerConfigSetting;
                this.realistic_metabolism_setting = realisticMetabolismSetting * 20L;
                this.new_exhaust_start_level = newExhaustStartLevel;
                return this;
            }
            
            public void run() {
                float current_exhaustion = p.getExhaustion();
                if (hunger_rate < 0.0F) {
                    if (-1.0F < current_exhaustion && current_exhaustion < 0.0F) {
                        p.setExhaustion(4.0F);
                    }
                    if (0.0F < current_exhaustion && current_exhaustion < 4.0F) {
                        p.setExhaustion(new_exhaust_start_level);
                    }
                }
                if (hunger_rate > 0.0F && current_exhaustion <= new_exhaust_start_level) {
                    p.setExhaustion(new_exhaust_start_level);
                }
                current_exhaustion = p.getExhaustion();
                if (realistic_metabolism_setting != 0.0F) {
                    if (hunger_rate < 0.0F) {
                        p.setExhaustion(current_exhaustion - realistic_metabolism_setting);
                    }
                    if (hunger_rate > 0.0F) {
                        p.setExhaustion(current_exhaustion + realistic_metabolism_setting);
                    }
                }
            }
        }.init(hunger_config_setting, realistic_metabolism_setting, new_exhaust_start_level), 0L, 20L);
    }
}
