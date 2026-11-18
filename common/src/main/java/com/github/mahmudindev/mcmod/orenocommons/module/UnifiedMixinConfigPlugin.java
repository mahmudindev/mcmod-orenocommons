package com.github.mahmudindev.mcmod.orenocommons.module;

import com.github.mahmudindev.mcmod.orenocommons.annotation.MixinCondition;
import com.github.mahmudindev.mcmod.orenocommons.annotation.MixinConditions;
import com.github.mahmudindev.mcmod.orenocommons.platform.LoaderType;
import com.github.mahmudindev.mcmod.orenocommons.platform.UnifiedPlatform;
import com.github.mahmudindev.mcmod.orenocommons.util.UnifiedVersion;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.service.IClassBytecodeProvider;
import org.spongepowered.asm.service.IMixinService;
import org.spongepowered.asm.service.MixinService;
import org.spongepowered.asm.util.Annotations;

import java.io.IOException;
import java.util.List;
import java.util.Set;

public abstract class UnifiedMixinConfigPlugin implements IMixinConfigPlugin {
    protected String mixinPackage;

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
        if (!this.shouldApplyMixin(mixinClassName.substring(mixinPackage.length() + 1))) {
            return false;
        }

        try {
            IMixinService mixinService = MixinService.getService();
            IClassBytecodeProvider bytecodeProvider = mixinService.getBytecodeProvider();
            ClassNode mixinClassNode = bytecodeProvider.getClassNode(mixinClassName);
            if (mixinClassNode.visibleAnnotations != null) {
                for (AnnotationNode mixinAnnotation : mixinClassNode.visibleAnnotations) {
                    if (!this.shouldApplyMixin(mixinAnnotation)) {
                        return false;
                    }
                }
            }
        } catch (ClassNotFoundException | IOException e) {
            throw new RuntimeException(e);
        }

        return true;
    }

    protected boolean shouldApplyMixin(String mixinName) {
        String compatPrefix = "unifiedcompat.";
        if (mixinName.startsWith(compatPrefix)) {
            String[] names = mixinName.substring(compatPrefix.length()).split("\\.");

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

    protected boolean shouldApplyMixin(AnnotationNode mixinAnnotation) {
        if (mixinAnnotation.desc.equals(Type.getDescriptor(MixinCondition.class))) {
            boolean result = true;

            MixinCondition.Type type = Annotations.getValue(mixinAnnotation, "type");
            switch (type) {
                case DEVELOPMENT_ENVIRONMENT:
                    result = UnifiedPlatform.isDevelopmentEnvironment();

                    break;
                case JAVA_VERSION:
                    String[] javaVersionPredicates = Annotations.getValue(
                            mixinAnnotation,
                            "versionPredicates"
                    );
                    if (javaVersionPredicates != null) {
                        String javaVersion = Runtime.version().toString();

                        for (String versionPredicate : javaVersionPredicates) {
                            if (!UnifiedVersion.matches(javaVersion, versionPredicate)) {
                                result = false;
                                break;
                            }
                        }
                    }

                    break;
                case LOADER_TYPE:
                    LoaderType[] loaderTypes = Annotations.getValue(
                            mixinAnnotation,
                            "loaderTypes"
                    );
                    if (loaderTypes != null) {
                        for (LoaderType loaderType : loaderTypes) {
                            result = loaderType == UnifiedPlatform.getLoaderType();

                            if (result) {
                                break;
                            }
                        }
                    }

                    break;
                case MINECRAFT_VERSION:
                    String[] minecraftVersionPredicates = Annotations.getValue(
                            mixinAnnotation,
                            "versionPredicates"
                    );
                    if (minecraftVersionPredicates != null) {
                        String minecraftVersion = UnifiedPlatform.getModVersion("minecraft");

                        for (String versionPredicate : minecraftVersionPredicates) {
                            if (!UnifiedVersion.matches(minecraftVersion, versionPredicate)) {
                                result = false;
                                break;
                            }
                        }
                    }

                    break;
                case MOD_LOADED:
                    String modId = Annotations.getValue(mixinAnnotation, "value");

                    result = UnifiedPlatform.isModLoaded(modId);

                    String[] modAliases = Annotations.getValue(mixinAnnotation, "aliases");
                    if (!result && modAliases != null) {
                        for (String alias : modAliases) {
                            if (UnifiedPlatform.isModLoaded(alias)) {
                                result = true;
                                modId = alias;
                                break;
                            }
                        }
                    }

                    String[] modVersionPredicates = Annotations.getValue(
                            mixinAnnotation,
                            "versionPredicates"
                    );
                    if (result && modVersionPredicates != null) {
                        String modVersion = UnifiedPlatform.getModVersion(modId);

                        for (String versionPredicate : modVersionPredicates) {
                            if (!UnifiedVersion.matches(modVersion, versionPredicate)) {
                                result = false;
                                break;
                            }
                        }
                    }

                    break;
            }

            boolean negate = Annotations.getValue(mixinAnnotation, "negate");

            return negate != result;
        }

        if (mixinAnnotation.desc.equals(Type.getDescriptor(MixinConditions.class))) {
            List<AnnotationNode> value = Annotations.getValue(mixinAnnotation, "value", true);
            MixinConditions.Mode mode = Annotations.getValue(mixinAnnotation, "mode");

            Boolean shouldApply = null;
            for (AnnotationNode annotation : value) {
                boolean result = this.shouldApplyMixin(annotation);

                switch (mode) {
                    case AND:
                        if (shouldApply == null) {
                            shouldApply = result;
                            break;
                        }

                        shouldApply = shouldApply && result;
                        break;
                    case OR:
                        if (shouldApply == null) {
                            shouldApply = result;
                            break;
                        }

                        shouldApply = shouldApply || result;
                        break;
                    case NAND:
                        if (shouldApply == null) {
                            shouldApply = !result;
                            break;
                        }

                        shouldApply = shouldApply || !result;
                        break;
                    case NOR:
                        if (shouldApply == null) {
                            shouldApply = !result;
                            break;
                        }

                        shouldApply = shouldApply && !result;
                        break;
                    case XOR:
                        if (shouldApply == null) {
                            shouldApply = result;
                            break;
                        }

                        shouldApply = shouldApply != result;
                        break;
                    case XNOR:
                        if (shouldApply == null) {
                            shouldApply = result;
                            break;
                        }

                        shouldApply = shouldApply == result;
                        break;
                }
            }

            return shouldApply == null || shouldApply;
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
