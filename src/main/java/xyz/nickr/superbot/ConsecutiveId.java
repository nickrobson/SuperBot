package xyz.nickr.superbot;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ConsecutiveId {

    private static Map<String, String> store = new HashMap<>();
    private static List<Character> chars = new LinkedList<>();

    static {
        for (char i = 'a', j = 'z'; i <= j; i++) {
            ConsecutiveId.chars.add(i);
        }
        for (char i = 'A', j = 'Z'; i <= j; i++) {
            ConsecutiveId.chars.add(i);
        }
        for (char i = '0', j = '9'; i <= j; i++) {
            ConsecutiveId.chars.add(i);
        }
    }

    public static String nextIdentifier() {
        return ConsecutiveId.next("");
    }

    public static String next(String namespace) {
        if (!ConsecutiveId.store.containsKey(namespace)) {
            return ConsecutiveId.store.computeIfAbsent(namespace, n -> chars.get(0).toString());
        }
        String curr = ConsecutiveId.store.get(namespace);
        int idx = ConsecutiveId.chars.indexOf(curr.charAt(curr.length() - 1));
        if (idx == -1) {
            throw new IllegalStateException("char '" + curr.charAt(curr.length() - 1) + "' is not in chars list");
        } else if (idx == ConsecutiveId.chars.size() - 1) {
            curr += chars.get(0);
        } else {
            curr = curr.substring(0, curr.length() - 1) + ConsecutiveId.chars.get(idx + 1);
        }
        ConsecutiveId.store.put(namespace, curr);
        return curr;
    }

}
