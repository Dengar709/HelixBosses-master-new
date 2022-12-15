package Dengar.Helix.Bosses;

import org.bukkit.*;
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
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootTables;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.List;

import static org.bukkit.potion.PotionEffectType.*;

public class SpiderBossOld extends Boss implements Listener {
    private final PluginMain pl;
    private final Spider boss;
    private final Zombie marker;
    private final BukkitTask summonSpiders;
    private final BukkitTask summonSilver;
    //private final BukkitTask burrow;
    //private final BukkitTask web;
    private final BukkitTask stuckBoss;
    private final BukkitTask webburst;
    private final KeyedBossBar bar;


    public SpiderBossOld(PluginMain pl, Location l) {
        this.pl = pl;
        this.boss = l.getWorld().spawn(l, Spider.class, spider ->{
          spider.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(650);
          spider.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(3);
          spider.setHealth(650);
          spider.setCustomName("Unusually Large Spider");
          spider.setCustomNameVisible(false);
          spider.setRemoveWhenFarAway(false);
          spider.setLootTable(LootTables.EMPTY.getLootTable());
          spider.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.4);
        });
        this.marker = l.getWorld().spawn(l, Zombie.class, mark ->{
            mark.addPotionEffect(new PotionEffect(INVISIBILITY, 600000, 0, true, true, false));
            mark.addPotionEffect(new PotionEffect(DAMAGE_RESISTANCE, 600000, 5, true, true, false));
            mark.setAI(false);
            mark.setAdult();
            mark.isSilent();
            mark.setRemoveWhenFarAway(false);
            mark.setCanPickupItems(false);
            mark.setLootTable(LootTables.EMPTY.getLootTable());
        });
        summonSpiders = Bukkit.getScheduler().runTaskTimer(pl, () ->{
            Location lB = (boss).getEyeLocation();
            float D = (boss).getEyeLocation().getYaw();
            double Xv = 0.8* Math.sin(Math.toRadians(D));
            double Zv = 0.8* Math.cos(Math.toRadians(D));
            List<Entity> SpawnCheck = boss.getNearbyEntities(20, 20, 20);
            SpawnCheck.removeIf(e -> !(e instanceof CaveSpider));
            int SpawnCount = SpawnCheck.size();
            if (SpawnCount <6) {
            CaveSpider poison = boss.getWorld().spawn(lB, CaveSpider.class, Poison ->{
               Poison.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(6);
               Poison.setCustomName("Funnel-Web Spider");
               Poison.setCustomNameVisible(false);
               Poison.setHealth(6);
               Poison.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(2);
               Poison.getAttribute(Attribute.GENERIC_ATTACK_KNOCKBACK).setBaseValue(1);
               Poison.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.4);
               Poison.setVelocity(new Vector(-Xv, 0.5, Zv));
               Poison.setLootTable(LootTables.EMPTY.getLootTable());
            }); }
        }, 40, 200);

        summonSilver = Bukkit.getScheduler().runTaskTimer(pl, () ->{
            Location lB = (boss).getEyeLocation().clone().add(0,0.25,0);
            Endermite baby = boss.getWorld().spawn(lB, Endermite.class, Dies->{
                Dies.setCustomName("Malformed Spider");
                Dies.setCustomNameVisible(false);
                Dies.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(6);
                Dies.setHealth(6);
                Dies.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(3);
                Dies.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.75);
                Dies.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 200, 1, true, false));
                Dies.setLootTable(LootTables.EMPTY.getLootTable());
            });
        }, 60, 200);

        stuckBoss = Bukkit.getScheduler().runTaskTimer(pl, ()-> {
            Location lB = (boss).getLocation();

            List<Entity> noMove = marker.getNearbyEntities(1,1,1);
            if (noMove.contains(boss)) {
                boss.addPotionEffect(new PotionEffect(INVISIBILITY, 10, 1, true, false));
                boss.addPotionEffect(new PotionEffect(INCREASE_DAMAGE, 20, 0, true, false));
                List<Entity> nearby = boss.getNearbyEntities(6,5,6);
                nearby.removeIf(e->!(e instanceof Player));
                boss.teleport(nearby.get(0));
            }
            marker.teleport(boss);
        },0 ,60);

/*
        burrow = Bukkit.getScheduler().runTaskTimer(pl, () ->{
            List <Entity> burrowtargs = boss.getNearbyEntities(20,20,20);
            burrowtargs.removeIf(e -> !(e instanceof Player));
            int SpawnCount = burrowtargs.size();
            if (SpawnCount < 7) {
            for (Entity e : burrowtargs) {
                if (!(e instanceof Player) || e.getLocation().distanceSquared(boss.getLocation()) > 16) continue;
                ((Player) e).addPotionEffect(new PotionEffect(BLINDNESS, 60, 0, true, true, false));
            }

            Entity target = burrowtargs.get(burrowtargs.size() -1);
            boss.teleport(target);
            List <Entity> nearTarg = boss.getNearbyEntities(4,4,4);
            nearTarg.removeIf(e -> !(e instanceof Player));
            for (Entity e : nearTarg) {
                if (!(e instanceof Player) || e.getLocation().distanceSquared(boss.getLocation()) > 16) continue;
                ((Player) e).addPotionEffect(new PotionEffect(BLINDNESS, 60, 0, true, true, false));
                ((Player) e).addPotionEffect(new PotionEffect(SLOW, 60, 1, true, true, false));
                ((Player) e).addPotionEffect(new PotionEffect(WITHER, 60, 1, true, true, false));
                }
            }
        }, 200, 200);

 */
