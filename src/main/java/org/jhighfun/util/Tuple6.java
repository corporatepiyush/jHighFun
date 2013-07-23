package org.jhighfun.util;

/**
 * Object wrapper around List of exact six referential element
 *
 * @author Piyush Katariya
 */

public final class Tuple6<F, S, T, FO, FI, SI> {

    public F _1;
    public S _2;
    public T _3;
    public FO _4;
    public FI _5;
    public SI _6;

    public Tuple6(F first, S second, T third, FO fourth, FI fifth, SI six) {
        this._1 = first;
        this._2 = second;
        this._3 = third;
        this._4 = fourth;
        this._5 = fifth;
        this._6 = six;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tuple6)) return false;

        Tuple6 tuple6 = (Tuple6) o;

        if (_1 != null ? !_1.equals(tuple6._1) : tuple6._1 != null) return false;
        if (_2 != null ? !_2.equals(tuple6._2) : tuple6._2 != null) return false;
        if (_3 != null ? !_3.equals(tuple6._3) : tuple6._3 != null) return false;
        if (_4 != null ? !_4.equals(tuple6._4) : tuple6._4 != null) return false;
        if (_5 != null ? !_5.equals(tuple6._5) : tuple6._5 != null) return false;
        if (_6 != null ? !_6.equals(tuple6._6) : tuple6._6 != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = _1 != null ? _1.hashCode() : 0;
        result = 31 * result + (_2 != null ? _2.hashCode() : 0);
        result = 31 * result + (_3 != null ? _3.hashCode() : 0);
        result = 31 * result + (_4 != null ? _4.hashCode() : 0);
        result = 31 * result + (_5 != null ? _5.hashCode() : 0);
        result = 31 * result + (_6 != null ? _6.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Tuple6{" +
                "_1=" + _1 +
                ", _2=" + _2 +
                ", _3=" + _3 +
                ", _4=" + _4 +
                ", _5=" + _5 +
                ", _6=" + _6 +
                '}';
    }
}