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
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootTables;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.List;

import static org.bukkit.potion.PotionEffectType.*;

public class ZombieBoss extends Boss implements Listener {
    private final PluginMain pl;
    private final Zombie undead;
    private final ZombieHorse horse;
    private final BukkitTask windslash;
    private final BukkitTask Roar;
    private final BukkitTask Cyclone;
    private BukkitTask CycloneTrack;
    private int Trace = 0;
    private final KeyedBossBar bar;

    public ZombieBoss(PluginMain pl, Location l) {
        this.pl = pl;
        this.undead = l.getWorld().spawn(l, Zombie.class, zombie -> {
            zombie.setAdult();
            zombie.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(1);
            zombie.setHealth(1);
            zombie.setAbsorptionAmount(399);
            zombie.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(4);
            zombie.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.2);
            zombie.getAttribute(Attribute.GENERIC_ATTACK_KNOCKBACK).setBaseValue(1);
            zombie.setCustomName(ChatColor.GOLD + "The Last King");
            zombie.setRemoveWhenFarAway(false);
            zombie.getAttribute(Attribute.ZOMBIE_SPAWN_REINFORCEMENTS).setBaseValue(0);
            zombie.setLootTable(LootTables.EMPTY.getLootTable());
            zombie.setCanPickupItems(false);
            zombie.getEquipment().setItemInMainHand(new ItemStack(Material.GOLDEN_SWORD, 1));
            zombie.getEquipment().setItemInMainHandDropChance(-10);
            zombie.getEquipment().setHelmet(new ItemStack(Material.GOLDEN_HELMET, 1));
            zombie.getEquipment().setHelmetDropChance(-10);
            zombie.getEquipment().setChestplate(new ItemStack(Material.LEATHER_CHESTPLATE, 1));
            zombie.getEquipment().setChestplateDropChance(-10);
            zombie.getEquipment().setLeggings(new ItemStack(Material.CHAINMAIL_LEGGINGS, 1));
            zombie.getEquipment().setLeggingsDropChance(-10);
            zombie.getEquipment().setBoots(new ItemStack(Material.CHAINMAIL_BOOTS, 1));
            zombie.getEquipment().setBootsDropChance(-10);
            zombie.addPotionEffect(new PotionEffect(WEAKNESS, 10000, 0 ,true, false));
        });
        this.horse = l.getWorld().spawn(l, ZombieHorse.class, horse -> {
            horse.setBaby();
            horse.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(1);
            horse.setHealth(1);
            horse.setAbsorptionAmount(99);
            horse.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.30);
            horse.setRemoveWhenFarAway(false);
            horse.addPassenger(undead);
            horse.setLootTable(LootTables.EMPTY.getLootTable());
            horse.isTamed();
        });

        windslash = Bukkit.getScheduler().runTaskTimer(pl, () -> {
            Location lB = (undead).getEyeLocation().clone().add(0, -0.5, 0);
            float D = (undead).getEyeLocation().getYaw();
            double Xv = 0.55 * Math.sin(Math.toRadians(D));
            double Zv = 0.55 * Math.cos(Math.toRadians(D));
            if (undead.getAbsorptionAmount()>280) {undead.addPotionEffect(new PotionEffect(SPEED, 800, 0, true));} else

            if (undead.getAbsorptionAmount() < 280) {
                undead.getWorld().playSound(lB, Sound.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, SoundCategory.HOSTILE, 1, 1.3f);
                undead.getWorld().playSound(lB, Sound.ITEM_TRIDENT_RIPTIDE_1, SoundCategory.HOSTILE, 1, 1.25f);

                //Summoning Cloud
                AreaEffectCloud Cloud = undead.getWorld().spawn(lB, AreaEffectCloud.class, whirlwind -> {
                    whirlwind.setCustomName("Whirlwind");
                    whirlwind.setBasePotionData(new PotionData(PotionType.WATER));
                    whirlwind.setRadius(2f);
                    whirlwind.setDuration(30);
                    whirlwind.setDurationOnUse(0);
                    whirlwind.setParticle(Particle.SWEEP_ATTACK);
                    whirlwind.setRadiusPerTick(1f / 80f); //f makes it a float so it doesn't literally round the result off
                    whirlwind.setReapplicationDelay(40);
                    whirlwind.setRadiusOnUse(0);
                    whirlwind.addCustomEffect(new PotionEffect(LEVITATION, 10, 20, true, true, false), true);
                    whirlwind.addCustomEffect(new PotionEffect(SLOW, 40, 3, true, true, false), true);
                    whirlwind.addCustomEffect(new PotionEffect(JUMP, 20, 250, true, false, true), true);
                    whirlwind.setVelocity(new Vector(Xv, 0, Zv));
                });
                //Summoning Snowball which cloud rides
                Snowball Snow = undead.getWorld().spawn(lB, Snowball.class, Carrier -> {
                    Carrier.setGravity(false);
                    Carrier.setItem(new ItemStack(Material.IRON_SWORD));
                    Carrier.setSilent(true);
                    Carrier.setVelocity(new Vector(-Xv, 0, Zv));
                    Carrier.addPassenger(Cloud);
                }); //Should implement a way to auto remove after 30 ticks (In an enclosed room will hit walls and remove self)
            }
        }, 160, 180);


        Roar = Bukkit.getScheduler().runTaskTimer(pl, () -> {
            Location lB = (undead).getLocation();
            Location lR = (undead).getLocation().clone().add(3*(Math.random()-0.5), 0.25, 3*(Math.random()-0.5));
            undead.getWorld().spawnParticle(Particle.SMOKE_NORMAL, lB, 40, 1.0, 2.0, 1.0, 0.75);
            undead.getWorld().playSound(lB, Sound.ENTITY_PILLAGER_CELEBRATE, SoundCategory.HOSTILE, 1, 1.3f);
            if (undead.getAbsorptionAmount() > 280) {
                List<Entity> RoarTargets = undead.getNearbyEntities(3, 3, 3);
                for (Entity e : RoarTargets) {
                    if (!(e instanceof Player) || e.getLocation().distanceSquared(undead.getLocation()) > 9) continue;
                    ((Player) e).addPotionEffect(new PotionEffect(WITHER, 60, 0, true, true, false));
                    ((Player) e).addPotionEffect(new PotionEffect(SLOW, 60, 2, true, true, false));
                }
            }
            else {
            List<Entity> SpawnCheck = undead.getNearbyEntities(20, 20, 20);
            SpawnCheck.removeIf(e -> !(e instanceof Skeleton));
            int SpawnCount = SpawnCheck.size();
            if (SpawnCount <6)
                {
                Skeleton servant = undead.getWorld().spawn(lR, Skeleton.class, thrall -> {
                    thrall.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(6);
                    thrall.setHealth(6);
                    thrall.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(4);
                    thrall.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.3);
                    thrall.setCustomName("Servant of the Last King");
                    thrall.setLootTable(LootTables.EMPTY.getLootTable());
                    thrall.getEquipment().setItemInMainHandDropChance(-10);
                    thrall.getEquipment().setItemInOffHandDropChance(-10);

                });
                Location lT = (servant).getLocation();
                servant.getWorld().spawnParticle(Particle.SMOKE_NORMAL, lT, 40, 0.25, 0.25, 0.25, 0.01);
                }
            }
        }, 40, 180);

        Cyclone = Bukkit.getScheduler().runTaskTimer(pl, () ->{
            Location lB = (undead).getLocation().clone().add(0,0.5,0);
            undead.addPotionEffect(new PotionEffect(SPEED, 20, 0, true, false));
            undead.getWorld().playSound(lB, Sound.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, SoundCategory.HOSTILE, 1, 1.3f);
            undead.getWorld().playSound(lB, Sound.ITEM_TRIDENT_RIPTIDE_1, SoundCategory.HOSTILE, 1, 1.25f);
            AreaEffectCloud Spin = undead.getWorld().spawn(lB, AreaEffectCloud.class, spinny ->{
                spinny.setCustomName("Cyclone");
                spinny.setBasePotionData(new PotionData(PotionType.WATER));
                spinny.addCustomEffect(new PotionEffect(LEVITATION, 10, 20, true, true, false), true);
                spinny.addCustomEffect(new PotionEffect(SLOW, 40, 3, true, true, false), true);
                spinny.addCustomEffect(new PotionEffect(JUMP, 20, 250, true, false, true), true);
                spinny.setRadius(2.5f);
                spinny.setDuration(20);
                spinny.setDurationOnUse(0);
                spinny.setReapplicationDelay(10);
                spinny.setParticle(Particle.SWEEP_ATTACK);
                spinny.setRadiusPerTick(1/40);
            });
            Trace = 0;
            CycloneTrack = Bukkit.getScheduler().runTaskTimer(pl, () ->{
                if (Trace >5) {CycloneTrack.cancel();}
                Trace = Trace +1;
                Spin.teleport(undead);
            }, 0, 2);
        }, 100, 180);


        bar = Bukkit.createBossBar(new NamespacedKey(pl, undead.getUniqueId().toString()), "The Last King", BarColor.RED, BarStyle.SEGMENTED_10);
        bar.setProgress(1);

        Bukkit.getPluginManager().registerEvents(this, pl);
    }


    @EventHandler
    public void onEffect(EntityPotionEffectEvent e) {
        if (e.getEntity().equals(undead)) e.setCancelled(true);
        if (e.getEntity().equals(horse)) e.setCancelled(true);
    }


    public void unregister() {
        HandlerList.unregisterAll(this);
        windslash.cancel();
        Roar.cancel();
        Cyclone.cancel();
        CycloneTrack.cancel();
        bar.removeAll();
        Bukkit.removeBossBar(new NamespacedKey(pl, undead.getUniqueId().toString()));
    }

    protected static double progress(LivingEntity undead, double damage) {
        if (undead == null) return 0D;
        double progress = ((undead.getAbsorptionAmount() - damage) * (undead.getAbsorptionAmount() - damage) + 1600)/ 160001;
        if (progress < 0) return 0;
        if (progress > 1) return 1;
        return progress;
    }

    @EventHandler(priority= EventPriority.MONITOR)
    public void DamageTaken(EntityDamageEvent e) {
        if (e.getEntity().equals(undead)) {
            double progress = progress(undead, e.getFinalDamage());
            bar.setProgress(progress);
        }}


    private void showBar(Player p, Location l) {
        if (l == null) return;
        if (undead.getWorld().equals(l.getWorld()))
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
        undead.setHealth(0);
    }

    public World getWorld() { return undead.getWorld();}


    //private final Player player = Bukkit.getPlayer("Dengar708");

    @EventHandler
    public void onDeath(EntityDeathEvent e) {

        if (e.getEntity().equals(undead)) {
            //player.sendMessage("boss dies");
            Bukkit.getPluginManager().callEvent(new BossDefeatedEvent(this));
            unregister();

        }
/*
        if (e.getEntity().equals(horse)) {
            player.sendMessage("horse dies");
            Bukkit.getScheduler().scheduleSyncDelayedTask(pl, () ->{
                player.sendMessage("Delayed Task");
                if (horse.getNearbyEntities(100,100,100).contains(undead)) {player.sendMessage("Alive");} else {
                    player.sendMessage("Boss is dead");
                    Bukkit.getPluginManager().callEvent(new BossDefeatedEvent(this));
                    unregister();
                }
            },0);
        } */
    }
}



