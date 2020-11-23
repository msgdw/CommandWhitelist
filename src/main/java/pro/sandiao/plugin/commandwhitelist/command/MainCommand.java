package pro.sandiao.plugin.commandwhitelist.command;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import pro.sandiao.plugin.commandwhitelist.Main;
import pro.sandiao.plugin.commandwhitelist.command.annotation.SubCommand;

public class MainCommand implements CommandExecutor, TabCompleter {

    private final Map<String, Method> subCommandMap = new HashMap<>();

    public MainCommand() {
        Method[] methods = this.getClass().getDeclaredMethods();
        for (int i = 0; i < methods.length; i++) {
            SubCommand subcmd = methods[i].getAnnotation(SubCommand.class);
            if (subcmd != null) {
                String subcmdStr = subcmd.value();
                if (subcmd.value().isEmpty()) {
                    subcmdStr = methods[i].getName();
                }

                subCommandMap.put(subcmdStr, methods[i]);
            }
        }
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
                        for (String line : permissionMessage.replace("<permission>", permission).split("\n")) {
                            sender.sendMessage(line);
                        }
                }

                return true;
            }
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return new ArrayList<>(0);
    }

    @SubCommand(value = "reload", permission = "commandwhitelist.command.reload", usage = "重载插件")
    private void onReloadCommand(CommandSender sender) {
        Main.getInstance().onReload(sender);
    }

    @SubCommand(value = "help", permission = "commandwhitelist.command.help", usage = "查看命令帮助")
    private void onHelpCommand(CommandSender sender, Command command, String label) {
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
}
