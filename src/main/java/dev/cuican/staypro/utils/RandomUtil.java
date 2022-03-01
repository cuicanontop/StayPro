package dev.cuican.staypro.utils;

import java.util.Random;

public class RandomUtil {
    private static final Random random;
    public static Random getRandom() {
        return random;
    }

    static {
        random = new Random();
    }
    public static int nextInt(int startInclusive, int endExclusive) {
        return endExclusive - startInclusive <= 0 ? startInclusive : startInclusive + new Random().nextInt(endExclusive - startInclusive);
    }
    public final long randomDelay(int minDelay, int maxDelay) {
        return nextInt(minDelay, maxDelay);
    }

    public static double nextDouble(double startInclusive, double endInclusive) {
        return startInclusive == endInclusive || endInclusive - startInclusive <= 0.0 ? startInclusive : startInclusive + (endInclusive - startInclusive) * Math.random();
    }
}
