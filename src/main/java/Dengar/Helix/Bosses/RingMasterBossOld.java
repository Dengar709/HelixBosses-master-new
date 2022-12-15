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
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.loot.LootTables;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;

import static org.bukkit.potion.PotionEffectType.*;

public class RingMasterBossOld extends Boss implements Listener {
    private final PluginMain pl;
    private final Piglin boss;
    //private final BukkitTask PParrot;
    private final BukkitTask AngyHorse;
    private final BukkitTask SmallCats;
    private final BukkitTask CloneBoss;
    //private final BukkitTask FireRing;
    //private BukkitTask Fire;
    private final BukkitTask Strongman;
    //private final BukkitTask Bounce;
    private final KeyedBossBar bar;
    private int D = 0;

    public RingMasterBossOld(PluginMain pl, Location l) {
        this.pl = pl;
        this.boss = l.getWorld().spawn(l, Piglin.class, boss -> {
            boss.setCustomName("The Ring Master");
            boss.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(600);
            boss.setHealth(600);
            boss.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(10);
            boss.getEquipment().setItemInMainHand(new ItemStack(Material.LEAD, 1));
            boss.getEquipment().setItemInMainHandDropChance(-10);
            boss.getEquipment().setChestplate(new ItemStack(Material.GOLDEN_CHESTPLATE, 1));
            boss.getEquipment().setChestplateDropChance(-10);
            boss.getEquipment().setItemInOffHand(new ItemStack(Material.STICK, 1));
            boss.getEquipment().setItemInOffHandDropChance(-10);
            boss.setImmuneToZombification(true);
            boss.setLootTable(LootTables.EMPTY.getLootTable());
            boss.setBaby();
            boss.setCanPickupItems(false);
            boss.addScoreboardTag("ai_aggro");
            boss.addScoreboardTag("attack_damage_6");
        });
 /*
    PParrot = Bukkit.getScheduler().runTaskTimer(pl, () ->{
        Location lB = (boss).getEyeLocation();
        float D = (boss).getEyeLocation().getYaw();
        double Xv = 0.8* Math.sin(Math.toRadians(D));
        double Zv = 0.8* Math.cos(Math.toRadians(D));
        List<Entity> SpawnCheck = boss.getNearbyEntities(20, 20, 20);
        SpawnCheck.removeIf(e -> !(e instanceof Parrot));
        int SpawnCount = SpawnCheck.size();
        if (SpawnCount <6) {
        boss.getWorld().spawn(boss.getLocation(), Parrot.class, pain -> { //referential to ParrotPhantom
            pain.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(10);
            pain.setHealth(10);
            pain.addScoreboardTag("ai_aggro");
            pain.addScoreboardTag("attack_damage_9");
        });}
    }, 200,600); */

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
            punchy.addScoreboardTag("attack_damage_8");
            punchy.setBaby();
            punchy.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.35);
            punchy.setLootTable(LootTables.EMPTY.getLootTable());
        }); }
        },0, 80);

    CloneBoss = Bukkit.getScheduler().runTaskTimer(pl, () ->{
        Location lB = (boss).getEyeLocation();
        List<Entity> SpawnCheckP = boss.getNearbyEntities(40, 20, 40);
        for (Entity e : SpawnCheckP) {
            if (!(e instanceof Player) || e.getLocation().distanceSquared(boss.getLocation()) > 25) continue;
            ((Player) e).addPotionEffect(new PotionEffect(BLINDNESS, 40, 1, true, false, false));
        }
        SpawnCheckP.removeIf(e -> !(e instanceof Piglin));
        int SpawnCountP = SpawnCheckP.size();
        ItemStack droppable = new ItemStack(org.bukkit.Material.SPLASH_POTION, 1);
        ItemMeta im = droppable.getItemMeta();
        if (im instanceof PotionMeta) {
            ((PotionMeta)im).setBasePotionData(new PotionData(PotionType.INSTANT_DAMAGE, false, false));
            ((PotionMeta)im).addCustomEffect(new PotionEffect(BLINDNESS,120, 0, true ,false, false), true);
            droppable.setItemMeta(im);
        }
        if (SpawnCountP < 4) {
        ThrownPotion splash = boss.getWorld().spawn(boss.getLocation(), ThrownPotion.class, bomb -> {
            bomb.setCustomName("Disappearing Act");
            bomb.setItem(droppable);
        });
        boss.getWorld().spawn(lB, Piglin.class, clone -> {
            clone.setCustomName("The Ring Master");
            clone.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(10);
            clone.setHealth(10);
            clone.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(7);
            clone.getEquipment().setItemInMainHand(new ItemStack(Material.LEAD, 1));
            clone.getEquipment().setItemInMainHandDropChance(-10);
            clone.getEquipment().setChestplate(new ItemStack(Material.GOLDEN_CHESTPLATE, 1));
            clone.getEquipment().setChestplateDropChance(-10);
            clone.getEquipment().setItemInOffHand(new ItemStack(Material.STICK, 1));
            clone.getEquipment().setItemInOffHandDropChance(-10);
            clone.setImmuneToZombification(true);
            clone.addPassenger(splash);
            clone.setLootTable(LootTables.EMPTY.getLootTable());
            clone.setBaby();
            clone.setCanPickupItems(false);
            clone.addScoreboardTag("ai_aggro");
            clone.addScoreboardTag("attack_damage_6");
        });
        }
    }, 200, 200);

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
                punchy.addScoreboardTag("attack_damage_6");
                punchy.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.3);
                punchy.setLootTable(LootTables.EMPTY.getLootTable());
                punchy.setAdult();
            }); }
    },1200, 120);
