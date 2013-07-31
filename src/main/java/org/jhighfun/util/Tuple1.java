package org.jhighfun.util;

/**
 * Object wrapper around List of exact one referential element
 *
 * @author Piyush Katariya
 */

public final class Tuple1<F> {

    public F _1;

    public Tuple1(F first) {
        this._1 = first;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tuple1)) return false;

        Tuple1 tuple1 = (Tuple1) o;

        if (tuple1._1 == _1) return true;

        if (_1 != null ? !_1.equals(tuple1._1) : tuple1._1 != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return _1 != null ? _1.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Tuple1{" +
                "_1=" + _1 +
                '}';
    }
}

