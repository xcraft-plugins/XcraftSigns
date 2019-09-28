package de.xcraft.voronwe.XcraftSigns;

import de.xcraft.voronwe.XcraftSigns.Checkpoints.CPEntrySignSet;
import de.xcraft.voronwe.XcraftSigns.Checkpoints.CPUnlockSignSet;
import de.xcraft.voronwe.XcraftSigns.ListenerBlock;
import de.xcraft.voronwe.XcraftSigns.ListenerPlayer;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

public class XcraftSigns
extends JavaPlugin {
    public final Logger log = Logger.getLogger("Minecraft");
    private Plugin vault = null;
    private Economy economy = null;
    private final ListenerBlock blockListener = new ListenerBlock(this);
    private final ListenerPlayer playerListener = new ListenerPlayer(this);
    private final CPUnlockSignSet cpUnlockSigns = new CPUnlockSignSet(this);
    private final CPEntrySignSet cpEntrySigns = new CPEntrySignSet(this);
    private PluginManager pm = null;

    public void onEnable() {
        this.pm = this.getServer().getPluginManager();
        this.pm.registerEvents((Listener)this.blockListener, (Plugin)this);
        this.pm.registerEvents((Listener)this.playerListener, (Plugin)this);
        this.cpUnlockSigns.load();
        this.cpEntrySigns.load();
        this.setupEconomy();
        this.getServer().getScheduler().runTaskTimerAsynchronously((Plugin)this, new Runnable(){

            public void run() {
                XcraftSigns.this.cpUnlockSigns.save();
                XcraftSigns.this.cpEntrySigns.save();
            }
        }, 12000L, 12000L);
        this.log.info(this.getNameBrackets() + "enabled.");
    }

    public void onDisable() {
        this.cpUnlockSigns.save();
        this.cpEntrySigns.save();
    }

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (cmd.getName().equalsIgnoreCase("xcs")) {
            sender.sendMessage("" + this.getCPUnlockSigns().size() + " Unlock Signs, " + this.getCPEntrySigns().size() + " Entry Signs.");
        }
        return false;
    }

    public static File getConfigFile(JavaPlugin plugin, String fileName) {
        File configFile = new File(plugin.getDataFolder(), fileName);
        if (!configFile.exists()) {
            try {
                plugin.getDataFolder().mkdir();
                plugin.getDataFolder().setWritable(true);
                plugin.getDataFolder().setExecutable(true);
                configFile.createNewFile();
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return configFile;
    }

    public CPUnlockSignSet getCPUnlockSigns() {
        return this.cpUnlockSigns;
    }

    public CPEntrySignSet getCPEntrySigns() {
        return this.cpEntrySigns;
    }

    public String getNameBrackets() {
        return "[" + this.getDescription().getFullName() + "] ";
    }

    public PluginManager getPluginManager() {
        return this.pm;
    }

    public boolean rewardPlayer(Player player, double reward) {
        if (player != null) {
            if (reward > 0.0) {
                this.getEconomy().depositPlayer((OfflinePlayer)player, reward);
                player.sendMessage(this.getCheckpointPrefix() + (Object)ChatColor.DARK_AQUA + "Du erh\u00e4ltst " + (Object)ChatColor.GOLD + reward + (Object)ChatColor.DARK_AQUA + " Euronen.");
            } else if (reward < 0.0 && this.getEconomy().has((OfflinePlayer)player, reward)) {
                this.getEconomy().withdrawPlayer((OfflinePlayer)player, - reward);
                player.sendMessage(this.getCheckpointPrefix() + (Object)ChatColor.DARK_AQUA + "Dir wurden " + (Object)ChatColor.RED + reward * -1.0 + (Object)ChatColor.DARK_AQUA + " Euronen abgezogen.");
            } else {
                if (reward == 0.0) {
                    return true;
                }
                return false;
            }
            return true;
        }
        return false;
    }

    public Economy getEconomy() {
        return this.economy;
    }

    public void setupEconomy() {
        if (this.vault != null) {
            return;
        }
        Plugin vaultCheck = this.pm.getPlugin("Vault");
        if (vaultCheck != null && vaultCheck.isEnabled()) {
            this.vault = vaultCheck;
            this.log.info(this.getNameBrackets() + "found Vault plugin.");
            RegisteredServiceProvider economyProvider = this.getServer().getServicesManager().getRegistration(Economy.class);
            if (economyProvider != null) {
                this.economy = (Economy)economyProvider.getProvider();
                this.log.info(this.getNameBrackets() + "Reported economy provider: " + this.economy.getName());
            }
        }
    }

    public String getCheckpointPrefix() {
        return (Object)ChatColor.WHITE + "[" + (Object)ChatColor.DARK_GREEN + "Checkpoint" + (Object)ChatColor.WHITE + "] ";
    }

    public void pluginLog(String message) {
        try {
            File saveTo;
            File dataFolder = this.getDataFolder();
            if (!dataFolder.exists()) {
                dataFolder.mkdir();
            }
            if (!(saveTo = new File(this.getDataFolder(), "log.txt")).exists()) {
                saveTo.createNewFile();
            }
            PrintWriter writer = new PrintWriter(new FileWriter(saveTo, true));
            String date = new SimpleDateFormat("HH:mm:ss").format(new Date());
            writer.println("[" + date + "]" + message);
            writer.flush();
            writer.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

}