/*
    FireRing = Bukkit.getScheduler().runTaskTimer(pl, ()-> {
        Fire = Bukkit.getScheduler().runTaskTimer(pl, () -> {
            Location lB = (boss).getEyeLocation();
            double Xv = 0.8* Math.sin(Math.toRadians(D));
            double Zv = 0.8* Math.cos(Math.toRadians(D));
            Location lP = (boss).getEyeLocation().clone().add((-6 * Xv), 0, (6 * Zv)).subtract(0, 8.5f, 0);
            Location lN = (boss).getEyeLocation().clone().subtract((-6 * Xv), 8.5f, (6 * Zv));
            Snowball flame = boss.getWorld().spawn(lP, Snowball.class, flare -> {
                flare.setCustomName("freeze");
                flare.setShooter(boss);
                flare.setGravity(false);
                flare.setItem(new ItemStack(Material.FIRE));
                flare.setSilent(true);
                flare.setVelocity(new Vector(-0.5*Xv, -0.1, 0.5*Zv));
            });
            D = D + (9);
            if (D > 359) {D = 0; Fire.cancel();};
        }, 0, 0);

    },00,40);
*/

    Strongman = Bukkit.getScheduler().runTaskTimer(pl, () -> {
        Location lB = (boss).getEyeLocation();
        List<Entity> SpawnCheck2 = boss.getNearbyEntities(20, 20, 20);
        SpawnCheck2.removeIf(e -> !(e instanceof IronGolem));
        int SpawnCount = SpawnCheck2.size();
        if (SpawnCount < 1) {
            boss.getWorld().spawn(lB, IronGolem.class, strong -> {
                strong.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(75);
                strong.setHealth(75);
                strong.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.25);
                strong.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(14);
                strong.addScoreboardTag("ai_aggro");
                strong.addScoreboardTag("attack_damage_14");
                strong.setLootTable(LootTables.EMPTY.getLootTable());
        }); }
    },1000,400);
/*
    Bounce = Bukkit.getScheduler().runTaskTimer(pl, () ->{
        Location lB = (boss).getEyeLocation();
        float D = (boss).getEyeLocation().getYaw();
        double Xv = 0.8* Math.sin(Math.toRadians(D));
        double Zv = 0.8* Math.cos(Math.toRadians(D));
        boss.getWorld().spawn(boss.getEyeLocation(), Arrow.class, knife ->{
            knife.setVelocity(new Vector(0.3*Xv, 0.2, 0.3*Zv));
            knife.setDamage(10);
        });
    }, 00, 60); */


        bar = Bukkit.createBossBar(new NamespacedKey(pl, boss.getUniqueId().toString()), "The Ring Master", BarColor.RED, BarStyle.SEGMENTED_10);
        bar.setProgress(1);

        Bukkit.getPluginManager().registerEvents(this, pl);
}

    public void unregister() {
        HandlerList.unregisterAll(this);
        //PParrot.cancel();
        AngyHorse.cancel();
        CloneBoss.cancel();
        SmallCats.cancel();
        //Fire.cancel();
        //FireRing.cancel();
        Strongman.cancel();
        //Bounce.cancel();
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

    @Override
    public void kill() {
    boss.setHealth(0);
    }

    @Override
    public World getWorld() {return boss.getWorld();}

    @EventHandler
    public void onDeath(EntityDeathEvent e) {
        if (e.getEntity().equals(boss)) {Bukkit.getPluginManager().callEvent(new BossDefeatedEvent(this)); unregister();}}



}
