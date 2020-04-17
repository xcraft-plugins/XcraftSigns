package de.xcraft.voronwe.XcraftSigns;

import de.xcraft.voronwe.XcraftSigns.Checkpoints.CPEntrySign;
import de.xcraft.voronwe.XcraftSigns.Checkpoints.CPUnlockSign;
import de.xcraft.voronwe.XcraftSigns.Util.SLocation;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.material.Lever;

public class ListenerBlock implements Listener {
    private XcraftSigns plugin;

    public ListenerBlock(XcraftSigns signs) {
        this.plugin = signs;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.getPlayer().hasPermission("XcraftSigns.checkpoints.create")
                && !event.isCancelled()) {
            if (event.getBlock().getType().name().contains("SIGN")
                    && this.plugin.getCPUnlockSigns()
                                  .getSignByLocation(event.getBlock().getLocation()) != null) {
                this.plugin.getCPUnlockSigns().removeSignByLocation(event.getBlock().getLocation());
            }
            if (event.getBlock().getType().name().contains("SIGN")
                    && this.plugin.getCPEntrySigns()
                                  .getSignByLocation(event.getBlock().getLocation()) != null) {
                this.plugin.getCPEntrySigns().removeSignByLocation(event.getBlock().getLocation());
            }
            if (event.getBlock().getType() == Material.LEVER
                    && this.plugin.getCPEntrySigns()
                                  .getSignByLeverLocation(event.getBlock().getLocation()) != null) {
                CPEntrySign thisSign = this.plugin.getCPEntrySigns()
                                                  .getSignByLeverLocation(event.getBlock().getLocation());
                event.getBlock().getWorld().getBlockAt(thisSign.getLocation()).breakNaturally();
                this.plugin.getCPEntrySigns().remove(thisSign);
            }
        }
    }

    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        if (event.getPlayer().hasPermission("XcraftSigns.signs.color")) {
            Sign sign = (Sign) event.getBlock().getState();
            for (int i = 0; i < 4; ++i) {
                event.setLine(i, ChatColor.translateAlternateColorCodes('&', event.getLine(i)));
            }
            sign.update();
        }
        if (event.getPlayer().hasPermission("XcraftSigns.checkpoints.create")
                && event.getLine(0).length() > 10
                && event.getLine(0).equalsIgnoreCase("[cp_unlock]")) {
            CPUnlockSign newSign = new CPUnlockSign(
                SLocation.getLocationString(event.getBlock().getLocation()),
                SLocation.getLocationString(event.getPlayer().getLocation()), event.getLine(1),
                event.getLine(2), event.getLine(3));
            newSign.updateSign();
            event.setCancelled(true);
            this.plugin.getCPUnlockSigns().add(newSign);
        }
        if (event.getPlayer().hasPermission("XcraftSigns.checkpoints.create")
                && event.getLine(0).length() > 9
                && event.getLine(0).equalsIgnoreCase("[cp_entry]")) {
            String name = event.getLine(1);
            if (name == null || this.plugin.getCPUnlockSigns().getSignByName(name) == null) {
                event.getPlayer().sendMessage(ChatColor.RED + "no suitable checkpoint found.");
                event.getBlock().breakNaturally();
                event.setCancelled(true);
                return;
            }
            Block lever = null;
            Block attached = event.getBlock()
                                  .getRelative(((org.bukkit.material.Sign) event.getBlock()
                                                                                .getState()
                                                                                .getData()).getAttachedFace());
            for (int x = attached.getX() - 1; x <= attached.getX() + 1; ++x) {
                for (int y = attached.getY() - 1; y <= attached.getY() + 1; ++y) {
                    for (int z = attached.getZ() - 1; z <= attached.getZ() + 1; ++z) {
                        Block check = event.getBlock().getWorld().getBlockAt(x, y, z);
                        if (check.getType() != Material.LEVER || !check.getRelative(
                            ((Lever) check.getState().getData()).getAttachedFace())
                                                                       .equals((Object) attached)) {
                            continue;
                        }
                        lever = check;
                    }
                }
            }
            CPEntrySign newSign = new CPEntrySign(
                SLocation.getLocationString(event.getBlock().getLocation()),
                lever != null ? SLocation.getLocationString(lever.getLocation()) : null,
                event.getLine(1));
            String strDuration = event.getLine(2);
            try {
                int duration = Integer.parseInt(strDuration);
                newSign.setDuration(duration);
            } catch (Exception ex) {
                event.getPlayer()
                     .sendMessage(
                         (Object) ChatColor.RED + "invalid or no duration given - using default (2)");
            }
            String strReward = event.getLine(3);
            try {
                double reward = Double.parseDouble(strReward);
                newSign.setReward(reward);
            } catch (Exception ex) {
                event.getPlayer()
                     .sendMessage(
                         (Object) ChatColor.RED + "invalid or no reward given - using default (0)");
            }
            newSign.updateSign();
            this.plugin.getCPEntrySigns().add(newSign);
            event.setCancelled(true);
        }
    }
}
