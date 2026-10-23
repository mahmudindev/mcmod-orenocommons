package com.github.mahmudindev.mcmod.orenocommons.mixin;

import com.github.mahmudindev.mcmod.orenocommons.platform.UnifiedPlatform;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Objects;
import java.util.Set;

public class UnifiedMixinConfigPlugin implements IMixinConfigPlugin {
    private String mixinPackage;

    @Override
    public void onLoad(String mixinPackage) {
        this.mixinPackage = mixinPackage;
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        String name = mixinClassName.substring(mixinPackage.length() + 1);

        String compatPrefix = "unifiedcompat.";
        if (name.startsWith(compatPrefix)) {
            String[] names = name.substring(compatPrefix.length()).split("\\.");

            boolean shouldApply = false;
            boolean invert = false;
            for (int i = 0; i < names.length - 1; i++) {
                if (Objects.equals(names[i], "not")) {
                    invert = true;
                    continue;
                }

                shouldApply = UnifiedPlatform.isModLoaded(names[i]);

                if (invert) {
                    shouldApply = !shouldApply;
                    invert = false;
                }

                if (!shouldApply) {
                    break;
                }
            }
            return shouldApply;
        }

        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(
            String targetClassName,
            ClassNode targetClass,
            String mixinClassName,
            IMixinInfo mixinInfo
    ) {
    }

    @Override
    public void postApply(
            String targetClassName,
            ClassNode classNode,
            String mixinClassName,
            IMixinInfo mixinInfo
    ) {
    }
}
