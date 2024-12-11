package io.github.pandier.atomi.internal.permission;

import io.github.pandier.atomi.Tristate;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.regex.Pattern;

/**
 * A tree structure for storing permissions, where each node inherits the permission value of its parent.
 * Nodes are represented by permission strings, where nodes are seperated by the '.' character and are case-insensitive.
 * The root node is represented by the permission string '*'.
 */
@ApiStatus.Internal
public class PermissionTree {
    private static final Pattern NODE_SPLIT = Pattern.compile("\\.");

    private final Node root = new Node(null);

    public PermissionTree() {
    }

    public PermissionTree(@NotNull Map<String, Boolean> permissions) {
        for (Map.Entry<String, Boolean> entry : permissions.entrySet()) {
            set(entry.getKey(), Tristate.of(entry.getValue()));
        }
    }

    /**
     * Splits the given permission string into a permission path.
     *
     * @param permission the permission string
     * @return the permission path
     */
    private static String[] split(String permission) {
        return NODE_SPLIT.split(permission.toLowerCase(Locale.ROOT));
    }

    /**
     * Resolves the given permission path into a list of nodes,
     * the first node being the root node.
     *
     * @param parts the permission path
     * @return the list of nodes or null if the path cannot be traversed (e.g. due to missing nodes)
     */
    @Nullable
    private List<Node> resolve(String[] parts) {
        List<Node> nodes = new ArrayList<>();
        Node node = this.root;
        nodes.add(node);
        for (String part : parts) {
            node = node.children.get(part);
            if (node == null)
                return null;
            nodes.add(node);
        }
        return nodes;
    }

    /**
     * Unsets the value of the given permission path and cleans up empty and unset nodes.
     *
     * @param parts the permission path
     */
    private void unset(String[] parts) {
        List<Node> nodes = resolve(parts);
        if (nodes == null) return;
        nodes.getLast().value = Tristate.UNSET;

        // Cleanup empty and unset nodes
        for (int i = nodes.size() - 1; i > 0; i--) {
            Node node = nodes.get(i);
            if (node.children.isEmpty() && node.value == Tristate.UNSET) {
                nodes.get(i - 1).children.remove(node.name);
            } else {
                break;
            }
        }
    }

    /**
     * Creates necessary nodes for the given permission path if they don't exist and sets the value.
     *
     * @param parts the permission path
     * @param value the value to set
     */
    private void setOrCreate(String[] parts, Tristate value) {
        Node node = this.root;
        for (String part : parts)
            node = node.children.computeIfAbsent(part, Node::new);
        node.value = value;
    }

    /**
     * Gets the value of the given permission string.
     * If the permission string is '*', the value of the root node is returned.
     *
     * @param permission the permission string
     * @return the value
     */
    @NotNull
    public synchronized Tristate get(@NotNull String permission) {
        if (permission.equals("*"))
            return getRoot();

        String[] parts = split(permission);

        Node node = this.root;
        Tristate value = this.root.value;

        int i = 0;
        do {
            node = node.children.get(parts[i]);
            if (node == null)
                break;
            if (node.value != Tristate.UNSET)
                value = node.value;
        } while (++i < parts.length);

        return value;
    }

    /**
     * Sets the value of the given permission string.
     * If the permission string is '*', the value of the root node is set.
     *
     * @param permission the permission string
     * @param value the value to set
     */
    public synchronized void set(@NotNull String permission, @NotNull Tristate value) {
        if (permission.equals("*")) {
            setRoot(value);
            return;
        }

        String[] parts = split(permission);

        if (value == Tristate.UNSET) {
            unset(parts);
        } else {
            setOrCreate(parts, value);
        }
    }

    /**
     * Sets the value of the root node.
     *
     * @param value the value to set
     */
    public synchronized void setRoot(@NotNull Tristate value) {
        this.root.value = value;
    }

    /**
     * Returns the value of the root node.
     *
     * @return the value
     */
    @NotNull
    public synchronized Tristate getRoot() {
        return this.root.value;
    }

    /**
     * Sets all the permissions in the given map.
     *
     * @param permissions the map of permissions
     */
    public void setAll(@NotNull Map<String, Boolean> permissions) {
        for (Map.Entry<String, Boolean> entry : permissions.entrySet()) {
            set(entry.getKey(), Tristate.of(entry.getValue()));
        }
    }

    /**
     * Clears all nodes and unsets all values.
     */
    public synchronized void clear() {
        this.root.value = Tristate.UNSET;
        this.root.children.clear();
    }

    /**
     * Returns this permission tree represented by a map,
     * where the key is the permission string and the value is the permission value.
     * The root node is represented by the '*' string.
     *
     * @return the map of permissions
     */
    @NotNull
    public synchronized Map<String, Boolean> asMap() {
        Map<String, Boolean> map = new HashMap<>();
        if (this.root.value != Tristate.UNSET)
            map.put("*", this.root.value.asBoolean());
        populateMap(map, "", this.root.children);
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
        return Objects.equals(root, that.root);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(root);
    }

    @Override
    public String toString() {
        return "PermissionTree{root=" + root + "}";
    }

    private static class Node {
        public final String name;
        public Tristate value;
        public final Map<String, Node> children = new HashMap<>();

        public Node(String name) {
            this(name, Tristate.UNSET);
        }

        public Node(String name, Tristate value) {
            this.name = name;
            this.value = value;
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
