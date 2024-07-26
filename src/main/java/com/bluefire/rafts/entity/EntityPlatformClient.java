package com.bluefire.rafts.entity;

import com.bluefire.rafts.data.Vec2d;
import lombok.extern.log4j.Log4j2;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;


@Log4j2
public class EntityPlatformClient extends EntityPlatformBase {

    private Entity target;
    private Entity host;
    private int type;
    private int layout;

    private Entity prevHost = null;
    private Entity prevTarget = null;

    private static int nextEntityIDClient = 16777216;


    public EntityPlatformClient(World worldIn)
    {
        super(worldIn);
        nextEntityID--; // de-incrementing next entity ID to maintain parity with the server.
        nextEntityIDClient++;
        this.setEntityId(nextEntityIDClient);
    }

    public EntityPlatformClient(World worldIn, Entity hostIn)
    {
        this(worldIn);
        this.host = hostIn;
    }

    public EntityPlatformClient(World worldIn, Entity hostIn, Entity targIn, int typeIn)
    {
        this(worldIn, hostIn);
        this.target = targIn;
        this.type = typeIn;
        if (debugInfo)
            log.info("Platform {} created", this.getEntityId());
    }

    @Override
    protected void entityInit() {
    }

    @Override
    public Entity getHost() {
        return this.host;
    }

    @Override
    public void removeTarget() {
        this.target = null;
        this.type = 0;
    }

    @Override
    public void setTarget(Entity targetIn, int typeIn) {
        this.target = targetIn;
        this.type = typeIn;
    }

    @Override
    public Entity getTarget() {
        return this.target;
    }

    @Override
    public int getType() {
        return this.type;
    }

    @Override
    public int getLayout() {
        return this.layout;
    }

    @Override
    public void setPlatformLayout(int id) {
        this.layout = id;
    }


    @Override
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
        if (!this.hasHost() && this.world.isRemote) {
            this.delete();
        }
    }

    @Override
    public void updateCollisionBox(Entity targ, Entity host)
    {
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

        //set collision box
        if (!newbox.intersects(targ.getEntityBoundingBox()) || !bounded) {
            this.setCollisionBoundingBox(newbox);
            if (debugInfo && this.getType() == 3) {
                log.info("*Platform *{}* box changed to x*{}* y*{}* z*{}* on tick *{}*, target coords x*{}* y*{}* z*{}*, host coords x*{}* y*{}* z*{}*, forced *{}", this.getEntityId(), x, y+h, z, this.world.getWorldTime(), targ.posX, targ.posY, targ.posZ, host.posX, host.posY, host.posZ, false );
                //this.world.updateEntityWithOptionalForce(this, false);
            }
        }
    }



}