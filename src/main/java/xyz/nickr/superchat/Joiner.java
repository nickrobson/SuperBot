package xyz.nickr.superchat;

public class Joiner {

    public static String join(String sep, Object[] strings) {
        StringBuilder builder = new StringBuilder();
        for (Object z : strings) {
            if (builder.length() > 0)
                builder.append(sep);
            builder.append(z);
        }
        return builder.toString();
    }

    public static String join(String sep, Iterable<?> strings) {
        StringBuilder builder = new StringBuilder();
        for (Object z : strings) {
            if (builder.length() > 0)
                builder.append(sep);
            builder.append(z);
        }
        return builder.toString();
    }
}
