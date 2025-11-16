package com.tejaslamba.smpcore.manager;

import com.tejaslamba.smpcore.Main;
import com.tejaslamba.smpcore.feature.Feature;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class FeatureManager {

    private final Main plugin;
    private final Map<String, Feature> features = new LinkedHashMap<>();

    public FeatureManager(Main plugin) {
        this.plugin = plugin;
    }

    public void loadFeatures() {
        features.clear();
        boolean verbose = plugin.getConfigManager().get().getBoolean("plugin.verbose", false);

        try {
            String packageName = "com.tejaslamba.smpcore.features";
            Class[] featureClasses = getClasses(packageName);

            if (verbose) {
                plugin.getLogger().info("[VERBOSE] Scanning for features in package: " + packageName);
                plugin.getLogger().info("[VERBOSE] Found " + featureClasses.length + " classes to check");
            }

            for (Class<?> clazz : featureClasses) {
                if (Feature.class.isAssignableFrom(clazz) && !clazz.isInterface()) {
                    try {
                        Feature feature = (Feature) clazz.getDeclaredConstructor().newInstance();
                        features.put(feature.getName(), feature);
                        feature.onEnable(plugin);
                        plugin.getLogger().info("Loaded feature: " + feature.getName());

                        if (verbose) {
                            plugin.getLogger().info("[VERBOSE] Feature '" + feature.getName() + "' registered");
                            plugin.getLogger().info("[VERBOSE]   - Config Path: " + feature.getConfigPath());
                            plugin.getLogger().info("[VERBOSE]   - Enabled: " + feature.isEnabled());
                            plugin.getLogger().info("[VERBOSE]   - Has Listener: " + (feature.getListener() != null));
                        }
                    } catch (Exception e) {
                        plugin.getLogger()
                                .warning("Failed to load feature: " + clazz.getName() + " - " + e.getMessage());
                        if (verbose) {
                            plugin.getLogger().warning("[VERBOSE] Stack trace:");
                            e.printStackTrace();
                        }
                    }
                }
            }

            if (verbose) {
                plugin.getLogger().info("[VERBOSE] Total features loaded: " + features.size());
            }
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to scan features: " + e.getMessage());
            if (verbose) {
                e.printStackTrace();
            }
        }
    }

    public void disableAll() {
        for (Feature feature : features.values()) {
            feature.onDisable();
        }
        features.clear();
    }

    public Collection<Feature> getFeatures() {
        return features.values();
    }

    public Feature getFeature(String name) {
        return features.get(name);
    }

    public List<ItemStack> getMenuItems() {
        List<ItemStack> items = new ArrayList<>();
        for (Feature feature : features.values()) {
            ItemStack item = feature.getMenuItem();
            if (item != null) {
                items.add(item);
            }
        }
        return items;
    }

    private Class[] getClasses(String packageName) throws Exception {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String path = packageName.replace('.', '/');
        File jarFile = new File(getClass().getProtectionDomain().getCodeSource().getLocation().toURI());

        List<Class> classes = new ArrayList<>();

        if (jarFile.isFile()) {
            try (JarFile jar = new JarFile(jarFile)) {
                Enumeration<JarEntry> entries = jar.entries();
                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    String name = entry.getName();
                    if (name.startsWith(path) && name.endsWith(".class")) {
                        String className = name.substring(0, name.length() - 6).replace('/', '.');
                        try {
                            classes.add(Class.forName(className));
                        } catch (ClassNotFoundException | NoClassDefFoundError ignored) {
                        }
                    }
                }
            }
        } else {
            URL resource = classLoader.getResource(path);
            if (resource != null) {
                File directory = new File(resource.toURI());
                if (directory.exists()) {
                    for (File file : directory.listFiles()) {
                        if (file.getName().endsWith(".class")) {
                            String className = packageName + '.'
                                    + file.getName().substring(0, file.getName().length() - 6);
                            try {
                                classes.add(Class.forName(className));
                            } catch (ClassNotFoundException | NoClassDefFoundError ignored) {
                            }
                        }
                    }
                }
            }
        }

        return classes.toArray(new Class[0]);
    }
}
