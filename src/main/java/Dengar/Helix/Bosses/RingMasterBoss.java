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
import org.bukkit.enchantments.Enchantment;
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
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.loot.LootTables;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.List;

import static java.lang.Math.*;
import static org.bukkit.potion.PotionEffectType.*;


public class RingMasterBoss extends Boss implements Listener {
    private final PluginMain pl;
    //private final Blaze flame3;
    private final PiglinBrute boss;
    //private Cat attcat;
    //private final BukkitTask SmallCats;
    //private final BukkitTask AngyHorse;
    private final BukkitTask Jump;
    private final KeyedBossBar bar;
    private double WeaponRoll = 0;
    private final BukkitTask RotateWeapon;
    private BukkitTask TridentPull;
    private int PullCount = 0;

    public RingMasterBoss(PluginMain pl, Location l) {
        this.pl = pl;
        ItemStack shield = new ItemStack(Material.GOLDEN_CHESTPLATE,1);
        ItemMeta ShieldEnch = shield.getItemMeta();
        if(ShieldEnch instanceof ItemMeta) {(ShieldEnch).addEnchant(Enchantment.PROTECTION_PROJECTILE, 7, true); shield.setItemMeta(ShieldEnch);}
        this.boss = l.getWorld().spawn(l, PiglinBrute.class, boss -> {
            boss.setCustomName("The Ring Master");
            boss.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(600);
            boss.setHealth(600);
            boss.setLootTable(LootTables.EMPTY.getLootTable());
            boss.setAdult();
            boss.setCanPickupItems(false);
            boss.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(0);
            boss.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.33);
            boss.getEquipment().setItemInMainHand(new ItemStack(Material.IRON_AXE, 1));
            boss.getEquipment().setItemInMainHandDropChance(-10);
            boss.getEquipment().setChestplate(new ItemStack(Material.GOLDEN_CHESTPLATE, 1));
            boss.getEquipment().setChestplateDropChance(-10);
            boss.getEquipment().setItemInOffHand(new ItemStack(Material.LEAD, 1));
            boss.getEquipment().setItemInOffHandDropChance(-10);
            boss.getAttribute(Attribute.GENERIC_ARMOR).setBaseValue(8);
            boss.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(1);
            boss.setRemoveWhenFarAway(false);
            boss.setImmuneToZombification(true);
            boss.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 10000, 1, true, false, false));
        });
        ItemStack fire = new ItemStack(Material.FLINT_AND_STEEL,1);
        ItemMeta FireEnch = fire.getItemMeta();
        if(FireEnch instanceof ItemMeta) {(FireEnch).addEnchant(Enchantment.FIRE_ASPECT, 5, true); fire.setItemMeta(FireEnch);}

        ItemStack fist = new ItemStack(Material.RED_CONCRETE,1);
        ItemMeta FistEnch = fist.getItemMeta();
        if(FistEnch instanceof ItemMeta) {(FistEnch).addEnchant(Enchantment.KNOCKBACK, 3, true); (FistEnch).addEnchant(Enchantment.DAMAGE_ALL, 2, true); fist.setItemMeta(FistEnch);}
