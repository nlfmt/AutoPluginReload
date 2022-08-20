package avaze.autopluginreload;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class Watcher extends BukkitRunnable {

    private static final AutoPluginReload plugin = AutoPluginReload.get();
    private static final AtomicBoolean running = new AtomicBoolean(false);
    public static BukkitTask task;

    public static void start() {
        running.set(true);
        task = new Watcher().runTaskAsynchronously(plugin);
    }
    public static void stop() {
        running.set(false);
        task.cancel();
    }

    public void run() {
        while (true) {
            try (WatchService service = FileSystems.getDefault().newWatchService()) {
                Map<WatchKey, Path> keyMap = new HashMap<>();
                Path path = Paths.get("plugins");
                keyMap.put(path.register(service,
                                StandardWatchEventKinds.ENTRY_CREATE,
                                StandardWatchEventKinds.ENTRY_DELETE,
                                StandardWatchEventKinds.ENTRY_MODIFY),
                        path);

                WatchKey watchKey;

                System.out.println("[AutoPluginReload] Watch Service running.");
                do {
                    watchKey = service.take();
                    Path dir = keyMap.get(watchKey);

                    for (WatchEvent<?> event : watchKey.pollEvents()) {
                        if (!running.get()) return;
                        Bukkit.getScheduler().runTask(plugin, () -> {
                            plugin.onFileEvent(event);
                        });
                    }
                } while (watchKey.reset());

                System.out.println("[AutoPluginReload] WatchKey invalid, restarting watch service...");

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
