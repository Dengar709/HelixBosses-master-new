package Dengar.Helix.Bosses;

import Dengar.Helix.Bosses.nms.core.NmsMethodsInterface;
import Dengar.Helix.Bosses.nms.v1_16_R3.NmsMethods;
import org.bukkit.plugin.java.JavaPlugin;

public class PluginMain extends JavaPlugin {
    @Override
    public void onEnable() {
        this.nms = new NmsMethods();
        new Commands(this);
    }
    private NmsMethodsInterface nms;
    public NmsMethodsInterface getNms () {return nms;}
}
