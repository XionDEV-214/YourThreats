package com.eisiadev.yourthreats.mixin;

import com.eisiadev.yourthreats.ActiveThreat;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NaturalSpawner.class)
public abstract class NaturalSpawnerMixin {
    @Inject(method = "spawnForChunk", at = @At("TAIL"))
    private static void yourthreats$extraMonsterSpawns(ServerLevel level, LevelChunk chunk, NaturalSpawner.SpawnState state,
                                                       boolean spawnFriendlies, boolean spawnMonsters, boolean forcedDespawn,
                                                       CallbackInfo ci) {
        if (!spawnMonsters) return;
        double extra = ActiveThreat.get().spawnRate - 1.0;
        if (extra <= 0) return;

        int attempts = (int) Math.floor(extra + 1e-9);
        if (level.getRandom().nextDouble() < extra - attempts) attempts++;

        SpawnStateInvoker invoker = (SpawnStateInvoker) (Object) state;
        for (int i = 0; i < attempts; i++) {
            if (!invoker.yourthreats$canSpawnForCategory(MobCategory.MONSTER, chunk.getPos())) return;
            NaturalSpawner.spawnCategoryForChunk(MobCategory.MONSTER, level, chunk,
                    invoker::yourthreats$canSpawn, invoker::yourthreats$afterSpawn);
        }
    }
}
