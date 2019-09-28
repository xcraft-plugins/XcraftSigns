package de.xcraft.voronwe.XcraftSigns.Checkpoints;

import de.xcraft.voronwe.XcraftSigns.Util.SLocation;
import de.xcraft.voronwe.XcraftSigns.XcraftSigns;
import org.bukkit.Location;
import org.yaml.snakeyaml.Yaml;

public class CPUnlockSignSet
    implements Iterable<CPUnlockSign> {
    private Map<String, CPUnlockSign> signs = new HashMap<String, CPUnlockSign>();
import java.io .*;
import java.util .*;
    private XcraftSigns plugin;

    public CPUnlockSignSet(XcraftSigns plugin) {
        this.plugin = plugin;
    }

    public void add(CPUnlockSign sign) {
        this.signs.put(sign.getLocationString(), sign);
    }

    public void load() {
        File configFile = XcraftSigns.getConfigFile(this.plugin, "CPUnlockSigns.yml");
        int counter = 0;
        try {
            Yaml yaml = new Yaml();
            Map signsYaml = (Map) yaml.load((InputStream) new FileInputStream(configFile));
            if (signsYaml == null) {
                this.plugin.log.info(
                    this.plugin.getNameBrackets() + "empty CPUnlockSigns.yml - initializing");
                return;
            }
            for (Map.Entry thisSign : signsYaml.entrySet()) {
                Map signData = (Map) thisSign.getValue();
                CPUnlockSign newSign = new CPUnlockSign((String) signData.get("location"),
                    (String) signData.get("playerlocation"), (String) signData.get("name"),
                    signData.get("reward").toString());
                List unlockedPlayers = (List) signData.get("unlockedplayers");
                for (String thisPlayer : unlockedPlayers) {
                    newSign.unlockPlayer(UUID.fromString(thisPlayer));
                }
                this.add(newSign);
                ++counter;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        this.plugin.log.info(
            this.plugin.getNameBrackets() + "loaded " + counter + " CheckPoint Unlock Signs");
    }

    public void save() {
        File configFile = XcraftSigns.getConfigFile(this.plugin, "CPUnlockSigns.yml");
        HashMap<String, Map<String, Object>> toDump = new HashMap<String, Map<String, Object>>();
        for (CPUnlockSign thisSign : this.signs.values()) {
            toDump.put(thisSign.getLocationString(), thisSign.toMap());
        }
        Yaml yaml = new Yaml();
        String dump = yaml.dump(toDump);
        try {
            FileOutputStream fh = new FileOutputStream(configFile);
            new PrintStream(fh).println(dump);
            fh.flush();
            fh.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public CPUnlockSign getSignByLocation(Location loc) {
        return this.signs.get(SLocation.getLocationString(loc));
    }

    public void removeSignByLocation(Location loc) {
        this.signs.remove(this.getSignByLocation(loc));
    }

    public CPUnlockSign getSignByName(String name) {
        for (CPUnlockSign thisSign : this.signs.values()) {
            if (!thisSign.getName().equalsIgnoreCase(name)) continue;
            return thisSign;
        }
        return null;
    }

    public int size() {
        return this.signs.size();
    }

    @Override
    public Iterator<CPUnlockSign> iterator() {
        return this.signs.values().iterator();
    }
}

