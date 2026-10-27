package com.eisiadev.yourthreats.mixin;

import net.minecraft.world.entity.ai.attributes.Attributes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(Attributes.class)
public abstract class AttributesMixin {
    private static final double YOURTHREATS_MAX_HEALTH_CAP = 1_000_000.0;

    @ModifyArg(
            method = "<clinit>",
            slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=attribute.name.generic.max_health")),
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/ai/attributes/RangedAttribute;<init>(Ljava/lang/String;DDD)V",
                    ordinal = 0),
            index = 3,
            remap = false)
    private static double yourthreats$raiseMaxHealthCap(double max) {
        return Math.max(max, YOURTHREATS_MAX_HEALTH_CAP);
    }
}
