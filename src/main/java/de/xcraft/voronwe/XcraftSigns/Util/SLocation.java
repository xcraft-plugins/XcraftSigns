package de.xcraft.voronwe.XcraftSigns.Util;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.text.DecimalFormat;

public class SLocation {
    public static String getLocationString(Location location) {
        if (location.getWorld() != null) {
            DecimalFormat df = new DecimalFormat("0.##");
            return location.getWorld().getName() + "," + df.format(location.getX())
                                                           .replace(",", ".") + "," + df.format(location.getY())
                                                                                        .replace(",", ".") + "," + df.format(location.getZ()).replace(",", ".");
        }
        return null;
    }

    public static Location getSaneLocation(Location loc) {
        double x = Math.floor(loc.getX()) + 0.5;
        double y = loc.getY();
        double z = Math.floor(loc.getZ()) + 0.5;
        return new Location(loc.getWorld(), x, y, z, loc.getYaw(), loc.getPitch());
    }

    public static Location getLocationFromString(String strLoc) {
        String[] strSplit = strLoc.split(",");
        return new Location(Bukkit.getServer().getWorld(strSplit[0]),
            Double.parseDouble(strSplit[1]), Double.parseDouble(strSplit[2]),
            Double.parseDouble(strSplit[3]));
    }
}

