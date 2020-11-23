package pro.sandiao.plugin.commandwhitelist.manager;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;

public class WhitelistManager {

    public static final String COMMAND_WHITELIST_PERMISSION = "commandwhitelist.allow.command";
    public static final String TAB_COMPLETE_WHITELIST_PERMISSION = "commandwhitelist.allow.tabcomplete";

    private List<String> commandWhitelist, tabCompleteWhitelist = commandWhitelist = new ArrayList<>();

    private WhitelistManager(Plugin plugin) {
        loadWhitelistByConfigFile(plugin.getConfig());
    }

    /**
     * 从配置文件中加载白名单
     * 
     * @param config 配置文件
     */
    public void loadWhitelistByConfigFile(FileConfiguration config) {
        commandWhitelist.clear();
        config.getStringList("command-whitelist.list").forEach(commandWhitelist::add);

        tabCompleteWhitelist.clear();
        config.getStringList("tab-complete-whitelist.list").forEach(tabCompleteWhitelist::add);
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
     * 获取玩家的白名单
     * 
     * @param player     玩家
     * @param premission 权限
     * @param newList    新的集合
     * @return 玩家白名单
     */
    private List<String> getPlayerWhitelist(Player player, String premission, List<String> newList) {
        for (PermissionAttachmentInfo premissionInfo : player.getEffectivePermissions()) {
            if (premissionInfo.getPermission().toLowerCase().startsWith(premission + "."))
                newList.add(premissionInfo.getPermission().substring(premission.length() + 1));
        }
        return newList;
    }

    /**
     * 获取单个玩家的命令白名单
     * 
     * @param player 玩家
     * @return 白名单
     */
    public List<String> getPlayerCommandWhitelist(Player player) {
        return getPlayerWhitelist(player, COMMAND_WHITELIST_PERMISSION, new ArrayList<>(getCommandWhitelist()));
    }

    /**
     * 获取单个玩家的Tab补全白名单
     * 
     * @param player 玩家
     * @return 白名单
     */
    public List<String> getPlayerTabCompleteWhitelist(Player player) {
        return getPlayerWhitelist(player, TAB_COMPLETE_WHITELIST_PERMISSION,
                new ArrayList<>(getTabCompleteWhitelist()));
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
        return getCommandWhitelist().contains(command);
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
        return getTabCompleteWhitelist().contains(command);
    }
}
