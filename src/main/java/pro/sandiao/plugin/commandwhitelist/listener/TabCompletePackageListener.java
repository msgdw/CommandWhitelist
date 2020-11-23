package pro.sandiao.plugin.commandwhitelist.listener;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;

import org.bukkit.plugin.Plugin;

import pro.sandiao.plugin.commandwhitelist.listener.adapter.TabCompletePacketAdapter;

public class TabCompletePackageListener {

    private Plugin plugin;
    private ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
    private PacketAdapter packetAdapter;

    public TabCompletePackageListener(Plugin plugin) {
        this.plugin = plugin;
    }

    public void registerListener() {
        if (packetAdapter != null)
            protocolManager.removePacketListener(packetAdapter);
    }

    /**
     * 注册监听器
     * 
     * @param isHighVersion 是高版本?
     */
    public void registerListener(boolean isHighVersion) {
        if (isHighVersion) {
            packetAdapter = new TabCompletePacketAdapter(plugin);
        } else {
            packetAdapter = new TabCompletePacketAdapter(plugin);
        }

        protocolManager.addPacketListener(packetAdapter);
    }
}