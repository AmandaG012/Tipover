package puzzles.common;

/**
 * The four cardinal directions represented as an enum.
 *
 * @author RIT CS
 *
 * Output:
 * NORTH
 * The north direction
 * Both are not facing north!
 * All values:
 * NORTH
 * SOUTH
 * EAST
 * WEST
 */
public enum Direction {
    /** North */
    NORTH,
    /** South */
    SOUTH,
    /** East */
    EAST,
    /** West */
    WEST;

    /**
     * Example usage of the Direction enum.
     *
     * @param args command line arguments (unused)
     */
    public static void main(String[] args) {
        /** create our first direction enum, d1, which is facing north */
        Direction d1 = Direction.NORTH;
        /** when printed, it is the enum's value, all in caps, "NORTH" */
        System.out.println(d1);
        /** example of switching on an enum based on different values */
        switch (d1) {
            case NORTH -> System.out.println("The north direction");
            case SOUTH -> System.out.println("The south direction");
            case EAST -> System.out.println("The east direction");
            case WEST -> System.out.println("The west direction");
        }
        /** create a second enum */
        Direction d2 = Direction.SOUTH;
        /** enums are compared with == using the values of each */
        if (d1 == d2) {
            System.out.println("Both facing north!");
        } else {
            System.out.println("Both are not facing north!");
        }
        /** demonstrates how to loop over all the different values */
        System.out.println("All values:");
        for (Direction dir : Direction.values()) {
            System.out.println(dir);
        }
    }
}

