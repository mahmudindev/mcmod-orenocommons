package com.github.mahmudindev.mcmod.orenocommons.util;

public class UnifiedMath {
    public static int gcd(int a, int b) {
        while (b != 0) {
            int c = b;
            b = a % b;
            a = c;
        }

        return a;
    }
}
