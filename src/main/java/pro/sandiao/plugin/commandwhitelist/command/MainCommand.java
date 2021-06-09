package pro.sandiao.plugin.commandwhitelist.command;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import pro.sandiao.plugin.commandwhitelist.Main;
import pro.sandiao.plugin.commandwhitelist.command.annotation.SubCommand;
import pro.sandiao.plugin.commandwhitelist.utils.MapOrderUtil;

public class MainCommand implements CommandExecutor, TabCompleter {

    private final Map<String, Method> subCommandMap;

    public MainCommand() {
        MapOrderUtil<String, Method> orderUtil = new MapOrderUtil<>();
        Method[] methods = this.getClass().getDeclaredMethods();
        for (Method method : methods) {
            SubCommand subcmd = method.getAnnotation(SubCommand.class);
            if (subcmd != null) {
                String subcmdStr = subcmd.value();
                if (subcmdStr.isEmpty()) {
                    subcmdStr = method.getName();
                }

                orderUtil.add(subcmd.order(), subcmdStr.toLowerCase(), method);
            }
        }
        subCommandMap = orderUtil.buildMap();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
            String subCmd = args[0].toLowerCase();

            Method commandMethod = subCommandMap.get(subCmd);
            if (commandMethod != null) {
                String permission = commandMethod.getAnnotation(SubCommand.class).permission();

                if (sender.hasPermission(permission)) {
                    Class<?>[] parameterTypes = commandMethod.getParameterTypes();
                    Object[] objects = new Object[parameterTypes.length];
                    for (int i = 0; i < parameterTypes.length; i++) {
                        if (parameterTypes[i].isInstance(sender)) {
                            objects[i] = sender;
                        } else if (parameterTypes[i].isInstance(command)) {
                            objects[i] = command;
                        } else if (parameterTypes[i].isInstance(label)) {
                            objects[i] = label;
                        } else if (parameterTypes[i].isInstance(args)) {
                            objects[i] = args;
                        } else {
                            objects[i] = null;
                        }
                    }

                    try {
                        commandMethod.invoke(this, objects);
                    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                } else {
                    String permissionMessage = command.getPermissionMessage();
                    if (!permissionMessage.isEmpty())
                        for (String line : permissionMessage.replace("<permission>", permission).split("\n"))
                            sender.sendMessage(line);
                }

                return true;
            }
        }

        help(sender, label);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completes = new ArrayList<>();
        if (args.length == 1) {
            for (Entry<String, Method> entry : subCommandMap.entrySet())
                completes.add(entry.getKey());
            return completes.stream().filter(str -> str.startsWith(args[0])).collect(Collectors.toList());
        }
        return completes;
    }

    @SubCommand(permission = "commandwhitelist.command.help", usage = "查看命令帮助", order = 1)
    private void help(CommandSender sender, String label) {
        List<String> helpList = new ArrayList<>();
        for (Entry<String, Method> entry : subCommandMap.entrySet()) {
            SubCommand subCommand = entry.getValue().getAnnotation(SubCommand.class);
            String permission = subCommand.permission();
            String usage = subCommand.usage();

            if (sender.hasPermission(permission)) {
                helpList.add("Usage: /" + label + " " + entry.getKey() + " " + usage);
            }
        }
        helpList.forEach(sender::sendMessage);
    }

    @SubCommand(permission = "commandwhitelist.command.addcommand", usage = "<cmd> 向命令白名单内添加命令", order = 2)
    private void addCommand(CommandSender sender, String[] args) {
        if (args.length != 2) {
            sender.sendMessage("参数不正确.");
            return;
        }

        FileConfiguration config = Main.getInstance().getConfig();
        List<String> list = config.getStringList("command-whitelist.list");
        list.add(args[1]);
        Collections.sort(list);
        config.set("command-whitelist.list", list);
        Main.getInstance().saveConfig();
        Main.getWhitelistManager().loadWhitelistByConfigFile(Main.getInstance().getConfig());
        Main.getInstance().updateCompleteList();
        sender.sendMessage("成功向命令白名单内添加命令 " + args[1]);
    }

    @SubCommand(permission = "commandwhitelist.command.addcomplete", usage = "<cmd> 向补全白名单内添加命令", order = 3)
    private void addComplete(CommandSender sender, String[] args) {
        if (args.length != 2) {
            sender.sendMessage("参数不正确.");
            return;
        }

        FileConfiguration config = Main.getInstance().getConfig();
        List<String> list = config.getStringList("tab-complete-whitelist.list");
        list.add(args[1]);
        Collections.sort(list);
        config.set("tab-complete-whitelist.list", list);
        Main.getInstance().saveConfig();
        Main.getWhitelistManager().loadWhitelistByConfigFile(Main.getInstance().getConfig());
        Main.getInstance().updateCompleteList();
        sender.sendMessage("成功向补全白名单内添加命令 " + args[1]);
    }

    @SubCommand(permission = "commandwhitelist.command.addgroupcommand", usage = "<group> <cmd> 向组内添加命令", order = 4)
    private void addGroupCommand(CommandSender sender, String[] args) {
        if (args.length != 3) {
            sender.sendMessage("参数不正确.");
            return;
        }

        FileConfiguration config = Main.getInstance().getGroupConfig();
        List<String> list = config.getStringList("group." + args[1]);
        list.add(args[2]);
        Collections.sort(list);
        config.set("group." + args[1], list);
        Main.getInstance().saveGroupConfig();
        Main.getWhitelistManager().loadGroupByConfigFile(Main.getInstance().getGroupConfig());
        Main.getInstance().updateCompleteList();
        sender.sendMessage("成功向组 " + args[1] + " 内添加命令 " + args[2]);
    }

