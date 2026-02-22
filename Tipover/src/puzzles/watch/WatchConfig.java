package puzzles.watch;

import java.util.*;
import puzzles.common.solver.Configuration;

/**
 * Configuration class for the watch puzzle.
 * Stores the current hour and minute, along with the target time.
 * Used by the solver to explore different time states.
 *
 * @author Amanda Guan
 */
public class WatchConfig implements Configuration {
    private int hours;
    private int hour;
    private int minute;
    private int targetHour;
    private int targetMinute;

    /**
     * Creates a new watch configuration.
     *
     * @param hours total number of hours on the watch
     * @param hour current hour
     * @param minute current minute
     * @param targetHour target hour
     * @param targetMinute target minute
     */
    public WatchConfig(int hours, int hour, int minute, int targetHour, int targetMinute) {
        this.hours = hours;
        this.hour = hour;
        this.minute = minute;
        this.targetHour = targetHour;
        this.targetMinute = targetMinute;
    }

    /**
     * Checks if the current time matches the target time.
     *
     * @return true if hour and minute match the target
     */
    @Override
    public boolean isSolution() {
        return this.hour == this.targetHour && this.minute == this.targetMinute;
    }

    /**
     * Generates all neighboring configurations.
     * Neighbors are formed by moving the hour hand or minute hand
     * forward or backward by one step, wrapping around when needed.
     *
     * @return a list of possible next configurations
     */
    @Override
    public Collection<Configuration> getNeighbors() {
        ArrayList<Configuration> list = new ArrayList<>(4);

        // Change hour if needed
        if (this.hour != this.targetHour) {
            // Hour backward
            int hb = this.hour - 1;
            if (hb < 1) {
                hb = this.hours;
            }
            list.add(new WatchConfig(hours, hb, minute, targetHour, targetMinute));

            // Hour forward
            int hf = this.hour + 1;
            if (hf > this.hours) {
                hf = 1;
            }
            list.add(new WatchConfig(hours, hf, minute, targetHour, targetMinute));
        }

        // Change minute if needed
        if (this.minute != this.targetMinute) {
            // Minute backward
            int mb = this.minute - 1;
            if (mb < 0) {
                mb = 59;
            }
            list.add(new WatchConfig(hours, hour, mb, targetHour, targetMinute));

            // Minute forward
            int mf = this.minute + 1;
            if (mf > 59) {
                mf = 0;
            }
            list.add(new WatchConfig(hours, hour, mf, targetHour, targetMinute));
        }
        return list;
    }

    /**
     * Checks if two configurations match in hours, hour hand, and minute hand.
     */
    @Override
    public boolean equals(Object other) {
        if (!(other instanceof WatchConfig)) {
            return false;
        }
        WatchConfig o = (WatchConfig) other;
        return this.hours == o.hours && this.hour == o.hour && this.minute == o.minute;
    }

    /**
     * Computes a hash code based on hours, hour, and minute.
     */
    @Override
    public int hashCode() {
        int res = 17;
        res = 31 * res + hours;
        res = 31 * res + hour;
        res = 31 * res + minute;
        return res;
    }

    /**
     * Returns the time in H:MM format.
     */
    @Override
    public String toString() {
        String min = (minute < 10) ? ("0" + minute) : ("" + minute);
        return hour + ":" + min;
    }
}