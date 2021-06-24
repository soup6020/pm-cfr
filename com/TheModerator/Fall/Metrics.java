/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.configuration.InvalidConfigurationException
 *  org.bukkit.configuration.file.YamlConfiguration
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.PluginDescriptionFile
 *  org.bukkit.scheduler.BukkitTask
 */
package com.TheModerator.Fall;

import com.TheModerator.Fall.Fall;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.scheduler.BukkitTask;

public class Metrics {
    private static final int REVISION = 6;
    private static final String BASE_URL = "http://mcstats.org";
    private static final String REPORT_URL = "/report/%s";
    private static final String CUSTOM_DATA_SEPARATOR = "~~";
    private static final int PING_INTERVAL = 10;
    private final Plugin plugin;
    private final Set<Graph> graphs = Collections.synchronizedSet(new HashSet());
    private final Graph defaultGraph = new Graph("Default");
    private final YamlConfiguration configuration;
    private final File configurationFile;
    private final String guid;
    private final boolean debug;
    private final Object optOutLock = new Object();
    private volatile BukkitTask task = null;

    public Metrics(Plugin plugin) throws IOException {
        if (plugin == null) {
            throw new IllegalArgumentException("Plugin cannot be null");
        }
        this.plugin = plugin;
        this.configurationFile = this.getConfigFile();
        this.configuration = YamlConfiguration.loadConfiguration((File)this.configurationFile);
        this.configuration.addDefault("opt-out", (Object)false);
        this.configuration.addDefault("guid", (Object)UUID.randomUUID().toString());
        this.configuration.addDefault("debug", (Object)false);
        if (this.configuration.get("guid", null) == null) {
            this.configuration.options().header(BASE_URL).copyDefaults(true);
            this.configuration.save(this.configurationFile);
        }
        this.guid = this.configuration.getString("guid");
        this.debug = this.configuration.getBoolean("debug", false);
    }

