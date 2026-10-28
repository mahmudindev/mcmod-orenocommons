package com.github.mahmudindev.mcmod.orenocommons.mixin;

import com.github.mahmudindev.mcmod.orenocommons.platform.UnifiedPlatform;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
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

            Boolean shouldApply = null;
            Boolean savedShouldApply = null;

            int mode = 0;
            boolean invert = false;
            for (int i = 0; i < names.length - 1; i++) {
                shouldApply = switch (names[i]) {
                    case "and", "or", "nand", "nor", "xor", "xnor" -> {
                        if (shouldApply != null) {
                            if (savedShouldApply != null) {
                                savedShouldApply = shouldApply && savedShouldApply;
                            } else {
                                savedShouldApply = shouldApply;
                            }
                        }
                        yield null;
                    }
                    default -> shouldApply;
                };

                switch (names[i]) {
                    case "not":
                        invert = !invert;
                        continue;
                    case "and":
                        mode = invert ? 2 : 0;
                        invert = false;
                        continue;
                    case "or":
                        mode = invert ? 3 : 1;
                        invert = false;
                        continue;
                    case "nand":
                        mode = invert ? 0 : 2;
                        invert = false;
                        continue;
                    case "nor":
                        mode = invert ? 1 : 3;
                        invert = false;
                        continue;
                    case "xor":
                        mode = invert ? 5 : 4;
                        continue;
                    case "xnor":
                        mode = invert ? 4 : 5;
                        continue;
                }

                boolean isModLoaded = UnifiedPlatform.isModLoaded(names[i]);

                if (invert) {
                    isModLoaded = !isModLoaded;
                    invert = false;
                }

                switch (mode) {
                    case 0:
                        if (shouldApply == null) {
                            shouldApply = isModLoaded;
                            break;
                        }

                        shouldApply = shouldApply && isModLoaded;
                        break;
                    case 1:
                        if (shouldApply == null) {
                            shouldApply = isModLoaded;
                            break;
                        }

                        shouldApply = shouldApply || isModLoaded;
                        break;
                    case 2:
                        if (shouldApply == null) {
                            shouldApply = !isModLoaded;
                            break;
                        }

                        shouldApply = shouldApply || !isModLoaded;
                        break;
                    case 3:
                        if (shouldApply == null) {
                            shouldApply = !isModLoaded;
                            break;
                        }

                        shouldApply = shouldApply && !isModLoaded;
                        break;
                    case 4:
                        if (shouldApply == null) {
                            shouldApply = isModLoaded;
                            break;
                        }

                        shouldApply = shouldApply != isModLoaded;
                        break;
                    case 5:
                        if (shouldApply == null) {
                            shouldApply = isModLoaded;
                            break;
                        }

                        shouldApply = shouldApply == isModLoaded;
                        break;
                }
            }

            if (savedShouldApply != null) {
                if (shouldApply != null) {
                    shouldApply = savedShouldApply && shouldApply;
                } else {
                    shouldApply = savedShouldApply;
                }
            }

            return shouldApply != null && shouldApply;
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
