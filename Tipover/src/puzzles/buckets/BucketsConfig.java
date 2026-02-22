package puzzles.buckets;

import puzzles.common.solver.Configuration;
import java.util.*;

/**
 * Configuration class for the buckets puzzle.
 * Stores bucket capacities, current water amounts, and the target amount.
 * Used by the solver to explore possible puzzle states.
 *
 * @author Amanda Guan
 */
public class BucketsConfig implements Configuration {
    private int[] caps;
    private int[] amounts;
    private int target;

    /**
     * Creates a new bucket configuration.
     *
     * @param caps the capacity of each bucket
     * @param amounts the current amount in each bucket
     * @param target the desired amount of water to reach
     */
    public BucketsConfig(int[] caps, int[] amounts, int target) {
        // Copy capacities
        this.caps = new int[caps.length];
        System.arraycopy(caps, 0, this.caps, 0, caps.length);

        // Copy amounts
        this.amounts = new int[amounts.length];
        System.arraycopy(amounts, 0, this.amounts, 0, amounts.length);
        this.target = target;
    }

    /**
     * Checks if this configuration has the target amount.
     *
     * @return true if any bucket contains the target amount
     */
    @Override
    public boolean isSolution() {
        // Scan all buckets for the target amount
        for (int a : amounts) {
            if (a == target) {
                return true;
            }
        }
        return false;
    }

    /**
     * Generates all next valid configurations.
     *
     * @return a collection of all neighboring configurations
     */
    @Override
    public Collection<Configuration> getNeighbors() {
        ArrayList<Configuration> list = new ArrayList<>();
        int n = caps.length;

        // Fill, empty, pour
        for (int i = 0; i < n; i++) {
            // Fill bucket i
            if (amounts[i] < caps[i]) {
                int[] na = copyAmounts();
                na[i] = caps[i];
                list.add(new BucketsConfig(caps, na, target));
            }

            // Empty bucket i
            if (amounts[i] > 0) {
                int[] na = copyAmounts();
                na[i] = 0;
                list.add(new BucketsConfig(caps, na, target));
            }

            // Pour from i to j
            for (int j = 0; j < n; j++) {
                if (i == j){
                    continue;
                }
                if (amounts[i] == 0){
                    continue;
                }
                if (amounts[j] == caps[j]){
                    continue;
                }

                // How much we can transfer
                int transfer = Math.min(amounts[i], caps[j] - amounts[j]);
                if (transfer > 0) {
                    int[] na = copyAmounts();
                    na[i] -= transfer;
                    na[j] += transfer;
                    list.add(new BucketsConfig(caps, na, target));
                }
            }
        }
        return list;
    }

    /**
     * Makes a copy of the amounts array.
     *
     * @return a new array with the same values
     */
    private int[] copyAmounts() {
        int[] a = new int[amounts.length];
        System.arraycopy(amounts, 0, a, 0, amounts.length);
        return a;
    }

    /**
     * Checks if two configurations have the same amounts.
     */
    @Override
    public boolean equals(Object other) {
        if (!(other instanceof BucketsConfig)) {
            return false;
        }
        BucketsConfig o = (BucketsConfig) other;

        // Check length
        if (this.amounts.length != o.amounts.length) {
            return false;
        }

        // Check each amount
        for (int i = 0; i < amounts.length; i++) {
            if (this.amounts[i] != o.amounts[i]) {
                return false;
            }
        }
        return true;
    }

    /**
     * Creates a hash code using the amounts.
     */
    @Override
    public int hashCode() {
        int res = 17;
        for (int a : amounts) {
            res = 31 * res + a;
        }
        return res;
    }

    /**
     * Returns a string.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < amounts.length; i++) {
            sb.append(amounts[i]);
            if (i < amounts.length - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    /**
     * Converts capacities into a readable string.
     *
     * @param caps the bucket capacities
     * @return a string
     */
    public static String capsToString(int[] caps) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < caps.length; i++) {
            sb.append(caps[i]);
            if (i < caps.length - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }
}