/*
        SmallCats = Bukkit.getScheduler().runTaskTimer(pl, () ->{
            Location lB = (boss).getEyeLocation();
            List<Entity> SpawnCheck2 = boss.getNearbyEntities(20, 20, 20);
            SpawnCheck2.removeIf(e -> !(e instanceof Cat));
            int SpawnCount = SpawnCheck2.size();
            if (SpawnCount < 3) {
                this.attcat = boss.getWorld().spawn(lB, Cat.class, punchy -> {
                    punchy.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(4);
                    punchy.setHealth(4);
                    punchy.addScoreboardTag("ai_aggro");
                    punchy.addScoreboardTag("attack_damage_3");
                    punchy.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.3);
                    punchy.setLootTable(LootTables.EMPTY.getLootTable());
                    punchy.setAdult();
                }); }
        },2000000, 300);

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
        },2400000, 300); */

        Jump = Bukkit.getScheduler().runTaskTimer(pl, () -> {
            Location lB = (boss).getEyeLocation().clone().add(0, -0.5, 0);
            boss.getWorld().playSound(lB, Sound.BLOCK_ANCIENT_DEBRIS_BREAK, SoundCategory.HOSTILE, 1, 0.5f);
            float D = (boss).getEyeLocation().getYaw();
            double Xv = 1.2 * Math.sin(Math.toRadians(D));
            double Zv = 1.2 * Math.cos(Math.toRadians(D));
            double motion = max(0.4, random()*1.5);
            boss.setVelocity(new Vector(-Xv, 0.4, Zv));
            //attcat.setVelocity(new Vector(-Xv, 0.4, Zv));
        },70,200);


        RotateWeapon = Bukkit.getScheduler().runTaskTimer(pl, () -> {
            WeaponRoll = Math.random();
            if (WeaponRoll < 0.2) {
                boss.getEquipment().setItemInMainHand(new ItemStack(Material.IRON_AXE, 1));
                boss.addPotionEffect(new PotionEffect(WEAKNESS, 200, 0, true, false));
                Location lB = (boss).getEyeLocation().clone().add(0, -0.5, 0);
                boss.getWorld().playSound(lB, Sound.BLOCK_IRON_TRAPDOOR_CLOSE, SoundCategory.HOSTILE, 1, 0.2f);
                float D = (boss).getEyeLocation().getYaw();
                double Xv = 1.2 * Math.sin(Math.toRadians(D));
                double Zv = 1.2 * Math.cos(Math.toRadians(D));
                boss.setVelocity(new Vector(-Xv, 0.6, Zv));
                //attcat.setVelocity(new Vector(-Xv, 0.6, Zv));
            } else if (WeaponRoll < 0.4) {
                boss.getEquipment().setItemInMainHand(new ItemStack(Material.IRON_SWORD, 1));
                Location lB = (boss).getEyeLocation().clone().add(0, -0.5, 0);

                float D = (boss).getEyeLocation().getYaw();
                double Xv = 0.55 * Math.sin(Math.toRadians(D));
                double Zv = 0.55 * Math.cos(Math.toRadians(D));

                boss.getWorld().playSound(lB, Sound.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, SoundCategory.HOSTILE, 1, 1.3f);
                boss.getWorld().playSound(lB, Sound.ITEM_TRIDENT_RIPTIDE_1, SoundCategory.HOSTILE, 1, 1.25f);

                //Summoning Cloud
                AreaEffectCloud Cloud = boss.getWorld().spawn(lB, AreaEffectCloud.class, whirlwind -> {
                    whirlwind.setCustomName("Whirlwind");
                    whirlwind.setBasePotionData(new PotionData(PotionType.INSTANT_DAMAGE));
                    whirlwind.setRadius(3);
                    whirlwind.setDuration(30);
                    whirlwind.setDurationOnUse(0);
                    whirlwind.setParticle(Particle.SWEEP_ATTACK);
                    whirlwind.setRadiusPerTick(1f / 60); //f makes it a float so it doesn't literally round the result off
                    whirlwind.setReapplicationDelay(0);
                    whirlwind.setRadiusOnUse(0);
                    whirlwind.addCustomEffect(new PotionEffect(LEVITATION, 5, 12, true, true, false), true);
                    whirlwind.setVelocity(new Vector(Xv, 0, Zv));
                });
                //Summoning Snowball which cloud rides
                Snowball Snow = boss.getWorld().spawn(lB, Snowball.class, Carrier -> {
                    Carrier.setGravity(false);
                    Carrier.setItem(new ItemStack(Material.IRON_SWORD));
                    Carrier.setSilent(true);
                    Carrier.setVelocity(new Vector(-Xv, 0, Zv));
                    Carrier.addPassenger(Cloud);
                });
            } else if (WeaponRoll < 0.6) {
                boss.getEquipment().setItemInMainHand(new ItemStack(Material.TRIDENT, 1));
                boss.addPotionEffect(new PotionEffect(WEAKNESS, 200, 0, true, false));
                Location lB = (boss).getEyeLocation().clone().add(0, -0.5, 0);
                boss.getWorld().playSound(lB, Sound.ITEM_TRIDENT_RIPTIDE_1, SoundCategory.HOSTILE, 1, 1.5f);
                TridentPull = Bukkit.getScheduler().runTaskTimer(pl, () -> {
                    List<Entity> inRoom = boss.getNearbyEntities(10, 10, 10);
                    inRoom.removeIf(e -> !(e instanceof Player));
                    for (Entity e : inRoom) {
                        ((Player) e).addPotionEffect(new PotionEffect(SLOW, 160, 1, true, false, false));
                        ((Player) e).setVelocity(e.getLocation().toVector().add(boss.getLocation().toVector().subtract(e.getLocation().toVector())).normalize());
                    }
                    inRoom = boss.getNearbyEntities(0,0,0);
                    PullCount = PullCount + 1;
                    if (PullCount >= 4) {
                        PullCount = 0;
                        TridentPull.cancel();
                    }

                }, 20, 20);


            } else if (WeaponRoll < 0.8) {
                boss.getEquipment().setItemInMainHand(fist);
                boss.addPotionEffect(new PotionEffect(FAST_DIGGING, 150, 14, true, false));
                boss.addPotionEffect(new PotionEffect(SPEED, 150, 1, true, false));
                boss.addPotionEffect(new PotionEffect(DAMAGE_RESISTANCE, 150, 0, true, false));
                boss.addPotionEffect(new PotionEffect(INCREASE_DAMAGE, 150, 0, true, false));
                Location lB = (boss).getEyeLocation().clone().add(0, -0.5, 0);
                boss.getWorld().playSound(lB, Sound.BLOCK_WOOL_BREAK, SoundCategory.HOSTILE, 1, 1.25f);
                float D = (boss).getEyeLocation().getYaw();
                double Xv = 1.5 * Math.sin(Math.toRadians(D));
                double Zv = 1.5 * Math.cos(Math.toRadians(D));
                boss.setVelocity(new Vector(-Xv, 0, Zv));

            } else {
                boss.getEquipment().setItemInMainHand(fire);
                Location lB = (boss).getEyeLocation().clone().add(0, -0.5, 0);
                boss.getWorld().playSound(lB, Sound.ENTITY_BLAZE_SHOOT, SoundCategory.HOSTILE, 1, 0.75f);
                boss.addPotionEffect(new PotionEffect(SPEED, 150, 2, true, false));
                boss.addPotionEffect(new PotionEffect(FIRE_RESISTANCE, 150, 1, true, false));
            }
        }, 100, round(100 + 100 * ((boss.getHealth() / 800))));


        bar = Bukkit.createBossBar(new NamespacedKey(pl, boss.getUniqueId().toString()), "The Ring Master", BarColor.RED, BarStyle.SEGMENTED_10);
        bar.setProgress(1);
        Bukkit.getPluginManager().registerEvents(this, pl);
    }

    public void unregister() {
        HandlerList.unregisterAll(this);
        //SmallCats.cancel();
        //AngyHorse.cancel();
        TridentPull.cancel();
        RotateWeapon.cancel();
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


    @Override
    public World getWorld() {
        return boss.getWorld();
    }

    @EventHandler
    public void AggroTrack(EntityTargetLivingEntityEvent e){
        if (e.getTarget().equals(boss)) e.setCancelled(true);
    }

    @EventHandler
    public void onDeath(EntityDeathEvent e) {
        if (e.getEntity().equals(boss)) {Bukkit.getPluginManager().callEvent(new BossDefeatedEvent(this)); unregister();} }
}