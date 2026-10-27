package com.eisiadev.yourthreats;

import com.eisiadev.yourthreats.network.ThreatNetwork;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraft.tags.DamageTypeTags;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = YourThreats.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class ThreatEvents {
    private ThreatEvents() {}

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        ThreatCommand.register(event.getDispatcher());
    }

    @SubscribeEvent
    public static void onEntityJoin(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide()) return;
        if (!(event.getEntity() instanceof LivingEntity living) || !ThreatManager.isHostile(living)) return;
        MinecraftServer server = event.getLevel().getServer();
        if (server == null) return;
        ThreatManager.apply(living, ThreatManager.getLevel(server));
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onHurt(LivingHurtEvent event) {
        LivingEntity target = event.getEntity();
        if (target.level().isClientSide()) return;
        Entity attacker = event.getSource().getEntity();
        if (attacker == null || !ThreatManager.isHostile(attacker)) return;
        MinecraftServer server = target.level().getServer();
        if (server == null) return;
        double mult = ThreatManager.getLevel(server).damage;
        if (mult != 1.0) {
            event.setAmount((float) (event.getAmount() * mult));
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onDrops(LivingDropsEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide() || !ThreatManager.isHostile(entity)) return;
        MinecraftServer server = entity.level().getServer();
        if (server == null) return;
        ThreatLevel level = ThreatManager.getLevel(server);
        if (level.dropChance == 1.0 && level.dropMultiplier == 1.0) return;

        RandomSource random = entity.getRandom();
        List<ItemEntity> result = new ArrayList<>();
        for (ItemEntity drop : event.getDrops()) {
            ItemStack stack = drop.getItem();
            if (stack.isEmpty()) continue;

            int rolls = (int) Math.floor(level.dropChance + 1e-9);
            double fraction = level.dropChance - rolls;
            if (fraction > 1e-9 && random.nextDouble() < fraction) rolls++;

            int perRoll = (int) Math.floor(stack.getCount() * level.dropMultiplier + 1e-9);
            int total = rolls * perRoll;

            while (total > 0) {
                int count = Math.min(total, stack.getMaxStackSize());
                ItemEntity copy = new ItemEntity(entity.level(), drop.getX(), drop.getY(), drop.getZ(),
                        stack.copyWithCount(count));
                copy.setDeltaMovement(drop.getDeltaMovement().add(
                        (random.nextDouble() - 0.5) * 0.1, 0.0, (random.nextDouble() - 0.5) * 0.1));
                copy.setDefaultPickUpDelay();
                result.add(copy);
                total -= count;
            }
        }
        event.getDrops().clear();
        event.getDrops().addAll(result);
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onDamage(LivingDamageEvent event) {
        LivingEntity target = event.getEntity();
        if (target.level().isClientSide() || !ThreatManager.isHostile(target)) return;
        if (event.getSource().is(DamageTypeTags.BYPASSES_INVULNERABILITY)) return;
        double reduction = ActiveThreat.get().damageReduction;
        if (reduction > 0) {
            event.setAmount((float) (event.getAmount() * (1.0 - reduction)));
        }
    }

    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent event) {
        ThreatManager.resetCache(event.getServer());
    }

    @SubscribeEvent
    public static void onServerStopped(ServerStoppedEvent event) {
        ThreatManager.resetCache(null);
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            ThreatNetwork.sendTo(player, ThreatManager.getLevel(player.server));
        }
    }
}