    @SubCommand(permission = "commandwhitelist.command.removecommand", usage = "<cmd> 从命令白名单中移除命令", order = 5)
    private void removeCommand(CommandSender sender, String[] args) {
        if (args.length != 2) {
            sender.sendMessage("参数不正确.");
            return;
        }

        FileConfiguration config = Main.getInstance().getConfig();
        List<String> list = config.getStringList("command-whitelist.list");
        if (!list.remove(args[1])) {
            sender.sendMessage("命令白名单中没有找到这条命令 " + args[1]);
            return;
        }
        Collections.sort(list);
        config.set("command-whitelist.list", list);
        Main.getInstance().saveConfig();
        Main.getWhitelistManager().loadWhitelistByConfigFile(Main.getInstance().getConfig());
        Main.getInstance().updateCompleteList();
        sender.sendMessage("成功从命令白名单中移除命令 " + args[1]);
    }

    @SubCommand(permission = "commandwhitelist.command.removecomplete", usage = "<cmd> 从补全白名单中移除命令", order = 6)
    private void removeComplete(CommandSender sender, String[] args) {
        if (args.length != 2) {
            sender.sendMessage("参数不正确.");
            return;
        }

        FileConfiguration config = Main.getInstance().getConfig();
        List<String> list = config.getStringList("tab-complete-whitelist.list");
        if (!list.remove(args[1])) {
            sender.sendMessage("补全白名单中没有找到这条命令 " + args[1]);
            return;
        }
        Collections.sort(list);
        config.set("tab-complete-whitelist.list", list);
        Main.getInstance().saveConfig();
        Main.getWhitelistManager().loadWhitelistByConfigFile(Main.getInstance().getConfig());
        Main.getInstance().updateCompleteList();
        sender.sendMessage("成功从补全白名单中移除命令 " + args[1]);
    }

    @SubCommand(permission = "commandwhitelist.command.removegroupcommand", usage = "<group> <cmd> 从组内移除命令", order = 7)
    private void removeGroupCommand(CommandSender sender, String[] args) {
        if (args.length != 3) {
            sender.sendMessage("参数不正确.");
            return;
        }

        FileConfiguration config = Main.getInstance().getGroupConfig();
        List<String> list = config.getStringList("group." + args[1]);
        if (list.isEmpty() || !list.remove(args[2])) {
            sender.sendMessage(args[1] + " 组内找不到命令 " + args[2]);
            return;
        }
        Collections.sort(list);
        config.set("group." + args[1], list.isEmpty() ? null : list);
        Main.getInstance().saveGroupConfig();
        Main.getWhitelistManager().loadGroupByConfigFile(Main.getInstance().getGroupConfig());
        Main.getInstance().updateCompleteList();
        sender.sendMessage("成功从组 " + args[1] + " 内移除命令 " + args[2]);
    }

    @SubCommand(permission = "commandwhitelist.command.canrun", usage = "<cmd> [player] 判断玩家能否执行该命令", order = 8)
    private void canRun(CommandSender sender, String[] args) {
        Player player;
        if (args.length == 2) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("当前命令不是由玩家执行, 请输入一个玩家名.");
                return;
            }
            player = (Player) sender;
        } else if (args.length == 3) {
            Player target = Bukkit.getPlayer(args[2]);
            if (target == null) {
                sender.sendMessage("找不到一个在线玩家 " + args[2]);
                return;
            }
            player = target;
        } else {
            sender.sendMessage("参数不正确.");
            return;
        }

        if (Main.getWhitelistManager().hasCommandWhitelist(player, args[1])) {
            sender.sendMessage("玩家 " + player.getName() + " 允许执行命令 " + args[1]);
        } else {
            sender.sendMessage("玩家 " + player.getName() + " 无法执行命令 " + args[1]);
        }
    }

    @SubCommand(permission = "commandwhitelist.command.cancomplete", usage = "<cmd> [player] 判断玩家能否补全该命令", order = 9)
    private void canComplete(CommandSender sender, String[] args) {
        Player player;
        if (args.length == 2) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("当前命令不是由玩家执行, 请输入一个玩家名.");
                return;
            }
            player = (Player) sender;
        } else if (args.length == 3) {
            Player target = Bukkit.getPlayer(args[2]);
            if (target == null) {
                sender.sendMessage("找不到一个在线玩家 " + args[2]);
                return;
            }
            player = target;
        } else {
            sender.sendMessage("参数不正确.");
            return;
        }

        if (Main.getWhitelistManager().hasTabCompleteWhitelist(player, args[1])) {
            sender.sendMessage("玩家 " + player.getName() + " 允许执行命令 " + args[1]);
        } else {
            sender.sendMessage("玩家 " + player.getName() + " 无法执行命令 " + args[1]);
        }
    }

    @SubCommand(permission = "commandwhitelist.command.reload", usage = "重载插件", order = 10)
    private void reload(CommandSender sender) {
        Main.getInstance().onReload(sender);
    }
}
