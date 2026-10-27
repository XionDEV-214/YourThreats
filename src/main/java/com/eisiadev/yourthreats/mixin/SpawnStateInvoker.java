package com.eisiadev.yourthreats.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.chunk.ChunkAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(NaturalSpawner.SpawnState.class)
public interface SpawnStateInvoker {
    @Invoker("canSpawnForCategory")
    boolean yourthreats$canSpawnForCategory(MobCategory category, ChunkPos pos);

    @Invoker("canSpawn")
    boolean yourthreats$canSpawn(EntityType<?> type, BlockPos pos, ChunkAccess chunk);

    @Invoker("afterSpawn")
    void yourthreats$afterSpawn(Mob mob, ChunkAccess chunk);
}