/*
        web = new BukkitRunnable() {
            int count = 0;

            @Override
            public void run() {
                while (count < 40) {
                    Location lB = (boss).getEyeLocation().clone().add(0, 0.2, 0);
                    double randX = (Math.random() - 0.5);
                    double randY = Math.random()/2;
                    double randZ = (Math.random() - 0.5);
                    count = count + 1;
                    boss.sendMessage("count");
                    Snowball venomball = boss.getWorld().spawn(lB, Snowball.class, venom -> {
                        venom.setShooter(boss);
                        venom.setGravity(true);
                        venom.setItem(new ItemStack(Material.GREEN_CONCRETE_POWDER));
                        venom.setSilent(true);
                        venom.setVelocity(new Vector(randX, randY, randZ));
                    });
                } count = 0;
            }
        }.runTaskTimer(pl, 0, 200);
 */

        webburst = new BukkitRunnable() {
            int count = 0;

            @Override
            public void run() {
                while (count < 40) {
                    Location lB = (boss).getEyeLocation().clone().add(0, 0.2, 0);
                    double randX = (Math.random() - 0.5);
                    double randY = (Math.random()-0.2)/2;
                    double randZ = (Math.random() - 0.5);
                    count = count + 1;
                    boss.sendMessage("count");
                    Snowball web = boss.getWorld().spawn(lB, Snowball.class, gumshot -> {
                        gumshot.setCustomName("webs");
                        gumshot.setShooter(boss);
                        gumshot.setGravity(true);
                        gumshot.setItem(new ItemStack(Material.COBWEB));
                        gumshot.setSilent(true);
                        gumshot.setVelocity(new Vector(randX, randY, randZ));
                    });
                } count = 0;
            }
        }.runTaskTimer(pl, 0, 200);

        bar = Bukkit.createBossBar(new NamespacedKey(pl, boss.getUniqueId().toString()), "Very Large Spider", BarColor.RED, BarStyle.SEGMENTED_10);
        bar.setProgress(1);

        Bukkit.getPluginManager().registerEvents(this, pl);
    }

    public void unregister() {
        HandlerList.unregisterAll(this);
        summonSilver.cancel();
        summonSpiders.cancel();
        //burrow.cancel();
        //web.cancel();
        stuckBoss.cancel();
        webburst.cancel();
        bar.removeAll();
        Bukkit.removeBossBar(new NamespacedKey(pl, boss.getUniqueId().toString()));
    }

    protected static double progress(LivingEntity boss, double damage) {
        if (boss == null) return 0D;
        double progress = ((((boss.getHealth() - damage)*(boss.getHealth() - damage))/Math.sqrt((boss.getHealth() - damage)))+330) / (boss.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()*boss.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()/Math.sqrt(boss.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()));
        if (progress < 0) return 0;
        if (progress > 1) return 1;
        return progress;
    }

    @EventHandler(priority= EventPriority.MONITOR)
    public void DamageTaken(EntityDamageEvent e) {
        if (e.getEntity().equals(boss)) {
            double progress = progress(boss, e.getFinalDamage());
            bar.setProgress(progress);
        }}


    private void showBar(Player p, Location l) {
        if (l == null) return;
        if (boss.getWorld().equals(l.getWorld()))
            bar.addPlayer(p);
        else
            bar.removePlayer(p);
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public final void eventShowBar(PlayerMoveEvent e) {
        showBar(e.getPlayer(), e.getTo());
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public final void eventShowBar(PlayerTeleportEvent e) {
        showBar(e.getPlayer(), e.getTo());
    }



    public void kill() {
        boss.setHealth(0);
        marker.setHealth(0);
    }
    public World getWorld() { return boss.getWorld();}


    @EventHandler
    public void onDeath(EntityDeathEvent e) {
        if (e.getEntity().equals(boss)) {Bukkit.getPluginManager().callEvent(new BossDefeatedEvent(this)); unregister();}}

    @EventHandler
    public void ProjectileHitEvent(ProjectileHitEvent e) {
        Entity damager = e.getEntity();
        Entity damaged = e.getHitEntity();

            if (damager instanceof Snowball && damaged instanceof Player) {
                ((Player) damaged).addPotionEffect(new PotionEffect(SLOW, 40, 2, true, false, true));
                ((Player) damaged).addPotionEffect(new PotionEffect(JUMP, 40, 250, true, false, true));
            }


    }

    @EventHandler
    public void AggroTrack(EntityTargetLivingEntityEvent e){
        if (e.getTarget().equals(boss)) e.setCancelled(true);
    }
}


