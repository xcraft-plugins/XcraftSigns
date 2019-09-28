package de.groovybyte.spigot.xcraftsigns;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class XcraftSignsPlugin extends JavaPlugin {

    private Economy economy;
    private final BlockListener blockListener = new BlockListener(this);
    private final PlayerListener playerListener = new PlayerListener(this);
    private final CheckPointRepository checkpointRepo = new CheckPointRepository(this);
    private final TravelPointRepository travelpointRepo = new TravelPointRepository(this);

    @Override
    public void onEnable() {
        setupEconomy();
        registerListeners();
        initRepositories();
        registerSaveJob();
    }

    @Override
    public void onDisable() {
        saveState();
    }

    private void registerListeners() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(blockListener, this);
        pm.registerEvents(playerListener, this);
    }

    private void initRepositories() {
        checkpointRepo.load();
        travelpointRepo.load();
    }

    private static final int AUTO_SAVE_DELAY = 12000;

    private void registerSaveJob() {
        getServer().getScheduler()
            .runTaskTimerAsynchronously(
                this,
                this::saveState,
                AUTO_SAVE_DELAY,
                AUTO_SAVE_DELAY
            );
    }

    private void saveState() {
        checkpointRepo.save();
        travelpointRepo.save();
    }

    private void setupEconomy() {
        Plugin vault = getServer().getPluginManager().getPlugin("Vault");
        if (vault != null && vault.isEnabled()) {
            RegisteredServiceProvider<Economy> economyProvider = getServer()
                .getServicesManager()
                .getRegistration(Economy.class);
            if (economyProvider != null) {
                this.economy = economyProvider.getProvider();
                this.getLogger().info("Found economy provider: " + this.economy.getName());
            }
        }
    }
}
