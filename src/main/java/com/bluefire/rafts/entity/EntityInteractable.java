package com.bluefire.rafts.entity;

import com.bluefire.rafts.data.Types;
import com.bluefire.rafts.data.Vec2d;
import lombok.extern.log4j.Log4j2;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec2f;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.HashSet;

@Log4j2
public class EntityInteractable extends Entity {

    private static final DataParameter<Integer> HOST = EntityDataManager.<Integer>createKey(EntityInteractable.class, DataSerializers.VARINT);
    private static final DataParameter<Float> OFFSETX = EntityDataManager.<Float>createKey(EntityInteractable.class, DataSerializers.FLOAT);
    private static final DataParameter<Float> OFFSETY = EntityDataManager.<Float>createKey(EntityInteractable.class, DataSerializers.FLOAT);
    private static final DataParameter<Float> OFFSETZ = EntityDataManager.<Float>createKey(EntityInteractable.class, DataSerializers.FLOAT);
    private static final DataParameter<Float> WIDTH = EntityDataManager.<Float>createKey(EntityInteractable.class, DataSerializers.FLOAT);
    private static final DataParameter<Float> HEIGHT = EntityDataManager.<Float>createKey(EntityInteractable.class, DataSerializers.FLOAT);
    /**
     * Valid values:
     * 0 = No collision
     * 1 = Player collision
     * 2 = Full collision (use sparingly)
     */
    private static final DataParameter<Integer> COLLIDES = EntityDataManager.<Integer>createKey(EntityInteractable.class, DataSerializers.VARINT);

    /**
     * Valid types:
     * 0 = Steering
     * 1 = Sail
     * 2 = Sail turning
     * 3 = Barrier
     * 4 = Storage
     * 5 = Cannon
     */
    private static final DataParameter<Integer> TYPE = EntityDataManager.<Integer>createKey(EntityInteractable.class, DataSerializers.VARINT);
    public int modifier = 0;

    public int interactions = 0;
    public int work = 0;

    private static final boolean debugInfo = true;


    private static final AxisAlignedBB ZERO_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D);

    public EntityInteractable(World worldIn)
    {
        super(worldIn);
        this.setSize(0.5F, 0.5F);
        this.entityCollisionReduction = 1.0f;
    }

    public EntityInteractable(World worldIn, Entity hostIn, float x, float y, float z, float w, float h, int collision, int typeIn, int modifierIn)
    {
        this(worldIn);
        this.dataManager.set(HOST, hostIn.getEntityId());
        this.dataManager.set(OFFSETX, x);
        this.dataManager.set(OFFSETY, y);
        this.dataManager.set(OFFSETZ, z);
        this.dataManager.set(WIDTH, w);
        this.dataManager.set(HEIGHT, h);
        this.dataManager.set(COLLIDES, collision);
        this.dataManager.set(TYPE, typeIn);
        this.modifier = modifierIn;
    }

    @Override
    protected void entityInit() {
        this.dataManager.register(HOST, -1);
        this.dataManager.register(OFFSETX, 0.0f);
        this.dataManager.register(OFFSETY, 0.0f);
        this.dataManager.register(OFFSETZ, 0.0f);
        this.dataManager.register(WIDTH, 0.0f);
        this.dataManager.register(HEIGHT, 0.0f);
        this.dataManager.register(COLLIDES, 0);
        this.dataManager.register(TYPE, 1);
    }

    public boolean hasHost() { return this.getHost() != null && !this.getHost().isDead; }

    public Entity getHost() {
        int val = (Integer) this.dataManager.get(HOST);
        return val == -1 ? null : this.world.getEntityByID(val);
    }

    public boolean hasCollision() { return this.dataManager.get(COLLIDES) > 0; }
    public boolean hasShipCollision() { return this.dataManager.get(COLLIDES) == 2; }
    public float getOffsetX() {return (float) this.dataManager.get(OFFSETX);}
    public float getOffsetY() {return (float) this.dataManager.get(OFFSETY);}
    public float getOffsetZ() {return (float) this.dataManager.get(OFFSETZ);}
    public float getWidth() {return (float) this.dataManager.get(WIDTH);}
    public float getHeight() {return (float) this.dataManager.get(HEIGHT);}
    public int getType() {return (int)this.dataManager.get(TYPE).intValue();}


    public void delete() { this.world.removeEntity(this); }

    public final boolean processInitialInteract(EntityPlayer player, EnumHand hand)
    {
        ItemStack itemstack = player.getHeldItem(hand);
        if (itemstack.isEmpty() && this.getType() != Types.BARRIER) {
            this.interactions++;
            if (player instanceof EntityPlayerSP)
                ((EntityPlayerSP)player).swingArm(EnumHand.MAIN_HAND);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
        if (this.hasHost()) {
            Entity host = this.getHost();
            this.setSize(this.getWidth(), this.getHeight());
            Vec2d relative = fromRelativeCoordinates(this.getOffsetX(), this.getOffsetZ(), host);
            this.setPositionAndUpdate(relative.x, host.posY + this.getOffsetY(), relative.y);
        }
        if (!this.hasHost() && !this.world.isRemote) {
            this.delete();
        }
        if (debugInfo && !this.world.isRemote) {
            if (this.getType() == Types.BARRIER)
                this.world.spawnParticle(EnumParticleTypes.VILLAGER_HAPPY, this.posX, this.posY+(height/2f), this.posZ, 0.0D, 0.0D, 0.0D);
            if (this.getType() == Types.STEERING)
                this.world.spawnParticle(EnumParticleTypes.CRIT_MAGIC, this.posX, this.posY+(height/2f), this.posZ, 0.0D, 0.0D, 0.0D);
            if (this.getType() == Types.SAIL)
                this.world.spawnParticle(EnumParticleTypes.END_ROD, this.posX, this.posY+(height/2f), this.posZ, 0.0D, 0.0D, 0.0D);
        }
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
        return entityIn.canBePushed() ? this.getEntityBoundingBox() : null;
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
        return this.canBeCollidedWith() ? this.getEntityBoundingBox() : null;
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
        return !this.isDead && this.hasCollision();
    }

    /**
     * Returns true if other Entities should be prevented from moving through this Entity.
     */
    public boolean canBeCollidedWith()
    {
        return !this.isDead && this.hasCollision();
    }



}