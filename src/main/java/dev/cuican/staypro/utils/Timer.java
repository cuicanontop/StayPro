package dev.cuican.staypro.utils;

import java.time.Duration;
import java.time.Instant;

public final class Timer {

    public static Instant now() {
        return Instant.now();
    }

    private long time;

    public Timer() {
        time = -1;
    }

    public void resetTimeSkipTo(int ms) {
        this.time = System.currentTimeMillis() + ms;
    }
    public boolean passedMs(long ms) {
        return this.passedNS(this.convertToNS(ms));
    }
    public long getPassedTimeMs() {
        return this.getMs(System.nanoTime() - this.time);
    }
    public long getMs(long time) {
        return time / 1000000L;
    }

    public boolean passed(double ms) {
        return ((System.currentTimeMillis() - this.time) >= ms);
    }
    public boolean passedMs(double ms) {
        return this.passedNS(this.convertToNS((long) ms));
    }
    public boolean passedSec(double second) {
        return ((System.currentTimeMillis() - this.time) >= second * 1000);
    }
    public boolean passedNS(long ns) {
        return System.nanoTime() - this.time >= ns;
    }
    public boolean passedTick(double tick) {
        return ((System.currentTimeMillis() - this.time) >= tick * 50);
    }
    public long convertToNS(long time) {
        return time * 1000000L;
    }
    public boolean passedMin(double minutes) {
        return ((System.currentTimeMillis() - this.time) >= minutes * 1000 * 60);
    }

    public long getPassed() {
        return (System.currentTimeMillis() - this.time);
    }


    public static long getSecondsPassed(Instant start, Instant current) {
        return Duration.between(start, current).getSeconds();
    }

    public static boolean haveSecondsPassed(Instant start, Instant current, long seconds) {
        return Timer.getSecondsPassed(start, current) >= seconds;
    }

    public static long getMilliSecondsPassed(Instant start, Instant current) {
        return Duration.between(start, current).toMillis();
    }

    public static boolean haveMilliSecondsPassed(Instant start, Instant current, long milliseconds) {
        return Timer.getMilliSecondsPassed(start, current) >= milliseconds;
    }

    public Timer reset() {
        this.time = System.currentTimeMillis();
        return this;
    }

    public Timer restart() {
        this.time = -1;
        return this;
    }

    public long getTime() {
        return this.time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long hasPassed() {
        return System.currentTimeMillis() - time;
    }

}