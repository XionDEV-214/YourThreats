package com.eisiadev.yourthreats;

import net.minecraftforge.common.ForgeConfigSpec;

public final class ThreatConfig {
    public static final ForgeConfigSpec CLIENT_SPEC;
    public static final ForgeConfigSpec.BooleanValue SHOW_HUD;
    public static final ForgeConfigSpec.IntValue HUD_X;
    public static final ForgeConfigSpec.IntValue HUD_Y;

    static {
        ForgeConfigSpec.Builder b = new ForgeConfigSpec.Builder();
        b.push("hud");
        SHOW_HUD = b.comment("Show the current threat level on screen").define("showHud", true);
        HUD_X = b.comment("HUD X position (pixels from the left)").defineInRange("x", 4, 0, 10000);
        HUD_Y = b.comment("HUD Y position (pixels from the top)").defineInRange("y", 4, 0, 10000);
        b.pop();
        CLIENT_SPEC = b.build();
    }

    private ThreatConfig() {}
}
