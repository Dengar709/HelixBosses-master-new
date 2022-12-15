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

public class EgyptBoss extends Boss implements Listener {
    private final PluginMain pl;
    private final Zombie marker;
    private final IronGolem boss;
    private final BukkitTask summonAdds;
    private final BukkitTask forcePlayerShift;
    private final BukkitTask phaseEffects;
    //private final BukkitTask burrow;
    //private final BukkitTask web;
    private final BukkitTask webburst;
    private final KeyedBossBar bar;



    public EgyptBoss(PluginMain pl, Location l) {
        this.pl = pl;
        this.marker = l.getWorld().spawn(l, Zombie.class, mark ->{
            mark.setInvisible(true);
            mark.setInvulnerable(true);
            mark.setAI(false);
            mark.setAdult();
            mark.isSilent();
            mark.setRemoveWhenFarAway(false);
            mark.setCanPickupItems(false);
            mark.setLootTable(LootTables.EMPTY.getLootTable());
        });

        this.boss = l.getWorld().spawn(l, IronGolem.class, diamondboss ->{
            diamondboss.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(650);
            diamondboss.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(4);
            diamondboss.setHealth(650);
            diamondboss.setCustomName("Ancient King Chicken-Cow");
            diamondboss.setCustomNameVisible(false);
            diamondboss.setRemoveWhenFarAway(false);
            diamondboss.setLootTable(LootTables.EMPTY.getLootTable());
            diamondboss.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.3);
            diamondboss.getEquipment().setItemInMainHand(new ItemStack(Material.WOODEN_AXE, 1));
            diamondboss.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(0.5);
            diamondboss.getAttribute(Attribute.GENERIC_ARMOR).setBaseValue(15);
            diamondboss.getAttribute(Attribute.GENERIC_ATTACK_KNOCKBACK).setBaseValue(2);

        });



