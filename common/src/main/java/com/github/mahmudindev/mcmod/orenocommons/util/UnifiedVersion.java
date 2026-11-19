package com.github.mahmudindev.mcmod.orenocommons.util;

import org.semver4j.Semver;
import org.semver4j.range.RangeList;
import org.semver4j.range.RangeListFactory;

public class UnifiedVersion {
    public static boolean matches(String v, String predicate) {
        Semver version = new Semver(v);
        RangeList rangeList = RangeListFactory.create(predicate);
        return version.satisfies(rangeList);
    }
}
