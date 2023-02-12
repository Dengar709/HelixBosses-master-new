package Dengar.Helix.Bosses;


import org.bukkit.*;
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
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.loot.LootTables;
import org.bukkit.potion.*;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import org.yaml.snakeyaml.scanner.Constant;

import java.awt.event.FocusEvent;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Boolean.FALSE;
import static java.lang.Math.max;
import static java.lang.Math.round;
import static org.bukkit.GameMode.CREATIVE;
import static org.bukkit.GameMode.SPECTATOR;
import static org.bukkit.potion.PotionEffectType.*;

public class IceBoss extends Boss implements Listener {
    private final PluginMain pl;
    private final EnderDragon boss;
    private final Zombie marker;
    private final BukkitTask ConstantIce;
    private final BukkitTask ResetAI;
    private final BukkitTask AdjustPos;
    private final BukkitTask SpiralBlast;
    private BukkitTask SpiralBink;
    //private final BukkitTask GrowingStorm;
    private final BukkitTask FreezeFeet;
    private BukkitTask RepeatAttack;
    //private final BukkitTask Apocalypse;
    private final BukkitTask Fireball;
    private final BukkitTask Summon;
    private BukkitTask SpawnMobs;
    private int D = 0;
    private int Counter = 0;
    private int rotation = 1;
    private int track = 0;
    private int spawnloop = 0;
    private final KeyedBossBar bar;


