package com.bluefire.rafts.entity;

import com.bluefire.rafts.data.RaftTemplate;
import com.bluefire.rafts.data.Vec2d;
import lombok.extern.log4j.Log4j2;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

@Log4j2
public abstract class EntityPlatformBase extends Entity {

    public static final HashSet<Object> IGNORELIST = new HashSet<>();

    public int timeWithNoTarget = 0;
    public int timeLimit = 1200; // 60 seconds
    private static final AxisAlignedBB ZERO_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D);
    private AxisAlignedBB collisionBoundingBox;
    private double boxX;
    private double boxY;
    private double boxZ;
    public static final boolean debugInfo = false;

    //raft movement information
    public int grounding = 0;
    public int stepTracker = 0;
    public double targStoredX = 0d;
    public double targStoredZ = 0d;

    private Entity prevHost = null;
    private Entity prevTarget = null;


    static
    {
        registerIgnorable(EntityInteractable.class);
        registerIgnorable(EntityPlatformClient.class);
        registerIgnorable(EntityPlatform.class);
        registerIgnorable(EntityPlatformBase.class);
        registerIgnorable(EntityRaft.class);
    }

    public static void registerIgnorable(Class<? extends Entity> cls)
    {
        IGNORELIST.add(cls);
    }

    public EntityPlatformBase(World worldIn)
    {
        super(worldIn);
        this.setSize(2.0F, 0.8F);
        this.collisionBoundingBox = ZERO_AABB;
        this.entityCollisionReduction = 1.0f;
    }

    public void setCollisionBoundingBox(AxisAlignedBB bb)
    {
        this.collisionBoundingBox = bb;
    }

    public EntityPlatformBase(World worldIn, Entity hostIn)
    {
        this(worldIn);
    }

    public EntityPlatformBase(World worldIn, Entity hostIn, Entity targIn, int typeIn)
    {
        this(worldIn, hostIn);
    }

    @Override
    protected void entityInit() {
    }


    public abstract Entity getHost();

    public abstract void removeTarget();

    public abstract void setTarget(Entity targetIn, int typeIn);

    public abstract Entity getTarget();

    public abstract int getType();

    public abstract int getLayout();

    public abstract void setPlatformLayout(int id);

    public float[] getPlatformLayout() {
        return RaftTemplate.layouts.get(this.getLayout()).layout;
    }

    public void delete() { this.world.removeEntity(this); }

    public boolean hasHost() { return this.getHost() != null && !this.getHost().isDead; }

    public boolean hasTarget() { return this.getTarget() != null; }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        this.prevDistanceWalkedModified = this.distanceWalkedModified;
        this.prevRotationPitch = this.rotationPitch;
        this.prevRotationYaw = this.rotationYaw;

        if (debugInfo) {
            if (this.prevHost != this.getHost()) {
                this.prevHost = this.getHost();
                if (this.hasHost()) {
                    log.info("Host for platform {} changed to {} {} on tick {}", this.getEntityId(), this.prevHost.getName(), this.prevHost.getEntityId(), this.world.getWorldTime());
                } else {
                    log.info("Host for platform {} removed on tick {}", this.getEntityId(), this.world.getWorldTime());
                }
            }
            if (this.prevTarget != this.getTarget()) {
                if (this.hasTarget()) {
                    this.prevTarget = this.getTarget();
                    log.info("Target for platform {} changed to {} {} on tick {}", this.getEntityId(), this.prevTarget.getName(), this.prevTarget.getEntityId(), this.world.getWorldTime());
                } else {
                    log.info("Target for platform {} removed on tick {}, was {} {}", this.getEntityId(), this.world.getWorldTime(), this.prevTarget.getName(), this.prevTarget.getEntityId());
                    this.prevTarget = this.getTarget();
                }
            }
        }

        if (this.hasHost()) {
            Entity host = this.getHost();
            if (this.hasTarget()) {
                this.getCollisionBoundingBox();
            } else {
                this.setPosition(host.posX, host.posY, host.posZ);
            }
        }

        if (debugInfo && !this.world.isRemote) {
            if (this.getType() == 1)
                this.world.spawnParticle(EnumParticleTypes.CRIT_MAGIC, this.posX, this.posY+1.05d, this.posZ, 0.0D, 0.0D, 0.0D);
            if (this.getType() == 2)
                this.world.spawnParticle(EnumParticleTypes.VILLAGER_HAPPY, this.posX, this.posY+1.05d, this.posZ, 0.0D, 0.0D, 0.0D);
            if (this.getType() == 3)
                this.world.spawnParticle(EnumParticleTypes.END_ROD, this.posX, this.posY+1.05d, this.posZ, 0.0D, 0.0D, 0.0D);
        }
        if (!this.hasHost() && !this.world.isRemote) {
            this.delete();
        }
        if (!this.hasHost() && this.world.isRemote && this.getType() == 4) {
            this.delete();
        }
    }

    public boolean isGrounded(Entity targ) {
        return this.collisionBoundingBox.grow(0.0d, 0.01d, 0.0d).intersects(targ.getEntityBoundingBox()) && targ.posY > this.posY;
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) {
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {
    }

    @Nullable
    public AxisAlignedBB getCollisionBox(Entity entityIn)
    {
        return entityIn.canBePushed() ? this.collisionBoundingBox : null;
    }

    /**
     * Returns the <b>solid</b> collision bounding box for this entity. Used to make (e.g.) boats solid. Return null if
     * this entity is not solid.
     *
     * For general purposes, use {@link #width} and {@link #height}.
     */
    @Nullable
    public AxisAlignedBB getCollisionBoundingBox()
    {
        if (this.hasHost()) {
            if (this.hasTarget()) {
                Entity targ = this.getTarget();
                Entity host = this.getHost();
                if (this.boxX != targ.posX || this.boxY != host.posY || this.boxZ != targ.posZ) {
                    this.boxX = targ.posX;
                    this.boxY = host.posY;
                    this.boxZ = targ.posZ;
                    this.updateCollisionBox(targ, host);
                }
                return this.collisionBoundingBox;
            }
        }

        //return this.getEntityBoundingBox();
        return null;
    }

    public void updateCollisionBox(Entity targ, Entity host)
    {
        // get forward-to-back location of player
        //Vec2f relative2 = fromRelativeCoordinates(0d, (double)relative.y, host);
        //if (!this.world.isRemote)
        //    this.world.spawnParticle(EnumParticleTypes.VILLAGER_HAPPY, relative2.x, this.posY+0.5d, relative2.y, 0.0D, 0.0D, 0.0D);

        //get initial position
        final double w = 0.35d;
        final double h = 1.0d;
        double x = targ.posX;
        double y = host.posY-0.75d;
        double z = targ.posZ;

        // get bounded position
        Vec2d relative;
        if (this.getType() == 3) {
            relative = toRelativeCoordinates(targ.posX + targ.motionX, targ.posZ + targ.motionZ, host);
        } else {
            relative = toRelativeCoordinates(targ, host);
        }
        float height = getPlatformHeight(relative.x, relative.y);
        boolean bounded = false;
        if (height <= -126f) {
            //x = host.posX;
            //z = host.posZ;
            if (this.getType() == 3) {
                relative = toRelativeCoordinates(targ, host);
            }
            relative = getPlatformEdge(relative.x, relative.y);
            relative = fromRelativeCoordinates(relative.x, relative.y, host);
            x = (double)relative.x;
            z = (double)relative.y;
            bounded = true;
        }
        if (this.getDistanceSq(x, y, z) < 0.02d && bounded) {
            x = this.posX;
            z = this.posZ;
        }

        // move platform
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        //this.setPosition(x, y, z);
        this.setPositionAndUpdate(x, y, z);
        AxisAlignedBB newbox = new AxisAlignedBB(x - w, y, z - w, x + w, y + h, z + w);
        if (this.getType() == 1 && (!bounded || targ.posY > y)) {
            if (targ.posY < y+h && targ.posY+targ.height > y && newbox.intersects(targ.getEntityBoundingBox()))
                targ.posY = y+h + 0.001d;
        }
        if (this.getType() == 2 && !bounded) {
            if (targ.posY < y+h && targ.posY+targ.height > y && newbox.intersects(targ.getEntityBoundingBox()))
                targ.posY = y+h + 0.0d;
                //targ.posY = y+h + 0.001d;
        }

        if (!newbox.intersects(targ.getEntityBoundingBox()) || !bounded) {
            this.setCollisionBoundingBox(newbox);
            if (debugInfo && this.getType() == 2) {
                log.info("*Platform *{}* box changed to x*{}* y*{}* z*{}* on tick *{}*, target coords x*{}* y*{}* z*{}*, host coords x*{}* y*{}* z*{}*, forced *{}", this.getEntityId(), x, y+h, z, this.world.getWorldTime(), targ.posX, targ.posY, targ.posZ, host.posX, host.posY, host.posZ, false );
            }
        }
    }

    public double getDistanceSq2D(Entity entityIn)
    {
        double d0 = this.posX - entityIn.posX;
        double d2 = this.posZ - entityIn.posZ;
        return d0 * d0 + d2 * d2;
    }

    public Vec2d getPlatformEdge(double x, double z) {
        double mult = 0.5f;
        double mod = 0.25f;
        int acc = 6; // 15x15? - 27x27 might need an extra degree of precision
        switch (this.getType()) {
            case 1:
                acc = 5; // 15x15? - 27x27 might need an extra degree of precision
                break;
            case 2:
                if (x > 0) {
                    x += (double)(getTarget().width / 2f);
                } else {
                    x -= (double)(getTarget().width / 2f);
                }
                break;
            case 3:
                if (z > 0) {
                    z += (double)(getTarget().width / 2f);
                } else {
                    z -= (double)(getTarget().width / 2f);
                }
                break;
        }
        for (int i = 0; i < acc; i++) {
            if (getPlatformHeight(x * mult, z * mult) <= -126f) {
                mult -= mod;
            } else {
                mult += mod;
            }
            mod /= 2;
        }
        return new Vec2d((x*mult), (z*mult));
    }

    public float getPlatformHeight(double xRelative, double zRelative) {
        xRelative*=2;
        zRelative*=2;
        if ( xRelative >= 15.0d || xRelative < -15.0d || zRelative >= 15.0d || zRelative < -15.0d ) { // 15x15 - 27x27 would be 27
            return -127f;
        }
        int x = (int)(xRelative+15.0d); // 15x15 - 27x27 would be +27
        int z = (int)(zRelative+15.0d); // 15x15 - 27x27 would be +27
        int index = x+(z*30); // 15x15 - 27x27 would be x54
        return this.getPlatformLayout()[index];
    }

    public Vec2d toRelativeCoordinates(double x, double z, Entity host) {
        double hostX = host.posX;
        double hostZ = host.posZ;
        double angle = (host.rotationYaw) / -57.295755f;
        double newX = (x-hostX)*Math.cos(angle) - (z-hostZ)*Math.sin(angle);
        double newZ = (x-hostX)*Math.sin(angle) + (z-hostZ)*Math.cos(angle);
        return new Vec2d((float)newX, (float)newZ);
    }

    public Vec2d toRelativeCoordinates(Entity target, Entity host) {
        return this.toRelativeCoordinates(target.posX, target.posZ, host);
    }

    public Vec2d fromRelativeCoordinates(double x, double z, Entity host) {
        double hostX = host.posX;
        double hostZ = host.posZ;
        double angle = (host.rotationYaw) / 57.295755f;
        double newX = hostX + (x)*Math.cos(angle) - (z)*Math.sin(angle);
        double newZ = hostZ + (x)*Math.sin(angle) + (z)*Math.cos(angle);
        return new Vec2d((float)newX, (float)newZ);
    }

    public Vec2d getRelativeCoordinates(double targX, double targZ, double hostX, double hostZ, double angle) {
        double newX = hostX + (targX-hostX)*Math.cos(angle) - (targZ-hostZ)*Math.sin(angle);
        double newZ = hostZ + (targX-hostX)*Math.sin(angle) + (targZ-hostZ)*Math.cos(angle);
        return new Vec2d((float)newX, (float)newZ);
    }

    public Vec2d getRelativeCoordinates(Entity targ, Entity host) {
        double hostX = host.posX;
        double hostZ = host.posZ;
        double targX = targ.posX;
        double targZ = targ.posZ;
        double angle = (host.rotationYaw) / -57.295755f;
        return this.getRelativeCoordinates(targX, targZ, hostX, hostZ, angle);
    }


    /**
     * Returns true if this entity should push and be pushed by other entities when colliding.
     */
    public boolean canBePushed()
    {
        //return false;
        return !this.isDead;
    }


    /**
     * Returns true if other Entities should be prevented from moving through this Entity.
     */
    public boolean canBeCollidedWith()
    {
        return !this.isDead;
    }


    @Override
    public List<Entity> getPassengers()
    {
        return Collections.emptyList();
    }

    @Override
    public void removePassengers() {}

    @Override
    protected void addPassenger(Entity passenger) {}

    @Override
    protected void removePassenger(Entity passenger) {}


    /*
    private void tickLerp()
    {
        if (this.lerpSteps > 0 && !this.canPassengerSteer())
        {
            double d0 = this.posX + (this.lerpX - this.posX) / (double)this.lerpSteps;
            double d1 = this.posY + (this.lerpY - this.posY) / (double)this.lerpSteps;
            double d2 = this.posZ + (this.lerpZ - this.posZ) / (double)this.lerpSteps;
            double d3 = MathHelper.wrapDegrees(this.lerpYaw - (double)this.rotationYaw);
            this.rotationYaw = (float)((double)this.rotationYaw + d3 / (double)this.lerpSteps);
            this.rotationPitch = (float)((double)this.rotationPitch + (this.lerpPitch - (double)this.rotationPitch) / (double)this.lerpSteps);
            --this.lerpSteps;
            this.setPosition(d0, d1, d2);
            this.setRotation(this.rotationYaw, this.rotationPitch);
        }
    }

     */


}