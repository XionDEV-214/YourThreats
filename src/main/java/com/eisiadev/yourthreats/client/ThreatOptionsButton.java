package com.eisiadev.yourthreats.client;

import com.eisiadev.yourthreats.ClientThreatState;
import com.eisiadev.yourthreats.ThreatLevel;
import com.eisiadev.yourthreats.YourThreats;
import com.eisiadev.yourthreats.network.ThreatNetwork;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.OptionsScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = YourThreats.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class ThreatOptionsButton {
    private static final Component LABEL = Component.translatable("yourthreats.options.threat");
    private static final int WIDTH = 150;
    private static final int HEIGHT = 20;
    private static final int ROW_SPACING = 24;

    private static CycleButton<ThreatLevel> button;

    private ThreatOptionsButton() {}

    @SubscribeEvent
    public static void onScreenInit(ScreenEvent.Init.Post event) {
        button = null;
        if (!(event.getScreen() instanceof OptionsScreen screen)) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) return;

        AbstractWidget anchor = null;
        for (GuiEventListener listener : event.getListenersList()) {
            if (listener instanceof AbstractWidget w
                    && (isOption(w.getMessage(), "options.difficulty") || isOption(w.getMessage(), "options.online"))) {
                if (anchor == null || isOption(w.getMessage(), "options.difficulty")) anchor = w;
            }
        }
        int x = anchor != null ? anchor.getX() : screen.width / 2 + 5;
        int y = (anchor != null ? anchor.getY() : screen.height / 6 - 12) + ROW_SPACING;

        boolean canEdit = mc.hasSingleplayerServer() || mc.player.hasPermissions(2);

        button = CycleButton.<ThreatLevel>builder(ThreatLevel::getName)
                .withValues(ThreatLevel.values())
                .withInitialValue(ClientThreatState.level)
                .withTooltip(level -> Tooltip.create(tooltip(level, canEdit)))
                .create(x, y, WIDTH, HEIGHT, LABEL, (btn, value) -> {
                    ClientThreatState.level = value;
                    ThreatNetwork.sendToServer(value);
                });
        button.active = canEdit;
        event.addListener(button);
    }

    @SubscribeEvent
    public static void onScreenRender(ScreenEvent.Render.Pre event) {
        if (button == null || !(event.getScreen() instanceof OptionsScreen)) return;
        ThreatLevel synced = ClientThreatState.level;
        if (button.getValue() != synced) button.setValue(synced);
        ThreatLevel value = button.getValue();
        if (value.isGlowing()) {
            button.setMessage(CommonComponents.optionNameValue(LABEL, value.getAnimatedName(Util.getMillis())));
        }
    }

    private static Component tooltip(ThreatLevel l, boolean canEdit) {
        MutableComponent c = Component.translatable("yourthreats.options.threat.tooltip").append("\n")
                .append(l.getStats().withStyle(ChatFormatting.GRAY));
        if (!canEdit) {
            c.append("\n").append(Component.translatable("yourthreats.options.threat.no_permission")
                    .withStyle(ChatFormatting.RED));
        }
        return c;
    }

    private static boolean isOption(Component message, String key) {
        if (!(message.getContents() instanceof TranslatableContents tc)) return false;
        if (tc.getKey().equals(key)) return true;
        if (tc.getKey().equals("options.generic_value") && tc.getArgs().length > 0
                && tc.getArgs()[0] instanceof Component name
                && name.getContents() instanceof TranslatableContents nameTc) {
            return nameTc.getKey().equals(key);
        }
        return false;
    }
}
