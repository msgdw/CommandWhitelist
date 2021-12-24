package pro.sandiao.plugin.commandwhitelist.listener;

import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandSendEvent;
import org.bukkit.plugin.Plugin;

import pro.sandiao.plugin.commandwhitelist.Main;
import pro.sandiao.plugin.commandwhitelist.manager.WhitelistManager;

/**
 * Tab补全监听器
 */
public class TabCompleteListener implements Listener {

    private Plugin plugin;

    public TabCompleteListener(Plugin plugin) {
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
        PlayerCommandSendEvent.getHandlerList().unregister(this);
    }

    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandSendEvent event) {
        if (event.getPlayer().hasPermission(WhitelistManager.TAB_COMPLETE_WHITELIST_PERMISSION)) {
            return;
        }

        event.getCommands().clear();
        Collection<String> newCmdList = Main.getWhitelistManager().getPlayerTabCompleteWhitelist(event.getPlayer());
        event.getCommands().addAll(newCmdList);
    }
}
