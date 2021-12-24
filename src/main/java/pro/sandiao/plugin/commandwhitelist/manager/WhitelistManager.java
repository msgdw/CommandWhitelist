package pro.sandiao.plugin.commandwhitelist.manager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;

import pro.sandiao.plugin.commandwhitelist.Main;

/**
 * 白名单管理器
 */
public class WhitelistManager {

    public static final String COMMAND_WHITELIST_PERMISSION = "commandwhitelist.allow.command";
    public static final String TAB_COMPLETE_WHITELIST_PERMISSION = "commandwhitelist.allow.tabcomplete";
    public static final String COMMAND_GROUP_PERMISSION = "commandwhitelist.group.command";
    public static final String TAB_COMPLETE_GROUP_PERMISSION = "commandwhitelist.group.tabcomplete";

    private List<String> commandWhitelist = new ArrayList<>();
    private List<String> tabCompleteWhitelist = new ArrayList<>();

    private Map<String, List<String>> groupMap = new HashMap<>();

    private WhitelistManager(Plugin plugin) {
        loadWhitelistByConfigFile(plugin.getConfig());
        loadGroupByConfigFile(((Main) plugin).getGroupConfig());
    }

    /**
     * 从配置文件中加载白名单
     *
     * @param config 配置文件
     */
    public void loadWhitelistByConfigFile(FileConfiguration config) {
        commandWhitelist.clear();
        commandWhitelist.addAll(config.getStringList("command-whitelist.list"));

        tabCompleteWhitelist.clear();
        tabCompleteWhitelist.addAll(config.getStringList("tab-complete-whitelist.list"));
    }

    /**
     * 从配种文件中加载组信息
     *
     * @param config 配置文件
     */
    public void loadGroupByConfigFile(FileConfiguration config) {
        groupMap.clear();
        ConfigurationSection groupConfig = config.getConfigurationSection("group");
        for (String string : groupConfig.getKeys(false)) {
            groupMap.put(string, groupConfig.getStringList(string));
        }
    }

    /**
     * 获取一个组的命令列表
     *
     * @param group 组名
     * @return 命令列表
     */
    public List<String> getGroupList(String group) {
        return groupMap.get(group);
    }

    /**
     * 获取命令白名单
     *
     * @return 命令白名单
     */
    public List<String> getCommandWhitelist() {
        return commandWhitelist;
    }

    /**
     * 获取Tab补全白名单
     *
     * @return Tab补全白名单
     */
    public List<String> getTabCompleteWhitelist() {
        return tabCompleteWhitelist;
    }

    /**
     * 获取单个玩家的命令白名单
     *
     * @param player 玩家
     * @return 白名单
     */
    public Collection<String> getPlayerCommandWhitelist(Player player) {
        Set<String> newList = new HashSet<>(getCommandWhitelist());
        for (PermissionAttachmentInfo premissionInfo : player.getEffectivePermissions())
            if (premissionInfo.getPermission().toLowerCase().startsWith(COMMAND_WHITELIST_PERMISSION + "."))
                newList.add(premissionInfo.getPermission().substring(COMMAND_WHITELIST_PERMISSION.length() + 1));
        for (Entry<String, List<String>> groupEntry : groupMap.entrySet())
            if (player.hasPermission(COMMAND_GROUP_PERMISSION + "." + groupEntry.getKey()))
                newList.addAll(groupEntry.getValue());
        return newList;
    }

    /**
     * 获取单个玩家的Tab补全白名单
     *
     * @param player 玩家
     * @return 白名单
     */
    public Collection<String> getPlayerTabCompleteWhitelist(Player player) {
        Set<String> newList = new HashSet<>(getCommandWhitelist());
        for (PermissionAttachmentInfo premissionInfo : player.getEffectivePermissions())
            if (premissionInfo.getPermission().toLowerCase().startsWith(TAB_COMPLETE_WHITELIST_PERMISSION + "."))
                newList.add(premissionInfo.getPermission().substring(TAB_COMPLETE_WHITELIST_PERMISSION.length() + 1));
        for (Entry<String, List<String>> groupEntry : groupMap.entrySet())
            if (player.hasPermission(TAB_COMPLETE_GROUP_PERMISSION + "." + groupEntry.getKey()))
                newList.addAll(groupEntry.getValue());
        return newList;
    }

    /**
     * 查找命令是否在玩家的命令白名单中
     *
     * @param player 玩家
     * @return 是否在白名单内
     */
    public boolean hasCommandWhitelist(Player player, String command) {
        if (player.hasPermission(COMMAND_WHITELIST_PERMISSION)
                || player.hasPermission(COMMAND_WHITELIST_PERMISSION + "." + command))
            return true;
        if (getCommandWhitelist().contains(command))
            return true;
        for (Entry<String, List<String>> groupEntry : groupMap.entrySet())
            if (player.hasPermission(COMMAND_GROUP_PERMISSION + "." + groupEntry.getKey()))
                if (groupEntry.getValue().contains(command))
                    return true;
        return false;
    }

    /**
     * 查找命令是否在玩家的Tab补全白名单中
     *
     * @param player 玩家
     * @return 是否在白名单内
     */
    public boolean hasTabCompleteWhitelist(Player player, String command) {
        if (player.hasPermission(TAB_COMPLETE_WHITELIST_PERMISSION)
                || player.hasPermission(TAB_COMPLETE_WHITELIST_PERMISSION + "." + command))
            return true;
        if (getTabCompleteWhitelist().contains(command))
            return true;
        for (Entry<String, List<String>> groupEntry : groupMap.entrySet())
            if (player.hasPermission(TAB_COMPLETE_GROUP_PERMISSION + "." + groupEntry.getKey()))
                if (groupEntry.getValue().contains(command))
                    return true;
        return false;
    }
}