        summonAdds = Bukkit.getScheduler().runTaskTimer(pl, () ->{
            Location lB = (boss).getEyeLocation();
            float D = (boss).getEyeLocation().getYaw();

            boss.getWorld().playSound(lB, Sound.ENTITY_SPIDER_DEATH, SoundCategory.HOSTILE, 1, 0.4f);
            int Repeating = 0;
            if (boss.getHealth() > 535) {
                List<Entity> SpawnCheck = boss.getNearbyEntities(20, 20, 20);
                SpawnCheck.removeIf(e -> !(e instanceof Endermite));
                int SpawnCount = SpawnCheck.size();
                if (SpawnCount < 20) {
                    while (Repeating < 7) {
                        double Xv = 0.8* Math.sin(Math.toRadians(D) + ((Math.random()-0.5)*22.5));
                        double Zv = 0.8* Math.cos(Math.toRadians(D) + ((Math.random()-0.5)*22.5));
                        Endermite poison = boss.getWorld().spawn(lB, Endermite.class, Spray -> {
                            Spray.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(3);
                            Spray.setCustomName("Ancient Scarab");
                            Spray.setCustomNameVisible(false);
                            Spray.setHealth(6);
                            Spray.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(5);
                            Spray.getAttribute(Attribute.GENERIC_ATTACK_KNOCKBACK).setBaseValue(1);
                            Spray.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.3);
                            Spray.setVelocity(new Vector(-Xv, 0.1, Zv));
                            Spray.setLootTable(LootTables.EMPTY.getLootTable());
                            Spray.setGliding(true);
                        });
                        Repeating = Repeating + 1;
                    }
                }
            }
            else {
                List<Entity> SpawnCheck = boss.getNearbyEntities(20, 20, 20);
                SpawnCheck.removeIf(e -> !(e instanceof Endermite));
                int SpawnCount = SpawnCheck.size();
                if (SpawnCount < 20) {
                    while (Repeating < 14) {
                        double Xv = 1* Math.sin(Math.toRadians(D) + ((Math.random()-0.5)*22.5));
                        double Zv = 1* Math.cos(Math.toRadians(D) + ((Math.random()-0.5)*22.5));
                        Endermite poison = boss.getWorld().spawn(lB, Endermite.class, Spray -> {
                            Spray.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(5);
                            Spray.setCustomName("Ancient Scarab");
                            Spray.setCustomNameVisible(false);
                            Spray.setHealth(6);
                            Spray.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(6);
                            Spray.getAttribute(Attribute.GENERIC_ATTACK_KNOCKBACK).setBaseValue(1);
                            Spray.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.35);
                            Spray.setVelocity(new Vector(-Xv, 0.1, Zv));
                            Spray.setLootTable(LootTables.EMPTY.getLootTable());
                            Spray.setGliding(true);
                        });
                        Repeating = Repeating + 1;
                    }
                }
            }
        }, 30, 120);

        forcePlayerShift = Bukkit.getScheduler().runTaskTimer(pl, () ->{
                List<Entity> inRoom = boss.getNearbyEntities(10, 10, 10);
                inRoom.removeIf(e -> !(e instanceof Player));
                for (Entity e : inRoom) {
                    ((Player) e).addPotionEffect(new PotionEffect(BLINDNESS, 10, 5, true, false, false));
                    ((Player) e).setVelocity(e.getLocation().toVector().add(boss.getEyeLocation().toVector().subtract(e.getLocation().toVector())).normalize().multiply(2.5));
                }
        }, 0, 800);


        phaseEffects = Bukkit.getScheduler().runTaskTimer(pl, ()-> {
            if (boss.getHealth() >535) {boss.addPotionEffect(new PotionEffect(SLOW_DIGGING, 50, 5, true, false )); boss.addPotionEffect(new PotionEffect(SLOW_FALLING, 50, 0, true, false));}
            else if (boss.getHealth() > 250) {boss.addPotionEffect(new PotionEffect(JUMP, 50, 7, true, false)); boss.addPotionEffect(new PotionEffect(SPEED, 50, 0, true, false));}
            else if (boss.getHealth() < 250) {
                boss.addPotionEffect(new PotionEffect(FAST_DIGGING, 50, 14, true, false));
                boss.addPotionEffect(new PotionEffect(SPEED, 50, 1, true, false));
                boss.addPotionEffect(new PotionEffect(INCREASE_DAMAGE, 50, 0, true, false));

            }
        },0, 20);

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
                Location lB = (boss).getEyeLocation().clone().add(0, 0.2, 0);
                boss.getWorld().playSound(lB, Sound.BLOCK_HONEY_BLOCK_FALL, SoundCategory.HOSTILE, 1, 1.2f);
                while (count < 40) {
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

        bar = Bukkit.createBossBar(new NamespacedKey(pl, boss.getUniqueId().toString()), "Unusually Large Spider", BarColor.RED, BarStyle.SEGMENTED_10);
        bar.setProgress(1);

        Bukkit.getPluginManager().registerEvents(this, pl);
    }

    public void unregister() {
        HandlerList.unregisterAll(this);
        forcePlayerShift.cancel();
        summonAdds.cancel();
        phaseEffects.cancel();
        //burrow.cancel();
        //web.cancel();
        webburst.cancel();
        bar.removeAll();
        Bukkit.removeBossBar(new NamespacedKey(pl, boss.getUniqueId().toString()));
    }

    protected static double progress(LivingEntity boss, double damage) {
        if (boss == null) return 0D;
        double progress = ((((boss.getHealth() - damage)*((boss.getHealth() - damage))/Math.sqrt((boss.getHealth() - damage))))+330) / (boss.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()*(boss.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()/Math.sqrt(boss.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue())));
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
        marker.setHealth(0);
        boss.setHealth(0);
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
            ((Player) damaged).addPotionEffect(new PotionEffect(WEAKNESS, 40, 0, true, false, true));
        }


    }

    @EventHandler
    public void AggroTrack(EntityTargetLivingEntityEvent e){
        if (e.getTarget().equals(boss)) e.setCancelled(true);
    }
}


