package com.eisiadev.yourthreats.mixin;

import com.eisiadev.yourthreats.ActiveThreat;
import net.minecraft.world.entity.MobCategory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MobCategory.class)
public abstract class MobCategoryMixin {
    @Inject(method = "getMaxInstancesPerChunk", at = @At("RETURN"), cancellable = true)
    private void yourthreats$scaleMonsterCap(CallbackInfoReturnable<Integer> cir) {
        if ((Object) this != MobCategory.MONSTER) return;
        double rate = ActiveThreat.get().spawnRate;
        if (rate != 1.0) {
            cir.setReturnValue((int) Math.round(cir.getReturnValueI() * rate));
        }
    }
}
