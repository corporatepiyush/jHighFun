package org.jhighfun.util;

/**
 * Object wrapper around List of exact three referential element
 *
 * @author Piyush Katariya
 */

public final class Tuple3<F, S, T> extends Tuple {

    public F _1;
    public S _2;
    public T _3;

    public Tuple3(F first, S second, T third) {
        this._1 = first;
        this._2 = second;
        this._3 = third;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tuple3)) return false;

        Tuple3 tuple3 = (Tuple3) o;

        if (tuple3._1 == _1 && tuple3._2 == _2 && tuple3._3 == _3) return true;

        if (_1 != null ? !_1.equals(tuple3._1) : tuple3._1 != null) return false;
        if (_2 != null ? !_2.equals(tuple3._2) : tuple3._2 != null) return false;
        if (_3 != null ? !_3.equals(tuple3._3) : tuple3._3 != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = _1 != null ? _1.hashCode() : 0;
        result = 31 * result + (_2 != null ? _2.hashCode() : 0);
        result = 31 * result + (_3 != null ? _3.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Tuple3{" +
                "_1=" + _1 +
                ", _2=" + _2 +
                ", _3=" + _3 +
                '}';
    }
}