    public IceBoss(PluginMain pl, Location l) {
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
        ItemStack prot = new ItemStack(Material.LEATHER_CHESTPLATE,1);
            ItemMeta protEnch = prot.getItemMeta();
            if(protEnch instanceof ItemMeta) {(protEnch).addEnchant(Enchantment.PROTECTION_PROJECTILE, 2, true); prot.setItemMeta(protEnch);}
        this.boss = l.getWorld().spawn(l, EnderDragon.class, enderDragon -> {
            enderDragon.setPhase(EnderDragon.Phase.FLY_TO_PORTAL);
            enderDragon.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(600);
            enderDragon.setHealth(600);
            enderDragon.setRemoveWhenFarAway(false);
            enderDragon.isSilent();
            enderDragon.setCustomName("Bitra the Trapped");
            enderDragon.getEquipment().setChestplate(prot);
        });
        ResetAI = Bukkit.getScheduler().runTaskTimer(pl, () ->{
            boss.setPhase(EnderDragon.Phase.FLY_TO_PORTAL);
        },0,10);
        AdjustPos = Bukkit.getScheduler().runTaskTimer(pl, () ->{
            boss.teleport(marker);
        }, 0,1);

    /*    ConeBlast = Bukkit.getScheduler().runTaskTimer(pl, () -> {
            float D = (boss).getEyeLocation().getYaw();
            double Xv = 1.5* Math.sin(Math.toRadians(D));
            double Zv = 1.5* Math.cos(Math.toRadians(D));
            Location lB = (boss).getEyeLocation().clone().subtract(5*Xv, 4, -5*Zv);
            AreaEffectCloud Breath = boss.getWorld().spawn(lB, AreaEffectCloud.class, IceBreath -> {
                IceBreath.setCustomName("Ice Breath");
                IceBreath.setBasePotionData(new PotionData(PotionType.INSTANT_DAMAGE));
                IceBreath.setRadius(3);
                IceBreath.setDuration(40);
                IceBreath.setDurationOnUse(0);
                IceBreath.setParticle(Particle.CLOUD);
                IceBreath.setRadiusPerTick(5f / 40); //f makes it a float so it doesn't literally round the result off
                IceBreath.setReapplicationDelay(5);
                IceBreath.setRadiusOnUse(0);
                IceBreath.addCustomEffect(new PotionEffect(SLOW, 100, 2, true, true, false), true);
            });
            Snowball iceball = boss.getWorld().spawn(lB, Snowball.class, Tracker ->{
                Tracker.setVelocity(new Vector(-Xv, 0, Zv));
                Tracker.addPassenger(Breath);
            });
        },100, 100);    */
        /*
        GrowingStorm = Bukkit.getScheduler().runTaskTimer(pl, ()->{
            List<Entity> droptargs = boss.getNearbyEntities(30,20,30);
            droptargs.removeIf(e -> !(e instanceof Player));
            for (Entity e : droptargs) {
                Location lE = e.getLocation();
                AreaEffectCloud Breath = e.getWorld().spawn(lE, AreaEffectCloud.class, IceBreath -> {
                    IceBreath.setCustomName("Ice Breath");
                    IceBreath.setBasePotionData(new PotionData(PotionType.INSTANT_DAMAGE));
                    IceBreath.setRadius(1.5f);
                    IceBreath.setDuration(60);
                    IceBreath.setDurationOnUse(0);
                    IceBreath.setParticle(Particle.CLOUD);
                    IceBreath.setRadiusPerTick(1f / 80); //f makes it a float so it doesn't literally round the result off
                    IceBreath.setReapplicationDelay(20);
                    IceBreath.setRadiusOnUse(0);
                    IceBreath.addCustomEffect(new PotionEffect(SLOW, 100, 2, true, true, false), true); });
            }
        },200,1200); */

        ConstantIce = Bukkit.getScheduler().runTaskTimer(pl, ()-> {
            List<Entity> playertarg = boss.getNearbyEntities(15,20,15);
            playertarg.removeIf(e-> !(e instanceof Player));
            for (Entity e : playertarg) {
                if (!(e instanceof Player)) continue;
                Player p = ((Player) e);
                if (p.getGameMode() == SPECTATOR || p.getGameMode() == CREATIVE) continue;
                if (p.getFireTicks() != 0) continue;
                int currfreeze = max(120,e.getFreezeTicks() + 3);
                e.setFreezeTicks(currfreeze);
                if (e.getFreezeTicks() > 60) ((Player) e).addPotionEffect(new PotionEffect(SLOW, 50, 2, true, false));
                if (e.getFreezeTicks() > 100) ((Player) e).addPotionEffect(new PotionEffect(HUNGER, 20, 20, true, false));
            }
        }, 0, 1);



        FreezeFeet = Bukkit.getScheduler().runTaskTimer(pl, ()->{
            RepeatAttack = Bukkit.getScheduler().runTaskTimer(pl, ()-> {
                List<Entity> droptargs = boss.getNearbyEntities(15,20,15);
                droptargs.removeIf(e -> !(e instanceof Player));
                for (Entity e : droptargs) {
                    if (!(e instanceof Player)) continue;
                    Player p = ((Player)e);
                    if (p.getGameMode() == SPECTATOR || p.getGameMode() == CREATIVE) continue;
                    Location lE = e.getLocation();
                    e.getWorld().playSound(lE, Sound.ENTITY_PLAYER_HURT_FREEZE, SoundCategory.HOSTILE, 1, 0.75f);
                    AreaEffectCloud Breath = e.getWorld().spawn(lE, AreaEffectCloud.class, IceBreath -> {
                        IceBreath.setCustomName("Ice Breath");
                        IceBreath.setBasePotionData(new PotionData(PotionType.INSTANT_DAMAGE));
                        IceBreath.setRadius(1f);
                        IceBreath.setDuration(150);
                        IceBreath.setDurationOnUse(0);
                        IceBreath.setParticle(Particle.CLOUD);
                        IceBreath.setRadiusPerTick(0.002f); //f makes it a float so it doesn't literally round the result off
                        IceBreath.setReapplicationDelay(60);
                        IceBreath.setRadiusOnUse(0);
                        IceBreath.addCustomEffect(new PotionEffect(SLOW, 100, 2, true, true, false), true);
                        IceBreath.addCustomEffect(new PotionEffect(HARM, 1, 0, true, true, false), true);
                    });

                }
                Counter = Counter + 1;
                if (Counter > 3) {
                    Counter = 0;
                    RepeatAttack.cancel();
                }
            }, 0, 20);

        },200,1200);





        SpiralBlast = Bukkit.getScheduler().runTaskTimer(pl, () -> {
            boss.getWorld().playSound(boss.getLocation(), Sound.ENTITY_ENDER_DRAGON_SHOOT, 1, 0.5f);
            SpiralBink = Bukkit.getScheduler().runTaskTimer(pl, () -> {
                double Xv = Math.sin(Math.toRadians(D));
                double Zv = Math.cos(Math.toRadians(D));
                Location lP = (boss).getEyeLocation().clone().add((-6 * Xv), 0, (6 * Zv)).subtract(0, 8.5f, 0);
                Location lN = (boss).getEyeLocation().clone().subtract((-6 * Xv), 8.5f, (6 * Zv));
                Snowball iceshot = boss.getWorld().spawn(lP, Snowball.class, iceball -> {
                    iceball.setCustomName("freeze");
                    iceball.setShooter(boss);
                    iceball.setGravity(false);
                    iceball.setItem(new ItemStack(Material.FIRE_CHARGE));
                    iceball.setSilent(true);
                    iceball.setVelocity(new Vector(-0.5*Xv, 0, 0.5*Zv));
                });
                Snowball iceshot2 = boss.getWorld().spawn(lN, Snowball.class, iceball -> {
                    iceball.setCustomName("freeze");
                    iceball.setShooter(boss);
                    iceball.setGravity(false);
                    iceball.setItem(new ItemStack(Material.FIRE_CHARGE));
                    iceball.setSilent(true);
                    iceball.setVelocity(new Vector(0.5*Xv, 0, -0.5*Zv));
                });
                D = D + (rotation * 11);
                //Bukkit.getPlayer("Dengar708").sendMessage("count: " + D);
                if (D * D > 518400) {D = 0; rotation = rotation*(-1); SpiralBink.cancel();};
            }, 0, 2);
        },0,400);

/*
        Apocalypse = Bukkit.getScheduler().runTaskTimer(pl, () ->{
            Location lC = boss.getLocation().clone().subtract(7, 8.5f, 0);
            AreaEffectCloud Safety = lC.getWorld().spawn(lC, AreaEffectCloud.class, mark ->{
               mark.setBasePotionData(new PotionData(PotionType.AWKWARD));
               mark.setRadius(4f);
               mark.setDuration(200);
               mark.setDurationOnUse(0);
               mark.setParticle(Particle.SOUL_FIRE_FLAME);
               mark.setRadiusPerTick(0f);
               mark.setReapplicationDelay(10);
               mark.setRadiusOnUse(0);
               mark.addCustomEffect(new PotionEffect(DAMAGE_RESISTANCE, 20, 5, true, false, false ), false);
            });
            Bukkit.getScheduler().scheduleSyncDelayedTask(pl,() ->{
                List<Entity> inRoom = boss.getNearbyEntities(32,20,32);
                List<Entity> atBeacon = Safety.getNearbyEntities(8,4,8);
                inRoom.removeIf(e -> !(e instanceof Player));
                inRoom.removeAll(atBeacon);
                for (Entity e : inRoom) {
                    ((Player) e).addPotionEffect(new PotionEffect(SLOW, 160, 3, true, false, false));
                    ((Player) e).addPotionEffect(new PotionEffect(HARM, 2, 0, true, false, false));
                    ((Player) e).addPotionEffect(new PotionEffect(WEAKNESS, 160, 0, true, false, false));
                    ((Player) e).addPotionEffect(new PotionEffect(WITHER, 160, 0 , true , false, false));
                }

            },140);
        },1000, 1200); */

        Fireball = Bukkit.getScheduler().runTaskTimer(pl,() ->{
            boss.getWorld().playSound(boss.getLocation(), Sound.ENTITY_PARROT_IMITATE_ENDER_DRAGON, 1, 2f);
            Location fbn = boss.getEyeLocation().clone().add(12,-7,0);
            Location fbe = boss.getEyeLocation().clone().add(0,-7,12);
            Location fbs = boss.getEyeLocation().clone().add(-12,-7,0);
            Location fbw = boss.getEyeLocation().clone().add(0,-7,-12);
            Vex FBN = fbn.getWorld().spawn(fbn, Vex.class, fall -> {
                fall.setVisualFire(true);
                fall.setAI(false);
                fall.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(10);
                fall.setHealth(10);

                /*fall.setBeamTarget(marker.getLocation());
                fall.setShowingBottom(false); */
            });
            Vex FBE = fbe.getWorld().spawn(fbe, Vex.class, fall -> {
                fall.setVisualFire(true);
                fall.setAI(false);
                fall.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(10);
                fall.setHealth(10);
            });
            Vex FBS = fbs.getWorld().spawn(fbs, Vex.class, fall -> {
                fall.setVisualFire(true);
                fall.setAI(false);
                fall.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(10);
                fall.setHealth(10);
            });
            Vex FBW = fbw.getWorld().spawn(fbw, Vex.class, fall -> {
                fall.setVisualFire(true);
                fall.setAI(false);
                fall.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(10);
                fall.setHealth(10);
            });
            Bukkit.getScheduler().scheduleSyncDelayedTask(pl, () -> {
                boss.getWorld().playSound(boss.getLocation(), Sound.ENTITY_PARROT_IMITATE_ENDER_DRAGON, 1, 0.5f);
                List<Entity> inRoom = boss.getNearbyEntities(10,20,10);
                inRoom.removeIf(e -> !(e instanceof Player));
                List<Entity> unkilled = boss.getNearbyEntities(20,20,20);
                unkilled.removeIf(e -> !(e instanceof Vex));
                int tracked = unkilled.size();
                for (Entity e : inRoom) {
                    ((Player) e).addPotionEffect(new PotionEffect(HARM, 2, round(tracked/2), true, false, false));
                    ((Player) e).addPotionEffect(new PotionEffect(WITHER, (tracked*20 + 40), 0, true, false, false));
                    ((Player) e).addPotionEffect(new PotionEffect(SLOW, (tracked*20 + 40), 4, true, false, false));
                    e.setFreezeTicks(400);
                }
                boss.setHealth(boss.getHealth() - ((4-tracked)*20));
                Location fbn2 = boss.getEyeLocation().clone().add(12,-7,0);
                Location fbe2 = boss.getEyeLocation().clone().add(0,-7,12);
                Location fbs2 = boss.getEyeLocation().clone().add(-12,-7,0);
                Location fbw2 = boss.getEyeLocation().clone().add(0,-7,-12);
                /* List<Entity> livecrystals = boss.getNearbyEntities(15,20,15 );
                for(Entity e : livecrystals) {
                    if (!(e instanceof EnderCrystal)) continue;
                    Location lE = e.getLocation();
                    TNTPrimed SN = fbn2.getWorld().spawn(fbn2, TNTPrimed.class, fall -> {
                        fall.setFuseTicks(1);
                        fall.setGravity(false);
                    });
                } */

/*
                List<Entity> endtargs = boss.getNearbyEntities(15,20,15);
                endtargs.removeIf(e -> !(e instanceof EnderCrystal));
                for (Entity e : endtargs) {
                    Location lE = e.getLocation();
                    e.getWorld().playSound(lE, Sound.ENTITY_PLAYER_HURT_FREEZE, SoundCategory.HOSTILE, 1, 0.75f);
                    AreaEffectCloud Breath = e.getWorld().spawn(lE, AreaEffectCloud.class, IceBreath -> {
                        IceBreath.setCustomName("Ice Breath");
                        IceBreath.setBasePotionData(new PotionData(PotionType.INSTANT_DAMAGE));
                        IceBreath.setRadius(3f);
                        IceBreath.setDuration(400);
                        IceBreath.setDurationOnUse(0);
                        IceBreath.setParticle(Particle.CLOUD);
                        IceBreath.setRadiusPerTick(0.01f); //f makes it a float so it doesn't literally round the result off
                        IceBreath.setReapplicationDelay(60);
                        IceBreath.setRadiusOnUse(0);
                        IceBreath.addCustomEffect(new PotionEffect(SLOW, 100, 2, true, true, false), true);
                        IceBreath.addCustomEffect(new PotionEffect(HARM, 1, 0, true, true, false), true);
                    });
                } */

                FBN.remove();
                FBE.remove();
                FBS.remove();
                FBW.remove();

            },180);
        },1000,1200);
/*
        FallingSnow = Bukkit.getScheduler().runTaskTimer(pl, ()-> {
            ItemStack droppable = new ItemStack(Material.LINGERING_POTION, 1);
            ItemMeta im = droppable.getItemMeta();
            if (im instanceof PotionMeta) {
                ((PotionMeta)im).setBasePotionData(new PotionData(PotionType.INSTANT_DAMAGE, false, false));
                ((PotionMeta)im).addCustomEffect(new PotionEffect(WITHER    ,120, 0, true ,false, false), true);
                droppable.setItemMeta(im); }
            RepeatingSnow = Bukkit.getScheduler().runTaskTimer(pl, ()-> {
                double Xv = Math.sin(Math.toRadians(D));
                double Zv = Math.cos(Math.toRadians(D));
                Location lP = (boss).getEyeLocation().clone();



                T = T + 1;
                if (T>8) {T = 0; RepeatingSnow.cancel;}
            },0,1);


        },1000,1200); */
 /*
        Apocalypse = Bukkit.getScheduler().runTaskTimer(pl, () -> {
            double circle = Math.random() *360;
            Location bL = boss.getLocation().clone().subtract(6*Math.cos(Math.toRadians(circle)),4,6*Math.sin(Math.toRadians(circle)));
            AreaEffectCloud HealPool = boss.getWorld().spawn(bL, AreaEffectCloud.class, heal -> {
                heal.setBasePotionData(new PotionData(PotionType.AWKWARD));
                heal.setRadius(1f);
                heal.setDuration(200);
                heal.setDurationOnUse(15);
                heal.setParticle(Particle.HEART);
                heal.setRadiusPerTick(0);
                heal.setReapplicationDelay(5);
                heal.setRadiusOnUse(0);
                heal.addCustomEffect(new PotionEffect(SLOW, 100, 2, true, true, false), true); });
            Heal = Bukkit.getScheduler().runTaskTimer(pl, () ->{
                if (HealPool.isDead()) {Heal.cancel();}
                boss.addPotionEffect(new PotionEffect(HEAL, 10, 4, true, true, false));
            },0,50);
        },1000, 1200);
*/
       /* Summon = new BukkitRunnable() {
            public void run() {
                while (track <4) {
                    List<Entity> SpawnCheck = boss.getNearbyEntities(60, 40, 60);
                    SpawnCheck.removeIf(e -> !(e instanceof Guardian));
                    int SpawnCount = SpawnCheck.size();
                    if (SpawnCount < 10) {
                        ItemStack power = new ItemStack(Material.BOW,1);
                            ItemMeta powerEnch = power.getItemMeta();
                            if(powerEnch instanceof ItemMeta) {(powerEnch).addEnchant(Enchantment.ARROW_DAMAGE, 3, true); power.setItemMeta(powerEnch);}
                        double RandX = 10* Math.sin(Math.toRadians(track*90));
                        double RandZ = 10* Math.cos(Math.toRadians(track*90));
                        Location lBS = (boss).getEyeLocation().clone().add(RandX, -7.5, RandZ);
                        track = track + 1;
                        Guardian beam = lBS.getWorld().spawn(l, Guardian.class, guard ->{
                            guard.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(10);
                            guard.setHealth(10);
                            guard.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(8);
                            guard.setLootTable(LootTables.EMPTY.getLootTable());
                        });
                        Skeleton mount = lBS.getWorld().spawn(l, Skeleton.class, guard ->{
                            guard.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20);
                            guard.setHealth(20);
                            guard.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(10);
                            guard.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.25);
                            guard.addPassenger(beam);
                            guard.getEquipment().setItemInMainHand(power);
                            guard.getEquipment().setItemInMainHandDropChance(-10);
                            guard.getEquipment().setBoots(new ItemStack(Material.ARROW, 4));
                            guard.getEquipment().setBootsDropChance(1);
                            guard.setLootTable(LootTables.EMPTY.getLootTable());
                            guard.getAttribute(Attribute.GENERIC_FOLLOW_RANGE).setBaseValue(40);
                        });
                    } else {track = track +1;}
                } track = 0;
            }
        }.runTaskTimer(pl,600,1200); */


        Summon = Bukkit.getScheduler().runTaskTimer(pl, () -> {
            List<Entity> droptargs = boss.getNearbyEntities(15,20,15 );
            List<AreaEffectCloud> SnowSpawners = new ArrayList<>();
            for(Entity e : droptargs) {
                if (!(e instanceof Player)) continue;
                Player p = ((Player)e);
                if (p.getGameMode() == SPECTATOR || p.getGameMode() == CREATIVE) continue;
                Location lE = e.getLocation();
                SnowSpawners.add(e.getWorld().spawn(lE, AreaEffectCloud.class, SpawnLocation -> {
                    SpawnLocation.setCustomName("SpawnBall");
                    SpawnLocation.setBasePotionData(new PotionData(PotionType.WATER));
                    SpawnLocation.setRadius(1.5f);
                    SpawnLocation.setDuration(600);
                    SpawnLocation.setDurationOnUse(-20);
                    SpawnLocation.setParticle(Particle.CRIT_MAGIC);
                    SpawnLocation.setRadiusPerTick(0); //f makes it a float so it doesn't literally round the result off
                    SpawnLocation.setReapplicationDelay(1);
                    SpawnLocation.setRadiusOnUse(0);
                    SpawnLocation.addCustomEffect(new PotionEffect(SLOW, 60, 1, true, true, false), true);
                }));
            }



            SpawnMobs = Bukkit.getScheduler().runTaskTimer(pl, () -> {
                for(AreaEffectCloud SnowSpawner : SnowSpawners) {
                List<Entity> SpawnerCheck = boss.getNearbyEntities(30, 40, 30);
                SpawnerCheck.removeIf(ent -> !(ent instanceof Stray));
                int SpawnerCount = SpawnerCheck.size();
                if (SpawnerCount < 10) {
                Location SpawnBall = SnowSpawner.getLocation();
                Stray ArrowFarm = SpawnBall.getWorld().spawn(SpawnBall, Stray.class, Farm ->{
                    Farm.setCustomName("Frozen Soul");
                    Farm.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(7);
                    Farm.setHealth(7);
                    Farm.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(2);
                    Farm.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.25);
                    Farm.getAttribute(Attribute.GENERIC_FOLLOW_RANGE).setBaseValue(64);
                    Farm.getEquipment().setChestplate(new ItemStack(Material.ARROW, 1));
                    Farm.getEquipment().setChestplateDropChance(1);
                    Farm.getEquipment().setItemInOffHand(new ItemStack(Material.NETHERITE_SCRAP, 1));
                    Farm.getEquipment().setItemInOffHandDropChance(0.25f);
                    Farm.getEquipment().setLeggings(new ItemStack(Material.GOLDEN_APPLE, 1));
                    Farm.getEquipment().setLeggingsDropChance(0.25f);
                    Farm.getEquipment().setItemInMainHand(new ItemStack(Material.STONE_AXE, 1));
                    Farm.getEquipment().setItemInMainHandDropChance(-10);
                    Farm.setLootTable(LootTables.EMPTY.getLootTable());
                    Farm.addPotionEffect(new PotionEffect(WEAKNESS, 100000, 0, true));
                });
                } }
                spawnloop = spawnloop + 1;
                if (spawnloop >= 4) {spawnloop = 0; SpawnMobs.cancel();};
            }, 10, 100);

        }, 600, 1200);


