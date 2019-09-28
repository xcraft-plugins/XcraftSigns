package de.xcraft.voronwe.XcraftSigns.Util;

public class Cast {
    public static Integer castInt(Object o) {
        if (o == null) {
            return 0;
        }
        if (o instanceof Byte) {
            return (int) ((Byte) o);
        }
        if (o instanceof Integer) {
            return (Integer) o;
        }
        if (o instanceof Double) {
            return (int) ((Double) o).doubleValue();
        }
        if (o instanceof Float) {
            return (int) ((Float) o).floatValue();
        }
        if (o instanceof Long) {
            return (int) ((Long) o).longValue();
        }
        if (o instanceof String) {
            try {
                return Integer.parseInt((String) o);
            } catch (Exception ex) {
                return 0;
            }
        }
        return 0;
    }

    public static double castDouble(Object o) {
        if (o == null) {
            return 0.0;
        }
        if (o instanceof Byte) {
            return ((Byte) o).byteValue();
        }
        if (o instanceof Integer) {
            return ((Integer) o).intValue();
        }
        if (o instanceof Double) {
            return (Double) o;
        }
        if (o instanceof Float) {
            return ((Float) o).floatValue();
        }
        if (o instanceof Long) {
            return ((Long) o).longValue();
        }
        if (o instanceof String) {
            try {
                return Double.parseDouble((String) o);
            } catch (Exception ex) {
                return 0.0;
            }
        }
        return 0.0;
    }

    public static Boolean castBoolean(Object o) {
        if (o == null) {
            return false;
        }
        if (o instanceof Boolean) {
            return (Boolean) o;
        }
        if (o instanceof String) {
            return ((String) o).equalsIgnoreCase("true");
        }
        return false;
    }
}

