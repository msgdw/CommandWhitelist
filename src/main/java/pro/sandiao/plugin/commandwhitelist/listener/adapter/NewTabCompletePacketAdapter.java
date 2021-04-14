package pro.sandiao.plugin.commandwhitelist.listener.adapter;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import pro.sandiao.plugin.commandwhitelist.Main;

public class NewTabCompletePacketAdapter extends TabCompletePacketAdapter {

    public NewTabCompletePacketAdapter(Plugin plugin) {
        super(plugin);
    }

    @Override
    public void sendPacket(Player player, String cmd) {
        PacketContainer completions = new PacketContainer(PacketType.Play.Server.TAB_COMPLETE);
        completions.getModifier().write(1, getSuggestions(player, cmd));

        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, completions);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取玩家可补全的命令建议
     * 
     * @param player  玩家
     * @param command 当前输入的命令
     * @return 可补全的命令数组
     */
    public Suggestions getSuggestions(Player player, String command) {
        StringRange range = new StringRange(Integer.MIN_VALUE, Integer.MAX_VALUE);
        List<Suggestion> suggestionList = new ArrayList<>();
        for (String cmd : Main.getWhitelistManager().getPlayerTabCompleteWhitelist(player)) {
            if (cmd.toLowerCase().startsWith(command))
                suggestionList.add(new Suggestion(range, cmd));
        }
        Suggestions suggestions = new Suggestions(range, suggestionList);
        return suggestions;
    }
}