package pro.sandiao.plugin.commandwhitelist.listener.adapter;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import pro.sandiao.plugin.commandwhitelist.Main;
import pro.sandiao.plugin.commandwhitelist.manager.WhitelistManager;

public class TabCompletePacketAdapter extends PacketAdapter {

    public TabCompletePacketAdapter(Plugin plugin) {
        super(plugin, PacketType.Play.Client.TAB_COMPLETE);
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {
        String message = event.getPacket().getStrings().read(0);

        // System.out.println(message);

        if (message.toCharArray()[0] != '/') {
            return;
        }

        Player player = event.getPlayer();
        if (event.getPlayer().hasPermission(WhitelistManager.COMMAND_WHITELIST_PERMISSION)) {
            return;
        }

        int index = message.indexOf(' ');
        if (index != -1) {
            if (!Main.getWhitelistManager().hasTabCompleteWhitelist(player,
                    message.substring(1, index).toLowerCase())) {
                event.setCancelled(true);
            }
            return;
        }

        String command = message.substring(1).toLowerCase();

        PacketContainer completions = new PacketContainer(PacketType.Play.Server.TAB_COMPLETE);
        completions.getStringArrays().write(0, getPlayerCompleteArrays(player, command));

        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, completions);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        event.setCancelled(true);
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