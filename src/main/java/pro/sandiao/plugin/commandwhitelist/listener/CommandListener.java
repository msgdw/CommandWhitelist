package pro.sandiao.plugin.commandwhitelist.listener;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.Plugin;

import net.md_5.bungee.api.ChatColor;
import pro.sandiao.plugin.commandwhitelist.Main;

public class CommandListener implements Listener {

    private Plugin plugin;

    public CommandListener(Plugin plugin) {
        this.plugin = plugin;
    }

    /**
     * 注册监听器
     */
    public void registerListener() {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    /**
     * 注销监听器
     */
    public void unregisterListener() {
        PlayerCommandPreprocessEvent.getHandlerList().unregister(this);
    }

    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        String message = event.getMessage().substring(1);

        int index = message.indexOf(' ');
        if (index != -1) {
            message = message.substring(0, index);
        }

        message = message.toLowerCase();
        if (Main.getWhitelistManager().hasCommandWhitelist(event.getPlayer(), message))
            return;

        String xx = plugin.getConfig().getString("command-whitelist.blocked-message");
        if (!xx.isEmpty()) {
            event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', xx));
        }

        event.setCancelled(true);
    }
}
