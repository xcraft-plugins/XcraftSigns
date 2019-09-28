package de.xcraft.voronwe.XcraftSigns;

import de.xcraft.voronwe.XcraftSigns.Checkpoints.CPEntrySign;
import de.xcraft.voronwe.XcraftSigns.Checkpoints.CPUnlockSign;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.Plugin;

public class ListenerPlayer implements Listener {
    private XcraftSigns plugin;

    public ListenerPlayer(XcraftSigns signs) {
        this.plugin = signs;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!event.hasBlock()) {
            return;
        }
        if (event.getAction().equals((Object) Action.LEFT_CLICK_BLOCK) && event.getPlayer()
            .isSneaking() && !event.getPlayer().hasPermission("XcraftSigns.signs.edit")) {
            return;
        }
        Location loc = null;
        if ((event.getAction().equals((Object) Action.LEFT_CLICK_BLOCK) || event.getAction()
            .equals((Object) Action.RIGHT_CLICK_BLOCK)) && (event.getClickedBlock()
            .getType()
            .equals((Object) Material.WALL_SIGN) || event.getClickedBlock()
            .getType()
            .equals((Object) Material.SIGN))) {
            loc = event.getClickedBlock().getLocation();
        }
        if (loc != null) {
            if (this.checkUnlockByLocation(loc, event.getPlayer())) {
                return;
            }
            if (this.checkEntryByLocation(loc, event.getPlayer())) {
                return;
            }
        }
    }

    private boolean checkEntryByLocation(Location location, Player player) {
        final CPEntrySign cpEntrySign = this.plugin.getCPEntrySigns().getSignByLocation(location);
        if (cpEntrySign != null) {
            CPUnlockSign cpUnlockSign = this.plugin.getCPUnlockSigns()
                .getSignByName(cpEntrySign.getName());
            if (!cpUnlockSign.isPlayerUnlocked(player.getUniqueId())) {
                player.sendMessage(
                    this.getChatprefix() + (Object) ChatColor.RED + "noch nicht freigeschaltet.");
            } else {
                double reward = cpEntrySign.getReward();
                if (reward < 0.0 && !this.plugin.getEconomy()
                    .has((OfflinePlayer) player, -reward)) {
                    player.sendMessage(
                        this.getChatprefix() + (Object) ChatColor.RED + "Du hast nicht genug Geld, um diesen Checkpoint zu verwenden.");
                    return true;
                }
                this.plugin.rewardPlayer(player, reward);
                if (cpEntrySign.hasLever()) {
                    if (cpEntrySign.activate()) {
                        player.sendMessage(
                            this.getChatprefix() + (Object) ChatColor.GOLD + cpEntrySign.getName() + (Object) ChatColor.DARK_AQUA + " aktiviert f\ufffdr " + cpEntrySign
                                .getDuration() + " Sekunden.");
                        this.plugin.getServer()
                            .getScheduler()
                            .scheduleSyncDelayedTask((Plugin) this.plugin, new Runnable() {

                                public void run() {
                                    cpEntrySign.deactivate();
                                }
                            }, (long) (cpEntrySign.getDuration() * 20));
                    }
                } else {
                    Location loc = cpUnlockSign.getPlayerLocation();
                    Float yaw = Float.valueOf(player.getLocation().getYaw());
                    Float pitch = Float.valueOf(player.getLocation().getPitch());
                    loc.setPitch(pitch.floatValue());
                    loc.setYaw(yaw.floatValue());
                    final Location targetLoc = loc;
                    final Player finalPlayer = player;
                    this.plugin.getServer()
                        .getScheduler()
                        .scheduleSyncDelayedTask((Plugin) this.plugin, new Runnable() {

                            public void run() {
                                finalPlayer.teleport(targetLoc);
                            }
                        }, 2L);
                }
            }
        }
        return false;
    }

    private boolean checkUnlockByLocation(Location loc, Player player) {
        CPUnlockSign cpUnlockSign = this.plugin.getCPUnlockSigns().getSignByLocation(loc);
        if (cpUnlockSign != null) {
            Double reward;
            if (cpUnlockSign.getReward() != null && (reward = cpUnlockSign.getReward()) < 0.0 && !this.plugin
                .getEconomy()
                .has((OfflinePlayer) player, cpUnlockSign.getReward() * -1.0)) {
                player.sendMessage(
                    this.getChatprefix() + (Object) ChatColor.RED + "Du hast nicht genug Geld, um diesen Checkpoint zu verwenden.");
                return false;
            }
            if (cpUnlockSign.hasDependency()) {
                if (cpUnlockSign.getDependency() == null) {
                    player.sendMessage(
                        this.getChatprefix() + (Object) ChatColor.RED + "Ausnahmefehler. Schreibe bitte ein Ticket!");
                    return true;
                }
                if (!cpUnlockSign.getDependency().isPlayerUnlocked(player.getUniqueId())) {
                    player.sendMessage(
                        this.getChatprefix() + (Object) ChatColor.RED + "Du musst erst " + cpUnlockSign
                            .getDependency()
                            .getName() + " freischalten.");
                    return true;
                }
            }
            if (cpUnlockSign.unlockPlayer(player.getUniqueId())) {
                player.sendMessage(
                    this.getChatprefix() + (Object) ChatColor.GOLD + cpUnlockSign.getName() + (Object) ChatColor.DARK_AQUA + " freigeschaltet!");
                this.plugin.rewardPlayer(player, cpUnlockSign.getReward());
                this.plugin.pluginLog(
                    "Checkpoint " + cpUnlockSign.getName() + " von " + player.getName() + " freigeschaltet");
                this.plugin.log.info(
                    this.plugin.getNameBrackets() + "Checkpoint " + cpUnlockSign.getName() + " von " + player
                        .getName() + " freigeschaltet");
                if (player.hasPermission("XcraftSigns.checkpoints.heal")) {
                    player.setHealth(20.0);
                }
                return true;
            }
            if (player.hasPermission("XcraftSigns.checkpoints.heal")) {
                player.setHealth(20.0);
            }
            if (player.hasPermission("XcraftSigns.checkpoints.spawn")) {
                player.setBedSpawnLocation(cpUnlockSign.getLocation(), true);
                player.sendMessage(
                    this.getChatprefix() + (Object) ChatColor.DARK_AQUA + "Spawnpunkt gesetzt!");
                return true;
            }
            player.sendMessage(
                this.getChatprefix() + (Object) ChatColor.GOLD + cpUnlockSign.getName() + (Object) ChatColor.RED + " hast du bereits freigeschaltet!");
        }
        return false;
    }

    public String getChatprefix() {
        return this.plugin.getCheckpointPrefix();
    }

}

