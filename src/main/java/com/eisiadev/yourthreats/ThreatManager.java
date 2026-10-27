package com.eisiadev.yourthreats;

import com.eisiadev.yourthreats.network.ThreatNetwork;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Enemy;

import java.util.UUID;

public final class ThreatManager {
    private static final UUID HEALTH_ID = UUID.fromString("6f1d2a0e-7b3c-4b8e-9a51-1c0f2e9d4a01");
    private static final UUID ARMOR_ID = UUID.fromString("6f1d2a0e-7b3c-4b8e-9a51-1c0f2e9d4a02");
    private static final UUID TOUGHNESS_ID = UUID.fromString("6f1d2a0e-7b3c-4b8e-9a51-1c0f2e9d4a03");

    private ThreatManager() {}

    public static ThreatLevel getLevel(MinecraftServer server) {
        ThreatLevel level = ThreatSavedData.get(server).getLevel();
        ActiveThreat.set(level);
        return level;
    }

    public static void resetCache(MinecraftServer server) {
        ActiveThreat.set(server != null ? ThreatSavedData.get(server).getLevel() : ThreatLevel.DEFAULT);
    }

    public static void setLevel(MinecraftServer server, ThreatLevel level) {
        ThreatSavedData.get(server).setLevel(level);
        ActiveThreat.set(level);
        for (ServerLevel world : server.getAllLevels()) {
            for (Entity entity : world.getAllEntities()) {
                if (entity instanceof LivingEntity living && isHostile(living)) {
                    apply(living, level);
                }
            }
        }
        ThreatNetwork.sendToAll(level);
    }

    public static boolean isHostile(Entity entity) {
        return entity instanceof Enemy;
    }

    public static void apply(LivingEntity entity, ThreatLevel level) {
        float oldMax = entity.getMaxHealth();
        setModifier(entity, Attributes.MAX_HEALTH, HEALTH_ID, "YourThreats health", level.health);
        float newMax = entity.getMaxHealth();
        if (oldMax > 0 && newMax != oldMax && entity.isAlive()) {
            entity.setHealth(entity.getHealth() * (newMax / oldMax));
        }

        setModifier(entity, Attributes.ARMOR, ARMOR_ID, "YourThreats armor", level.armor);
        setModifier(entity, Attributes.ARMOR_TOUGHNESS, TOUGHNESS_ID, "YourThreats toughness", level.toughness);
    }

    private static void setModifier(LivingEntity entity, Attribute attribute, UUID id, String name, double multiplier) {
        AttributeInstance instance = entity.getAttribute(attribute);
        if (instance == null) return;
        instance.removeModifier(id);
        if (multiplier != 1.0) {
            instance.addPermanentModifier(new AttributeModifier(id, name, multiplier - 1.0,
                    AttributeModifier.Operation.MULTIPLY_TOTAL));
        }
    }
}
