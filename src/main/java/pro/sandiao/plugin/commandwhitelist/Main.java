package pro.sandiao.plugin.commandwhitelist;

import java.lang.reflect.Constructor;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import pro.sandiao.plugin.commandwhitelist.command.MainCommand;
import pro.sandiao.plugin.commandwhitelist.listener.CommandListener;
import pro.sandiao.plugin.commandwhitelist.listener.TabCompleteListener;
import pro.sandiao.plugin.commandwhitelist.listener.TabCompletePackageListener;
import pro.sandiao.plugin.commandwhitelist.manager.WhitelistManager;

public class Main extends JavaPlugin {

    private static Main instance;
    private static WhitelistManager whitelistManager;
    private CommandListener commandListener;
    private TabCompleteListener tabCompleteListener;
    private PluginCommand command;
    private boolean isHighVersion = true;
    private boolean hasProtocolLib = false;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();
        try {
            Constructor<WhitelistManager> c = WhitelistManager.class.getDeclaredConstructor(Plugin.class);
            c.setAccessible(true);
            whitelistManager = c.newInstance(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Set<String> commands = getDescription().getCommands().keySet();
        if (commands != null && commands.size() == 1) {
            command = getCommand(commands.toArray()[0].toString());
        }

        if (command != null) {
            MainCommand mainCommand = new MainCommand();
            command.setExecutor(mainCommand);
            command.setTabCompleter(mainCommand);
        }

        String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        Bukkit.getConsoleSender().sendMessage("[CommandWhitelis] §aBukkit kernel version §b" + version);

        commandListener = new CommandListener(this);
        if (getConfig().getBoolean("command-whitelist.enable")) {
            commandListener.registerListener();
            Bukkit.getConsoleSender().sendMessage("[CommandWhitelis] §aBlocking command turned on.");
        }

        int i = Integer.valueOf(version.split("_")[1]);
        if (version.startsWith("v1") && i < 13) {
            isHighVersion = false;
        } else {
            tabCompleteListener = new TabCompleteListener(this);
        }

        hasProtocolLib = Bukkit.getPluginManager().getPlugin("ProtocolLib") != null;

        if (getConfig().getBoolean("tab-complete-whitelist.enable")) {
            Bukkit.getConsoleSender().sendMessage("[CommandWhitelis] §aBlocking tab complete turned on.");
            if (getConfig().getBoolean("tab-complete-whitelist.protocol-lib")) {
                if (isHighVersion)
                    tabCompleteListener.registerListener();
                if (hasProtocolLib) {
                    new TabCompletePackageListener(this).registerListener(isHighVersion);
                } else {
                    getLogger().warning("We not found the ProtocolLib.");
                }
            } else if (isHighVersion) {
                tabCompleteListener.registerListener();
            } else {
                getLogger().warning("No! You didn't open ProtocolLib.");
                getLogger().warning("We can't stop the tab completing.");
            }
        }
    }

    @Override
    public void onDisable() {

    }

    /**
     * 重载插件
     * 
     * @param sender 发起者
     */
    public void onReload(CommandSender sender) {
        sender.sendMessage("[CommandWhitelis] §aPlugin starts to reload.");
        saveDefaultConfig();
        reloadConfig();
        whitelistManager.loadWhitelistByConfigFile(getConfig());
        sender.sendMessage("[CommandWhitelis] §aPlugin reload success.");
    }

    /**
     * 获取插件实例
     * 
     * @return this
     */
    public static Main getInstance() {
        return instance;
    }

    /**
     * 获取白名单管理器
     * 
     * @return 白名单管理器
     */
    public static WhitelistManager getWhitelistManager() {
        return whitelistManager;
    }
}
