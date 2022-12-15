package Dengar.Helix.Bosses;

import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;


public class Commands implements CommandExecutor, TabCompleter {
    private final PluginMain pl;

    public Commands(PluginMain pl) {
        PluginCommand cmd = Objects.requireNonNull(pl.getCommand("SummonBoss"));
        cmd.setExecutor(this);
        this.pl = pl;  //Initialises the private final section by defining itself as itself
    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) return(false);
        if (sender instanceof Entity) {
            Location l = ((Entity) sender).getLocation();
            switch (args[0]) {
                case"zombie": new ZombieBoss(pl, l); break;
                case"spider": new SpiderBoss(pl, l); break;
                case"dragon": new IceBoss(pl, l); break;
                case"circus": new RingMasterBoss(pl, l); break;
                case"zombienew": new ZombieBossOriginal(pl,l); break;
                case"spidernew": new SpiderBossOld(pl,l); break;
                case"piglinnew": new RingMasterBossOld2(pl,l); break;
                case"egypt": new EgyptBoss(pl,l); break;
                default: sender.sendMessage("Unknown Boss");
            }


        } else sender.sendMessage(ChatColor.RED + "Summon Failed due to Summoner being not LivingEntity");
        return true;

    }


    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) return Arrays.asList("zombie", "spider", "dragon", "circus", "zombienew", "spidernew", "piglinnew","egypt");
        return Collections.emptyList();
    }
}

