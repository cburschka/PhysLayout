/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package util;

import java.util.Objects;

/**
 * Ein ungeordnetes Paar von Schlüsseln für ein Map-Objekt.
 * @author christoph
 * @param <T> Typ der Schlüssel
 */
public class UnorderedPair<T> {
    private final T a, b;
    public UnorderedPair(T a, T b) {
        this.a = a;
        this.b = b;
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
        return Objects.hashCode(this.a) * Objects.hashCode(this.b);
    }
}
