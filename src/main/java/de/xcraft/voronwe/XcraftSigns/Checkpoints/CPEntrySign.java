package de.xcraft.voronwe.XcraftSigns.Checkpoints;

import de.xcraft.voronwe.XcraftSigns.Util.SLocation;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.material.Lever;

public class CPEntrySign {
    private String location;
    private String leverLocation;
    private int duration = 2;
    private double reward = 0.0;
    private String name;
    private boolean activated = false;

    public CPEntrySign(String location, String leverLocation, String name) {
        this.setLocation(location);
        this.setLeverLocation(leverLocation);
        this.setName(name);
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> ret = new HashMap<String, Object>();
        ret.put("name", this.name);
        ret.put("location", this.location);
        ret.put("leverlocation", this.leverLocation);
        ret.put("duration", this.duration);
        ret.put("reward", this.reward);
        return ret;
    }

    public double getReward() {
        return this.reward;
    }

    public void setReward(double reward) {
        this.reward = reward;
    }

    public int getDuration() {
        return this.duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public boolean hasLever() {
        return this.getLever() != null;
    }

    public Block getLever() {
        if (this.leverLocation == null) {
            return null;
        }
        Location leverLoc = SLocation.getLocationFromString(this.leverLocation);
        if (leverLoc != null && leverLoc.getWorld() != null) {
            return leverLoc.getWorld().getBlockAt(leverLoc);
        }
        return null;
    }

    public boolean activate() {
        if (this.getLever() != null && !this.activated) {
            ((Lever)this.getLever()).setPowered(true);
            this.activated = true;
            return true;
        }
        return false;
    }

    public void deactivate() {
        if (this.getLever() != null) {
            ((Lever)this.getLever()).setPowered(false);
            this.activated = false;
        }
    }

    public void setLeverLocation(String leverLocation) {
        this.leverLocation = leverLocation;
    }

    public boolean isActivated() {
        return this.activated;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Location getLocation() {
        return SLocation.getLocationFromString(this.location);
    }

    public String getLocationString() {
        return this.location;
    }

    public void setLocation(String location) {
        this.location = location;
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
            sign.setLine(1, (Object)ChatColor.AQUA + "Betreten");
            sign.setLine(2, "" + this.getName());
            sign.setLine(3, "" + this.getReward());
            sign.update(true);
        }
    }
}

