package pro.sandiao.plugin.commandwhitelist.listener.adapter;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;


import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import pro.sandiao.plugin.commandwhitelist.Main;

public class OldTabCompletePacketAdapter extends TabCompletePacketAdapter {

    public OldTabCompletePacketAdapter(Plugin plugin) {
        super(plugin);
    }

    @Override
    public void sendPacket(Player player, String cmd) {
        PacketContainer completions = new PacketContainer(PacketType.Play.Server.TAB_COMPLETE);
        completions.getStringArrays().write(0, getPlayerCompleteArrays(player, cmd));

        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, completions);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取玩家可补全的命令数组
     * 
     * @param player  玩家
     * @param command 当前输入的命令
     * @return 可补全命令数组
     */
    public String[] getPlayerCompleteArrays(Player player, String command) {
        List<String> completions = new ArrayList<>();
        for (String cmd : Main.getWhitelistManager().getPlayerTabCompleteWhitelist(player)) {
            if (cmd.toLowerCase().startsWith(command))
                completions.add("/" + cmd);
        }
        return completions.toArray(new String[completions.size()]);
    }
}