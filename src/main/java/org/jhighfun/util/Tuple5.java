package org.jhighfun.util;

public class Tuple5<F, S, T, FO, FI> {

    public F _1;
    public S _2;
    public T _3;
    public FO _4;
    public FI _5;

    public Tuple5(F first, S second, T third, FO fourth, FI fifth) {
        this._1 = first;
        this._2 = second;
        this._3 = third;
        this._4 = fourth;

        this._5 = fifth;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tuple5)) return false;

        Tuple5 tuple5 = (Tuple5) o;

        if (_1 != null ? !_1.equals(tuple5._1) : tuple5._1 != null) return false;
        if (_2 != null ? !_2.equals(tuple5._2) : tuple5._2 != null) return false;
        if (_3 != null ? !_3.equals(tuple5._3) : tuple5._3 != null) return false;
        if (_4 != null ? !_4.equals(tuple5._4) : tuple5._4 != null) return false;
        if (_5 != null ? !_5.equals(tuple5._5) : tuple5._5 != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = _1 != null ? _1.hashCode() : 0;
        result = 31 * result + (_2 != null ? _2.hashCode() : 0);
        result = 31 * result + (_3 != null ? _3.hashCode() : 0);
        result = 31 * result + (_4 != null ? _4.hashCode() : 0);
        result = 31 * result + (_5 != null ? _5.hashCode() : 0);
        return result;
    }

}