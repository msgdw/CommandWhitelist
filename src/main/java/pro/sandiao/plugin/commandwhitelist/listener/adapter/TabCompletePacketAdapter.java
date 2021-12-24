package pro.sandiao.plugin.commandwhitelist.listener.adapter;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import pro.sandiao.plugin.commandwhitelist.Main;
import pro.sandiao.plugin.commandwhitelist.manager.WhitelistManager;

/**
 * Tab补全数据包切面抽象层
 *
 * @see OldTabCompletePacketAdapter 旧版本的Tab补全数据包切面
 * @see NewTabCompletePacketAdapter 新版本的Tab补全数据包切面
 */
public abstract class TabCompletePacketAdapter extends PacketAdapter {

    public TabCompletePacketAdapter(Plugin plugin) {
        super(plugin, PacketType.Play.Client.TAB_COMPLETE);
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {
        Player player = event.getPlayer();
        String message;
        // 获取到输入框内容
        try {
            message = event.getPacket().getStrings().read(0);
        } catch (UnsupportedOperationException e) {
            plugin.getLogger().warning("无法处理玩家 " + player.getName() + " 的命令补全请求: " + e.getMessage());
            event.setCancelled(true);
            return;
        }

        // 如果开头不是 / 就不是命令 不进行处理
        if (message.toCharArray()[0] != '/') {
            return;
        }

        // 如果玩家有 COMMAND_WHITELIST_PERMISSION 权限 则不进行处理 补全所有命令
        if (event.getPlayer().hasPermission(WhitelistManager.COMMAND_WHITELIST_PERMISSION)) {
            return;
        }

        // 对命令的子参数进行处理
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
     * @param cmd    命令
     */
    abstract public void sendPacket(Player player, String cmd);

    /**
     * 向玩家发送 数据包
     *
     * @param player 玩家
     */
    public void sendPacket(Player player) {
        sendPacket(player, "/");
    }
}
