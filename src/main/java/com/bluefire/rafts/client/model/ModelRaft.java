package com.bluefire.rafts.client.model;

import com.bluefire.rafts.Rafts;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;


public class ModelRaft extends AnimatedGeoModel<com.bluefire.rafts.entity.EntityRaft> {
	private static final ResourceLocation modelResource = new ResourceLocation(Rafts.MODID, "geo/raft.geo.json");
	private static final ResourceLocation textureResource = new ResourceLocation(Rafts.MODID, "textures/entity/raft.png");
	private static final ResourceLocation animationResource = new ResourceLocation(Rafts.MODID, "animations/raft.animation.json");

	@Override
	public ResourceLocation getModelLocation(com.bluefire.rafts.entity.EntityRaft object) {
		return modelResource;
	}

	@Override
	public ResourceLocation getTextureLocation(com.bluefire.rafts.entity.EntityRaft object) {
		return textureResource;
	}

	@Override
	public ResourceLocation getAnimationFileLocation(com.bluefire.rafts.entity.EntityRaft object) {
		return animationResource;
	}
}