package com.bluefire.rafts.entity;

import javax.annotation.Nullable;

//import com.bluefire.rafts.network.SyncPlatformsPacket;
import com.bluefire.rafts.data.Interactable;
import com.bluefire.rafts.data.InteractableHolder;
import com.bluefire.rafts.data.RaftTemplate;
import com.bluefire.rafts.data.Types;
import com.bluefire.rafts.network.RaftSyncPacket;
import com.google.common.base.Predicate;
import lombok.extern.log4j.Log4j2;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.EntityLookHelper;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityParrot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import com.bluefire.rafts.data.Vec2d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import java.util.*;

@Log4j2
public class EntityRaft extends EntityLiving implements IAnimatable {

    // Model and animations

    private AnimationFactory factory = new AnimationFactory(this);

    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.raft.idle", true));
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<EntityRaft>(this, "controller", 0, this::predicate));
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }

    // Controls

    private static final DataParameter<Integer> STEERINGWHEEL = EntityDataManager.<Integer>createKey(EntityRaft.class, DataSerializers.VARINT);
    public void setSteeringWheel(int i) {
        this.dataManager.set(STEERINGWHEEL, i);
    }
    public int getSteeringWheel() {
        return (int) this.dataManager.get(STEERINGWHEEL);
    }

    private static final DataParameter<Integer> SAILS = EntityDataManager.<Integer>createKey(EntityRaft.class, DataSerializers.VARINT);
    public void setSails(int i) {this.dataManager.set(SAILS, i);}
    public int getSails() {return (int) this.dataManager.get(SAILS);}

    private static final DataParameter<Float> STEERACCEL = EntityDataManager.<Float>createKey(EntityRaft.class, DataSerializers.FLOAT);
    public void setSteerAcceleration(float i) {this.dataManager.set(STEERACCEL, i);}
    public float getSteerAcceleration() {return (float) this.dataManager.get(STEERACCEL);}

    private static final DataParameter<Float> STEERMULT = EntityDataManager.<Float>createKey(EntityRaft.class, DataSerializers.FLOAT);
    public void setSteeringMult(float i) {this.dataManager.set(STEERMULT, i);}
    public float getSteeringMult() {return (float) this.dataManager.get(STEERMULT);}

    private static final DataParameter<Float> SPEED = EntityDataManager.<Float>createKey(EntityRaft.class, DataSerializers.FLOAT);
    public void setSpeed(float i) {this.dataManager.set(SPEED, i);}
    public float getSpeed() {return (float) this.dataManager.get(SPEED);}

    private static final DataParameter<Float> DRAG = EntityDataManager.<Float>createKey(EntityRaft.class, DataSerializers.FLOAT);
    public void setDrag(float i) {this.dataManager.set(DRAG, i);}
    public float getDrag() {return (float) this.dataManager.get(DRAG);}

    private static final DataParameter<Integer> LAYOUT = EntityDataManager.<Integer>createKey(EntityRaft.class, DataSerializers.VARINT);
    public void setLayout(int i) {this.dataManager.set(LAYOUT, i);}
    public int getLayout() {return (int) this.dataManager.get(LAYOUT);}


    // Platform system

    public static final int momentumTicks = 20;

    private List<EntityPlatform> platforms = new ArrayList<>();
    private HashSet<Integer> platformed = new HashSet<>();

    // soft limit, will try to remove unused platforms over this number
    public final int platformLimit = 3;

    public EntityPlatform addPlatform() {
        EntityPlatform platform = new EntityPlatform(this.world, this);
        platform.setLocationAndAngles(this.posX, this.posY, this.posZ, 0.0F, 0.0F);
        this.world.spawnEntity(platform);
        this.platforms.add(platform);
        platform.timeLimit = (1200/platforms.size()) + 300;
        return platform;
    }

    public EntityPlatform addPlatform(Entity en, int type) {
        EntityPlatform platform = new EntityPlatform(this.world, this, en, type);
        platform.setLocationAndAngles(this.posX, this.posY, this.posZ, 0.0F, 0.0F);
        platform.setPlatformLayout(this.getLayout());
        this.world.spawnEntity(platform);
        this.platforms.add(platform);
        platform.timeLimit = (1200/platforms.size()) + 300;
        return platform;
    }

    public EntityPlatformClient addPlatformClient(Entity en, int type) {
        EntityPlatformClient platform = new EntityPlatformClient(this.world, this, en, type);
        platform.setLocationAndAngles(this.posX, this.posY, this.posZ, 0.0F, 0.0F);
        platform.setPlatformLayout(this.getLayout());
        this.world.spawnEntity(platform);
        return platform;
    }

    private static final Predicate<Entity> PLATFORMABLE = new Predicate<Entity>()
    {
        public boolean apply(@Nullable Entity en)
        {
            return en != null && en.isEntityAlive() && en.canBeCollidedWith() && !EntityPlatform.IGNORELIST.contains(en.getClass());
        }
    };

    public void updatePlatforms() {
        if (this.world.isRemote || !(this.world instanceof WorldServer)) {
            this.updatePlatformsClient();
            return;
        }
        List<Entity> list = this.world.<Entity>getEntitiesWithinAABB(Entity.class, this.getEntityBoundingBox().grow(12.0D, 5.0D, 12.0D), PLATFORMABLE); // 15x15

        for (Entity en : list) {
            if (!platformed.contains(en.getEntityId())) {
                platformed.add(en.getEntityId());
                if (en instanceof EntityPlayer) {
                    this.getUnusedPlatform(en, 2);
                    //this.getUnusedPlatform(en, 3);
                } else {
                    this.getUnusedPlatform(en, 1);
                }
            }
        }

        List<EntityPlatform> markForRemoval = new ArrayList<>();
        for (EntityPlatform platform : platforms) {
            if (platform.hasTarget()) {
                Entity targ = platform.getTarget();
                if (targ.isDead || this.getDistanceSq(targ) > 256.0d || targ.dimension != this.dimension) { // 15x15
                    platform.removeTarget();
                    platformed.remove(targ.getEntityId());
                } else {
                    platform.timeWithNoTarget = 0;
                }
            } else {
                platform.timeWithNoTarget++;
            }
            if (platforms.size() > platformLimit) {
                if (platform.timeWithNoTarget > platform.timeLimit) {
                    markForRemoval.add(platform);
                    platform.delete();
                }
            }
        }
        if (markForRemoval.size() > 0) {
            for (EntityPlatform platform : markForRemoval) {
                platforms.remove(platform);
            }
        }

        //new SyncPlatformsPacket(changedPlatforms, changedEntitys).sendPacketToAllAround(this.world, this.posX, this.posY, this.posZ, 128.0D);
    }

    private boolean playerInRange = false;
    private EntityPlatformClient clientPlatform = null;

    public void updatePlatformsClient() {
        if (Minecraft.getMinecraft().player == null)
            return;
        EntityPlayerSP player = Minecraft.getMinecraft().player;

        boolean inRange = this.getDistanceSq(player) < 256.0d && player.dimension == this.dimension; // 15x15

        if (this.playerInRange != inRange) {
            this.playerInRange = inRange;
            if (inRange) {
                if (this.clientPlatform == null) {
                    this.clientPlatform = this.addPlatformClient(player, 3);
                } else {
                    this.clientPlatform.setTarget(player, 3);
                }
            } else {
                clientPlatform.removeTarget();
            }
        }
    }

    public EntityPlatform getUnusedPlatform(Entity en, int type) {
        EntityPlatform out = null;
        for (EntityPlatform platform : platforms) {
            if (!platform.hasTarget()) {
                out = platform;
                out.setTarget(en, type);
                return out;
            }
        }
        if (out == null) {
            out = this.addPlatform(en, type);
        }
        return out;
    }

    // Template & stats

    public void applyRaftTemplate(int id) {
        RaftTemplate template = RaftTemplate.raftTemplates.get(id);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.5D); // change later
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue((double)template.getMaxHP());
        this.setSteerAcceleration(template.steerAcc);
        this.setSteeringMult(template.steerMult);
        this.setSpeed(template.speed);
        this.setDrag(template.drag);

        this.interactableHolders = new ArrayList<>();
        for (Interactable i : template.getInteractables()) {
            InteractableHolder en = new InteractableHolder(i);
            this.interactableHolders.add(en);
        }
        this.setLayout(template.layout);
    }

    // Interactables

    public List<EntityInteractable> interactables = new ArrayList<>();
    public List<InteractableHolder> interactableHolders;

    public void updateInteractables() {
        if (this.world.isRemote || !(this.world instanceof WorldServer)) {
            return;
        }
        if (!this.interactableHolders.isEmpty()) {
            for (InteractableHolder ih : this.interactableHolders) {
                // if a holder has no entity, purge list, add new entities as needed
                if (ih.entity == null) {
                    this.interactables.clear();
                    Interactable i = ih.interactable;
                    EntityInteractable en = new EntityInteractable(this.world, this, i.offsetX, i.offsetY, i.offsetZ, i.width, i.height, i.collides, i.type, i.modifier);
                    en.setLocationAndAngles(this.posX, this.posY, this.posZ, 0.0F, 0.0F);
                    ih.entity = en;
                    this.world.spawnEntity(en);
                }
            }
            // if the holder list was cleared or never filled out, refill it
            if (this.interactables.isEmpty()) {
                for (InteractableHolder ih : this.interactableHolders) {
                    if (ih.entity != null) {
                        this.interactables.add(ih.entity);
                    }
                }
            }
            if (!this.interactables.isEmpty()) {
                for (EntityInteractable i : this.interactables) {
                    this.processInteractions(i);
                }
            }
        }
    }

    public void processInteractions(EntityInteractable i) {
        int t = i.getType();
        final int workAdd = 32;

        switch (t) {
            case Types.BARRIER:
                i.interactions = 0;
                return;
            case Types.STEERING:
                if (i.interactions > 0) {
                    i.work+=workAdd;
                    i.interactions = 0;
                }
                if (i.work > 0) {
                    int s = this.getSteeringWheel();
                    s -= i.modifier;
                    if (s > 1000)
                        s = 1000;
                    if (s < -1000)
                        s = -1000;
                    if (this.getSteeringWheel() > 0 && s <= 0) {
                        s = 0;
                        i.work = 0;
                    }
                    if (this.getSteeringWheel() < 0 && s >= 0) {
                        s = 0;
                        i.work = 0;
                    }
                    this.setSteeringWheel(s);
                    i.work/=3;
                    i.work*=2;
                }
                return;
            case Types.SAIL:
                if (i.interactions > 0) {
                    i.work+=workAdd;
                    i.interactions = 0;
                }
                if (i.work > 0) {
                    int s = this.getSails();
                    s += i.modifier;
                    if (s > 1000)
                        s = 1000;
                    if (s < 0)
                        s = 0;
                    this.setSails(s);
                    i.work/=3;
                    i.work*=2;
                }
                return;
        }
    }

    // Raft



    public EntityRaft(World worldIn) {
        super(worldIn);
        this.ignoreFrustumCheck = true;
        this.setSize(3.0F, 0.2F);
        this.entityCollisionReduction = 1.0f;
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(STEERINGWHEEL, 0);
        this.dataManager.register(SAILS, 0);
        this.dataManager.register(STEERACCEL, 0f);
        this.dataManager.register(STEERMULT, 0f);
        this.dataManager.register(SPEED, 0f);
        this.dataManager.register(DRAG, 0f);
        this.dataManager.register(LAYOUT, 0);
    }


    public float turningRate = 0f;

    /**
     * Gets called every tick from main Entity class
     */
    @Override
    public void onUpdate()
    {
        this.updatePlatforms();
        this.updateInteractables();


        // store relative coords of boarded entities
        if (!this.platforms.isEmpty()) {
            for (EntityPlatform en : this.platforms) {
                if (en.hasTarget() && (en.getType() == 1 || (en.getType() == 2 && !this.world.isRemote))) {
                    Entity targ = en.getTarget();
                    if (en.isGrounded(targ)) {
                        en.grounding = momentumTicks;
                    } else {
                        en.grounding--;
                    }
                    if (en.grounding > 0) {
                        Vec2d vec = en.toRelativeCoordinates(targ, this);
                        en.targStoredX = vec.x;
                        en.targStoredZ = vec.y;
                    }
                }
            }
        }
        if (this.world.isRemote && this.clientPlatform != null) {
            if (this.clientPlatform.hasTarget()) {
                Entity targ = this.clientPlatform.getTarget();
                if (this.clientPlatform.isGrounded(targ)) {
                    this.clientPlatform.grounding = momentumTicks;
                } else {
                    this.clientPlatform.grounding--;
                }
                if (this.clientPlatform.grounding > 0) {
                    Vec2d vec = this.clientPlatform.toRelativeCoordinates(targ, this);
                    this.clientPlatform.targStoredX = vec.x;
                    this.clientPlatform.targStoredZ = vec.y;
                }
            }
        }


        // extra stuff
        this.prevMovedDistance = this.movedDistance;
        this.prevRenderYawOffset = this.renderYawOffset;
        this.prevRotationYaw = this.rotationYaw;
        this.prevRotationPitch = this.rotationPitch;
        this.newPosRotationIncrements = 0;
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        // handle boat movement

        float steeringTarget = ((float)this.getSteeringWheel()) * 0.001f;
        if (this.turningRate > steeringTarget) {
            this.turningRate -= this.getSteerAcceleration();
            if (this.turningRate < steeringTarget)
                this.turningRate = steeringTarget;
        } else if (this.turningRate < steeringTarget) {
            this.turningRate += this.getSteerAcceleration();
            if (this.turningRate > steeringTarget)
                this.turningRate = steeringTarget;
        }

        float yawChange = 0;

        double dX = 0d;
        double dY = 0d;
        double dZ = 0d;

        if (this.world.isRemote) {
            double debtMult = syncDebtPayment;
            double debtClear = 1d - debtMult;
            this.ticksSinceLastSync++;

            log.info("*Raft *{}* debt synced, adding x*{}* y*{}* z*{}* mX*{}* mY*{}* mZ*{}* yaw*{}* turning*{}",
                    this.getEntityId(),
                    this.debtX*debtMult,
                    this.debtY*debtMult,
                    this.debtZ*debtMult,
                    this.debtMotionX*debtMult,
                    this.debtMotionY*debtMult,
                    this.debtMotionZ*debtMult,
                    this.debtTurning*debtMult,
                    this.debtYaw*debtMult
            );

            dX += this.debtX*debtMult;
            dY += this.debtY*debtMult;
            dZ += this.debtZ*debtMult;
            yawChange += (float)(this.debtYaw*debtMult);
            this.motionX += this.debtMotionX*debtMult;
            this.motionY += this.debtMotionY*debtMult;
            this.motionZ += this.debtMotionZ*debtMult;
            this.turningRate += (float)(this.debtTurning*debtMult);

            this.debtX = this.debtX*debtClear;
            this.debtY = this.debtY*debtClear;
            this.debtZ = this.debtZ*debtClear;
            this.debtMotionX = this.debtMotionX*debtClear;
            this.debtMotionY = this.debtMotionY*debtClear;
            this.debtMotionZ = this.debtMotionZ*debtClear;
            this.debtTurning = this.debtTurning*debtClear;
            this.debtYaw = this.debtYaw*debtClear;
        }

        yawChange += this.turningRate;

        float drag = this.getDrag();
        this.motionX *= drag;
        this.motionY *= drag;
        this.motionZ *= drag;

        float speedMult = 1.0f;
        float sails = ((float)this.getSails()) * 0.001f;
        float speed = this.getSpeed() * speedMult * sails;

        float f = this.rotationYaw * 0.017453292F;
        this.motionX -= (double)(MathHelper.sin(f) * speed);
        this.motionZ += (double)(MathHelper.cos(f) * speed);

        double yChange = this.motionY;

        this.rotationYaw = this.rotationYaw + yawChange;
        //this.interpTargetYaw = this.rotationYaw;
        this.setRotation(this.rotationYaw, this.rotationPitch);
        this.renderYawOffset = this.rotationYaw;

        // resync
        if (!this.world.isRemote && this.ticksExisted % 2 == 0) {
            new RaftSyncPacket(this.getEntityId(), this.posX, this.posY, this.posZ, (double) this.rotationYaw, this.motionX, this.motionY, this.motionZ, (double) this.turningRate).sendPacketToAllAround(this.world, this.posX, this.posY, this.posZ, 256.0d);

            log.info("*Raft *{}* debt packet sent with x*{}* y*{}* z*{}* mX*{}* mY*{}* mZ*{}* yaw*{}* turning*{}",
                    this.getEntityId(),
                    this.posX,
                    this.posY,
                    this.posZ,
                    this.motionX,
                    this.motionY,
                    this.motionZ,
                    (double) this.rotationYaw,
                    (double) this.turningRate
            );
        }

        this.move(MoverType.SELF, this.motionX+dX, this.motionY+dY, this.motionZ+dZ );

        final float velocityMult = 1.098f;
        // convert relative coords back to real coords for stored entities and move them to new positions
        if (!this.platforms.isEmpty()) {
            for (EntityPlatform en : this.platforms) {
                if (en.grounding > 0 && (en.getType() == 1 || (en.getType() == 2 && !this.world.isRemote)) && en.hasTarget()) {
                    Entity targ = en.getTarget();
                    Vec2d vec = en.fromRelativeCoordinates(en.targStoredX, en.targStoredZ, this);
                    if (en.grounding == momentumTicks-1)
                        targ.addVelocity((vec.x - targ.posX)*velocityMult, (yChange)*velocityMult, (vec.y - targ.posZ)*velocityMult);
                    if (en.grounding >= momentumTicks-1) {
                        targ.setPositionAndRotation(vec.x, targ.posY + yChange, vec.y, targ.rotationYaw + yawChange, targ.rotationPitch);
                        if (!targ.noClip)
                        {
                            if ((!targ.onGround || !targ.isSneaking() || !(targ instanceof EntityPlayer)) && !targ.isRiding())
                            {
                                if (targ.distanceWalkedOnStepModified > (float)(en.stepTracker))
                                {
                                    en.stepTracker = (int)targ.distanceWalkedOnStepModified + 1;
                                    SoundType soundtype = Blocks.PLANKS.getSoundType();
                                    targ.playSound(soundtype.getStepSound(), soundtype.getVolume() * 0.15F, soundtype.getPitch());
                                }
                            }
                        }
                    }
                }
            }
        }
        if (this.world.isRemote && this.clientPlatform != null) {
            if (this.clientPlatform.grounding > 0 && this.clientPlatform.hasTarget()) {
                Entity targ = this.clientPlatform.getTarget();
                Vec2d vec = this.clientPlatform.fromRelativeCoordinates(this.clientPlatform.targStoredX, this.clientPlatform.targStoredZ, this);
                if (this.clientPlatform.grounding == momentumTicks-1)
                    targ.addVelocity((vec.x - targ.posX)*velocityMult, (yChange)*velocityMult, (vec.y - targ.posZ)*velocityMult);
                if (this.clientPlatform.grounding > 0)
                    targ.rotationYaw = targ.rotationYaw + (yawChange * ((float)this.clientPlatform.grounding) / (float)momentumTicks);
                if (this.clientPlatform.grounding >= momentumTicks-1) {
                    targ.setPosition(vec.x, targ.posY + yChange, vec.y);
                    if (!targ.noClip)
                    {
                        if ((!targ.onGround || !targ.isSneaking() || !(targ instanceof EntityPlayer)) && !targ.isRiding())
                        {
                            if (targ.distanceWalkedOnStepModified > (float)(this.clientPlatform.stepTracker))
                            {
                                this.clientPlatform.stepTracker = (int)targ.distanceWalkedOnStepModified + 1;
                                SoundType soundtype = Blocks.PLANKS.getSoundType();
                                targ.playSound(soundtype.getStepSound(), soundtype.getVolume() * 0.15F, soundtype.getPitch());
                            }
                        }
                    }

                }
            }
        }


    }

    public final static double syncDebt = 0.3d;
    public final static double syncDebtPayment = 0.3f;
    public int ticksSinceLastSync = 0;
    public double debtX = 0d;
    public double debtY = 0d;
    public double debtZ = 0d;
    public double debtYaw = 0d;
    public double debtMotionX = 0d;
    public double debtMotionY = 0d;
    public double debtMotionZ = 0d;
    public double debtTurning = 0d;

    @Override
    @SideOnly(Side.CLIENT)
    public void setPositionAndRotationDirect(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean teleport)
    {
        if (teleport) {
            super.setPositionAndRotationDirect(x, y, z, yaw, pitch, posRotationIncrements, true);
            this.debtX = 0d;
            this.debtY = 0d;
            this.debtZ = 0d;
            this.debtYaw = 0d;
            this.debtMotionX = 0d;
            this.debtMotionY = 0d;
            this.debtMotionZ = 0d;
            this.debtTurning = 0d;
        }
    }

    public void sync(double x, double y, double z, double yaw, double mX, double mY, double mZ, double turning)
    {
        double debtMult = Math.min(syncDebt*(double)ticksSinceLastSync, 1.0d);
        double debtClear = 1d - debtMult;
        this.ticksSinceLastSync = 0;

        this.debtX = this.debtX*debtClear + (x - this.posX)*debtMult;
        this.debtY = this.debtY*debtClear + (y - this.posY)*debtMult;
        this.debtZ = this.debtZ*debtClear + (z - this.posZ)*debtMult;
        this.debtYaw = this.debtYaw*debtClear + (yaw - this.rotationYaw)*debtMult;
        this.debtMotionX = this.debtMotionX*debtClear + (mX - this.motionX)*debtMult;
        this.debtMotionY = this.debtMotionY*debtClear + (mY - this.motionY)*debtMult;
        this.debtMotionZ = this.debtMotionZ*debtClear + (mZ - this.motionZ)*debtMult;
        this.debtTurning = this.debtTurning*debtClear + (turning - this.turningRate)*debtMult;

        log.info("*Raft *{}* debt accrued, setting debt to x*{}* y*{}* z*{}* mX*{}* mY*{}* mZ*{}* yaw*{}* turning*{}",
                this.getEntityId(),
                this.debtX,
                this.debtY,
                this.debtZ,
                this.debtMotionX,
                this.debtMotionY,
                this.debtMotionZ,
                this.debtTurning,
                this.debtYaw
        );
    }
    

    @Override
    @SideOnly(Side.CLIENT)
    public void setVelocity(double x, double y, double z)
    {
        this.motionX = x;
        this.motionY = y;
        this.motionZ = z;
    }
    @Override
    public void setPositionAndRotation(double x, double y, double z, float yaw, float pitch)
    {
        /*
        this.posX = MathHelper.clamp(x, -3.0E7D, 3.0E7D);
        this.posY = y;
        this.posZ = MathHelper.clamp(z, -3.0E7D, 3.0E7D);
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        pitch = MathHelper.clamp(pitch, -90.0F, 90.0F);
        this.rotationYaw = yaw;
        this.rotationPitch = pitch;
        this.prevRotationYaw = this.rotationYaw;
        this.prevRotationPitch = this.rotationPitch;
        double d0 = (double)(this.prevRotationYaw - yaw);

        if (d0 < -180.0D)
        {
            this.prevRotationYaw += 360.0F;
        }

        if (d0 >= 180.0D)
        {
            this.prevRotationYaw -= 360.0F;
        }

        if (!this.world.isRemote)
            this.world.getChunk((int) Math.floor(this.posX) >> 4, (int) Math.floor(this.posZ) >> 4); // Forge - ensure target chunk is loaded.
        this.setPosition(this.posX, this.posY, this.posZ);
        this.setRotation(yaw, pitch);

         */
    }


    @Override
    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.applyRaftTemplate(0);
    }

    @Override
    protected boolean canTriggerWalking()
    {
        return false;
    }

    /**
     * Returns a boundingBox used to collide the entity with other entities and blocks. This enables the entity to be
     * pushable on contact, like boats or minecarts.
     */
    @Nullable
    public AxisAlignedBB getCollisionBox(Entity entityIn)
    {
        //return entityIn.canBePushed() ? entityIn.getEntityBoundingBox() : null;
        return null;
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
        return this.getEntityBoundingBox();
    }

    /**
     * Returns true if other Entities should be prevented from moving through this Entity.
     */
    public boolean canBeCollidedWith()
    {
        return !this.isDead;
    }

    @Override
    protected boolean canDespawn()
    {
        return false;
    }


    @Override
    public boolean processInteract(EntityPlayer player, EnumHand hand) {
        return super.processInteract(player, hand);
    }

    @Override
    public void travel(float strafe, float vertical, float forward) {
        if (this.isEntityAlive()) {
            //log.info("Raft {} rotated to {}", this.getEntityId(), this.rotationYaw);
            //super.travel(0f, 0f, 0f);
            //super.travel(f, vertical, f1);
            /*
            if (this.isBeingRidden()) {
                EntityLivingBase livingentity = (EntityLivingBase) this.getControllingPassenger();
                this.rotationYaw = livingentity.rotationYaw;
                this.prevRotationYaw = this.rotationYaw;
                this.rotationPitch = livingentity.rotationPitch * 0.5F;
                this.setRotation(this.rotationYaw, this.rotationPitch);
                this.renderYawOffset = this.rotationYaw;
                this.rotationYawHead = this.renderYawOffset;
                float f = livingentity.moveStrafing * 0.5F;
                float f1 = livingentity.moveForward;
                if (f1 <= 0.0F) {
                    f1 *= 0.25F;
                }

                this.setAIMoveSpeed(0.3F);
                super.travel(f, vertical, f1);
            }
             */
        }
    }

}