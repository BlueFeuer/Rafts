package com.bluefire.rafts.entity;

import com.bluefire.rafts.data.RaftTemplate;
import lombok.extern.log4j.Log4j2;
import net.minecraft.entity.*;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.world.World;

@Log4j2
public class EntityPlatform extends EntityPlatformBase {

    private static final DataParameter<Integer> HOST = EntityDataManager.<Integer>createKey(EntityPlatform.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> TARGET = EntityDataManager.<Integer>createKey(EntityPlatform.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> TYPE = EntityDataManager.<Integer>createKey(EntityPlatform.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> LAYOUT = EntityDataManager.<Integer>createKey(EntityPlatform.class, DataSerializers.VARINT);

    //raft movement information
    public int grounding = 0;
    public int stepTracker = 0;
    public double targStoredX = 0d;
    public double targStoredZ = 0d;

    public EntityPlatform(World worldIn)
    {
        super(worldIn);
    }

    public EntityPlatform(World worldIn, Entity hostIn)
    {
        this(worldIn);
        this.dataManager.set(HOST, hostIn.getEntityId());
    }

    public EntityPlatform(World worldIn, Entity hostIn, Entity targIn, int typeIn)
    {
        this(worldIn, hostIn);
        this.dataManager.set(HOST, hostIn.getEntityId());
        this.dataManager.set(TARGET, targIn.getEntityId());
        this.dataManager.set(TYPE, typeIn);
        if (debugInfo)
            log.info("Platform {} created", this.getEntityId());
    }

    @Override
    protected void entityInit() {
        this.dataManager.register(HOST, -1);
        this.dataManager.register(TARGET, -1);
        this.dataManager.register(TYPE, 1);
        this.dataManager.register(LAYOUT, 0);
    }

    @Override
    public Entity getHost() {
        int val = (Integer) this.dataManager.get(HOST);
        return val == -1 ? null : this.world.getEntityByID(val);
    }
    @Override
    public void removeTarget() {
        this.dataManager.set(TARGET, -1);
        this.dataManager.set(TYPE, 0);
    }

    @Override
    public void setTarget(Entity targetIn, int typeIn) {
        this.dataManager.set(TARGET, targetIn.getEntityId());
        this.dataManager.set(TYPE, typeIn);
    }

    @Override
    public Entity getTarget() {
        int val = (Integer) this.dataManager.get(TARGET);
        return val == -1 ? null : this.world.getEntityByID(val);
    }

    @Override
    public int getType() {
        return (int)this.dataManager.get(TYPE).intValue();
    }

    @Override
    public int getLayout() {
        return (int)this.dataManager.get(LAYOUT).intValue();
    }

    @Override
    public void setPlatformLayout(int id) {
        this.dataManager.set(LAYOUT, id);
    }

    @Override
    public float[] getPlatformLayout() {
        return RaftTemplate.layouts.get(this.getLayout()).layout;
    }


}