    public Graph createGraph(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Graph name cannot be null");
        }
        Graph graph = new Graph(name);
        this.graphs.add(graph);
        return graph;
    }

    public void addGraph(Graph graph) {
        if (graph == null) {
            throw new IllegalArgumentException("Graph cannot be null");
        }
        this.graphs.add(graph);
    }

    public void addCustomData(Plotter plotter) {
        if (plotter == null) {
            throw new IllegalArgumentException("Plotter cannot be null");
        }
        this.defaultGraph.addPlotter(plotter);
        this.graphs.add(this.defaultGraph);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean start() {
        Object object = this.optOutLock;
        synchronized (object) {
            block6: {
                block5: {
                    if (!this.isOptOut()) break block5;
                    return false;
                }
                if (this.task == null) break block6;
                return true;
            }
            this.task = this.plugin.getServer().getScheduler().runTaskTimerAsynchronously(this.plugin, new Runnable(){
                private boolean firstPost = true;

                /*
                 * WARNING - Removed try catching itself - possible behaviour change.
                 */
                @Override
                public void run() {
                    block7: {
                        try {
                            Object object = Metrics.this.optOutLock;
                            synchronized (object) {
                                if (Metrics.this.isOptOut() && Metrics.this.task != null) {
                                    Metrics.this.task.cancel();
                                    Metrics.this.task = null;
                                    for (Graph graph : Metrics.this.graphs) {
                                        graph.onOptOut();
                                    }
                                }
                            }
                            Metrics.this.postPlugin(!this.firstPost);
                            this.firstPost = false;
                        }
                        catch (IOException e) {
                            if (!Metrics.this.debug) break block7;
                            Bukkit.getLogger().log(Level.INFO, "[Metrics] " + e.getMessage());
                        }
                    }
                }
            }, 0L, 12000L);
            return true;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public boolean isOptOut() {
        Object object = this.optOutLock;
        synchronized (object) {
            try {
                this.configuration.load(this.getConfigFile());
            }
            catch (IOException ex) {
                if (this.debug) {
                    Bukkit.getLogger().log(Level.INFO, "[Metrics] " + ex.getMessage());
                }
                return true;
            }
            catch (InvalidConfigurationException ex) {
                if (this.debug) {
                    Bukkit.getLogger().log(Level.INFO, "[Metrics] " + ex.getMessage());
                }
                return true;
            }
            return this.configuration.getBoolean("opt-out", false);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void enable() throws IOException {
        Object object = this.optOutLock;
        synchronized (object) {
            if (this.isOptOut()) {
                this.configuration.set("opt-out", (Object)false);
                this.configuration.save(this.configurationFile);
            }
            if (this.task == null) {
                this.start();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void disable() throws IOException {
        Object object = this.optOutLock;
        synchronized (object) {
            if (!this.isOptOut()) {
                this.configuration.set("opt-out", (Object)true);
                this.configuration.save(this.configurationFile);
            }
            if (this.task != null) {
                this.task.cancel();
                this.task = null;
            }
        }
    }

    public File getConfigFile() {
        File pluginsFolder = this.plugin.getDataFolder().getParentFile();
        return new File(new File(pluginsFolder, "PluginMetrics"), "config.yml");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void postPlugin(boolean isPing) throws IOException {
        PluginDescriptionFile description = this.plugin.getDescription();
        String pluginName = description.getName();
        boolean onlineMode = Bukkit.getServer().getOnlineMode();
        String pluginVersion = description.getVersion();
        String serverVersion = Bukkit.getVersion();
        int playersOnline = Bukkit.getServer().getOnlinePlayers().length;
        StringBuilder data = new StringBuilder();
        data.append(Metrics.encode("guid")).append('=').append(Metrics.encode(this.guid));
        Metrics.encodeDataPair(data, "version", pluginVersion);
        Metrics.encodeDataPair(data, "server", serverVersion);
        Metrics.encodeDataPair(data, "players", Integer.toString(playersOnline));
        Metrics.encodeDataPair(data, "revision", String.valueOf(6));
        String osname = System.getProperty("os.name");
        String osarch = System.getProperty("os.arch");
        String osversion = System.getProperty("os.version");
        String java_version = System.getProperty("java.version");
        int coreCount = Runtime.getRuntime().availableProcessors();
        if (osarch.equals("amd64")) {
            osarch = "x86_64";
        }
        Metrics.encodeDataPair(data, "osname", osname);
        Metrics.encodeDataPair(data, "osarch", osarch);
        Metrics.encodeDataPair(data, "osversion", osversion);
        Metrics.encodeDataPair(data, "cores", Integer.toString(coreCount));
        Metrics.encodeDataPair(data, "online-mode", Boolean.toString(onlineMode));
        Metrics.encodeDataPair(data, "java_version", java_version);
        if (isPing) {
            Metrics.encodeDataPair(data, "ping", "true");
        }
        Set<Graph> set = this.graphs;
        synchronized (set) {
            for (Graph graph : this.graphs) {
                for (Plotter plotter : graph.getPlotters()) {
                    String key = String.format("C%s%s%s%s", CUSTOM_DATA_SEPARATOR, graph.getName(), CUSTOM_DATA_SEPARATOR, plotter.getColumnName());
                    String value = Integer.toString(plotter.getValue());
                    Metrics.encodeDataPair(data, key, value);
                }
            }
        }
        Fall.Punishments[] punishmentsArray = Fall.Punishments.values();
        int graph = punishmentsArray.length;
        int iter = 0;
        while (iter < graph) {
            Fall.Punishments punishment = punishmentsArray[iter];
            String key = String.format("C%s%s%s%s", CUSTOM_DATA_SEPARATOR, "Punishments", CUSTOM_DATA_SEPARATOR, punishment.toString());
            String value = Integer.toString(Fall.PunishmentUsage.get((Object)punishment));
            Fall.PunishmentUsage.remove((Object)punishment);
            Fall.PunishmentUsage.put(punishment, 0);
            Metrics.encodeDataPair(data, key, value);
            ++iter;
        }
        URL url = new URL(BASE_URL + String.format(REPORT_URL, Metrics.encode(pluginName)));
        URLConnection connection = this.isMineshafterPresent() ? url.openConnection(Proxy.NO_PROXY) : url.openConnection();
        connection.setDoOutput(true);
        OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
        writer.write(data.toString());
        writer.flush();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String response = bufferedReader.readLine();
        writer.close();
        bufferedReader.close();
        if (response == null || response.startsWith("ERR")) {
            throw new IOException(response);
        }
        if (response.contains("OK This is your first update this hour")) {
            Set<Graph> set2 = this.graphs;
            synchronized (set2) {
                for (Graph graph2 : this.graphs) {
                    for (Plotter plotter3 : graph2.getPlotters()) {
                        plotter3.reset();
                    }
                }
            }
        }
    }

    private boolean isMineshafterPresent() {
        try {
            Class.forName("mineshafter.MineServer");
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }

    private static void encodeDataPair(StringBuilder buffer, String key, String value) throws UnsupportedEncodingException {
        buffer.append('&').append(Metrics.encode(key)).append('=').append(Metrics.encode(value));
    }

    private static String encode(String text) throws UnsupportedEncodingException {
        return URLEncoder.encode(text, "UTF-8");
    }

    public static class Graph {
        private final String name;
        private final Set<Plotter> plotters = new LinkedHashSet<Plotter>();

        private Graph(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

        public void addPlotter(Plotter plotter) {
            this.plotters.add(plotter);
        }

        public void removePlotter(Plotter plotter) {
            this.plotters.remove(plotter);
        }

        public Set<Plotter> getPlotters() {
            return Collections.unmodifiableSet(this.plotters);
        }

        public int hashCode() {
            return this.name.hashCode();
        }

        public boolean equals(Object object) {
            if (!(object instanceof Graph)) {
                return false;
            }
            Graph graph = (Graph)object;
            return graph.name.equals(this.name);
        }

        protected void onOptOut() {
        }
    }

    public static abstract class Plotter {
        private final String name;

        public Plotter() {
            this("Default");
        }

        public Plotter(String name) {
            this.name = name;
        }

        public abstract int getValue();

        public String getColumnName() {
            return this.name;
        }

        public void reset() {
        }

        public int hashCode() {
            return this.getColumnName().hashCode();
        }

        public boolean equals(Object object) {
            if (!(object instanceof Plotter)) {
                return false;
            }
            Plotter plotter = (Plotter)object;
            return plotter.name.equals(this.name) && plotter.getValue() == this.getValue();
        }
    }
}

