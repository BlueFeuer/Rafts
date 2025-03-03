package com.bluefire.rafts;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@SuppressWarnings("WeakerAccess")
@Config(modid = Rafts.MODID)
@Mod.EventBusSubscriber(modid = Rafts.MODID)
public class RaftsConfig {
    private final static String config = Rafts.MODID + ".config.";

    @Config.LangKey(config + "raft_layouts")
    @Config.Comment("Individual boat configurations")
    public static String[] RAFT_LAYOUTS = new String[]{
            "-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,0,0,0,0,0,0,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,0,0,0,0,0,0,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,0,0,0,0,0,0,0,0,0,0,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,0,0,0,0,0,0,0,0,0,0,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,0,0,0,0,0,0,0,0,0,0,0,0,0,0,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,0,0,0,0,0,0,0,0,0,0,0,0,0,0,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,0,0,0,0,0,0,0,0,0,0,0,0,0,0,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,0,0,0,0,0,0,0,0,0,0,0,0,0,0,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,0,0,0,0,0,0,0,0,0,0,0,0,0,0,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,0,0,0,0,0,0,0,0,0,0,0,0,0,0,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,0,0,0,0,0,0,0,0,0,0,0,0,0,0,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,0,0,0,0,0,0,0,0,0,0,0,0,0,0,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,0,0,0,0,0,0,0,0,0,0,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,0,0,0,0,0,0,0,0,0,0,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,0,0,0,0,0,0,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,0,0,0,0,0,0,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127,-127"
    };

    @Config.LangKey(config + "lean_angle_start")
    @Config.RangeDouble(min = -90.0d, max = 90d)
    @Config.Comment("What angle in degrees leaning starts at, with 0 being flat and values above that being up.")
    public static double LEAN_ANGLE_START = 20.0d;

    @Config.RequiresMcRestart
    @Config.LangKey(config + "enable_sprinting")
    @Config.Comment("Whether or not sprinting should be enabled.")
    public static boolean ENABLE_SPRINTING = true;

    @SubscribeEvent
    public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals(Rafts.MODID)) {
            ConfigManager.sync(Rafts.MODID, Config.Type.INSTANCE);
        }
    }
}