package net.blueberrymc.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.ConcurrentHashMap;

// Currently not used.
public class ModConfigTracker {
    private static final Logger LOGGER = LogManager.getLogger();
    static final ModConfigTracker INSTANCE = new ModConfigTracker();

    private final ConcurrentHashMap<String, ModConfig> fileMap = new ConcurrentHashMap<>();

    private ModConfigTracker() {
        //
    }

    void register(ModConfig config) {
        if (fileMap.containsKey(config.getFilename())) {
            throw new IllegalArgumentException("Config file conflict: " + fileMap.get(config.getFilename()).getModId() + " and " + config.getModId());
        }
    }

    void unregister(ModConfig config) {
        fileMap.remove(config.getFilename());
    }
}
