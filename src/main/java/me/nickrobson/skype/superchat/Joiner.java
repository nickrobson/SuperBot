package me.nickrobson.skype.superchat;

public class Joiner {

    public static String join(String sep, Object[] strings) {
        String s = "";
        for (Object z : strings) {
            if (!s.isEmpty())
                s += sep;
            s += z.toString();
        }
        return s;
    }

    public static String join(String sep, Iterable<?> strings) {
        String s = "";
        for (Object z : strings) {
            if (!s.isEmpty())
                s += sep;
            s += z.toString();
        }
        return s;
    }

}
