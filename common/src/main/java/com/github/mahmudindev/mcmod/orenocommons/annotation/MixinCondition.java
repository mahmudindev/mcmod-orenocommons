package com.github.mahmudindev.mcmod.orenocommons.annotation;

import com.github.mahmudindev.mcmod.orenocommons.platform.LoaderType;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface MixinCondition {
    Type type();

    String value() default "";

    String[] aliases()  default {};

    String[] versionPredicates() default {};

    LoaderType[] loaderTypes() default {};

    boolean negate() default false;

    enum Type {
        DEVELOPMENT_ENVIRONMENT,
        JAVA_VERSION,
        LOADER_TYPE,
        MINECRAFT_VERSION,
        MOD_LOADED
    }
}
