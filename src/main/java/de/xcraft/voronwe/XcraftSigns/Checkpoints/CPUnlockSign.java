package de.xcraft.voronwe.XcraftSigns.Checkpoints;

import de.xcraft.voronwe.XcraftSigns.Checkpoints.CPUnlockSignSet;
import de.xcraft.voronwe.XcraftSigns.Util.Cast;
import de.xcraft.voronwe.XcraftSigns.Util.SLocation;
import de.xcraft.voronwe.XcraftSigns.XcraftSigns;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

public class CPUnlockSign {
    private String location;
    private String playerLocation;
    private String name;
    private String depends;
    private Double reward;
    private List<String> unlockedPlayers = new ArrayList<String>();

    public CPUnlockSign(String location, String playerLocation, String name, String depends, String reward) {
        this.location = location;
        this.playerLocation = playerLocation;
        this.name = name;
        this.depends = depends.length() > 0 ? depends : null;
        this.reward = Cast.castDouble(reward);
    }

    public CPUnlockSign(String location, String playerLocation, String name, String reward) {
        this.location = location;
        this.playerLocation = playerLocation;
        this.name = name;
        this.depends = null;
        this.reward = Double.valueOf(reward);
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> ret = new HashMap<String, Object>();
        ret.put("name", this.name);
        ret.put("location", this.location);
        ret.put("depends", this.depends);
        ret.put("reward", this.reward);
        ret.put("playerlocation", this.playerLocation);
        ret.put("unlockedplayers", this.unlockedPlayers);
        return ret;
    }

    public boolean hasDependency() {
        return this.depends != null;
    }

    public CPUnlockSign getDependency() {
        if (!this.hasDependency()) {
            return null;
        }
        XcraftSigns plugin = (XcraftSigns)Bukkit.getServer().getPluginManager().getPlugin("XcraftSigns");
        return plugin.getCPUnlockSigns().getSignByName(this.depends);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setLocation(String loc) {
        this.location = loc;
    }

    public void setLocation(Location loc) {
        this.location = SLocation.getLocationString(loc);
    }

    public Location getLocation() {
        return SLocation.getLocationFromString(this.location);
    }

    public String getLocationString() {
        return this.location;
    }

    public void setPlayerLocation(String loc) {
        this.playerLocation = loc;
    }

    public void setPlayerLocation(Location loc) {
        this.playerLocation = SLocation.getLocationString(loc);
    }

    public Location getPlayerLocation() {
        return SLocation.getLocationFromString(this.playerLocation);
    }

    public boolean unlockPlayer(UUID uuid) {
        if (!this.isPlayerUnlocked(uuid)) {
            this.unlockedPlayers.add(uuid.toString());
            return true;
        }
        return false;
    }

    public void lockPlayer(Player player) {
        this.lockPlayer(player.getUniqueId().toString());
    }

    public void lockPlayer(String playerUID) {
        this.unlockedPlayers.remove(playerUID);
    }

    public boolean isPlayerUnlocked(UUID uuid) {
        return this.isPlayerUnlocked(uuid.toString());
    }

    public boolean isPlayerUnlocked(String playerUID) {
        return this.unlockedPlayers.contains(playerUID);
    }

    public void setReward(Double reward) {
        this.reward = reward;
    }

    public Double getReward() {
        return this.reward;
    }

    public void updateSign() {
        Location myLoc = SLocation.getLocationFromString(this.location);
        if (myLoc == null) {
            return;
        }
        Block block = myLoc.getWorld().getBlockAt(myLoc);
        if ((block.getType() == Material.WALL_SIGN || block.getType() == Material.SIGN) && block.getState() instanceof Sign) {
            Sign sign = (Sign)block.getState();
            sign.setLine(0, "[" + (Object)ChatColor.DARK_BLUE + "Checkpoint" + (Object)ChatColor.BLACK + "]");
            sign.setLine(1, (Object)ChatColor.AQUA + "Freischalten");
            sign.setLine(2, "" + this.getName());
            sign.setLine(3, "" + this.getReward());
            sign.update(true);
        }
    }
}

