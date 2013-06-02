package org.jhighfun.util;


/**
 *  List of exact four referential element
 *
 *  @author Piyush Katariya
 *
 **/

public final class Tuple4<F, S, T, FO> {

    public F _1;
    public S _2;
    public T _3;
    public FO _4;

    public Tuple4(F first, S second, T third, FO fourth) {
        this._1 = first;
        this._2 = second;
        this._3 = third;
        this._4 = fourth;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tuple4)) return false;

        Tuple4 tuple4 = (Tuple4) o;

        if (_1 != null ? !_1.equals(tuple4._1) : tuple4._1 != null) return false;
        if (_2 != null ? !_2.equals(tuple4._2) : tuple4._2 != null) return false;
        if (_3 != null ? !_3.equals(tuple4._3) : tuple4._3 != null) return false;
        if (_4 != null ? !_4.equals(tuple4._4) : tuple4._4 != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = _1 != null ? _1.hashCode() : 0;
        result = 31 * result + (_2 != null ? _2.hashCode() : 0);
        result = 31 * result + (_3 != null ? _3.hashCode() : 0);
        result = 31 * result + (_4 != null ? _4.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Tuple4{" +
                "_1=" + _1 +
                ", _2=" + _2 +
                ", _3=" + _3 +
                ", _4=" + _4 +
                '}';
    }
}
