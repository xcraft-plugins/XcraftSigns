package de.xcraft.voronwe.XcraftSigns.Checkpoints;

import de.xcraft.voronwe.XcraftSigns.Checkpoints.CPEntrySign;
import de.xcraft.voronwe.XcraftSigns.Util.Cast;
import de.xcraft.voronwe.XcraftSigns.Util.SLocation;
import de.xcraft.voronwe.XcraftSigns.XcraftSigns;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.yaml.snakeyaml.Yaml;

public class CPEntrySignSet
implements Iterable<CPEntrySign> {
    private Map<String, CPEntrySign> signs = new HashMap<String, CPEntrySign>();
    private XcraftSigns plugin;

    public CPEntrySignSet(XcraftSigns plugin) {
        this.plugin = plugin;
    }

    public void add(CPEntrySign sign) {
        this.signs.put(sign.getLocationString(), sign);
    }

    public void load() {
        File configFile = XcraftSigns.getConfigFile(this.plugin, "CPEntrySigns.yml");
        int counter = 0;
        try {
            Yaml yaml = new Yaml();
            Map signsYaml = (Map)yaml.load((InputStream)new FileInputStream(configFile));
            if (signsYaml == null) {
                this.plugin.log.info(this.plugin.getNameBrackets() + "empty CPEntrySigns.yml - initializing");
                return;
            }
            for (Map.Entry thisSign : signsYaml.entrySet()) {
                Map signData = (Map)thisSign.getValue();
                CPEntrySign newSign = new CPEntrySign((String)signData.get("location"), (String)signData.get("leverlocation"), (String)signData.get("name"));
                newSign.setDuration(Cast.castInt(signData.get("duration")));
                newSign.setReward(Cast.castDouble(signData.get("reward")));
                this.add(newSign);
                ++counter;
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        this.plugin.log.info(this.plugin.getNameBrackets() + "loaded " + counter + " CheckPoint Entry Signs");
    }

    public void save() {
        File configFile = XcraftSigns.getConfigFile(this.plugin, "CPEntrySigns.yml");
        HashMap<String, Map<String, Object>> toDump = new HashMap<String, Map<String, Object>>();
        for (CPEntrySign thisSign : this.signs.values()) {
            toDump.put(thisSign.getLocationString(), thisSign.toMap());
        }
        Yaml yaml = new Yaml();
        String dump = yaml.dump(toDump);
        try {
            FileOutputStream fh = new FileOutputStream(configFile);
            new PrintStream(fh).println(dump);
            fh.flush();
            fh.close();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public CPEntrySign getSignByLocation(Location loc) {
        return this.signs.get(SLocation.getLocationString(loc));
    }

    public CPEntrySign getSignByLeverLocation(Location loc) {
        for (CPEntrySign thisSign : this.signs.values()) {
            if (!thisSign.hasLever() || !thisSign.getLever().getLocation().equals((Object)loc)) continue;
            return thisSign;
        }
        return null;
    }

    public void remove(CPEntrySign sign) {
        this.signs.remove(sign);
    }

    public void removeSignByLocation(Location loc) {
        this.signs.remove(this.getSignByLocation(loc));
    }

    public int size() {
        return this.signs.size();
    }

    @Override
    public Iterator<CPEntrySign> iterator() {
        return this.signs.values().iterator();
    }
}

