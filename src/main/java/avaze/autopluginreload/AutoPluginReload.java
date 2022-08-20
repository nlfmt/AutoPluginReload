package avaze.autopluginreload;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;

public final class AutoPluginReload extends JavaPlugin implements Listener {

    private static AutoPluginReload plugin;
    public static AutoPluginReload get() {
        return plugin;
    }

    private static boolean enabled = true;
    public static boolean toggle() {
        enabled = !enabled;
        return enabled;
    }

    @Override
    public void onEnable() {
        plugin = this;

        getCommand("autopluginreload").setExecutor(new ToggleCommand());

        Watcher.start();
    }


    public void onFileEvent(WatchEvent<?> event) {
        if (!enabled) return;


        WatchEvent.Kind<?> kind = event.kind();
        Path eventPath = (Path) event.context();

        if (!eventPath.toString().endsWith(".jar")) return;

        String action = switch (kind.name()) {
            case "ENTRY_CREATE" -> "§aadded";
            case "ENTRY_DELETE" -> "§cdeleted";
            case "ENTRY_MODIFY" -> "§6modified";
            default -> null;
        };

        getServer().broadcastMessage("[§6AutoPluginReload§f] Plugin '§e" + eventPath + "§f' " + action);
        Bukkit.reload();
    }


    @Override
    public void onDisable() {
        Watcher.stop();
        while (!Watcher.task.isCancelled()) {
            Thread.onSpinWait();
        }
        System.out.println("[AutoPluginReload] Successfully shut down.");
    }
}