        bar = Bukkit.createBossBar(new NamespacedKey(pl, boss.getUniqueId().toString()), "Bitra the Trapped", BarColor.RED, BarStyle.SEGMENTED_10);
        bar.setProgress(1);

        Bukkit.getPluginManager().registerEvents(this, pl);
    }
    public void unregister() {
        HandlerList.unregisterAll(this);
        ConstantIce.cancel();
        SpawnMobs.cancel();
        ResetAI.cancel();
        AdjustPos.cancel();
        SpiralBink.cancel();
        SpiralBlast.cancel();
        //GrowingStorm.cancel();
        FreezeFeet.cancel();
        //Apocalypse.cancel();
        Fireball.cancel();
        Summon.cancel();
        RepeatAttack.cancel();
        Fireball.cancel();
        bar.removeAll();
        Bukkit.removeBossBar(new NamespacedKey(pl, boss.getUniqueId().toString()));
    }

    protected static double progress(LivingEntity boss, double damage) {
        if (boss == null) return 0D;
        double progress = ((((boss.getHealth() - damage)*(boss.getHealth() - damage))/Math.sqrt((boss.getHealth() - damage)))+100) / (boss.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()*boss.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()/Math.sqrt(boss.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()));
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
    }
    public void killmark() {
        marker.setHealth(0);}
    public World getWorld() { return boss.getWorld();}

    @EventHandler
    public void onDeath(EntityDeathEvent e) {
        if (e.getEntity().equals(boss)) {Bukkit.getPluginManager().callEvent(new BossDefeatedEvent(this)); killmark(); unregister();} }

    @EventHandler
    public void AggroTrack(EntityTargetLivingEntityEvent e){
        if (e.getTarget().equals(boss)) e.setCancelled(true);
    }

    @EventHandler
    public void iceproj(ProjectileHitEvent e) {
        Entity damager = e.getEntity();
        Entity damaged = e.getHitEntity();

            if (damager instanceof Snowball && damaged instanceof Player) {
                ((Player) damaged).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 40, 0, true, false, true));
                ((Player) damaged).setFireTicks(100);
                ((Player) damaged).addPotionEffect(new PotionEffect(HARM, 1, 0, true, false, false));
            }
    }

    @EventHandler
    public void icetickcheck(EntityDamageEvent e) {
        if (e.getCause() == EntityDamageEvent.DamageCause.FREEZE) {
            e.setCancelled(true);
            if (e.getEntity() instanceof Player) {
                ((Player) e.getEntity()).addPotionEffect(new PotionEffect(SLOW, 10, 4, true, false, true));
            }
        }
    }

    /*
    @EventHandler
    public void guardpoke(Event) {
            if (damager instanceof Guardian && damaged instanceof Player) {
                ((Player) damaged).addPotionEffect(new PotionEffect(BLINDNESS, 20, 0, true, false, true));
                ((Player) damaged).addPotionEffect(new PotionEffect(POISON,200,2, true, false, true));
            }

    } */

}