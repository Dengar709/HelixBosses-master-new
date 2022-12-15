package Dengar.Helix.Bosses;

import org.bukkit.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.KeyedBossBar;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootTables;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.List;


public class RingMasterBossOld2 extends Boss implements Listener {
    private final PluginMain pl;
    private final Blaze flame;
    private final Blaze flame2;
    //private final Blaze flame3;
    private final PiglinBrute boss;
    private final BukkitTask SmallCats;
    private final BukkitTask AngyHorse;
    private final BukkitTask Jump;
    private final KeyedBossBar bar;

    public RingMasterBossOld2(PluginMain pl, Location l) {
        this.pl = pl;
        this.flame = l.getWorld().spawn(l, Blaze.class, blz -> {
            blz.setInvisible(true);
            blz.setInvulnerable(true);
            blz.setRemoveWhenFarAway(false);
        });
        this.flame2 = l.getWorld().spawn(l, Blaze.class, blz -> {
            blz.setInvisible(true);
            blz.setInvulnerable(true);
            blz.setRemoveWhenFarAway(false);
        });

        this.boss = l.getWorld().spawn(l, PiglinBrute.class, boss -> {
            boss.setCustomName("The Ring Master");
            boss.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(600);
            boss.setHealth(600);
            boss.setLootTable(LootTables.EMPTY.getLootTable());
            boss.setAdult();
            boss.setCanPickupItems(false);
            boss.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(9);
            boss.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.3);
            boss.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(0.7);
            boss.getEquipment().setItemInMainHand(new ItemStack(Material.LEAD, 1));
            boss.getEquipment().setItemInMainHandDropChance(-10);
            boss.getEquipment().setChestplate(new ItemStack(Material.GOLDEN_CHESTPLATE, 1));
            boss.getEquipment().setChestplateDropChance(-10);
            boss.getEquipment().setItemInOffHand(new ItemStack(Material.STICK, 1));
            boss.getEquipment().setItemInOffHandDropChance(-10);
            boss.addPassenger(flame);
            boss.addPassenger(flame2);
            boss.setRemoveWhenFarAway(false);
            boss.setImmuneToZombification(true);
            boss.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 10000, 1, true, false, false));
        });

        SmallCats = Bukkit.getScheduler().runTaskTimer(pl, () ->{
            Location lB = (boss).getEyeLocation();
            List<Entity> SpawnCheck2 = boss.getNearbyEntities(20, 20, 20);
            SpawnCheck2.removeIf(e -> !(e instanceof Cat));
            int SpawnCount = SpawnCheck2.size();
            if (SpawnCount < 3) {
                boss.getWorld().spawn(lB, Cat.class, punchy -> {
                    punchy.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(4);
                    punchy.setHealth(4);
                    punchy.addScoreboardTag("ai_aggro");
                    punchy.addScoreboardTag("attack_damage_3");
                    punchy.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.3);
                    punchy.setLootTable(LootTables.EMPTY.getLootTable());
                    punchy.setAdult();
                }); }
        },20, 200);

        AngyHorse = Bukkit.getScheduler().runTaskTimer(pl, () -> {
            Location lB = (boss).getEyeLocation();
            List<Entity> SpawnCheck = boss.getNearbyEntities(40, 20, 40);
            SpawnCheck.removeIf(e -> !(e instanceof Horse));
            int SpawnCount = SpawnCheck.size();
            if (SpawnCount < 3) {
                boss.getWorld().spawn(boss.getLocation(), Horse.class, punchy -> {
                    punchy.addScoreboardTag("ai_aggro");
                    punchy.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(8);
                    punchy.setHealth(8);
                    punchy.addScoreboardTag("attack_damage_5");
                    punchy.setBaby();
                    punchy.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.25);
                    punchy.setLootTable(LootTables.EMPTY.getLootTable());
                }); }
        },120, 200);

        Jump = Bukkit.getScheduler().runTaskTimer(pl, () -> {
            Location lB = (boss).getEyeLocation().clone().add(0, -0.5, 0);
            float D = (boss).getEyeLocation().getYaw();
            double Xv = 1.2 * Math.sin(Math.toRadians(D));
            double Zv = 1.2 * Math.cos(Math.toRadians(D));
            boss.setVelocity(new Vector(-Xv, 0.4, Zv));
        },70,200);


        bar = Bukkit.createBossBar(new NamespacedKey(pl, boss.getUniqueId().toString()), "The Ring Master", BarColor.RED, BarStyle.SEGMENTED_10);
        bar.setProgress(1);
        Bukkit.getPluginManager().registerEvents(this, pl);
    }

    public void unregister() {
        HandlerList.unregisterAll(this);
        SmallCats.cancel();
        AngyHorse.cancel();
        bar.removeAll();
        Bukkit.removeBossBar(new NamespacedKey(pl, boss.getUniqueId().toString()));
    }

    protected static double progress(LivingEntity boss, double damage) {
        if (boss == null) return 0D;
        double progress = ((((boss.getHealth() - damage) * (boss.getHealth() - damage)) / Math.sqrt((boss.getHealth() - damage))) + 330) / (boss.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() * boss.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() / Math.sqrt(boss.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()));
        if (progress < 0) return 0;
        if (progress > 1) return 1;
        return progress;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void DamageTaken(EntityDamageEvent e) {
        if (e.getEntity().equals(boss)) {
            double progress = progress(boss, e.getFinalDamage());
            bar.setProgress(progress);
        }
    }


    private void showBar(Player p, Location l) {
        if (l == null) return;
        if (boss.getWorld().equals(l.getWorld()))
            bar.addPlayer(p);
        else
            bar.removePlayer(p);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public final void eventShowBar(PlayerMoveEvent e) {
        showBar(e.getPlayer(), e.getTo());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public final void eventShowBar(PlayerTeleportEvent e) {
        showBar(e.getPlayer(), e.getTo());
    }

    @Override
    public void kill() {
        boss.setHealth(0);
    }

    public void killBlazes() {
        flame.setHealth(0);
        flame2.setHealth(0);
    }

    @Override
    public World getWorld() {
        return boss.getWorld();
    }

    @EventHandler
    public void AggroTrack(EntityTargetLivingEntityEvent e){
        if (e.getTarget().equals(boss)) e.setCancelled(true);
        if (e.getTarget().equals(flame)) e.setCancelled(true);
        if (e.getTarget().equals(flame2)) e.setCancelled(true);
    }

    @EventHandler
    public void onDeath(EntityDeathEvent e) {
        if (e.getEntity().equals(boss)) {killBlazes(); Bukkit.getPluginManager().callEvent(new BossDefeatedEvent(this)); unregister();} }
}