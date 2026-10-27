package com.eisiadev.yourthreats.mixin;

import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.loading.LoadingModList;
import org.objectweb.asm.tree.ClassNode;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class YourThreatsMixinPlugin implements IMixinConfigPlugin {
    private static final Logger LOGGER = LogUtils.getLogger();
    private boolean attributeFixLoaded;

    @Override
    public void onLoad(String mixinPackage) {
        LoadingModList modList = LoadingModList.get();
        attributeFixLoaded = modList != null && modList.getModFileById("attributefix") != null;
        if (attributeFixLoaded) {
            LOGGER.info("[YourThreats] AttributeFix detected - leaving the max health cap to AttributeFix's config");
        }
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (mixinClassName.endsWith(".AttributesMixin")) {
            return !attributeFixLoaded;
        }
        return true;
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}
}
