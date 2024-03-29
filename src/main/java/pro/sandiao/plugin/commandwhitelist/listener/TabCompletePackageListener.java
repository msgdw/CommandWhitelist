package pro.sandiao.plugin.commandwhitelist.listener;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import pro.sandiao.plugin.commandwhitelist.listener.adapter.NewTabCompletePacketAdapter;
import pro.sandiao.plugin.commandwhitelist.listener.adapter.OldTabCompletePacketAdapter;
import pro.sandiao.plugin.commandwhitelist.listener.adapter.TabCompletePacketAdapter;

/**
 * Tab补全数据包监听器
 */
public class TabCompletePackageListener {

    private Plugin plugin;
    private ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
    private TabCompletePacketAdapter tabCompletePacketAdapter;

    public TabCompletePackageListener(Plugin plugin) {
        this.plugin = plugin;
    }

    /**
     * 注销监听器
     */
    public void unregisterListener() {
        if (tabCompletePacketAdapter != null)
            protocolManager.removePacketListener(tabCompletePacketAdapter);
    }

    /**
     * 注册监听器
     * 
     * @param isHighVersion 是高版本?
     */
    public void registerListener(boolean isHighVersion) {
        if (isHighVersion) {
            tabCompletePacketAdapter = new NewTabCompletePacketAdapter(plugin);
        } else {
            tabCompletePacketAdapter = new OldTabCompletePacketAdapter(plugin);
        }

        protocolManager.addPacketListener(tabCompletePacketAdapter);
    }

    public TabCompletePacketAdapter getTabCompletePacketAdapter() {
        return tabCompletePacketAdapter;
    }

    /**
     * 更新补全列表
     *
     * 1.13+
     */
    public void updateCompleteList() {
        if (tabCompletePacketAdapter instanceof NewTabCompletePacketAdapter) {
            // 高版本要重新发包给客户端更新命令列表
            for (Player player : Bukkit.getOnlinePlayers()) {
                tabCompletePacketAdapter.sendPacket(player);
                player.updateCommands();
            }
        }
    }
}