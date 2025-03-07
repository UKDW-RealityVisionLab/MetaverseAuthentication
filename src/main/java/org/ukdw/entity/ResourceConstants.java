package org.ukdw.entity;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class ResourceConstants {
    // Permission (long only contains 64 bit, therefore limiting it to 64 permissions)
    public static final long ENTER_MATH_CLASSROOM = 1L;  // 0001
    public static final long TEACHING_MATH_CLASSROOM  = 1L << 1; // 0010
    public static final long ADMINISTER_MATH_CLASSROOM  = 1L << 2; // 0100
    public static final long ENTER_BIOLOGY_CLASSROOM = 1L << 3; // 1000
    public static final long TEACHING_BIOLOGY_CLASSROOM = 1L << 4; // 0001 0000
    public static final long ADMINISTER_BIOLOGY_CLASSROOM  = 1L << 5; // 0010 0000
    public static final long ENTER_PHYSICS_CLASSROOM = 1L << 6; // 0100 0000
    public static final long TEACHING_PHYSICS_CLASSROOM = 1L << 7; // 1000 0000
    public static final long ADMINISTER_PHYSICS_CLASSROOM  = 1L << 8; // 0001 0000 0000

    // Map of permission bit values to names
    private static final Map<Long, String> PERMISSION_NAMES = new HashMap<>();
    static {
        PERMISSION_NAMES.put(ENTER_MATH_CLASSROOM, "ENTER_MATH_CLASSROOM");
        PERMISSION_NAMES.put(TEACHING_MATH_CLASSROOM, "TEACHING_MATH_CLASSROOM");
        PERMISSION_NAMES.put(ADMINISTER_MATH_CLASSROOM, "ADMINISTER_MATH_CLASSROOM");
        PERMISSION_NAMES.put(ENTER_BIOLOGY_CLASSROOM, "ENTER_BIOLOGY_CLASSROOM");
        PERMISSION_NAMES.put(TEACHING_BIOLOGY_CLASSROOM, "TEACHING_BIOLOGY_CLASSROOM");
        PERMISSION_NAMES.put(ADMINISTER_BIOLOGY_CLASSROOM, "ADMINISTER_BIOLOGY_CLASSROOM");
        PERMISSION_NAMES.put(ENTER_PHYSICS_CLASSROOM, "ENTER_PHYSICS_CLASSROOM");
        PERMISSION_NAMES.put(TEACHING_PHYSICS_CLASSROOM, "TEACHING_PHYSICS_CLASSROOM");
        PERMISSION_NAMES.put(ADMINISTER_PHYSICS_CLASSROOM, "ADMINISTER_PHYSICS_CLASSROOM");
    }

    /**
     * Load resources based on the permission bitmask.
     * @param permission The bitmask representing the permissions.
     * @return A map of permission values to their respective variable names.
     */
    public static Map<Long, String> loadResourceNames(long permission) {
        Map<Long, String> resources = new TreeMap<>();
        for (Map.Entry<Long, String> entry : PERMISSION_NAMES.entrySet()) {
            if ((permission & entry.getKey()) == entry.getKey()) {
                resources.put(entry.getKey(), entry.getValue());
            }
        }
        return resources;
    }
}
