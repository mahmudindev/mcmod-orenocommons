package com.github.mahmudindev.mcmod.orenocommons.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface MixinConditions {
    MixinCondition[] value();

    Mode mode() default Mode.AND;

    enum Mode {
        AND,
        OR,
        NAND,
        NOR,
        XOR,
        XNOR
    }
}
