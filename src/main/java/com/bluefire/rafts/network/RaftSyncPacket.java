package com.bluefire.rafts.network;

import com.bluefire.rafts.entity.EntityRaft;
import hohserg.elegant.networking.api.ElegantPacket;
import hohserg.elegant.networking.api.ServerToClientPacket;
import lombok.Value;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

@ElegantPacket
@Value
public class RaftSyncPacket implements ServerToClientPacket {

    int entityID;
    double x;
    double y;
    double z;
    double yaw;
    double motionX;
    double motionY;
    double motionZ;
    double turningRate;


    @Override
    public void onReceive(Minecraft mc) {
        World world = mc.player.world;
        Entity en = world.getEntityByID(entityID);
        if (en instanceof EntityRaft) {
            ((EntityRaft) en).sync(x, y, z, yaw, motionX, motionY, motionZ, turningRate);
        }
    }

}