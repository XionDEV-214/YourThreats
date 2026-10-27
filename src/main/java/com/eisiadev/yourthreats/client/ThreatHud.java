package com.eisiadev.yourthreats.client;

import com.eisiadev.yourthreats.ClientThreatState;
import com.eisiadev.yourthreats.ThreatConfig;
import com.eisiadev.yourthreats.YourThreats;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

public final class ThreatHud {
    private ThreatHud() {}

    @Mod.EventBusSubscriber(modid = YourThreats.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static final class ModEvents {
        @SubscribeEvent
        public static void registerOverlays(RegisterGuiOverlaysEvent event) {
            event.registerAboveAll("threat_level", (gui, graphics, partialTick, width, height) -> {
                Minecraft mc = Minecraft.getInstance();
                if (!ThreatConfig.SHOW_HUD.get() || mc.options.hideGui || mc.options.renderDebug) return;
                Component text = Component.translatable("yourthreats.hud.label").withStyle(ChatFormatting.GRAY)
                        .append(ClientThreatState.level.getAnimatedName(Util.getMillis()));
                graphics.drawString(mc.font, text, ThreatConfig.HUD_X.get(), ThreatConfig.HUD_Y.get(), 0xFFFFFF, true);
            });
        }
    }

    @Mod.EventBusSubscriber(modid = YourThreats.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static final class ForgeEvents {
        @SubscribeEvent
        public static void onLogout(ClientPlayerNetworkEvent.LoggingOut event) {
            ClientThreatState.level = com.eisiadev.yourthreats.ThreatLevel.DEFAULT;
        }
    }
}
