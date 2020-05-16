package app.models;

import java.time.LocalTime;

/**
 * Help functions for work with time.
 * @author Petr Křehlík, Martin Klobušický
 * @date 16.5.2020
 */
public abstract class TimeExtender {
    /**
     * Distract one tim from another.
     * @param diff1 Time 1
     * @param diff2 Time 2
     * @return Count of seconds Time 1 - Time 2
     */
    public static int minusLocalTime(LocalTime diff1, LocalTime diff2) {
        LocalTime diff = diff1.minusHours(diff2.getHour())
                .minusMinutes(diff2.getMinute())
                .minusSeconds(diff2.getSecond());

        return (diff.getHour() * 60 * 60) + (diff.getMinute() * 60) + (diff.getSecond());
    }

    /**
     * Add seconds to time.
     * @param diff1 Time 1
     * @param diff2 Seconds
     * @return Result time.
     */
    public static LocalTime plusLocalTime(LocalTime diff1, long diff2) {
        LocalTime diff = diff1.plusSeconds(diff2);

        return diff;
    }

    /**
     * Minus seconds from time.
     * @param diff1 Time 1
     * @param diff2 Seconds
     * @return Result time.
     */
    public static LocalTime minusLocalTime(LocalTime diff1, long diff2) {
        LocalTime diff = diff1.minusSeconds(diff2);

        return diff;
    }
}

