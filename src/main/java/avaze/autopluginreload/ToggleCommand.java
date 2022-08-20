package avaze.autopluginreload;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ToggleCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        boolean state = AutoPluginReload.toggle();
        sender.sendMessage("[§9AutoPluginReload§f] Auto-Reload " + (state ? "§aenabled" : "§cdisabled"));

        return true;
    }
}
