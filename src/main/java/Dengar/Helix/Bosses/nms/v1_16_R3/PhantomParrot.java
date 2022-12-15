/*
package Dengar.Helix.Bosses.nms.v1_16_R3;

import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.entity.Parrot;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Consumer;

public class PhantomParrot extends EntityParrot {
    private Vec3D moveTargetPoint;
    private BlockPosition anchorPoint;
    private AttackPhase attackPhase;

    public PhantomParrot(Location l) {
        super(EntityTypes.PARROT, ((CraftWorld) l.getWorld()).getHandle());
        this.setPosition(l.getX(), l.getY(), l.getZ());
        this.moveTargetPoint = Vec3D.ORIGIN;
        this.anchorPoint = this.getChunkCoordinates().up(5);
        this.attackPhase = AttackPhase.CIRCLE;
        this.moveController = new PhantomMoveControl();
        this.lookController = new PhantomLookControl();
    }

    public static Parrot spawn(@NotNull Location l, @NotNull Consumer<Parrot> function) {
        World w = l.getWorld();
        if (w == null) return null;
        PhantomParrot e = new PhantomParrot(l);
        return ((CraftWorld) w).addEntity(e, CreatureSpawnEvent.SpawnReason.CUSTOM, function::accept);
    }

    @Override
    protected void initPathfinder() {
        this.goalSelector.a(0, new PathfinderGoalFloat(this));
        this.goalSelector.a(2, new PathfinderGoalSweepAttack());
        this.goalSelector.a(3, new PathfinderGoalCircleAroundAnchor());
        this.targetSelector.a(1, new PathfinderGoalPhantomTarget());
    }

    private enum AttackPhase {
        CIRCLE, SWOOP
    }

    private class PathfinderGoalPhantomTarget extends PathfinderGoal {
        private final PathfinderTargetCondition attackTargeting;
        private int nextScanTick;

        private PathfinderGoalPhantomTarget() {
            // forCombat(range=64)
            this.attackTargeting = (new PathfinderTargetCondition()).a(64.0D);
            this.nextScanTick = 20;
        }

        // canUse
        public boolean a() {
            if (nextScanTick > 0)
                --nextScanTick;
            else {
                nextScanTick = 60;
                // getNearbyPlayers
                List<EntityHuman> list = world.a(attackTargeting, PhantomParrot.this,
                        getBoundingBox().grow(16.0D, 64.0D, 16.0D));
                if (!list.isEmpty()) {
                    list.sort(Comparator.comparing(Entity::locY).reversed());

                    for (EntityHuman entityhuman : list) {
                        // canAttack(target, DEFAULT)
                        if (PhantomParrot.this.a(entityhuman, PathfinderTargetCondition.a)) {
                            setGoalTarget(entityhuman, EntityTargetEvent.TargetReason.CLOSEST_PLAYER, true);
                            return true;
                        }
                    }
                }
            }
            return false;
        }

        // canContinueToUse
        public boolean b() {
            EntityLiving entityliving = getGoalTarget();
            // canAttack(target, DEFAULT)
            return entityliving != null && PhantomParrot.this.a(entityliving, PathfinderTargetCondition.a);
        }
    }

    class PathfinderGoalAttackStrategy extends PathfinderGoal {
        private int nextSweepTick;

        // canUse
        public boolean a() {
            EntityLiving entityliving = getGoalTarget();
            // canAttack(target, DEFAULT)
            return entityliving != null && PhantomParrot.this.a(entityliving, PathfinderTargetCondition.a);
        }

        // start
        public void c() {
            this.nextSweepTick = 10;
            attackPhase = AttackPhase.CIRCLE;
            setAnchorAboveTarget();
        }

        // stop
        public void d() {
            anchorPoint = world.getHighestBlockYAt(HeightMap.Type.MOTION_BLOCKING, anchorPoint)
                    .up(10 + random.nextInt(20));
        }

        // tick
        public void e() {
            if (attackPhase == AttackPhase.CIRCLE) {
                --nextSweepTick;
                if (nextSweepTick <= 0) {
                    attackPhase = AttackPhase.SWOOP;
                    setAnchorAboveTarget();
                    nextSweepTick = (8 + random.nextInt(4)) * 20;
                    playSound(SoundEffects.ENTITY_PHANTOM_SWOOP, 10.0F, 0.95F + random.nextFloat() * 0.1F);
                }
            }
        }

        private void setAnchorAboveTarget() {
            anchorPoint = getGoalTarget().getChunkCoordinates().up(20 + random.nextInt(20));
            if (anchorPoint.getY() < world.getSeaLevel())
                anchorPoint = new BlockPosition(anchorPoint.getX(),
                        world.getSeaLevel() + 1, anchorPoint.getZ());
        }
    }

    private abstract class PathfinderGoalMoveTarget extends PathfinderGoal {
        public PathfinderGoalMoveTarget() {
            this.a(EnumSet.of(Type.MOVE));
        }

        protected boolean touchingTarget() {
            return moveTargetPoint.c(locX(), locY(), locZ()) < 4.0D;
        }
    }

    private class PathfinderGoalCircleAroundAnchor extends PathfinderGoalMoveTarget {
        private float angle;
        private float distance;
        private float height;
        private float clockwise;

        // canUse
        public boolean a() {
            return getGoalTarget() == null || attackPhase == AttackPhase.CIRCLE;
        }

        // start
        public void c() {
            this.distance = 5.0F + random.nextFloat() * 10.0F;
            this.height = -4.0F + random.nextFloat() * 9.0F;
            this.clockwise = random.nextBoolean() ? 1.0F : -1.0F;
            this.selectNext();
        }

        // tick
        public void e() {
            if (random.nextInt(350) == 0)
                this.height = -4.0F + random.nextFloat() * 9.0F;

            if (random.nextInt(250) == 0) {
                ++this.distance;
                if (this.distance > 15.0F) {
                    this.distance = 5.0F;
                    this.clockwise = -this.clockwise;
                }
            }

            if (random.nextInt(450) == 0) {
                this.angle = random.nextFloat() * 2.0F * 3.1415927F;
                selectNext();
            }

            if (this.touchingTarget())
                selectNext();

            if (moveTargetPoint.y < locY() && !world.isEmpty(getChunkCoordinates().down(1))) {
                this.height = Math.max(1.0F, this.height);
                selectNext();
            }

            if (moveTargetPoint.y > locY() && !world.isEmpty(getChunkCoordinates().up(1))) {
                this.height = Math.min(-1.0F, this.height);
                selectNext();
            }
        }

        private void selectNext() {
            if (BlockPosition.ZERO.equals(anchorPoint))
                anchorPoint = getChunkCoordinates();

            this.angle += this.clockwise * 15.0F * 0.017453292F;
            moveTargetPoint = Vec3D.b(anchorPoint).add(distance * MathHelper.cos(angle),
                    -4.0F + height, distance * MathHelper.sin(angle));
        }
    }

    private class PathfinderGoalSweepAttack extends PathfinderGoalMoveTarget {
        // canStart
        public boolean a() {
            return getGoalTarget() != null && attackPhase == AttackPhase.SWOOP;
        }

        // start
        public boolean b() {
            EntityLiving entityliving = getGoalTarget();
            if (entityliving == null)
                return false;
            else if (!entityliving.isAlive())
                return false;
            else if (entityliving instanceof EntityHuman &&
                    (entityliving.isSpectator() || ((EntityHuman) entityliving).isCreative()))
                return false;
            else if (!this.a())
                return false;
            else {
                if (ticksLived % 20 == 0) {
                    // getEntitiesOfClass(EntityCat.class, bb.inflate(16), ENTITY_STILL_ALIVE)
                    List<EntityCat> list = world.a(EntityCat.class, getBoundingBox().g(16.0D), IEntitySelector.a);
                    if (!list.isEmpty()) {
                        for (EntityCat entitycat : list)
                            entitycat.eZ(); // hiss
                        return false;
                    }
                }
                return true;
            }
        }

        // start
        public void c() {
        }

        // stop
        public void d() {
            setGoalTarget(null);
            attackPhase = AttackPhase.CIRCLE;
        }

        // tick
        public void e() {
            EntityLiving entityliving = getGoalTarget();
            moveTargetPoint = new Vec3D(entityliving.locX(), entityliving.e(0.5D), entityliving.locZ());
            // bb.inflate.intersects
            if (getBoundingBox().g(0.20000000298023224D).c(entityliving.getBoundingBox())) {
                attackEntity(entityliving);
                attackPhase = AttackPhase.CIRCLE;
                if (!isSilent())
                    // 1039 = "Phantom bites"
                    world.triggerEffect(1039, getChunkCoordinates(), 0);
            } else if (positionChanged || hurtTicks > 0)
                attackPhase = AttackPhase.CIRCLE;
        }
    }
    private class PhantomLookControl extends ControllerLook {
        public PhantomLookControl() {
            super(PhantomParrot.this);
        }

        // tick
        public void a() {
        }
    }

    private class PhantomMoveControl extends ControllerMove {
        private float j = 0.1F;

        public PhantomMoveControl() {
            super(PhantomParrot.this);
        }

        // tick
        public void a() {
            if (positionChanged) {
                yaw += 180.0F;
                this.j = 0.1F;
            }

            float f = (float) (moveTargetPoint.x - locX());
            float f1 = (float) (moveTargetPoint.y - locY());
            float f2 = (float) (moveTargetPoint.z - locZ());
            double d0 = MathHelper.c(f * f + f2 * f2);
            double d1 = 1.0D - (double) MathHelper.e(f1 * 0.7F) / d0;
            f = (float) ((double) f * d1);
            f2 = (float) ((double) f2 * d1);
            d0 = MathHelper.c(f * f + f2 * f2);
            double d2 = MathHelper.c(f * f + f2 * f2 + f1 * f1);
            float f3 = yaw;
            float f4 = (float) MathHelper.d(f2, (double) f);
            float f5 = MathHelper.g(yaw + 90.0F);
            float f6 = MathHelper.g(f4 * 57.295776F);
            yaw = MathHelper.d(f5, f6, 4.0F) - 90.0F;
            aA = yaw;
            if (MathHelper.d(f3, yaw) < 3.0F) {
                this.j = MathHelper.c(this.j, 1.8F, 0.005F * (1.8F / this.j));
            } else {
                this.j = MathHelper.c(this.j, 0.2F, 0.025F);
            }

            float f7 = (float) (-(MathHelper.d(-f1, d0) * 57.2957763671875D));
            pitch = f7;
            float f8 = yaw + 90.0F;
            double d3 = (double) (this.j * MathHelper.cos(f8 * 0.017453292F)) * Math.abs((double) f / d2);
            double d4 = (double) (this.j * MathHelper.sin(f8 * 0.017453292F)) * Math.abs((double) f2 / d2);
            double d5 = (double) (this.j * MathHelper.sin(f7 * 0.017453292F)) * Math.abs((double) f1 / d2);
            Vec3D vec3d = getMot();
            setMot(vec3d.e((new Vec3D(d3, d5, d4)).d(vec3d).a(0.2D)));
        }
    }
    protected EntityAIBodyControl r() {
        return new PhantomBodyControl();
    }

    class PhantomBodyControl extends EntityAIBodyControl {
        public PhantomBodyControl () {
            super(PhantomParrot.this);
        }

        // clientTick
        public void a() {
            PhantomParrot.this.aC = PhantomParrot.this.aA;
            PhantomParrot.this.aA = PhantomParrot.this.yaw;
        }
    }
}


 */
