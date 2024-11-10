package io.github.pandier.atomi.internal.permission;

import io.github.pandier.atomi.Tristate;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.regex.Pattern;

@ApiStatus.Internal
public class PermissionTree {
    private static final Pattern NODE_SPLIT = Pattern.compile("\\.");

    private final Map<String, Node> children = new HashMap<>();

    public PermissionTree() {
    }

    public PermissionTree(@NotNull Map<String, Boolean> permissions) {
        for (Map.Entry<String, Boolean> entry : permissions.entrySet()) {
            set(entry.getKey(), Tristate.of(entry.getValue()));
        }
    }

    private static String[] split(String permission) {
        return NODE_SPLIT.split(permission.toLowerCase(Locale.ROOT));
    }

    @NotNull
    public Tristate get(@NotNull String permission) {
        String[] parts = split(permission);
        Map<String, Node> container = this.children;
        Tristate value = Tristate.UNSET;
        int i = 0;
        do {
            Node node = container.get(parts[i]);
            if (node == null)
                return value;
            if (node.value != Tristate.UNSET)
                value = node.value;
            container = node.children;
        } while (++i < parts.length);
        return value;
    }

    public void set(@NotNull String permission, @NotNull Tristate value) {
        String[] parts = split(permission);

        if (value == Tristate.UNSET) {
            unset(parts);
        } else {
            setOrCreate(parts, value);
        }
    }

    private List<Node> resolve(String[] parts) {
        List<Node> nodes = new ArrayList<>();
        Map<String, Node> container = this.children;
        for (String part : parts) {
            if (!container.containsKey(part))
                return null;
            Node node = container.get(part);
            nodes.add(node);
            container = node.children;
        }
        return nodes;
    }

    private void unset(String[] parts) {
        List<Node> nodes = resolve(parts);
        if (nodes == null || nodes.isEmpty()) return;
        nodes.getLast().value = Tristate.UNSET;

        // Cleanup empty and unset nodes
        for (int i = nodes.size() - 1; i >= 0; i--) {
            Node node = nodes.get(i);
            if (node.children.isEmpty() && node.value == Tristate.UNSET) {
                (i != 0 ? nodes.get(i - 1).children : this.children).remove(node.name);
            }
        }
    }

    private void setOrCreate(String[] parts, Tristate value) {
        Node node = children.computeIfAbsent(parts[0], name -> new Node(name, Tristate.UNSET, new HashMap<>()));
        for (int i = 1; i < parts.length; i++)
            node = node.children.computeIfAbsent(parts[i], name -> new Node(name, Tristate.UNSET, new HashMap<>()));
        node.value = value;
    }

    @NotNull
    public Map<String, Boolean> asMap() {
        Map<String, Boolean> map = new HashMap<>();
        populateMap(map, "", children);
        return map;
    }

    private static void populateMap(Map<String, Boolean> map, String prefix, Map<String, Node> children) {
        for (Map.Entry<String, Node> entry : children.entrySet()) {
            String key = prefix + entry.getKey();
            if (entry.getValue().value != Tristate.UNSET)
                map.put(key, entry.getValue().value == Tristate.TRUE);
            populateMap(map, key + ".", entry.getValue().children);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PermissionTree that)) return false;
        return Objects.equals(children, that.children);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(children);
    }

    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner(", ", "PermissionTree{", "}");
        for (Map.Entry<String, Node> entry : children.entrySet())
            joiner.add(entry.getKey() + "=" + entry.getValue());
        return joiner.toString();
    }

    private static class Node {
        public final String name;
        public Tristate value;
        public final Map<String, Node> children;

        public Node(String name, Tristate value, Map<String, Node> children) {
            this.name = name;
            this.value = value;
            this.children = new HashMap<>(children);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Node node = (Node) o;
            return Objects.equals(name, node.name) && value == node.value && Objects.equals(children, node.children);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, value, children);
        }

        @Override
        public String toString() {
            StringJoiner joiner = new StringJoiner(", ", "Node{", "}");
            joiner.add("value=" + value);
            StringJoiner childrenJoiner = new StringJoiner(", ", "children={", "}");
            for (Map.Entry<String, Node> entry : children.entrySet())
                childrenJoiner.add(entry.getKey() + "=" + entry.getValue());
            joiner.add(childrenJoiner.toString());
            return joiner.toString();
        }
    }
}
