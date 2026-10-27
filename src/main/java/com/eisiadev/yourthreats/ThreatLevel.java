package com.eisiadev.yourthreats;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;

import java.util.Locale;

public enum ThreatLevel {
    CASUAL("Casual", 0.5, 0.5, 1.0, 1.0, 0.5, 1.0, 1.0, 0.0, false, ChatFormatting.AQUA),
    ADVENTURE("Adventure", 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 0.0, false, ChatFormatting.GREEN),
    EXPERT("Expert", 2.5, 2.0, 1.0, 1.0, 1.0, 1.0, 1.1, 0.05, false, ChatFormatting.YELLOW),
    EXTREME("Extreme", 4.0, 3.0, 1.5, 1.5, 1.1, 1.0, 1.2, 0.1, false, ChatFormatting.GOLD),
    VENGEANCE("Vengeance", 6.0, 4.0, 1.8, 1.8, 1.2, 1.0, 1.35, 0.16, false, ChatFormatting.RED),
    DEATH("Death", 9.0, 5.0, 2.3, 2.3, 1.3, 1.1, 1.5, 0.23, false, ChatFormatting.DARK_RED),
    MALICE("Malice", 13.0, 6.0, 3.0, 2.8, 1.4, 1.1, 1.7, 0.31, false, ChatFormatting.DARK_PURPLE),
    MENACE("Menace", 18.0, 7.0, 3.5, 3.25, 1.5, 1.2, 1.9, 0.4, true,
            ChatFormatting.DARK_RED, ChatFormatting.RED),
    CATASTROPHIC("Catastrophic", 25.0, 8.0, 3.8, 3.8, 1.6, 1.25, 2.15, 0.5, true,
            ChatFormatting.DARK_PURPLE, ChatFormatting.DARK_RED, ChatFormatting.LIGHT_PURPLE),
    MASOCHIST("Masochist", 35.0, 9.0, 4.3, 4.15, 1.8, 1.3, 2.45, 0.61, true,
            ChatFormatting.AQUA, ChatFormatting.DARK_AQUA, ChatFormatting.BLUE),
    PSYCHOSIS("Psychosis", 50.0, 10.0, 5.0, 5.0, 2.0, 1.5, 2.8, 0.73, true,
            ChatFormatting.RED, ChatFormatting.AQUA);

    public static final ThreatLevel DEFAULT = ADVENTURE;

    private static final double GLOW_SEGMENT_MS = 900.0;
    private static final double GLOW_SPREAD = 1.0;

    private final String displayName;
    public final double health;
    public final double damage;
    public final double armor;
    public final double toughness;
    public final double dropChance;
    public final double dropMultiplier;
    public final double spawnRate;
    public final double damageReduction;
    private final boolean bold;
    private final int[] rgb;

    ThreatLevel(String displayName, double health, double damage, double armor, double toughness,
                double dropChance, double dropMultiplier, double spawnRate, double damageReduction, boolean bold, ChatFormatting... colors) {
        this.displayName = displayName;
        this.health = health;
        this.damage = damage;
        this.armor = armor;
        this.toughness = toughness;
        this.dropChance = dropChance;
        this.dropMultiplier = dropMultiplier;
        this.spawnRate = spawnRate;
        this.damageReduction = damageReduction;
        this.bold = bold;
        this.rgb = new int[colors.length];
        for (int i = 0; i < colors.length; i++) {
            Integer c = colors[i].getColor();
            this.rgb[i] = c != null ? c : 0xFFFFFF;
        }
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isGlowing() {
        return rgb.length > 1;
    }

    public MutableComponent getName() {
        return isGlowing() ? gradient(0.0) : styled(rgb[0]);
    }

    public MutableComponent getAnimatedName(long timeMs) {
        return isGlowing() ? gradient(timeMs / GLOW_SEGMENT_MS) : styled(rgb[0]);
    }

    private MutableComponent styled(int color) {
        return Component.literal(displayName)
                .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(color)).withBold(bold));
    }

    private MutableComponent gradient(double phase) {
        MutableComponent out = Component.empty();
        int n = rgb.length;
        int len = displayName.length();
        for (int i = 0; i < len; i++) {
            double t = phase - (double) i / len * GLOW_SPREAD;
            t = ((t % n) + n) % n;
            int k = (int) t;
            double f = smooth(t - k);
            int color = lerp(rgb[k], rgb[(k + 1) % n], f);
            out.append(Component.literal(String.valueOf(displayName.charAt(i)))
                    .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(color)).withBold(bold)));
        }
        return out;
    }

    private static double smooth(double x) {
        return x * x * (3 - 2 * x);
    }

    private static int lerp(int a, int b, double f) {
        int r = (int) Math.round(((a >> 16) & 0xFF) + (((b >> 16) & 0xFF) - ((a >> 16) & 0xFF)) * f);
        int g = (int) Math.round(((a >> 8) & 0xFF) + (((b >> 8) & 0xFF) - ((a >> 8) & 0xFF)) * f);
        int bl = (int) Math.round((a & 0xFF) + ((b & 0xFF) - (a & 0xFF)) * f);
        return (r << 16) | (g << 8) | bl;
    }

    public MutableComponent getStats() {
        return Component.translatable("yourthreats.command.stats",
                x(health), x(damage), x(armor), x(toughness), x(dropChance), x(dropMultiplier),
                x(spawnRate), Math.round(damageReduction * 100) + "%");
    }

    private static String x(double v) {
        return (v == Math.floor(v) ? String.valueOf((long) v) : String.valueOf(v)) + "x";
    }

    public static ThreatLevel byName(String name) {
        try {
            return valueOf(name.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static ThreatLevel byOrdinal(int ordinal) {
        ThreatLevel[] values = values();
        return ordinal >= 0 && ordinal < values.length ? values[ordinal] : DEFAULT;
    }
}
