package com.bluefire.rafts.client.render;

import com.bluefire.rafts.Rafts;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

import javax.annotation.Nullable;

@SideOnly(Side.CLIENT)
public class RenderRaft extends GeoEntityRenderer
{
    private static final ResourceLocation textureResource = new ResourceLocation(Rafts.MODID, "textures/entity/raft.png");

    public RenderRaft(RenderManager renderManager)
    {
        super(renderManager, new com.bluefire.rafts.client.model.ModelRaft());
        this.shadowSize = 0.3f;
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return textureResource;
    }
}