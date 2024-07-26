package com.bluefire.rafts;

import com.bluefire.rafts.client.render.RenderRaft;
import com.bluefire.rafts.entity.EntityInteractable;
import com.bluefire.rafts.entity.EntityPlatform;
import com.bluefire.rafts.entity.EntityPlatformClient;
import com.bluefire.rafts.entity.EntityRaft;
import lombok.extern.log4j.Log4j2;

import static com.bluefire.rafts.Rafts.*;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import software.bernie.geckolib3.GeckoLib;

@Mod(
        modid = MODID,
        name = "Rafts",
        version = "1.0",
        dependencies = "required-after:mixinbooter@[8.0,);after:geckolib3;"
)
@Log4j2
public class Rafts {

    public static final String MODID = "rafts";

    @Mod.Instance(MODID)
    public static Rafts instance;

    public Rafts() {
        GeckoLib.initialize();
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        EntityRegistry.registerModEntity(new ResourceLocation(Rafts.MODID, "raft"), EntityRaft.class, "Raft", 1, Rafts.instance, 256, 1, false, 9804699, 0x582827);
        EntityRegistry.registerModEntity(new ResourceLocation(Rafts.MODID, "platform"), EntityPlatform.class, "Platform", 2, Rafts.instance, 256, 1, false, 9804699, 0x582827);
        EntityRegistry.registerModEntity(new ResourceLocation(Rafts.MODID, "platformclient"), EntityPlatformClient.class, "PlatformClient", 3, Rafts.instance, 256, 1, false, 9804699, 0x582827);
        EntityRegistry.registerModEntity(new ResourceLocation(Rafts.MODID, "interactable"), EntityInteractable.class, "Interactable", 4, Rafts.instance, 256, 1, false, 9804699, 0x582827);

        if (event.getSide().isClient()) {
            RenderingRegistry.registerEntityRenderingHandler(EntityRaft.class, RenderRaft::new);
        }
    }

    @EventHandler
    public void initRegistries(FMLInitializationEvent event)
    {
    }

}
