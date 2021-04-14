package pro.sandiao.plugin.commandwhitelist.listener.adapter;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import pro.sandiao.plugin.commandwhitelist.Main;
import pro.sandiao.plugin.commandwhitelist.manager.WhitelistManager;

public abstract class TabCompletePacketAdapter extends PacketAdapter {

    public TabCompletePacketAdapter(Plugin plugin) {
        super(plugin, PacketType.Play.Client.TAB_COMPLETE);
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {
        String message = event.getPacket().getStrings().read(0);

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

        sendPacket(player, command);

        event.setCancelled(true);
    }

    /**
     * 发包告知客户端补全命令列表
     *
     * @param player 玩家
     * @param cmd 命令
     */
    abstract public void sendPacket(Player player, String cmd);

    public void sendPacket(Player player) {
        sendPacket(player, "/");
    }
}
