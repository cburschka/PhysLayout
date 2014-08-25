package util;

import java.util.Objects;

/**
 * An unordered pair of keys for a Map.
 * @author Christoph Burschka &lt;christoph@burschka.de&gt;
 * @param <T> Type of both keys.
 */
public class UnorderedPair<T> {
    private final T a, b;
    public UnorderedPair(T a, T b) {
        this.a = a;
        this.b = b;
    }

    public T getA() {
        return a;
    }

    public T getB() {
        return b;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof UnorderedPair)) return false;
        UnorderedPair x = (UnorderedPair) obj;
        return (a.equals(x.a) && b.equals(x.b)) || (a.equals(x.b) && b.equals(x.a));
    }

    @Override
    public int hashCode() {
        // Use arithmetic product to combine hashes.
        // TODO: Might not be optimal.
        return Objects.hashCode(this.a) * Objects.hashCode(this.b);
    }
}
