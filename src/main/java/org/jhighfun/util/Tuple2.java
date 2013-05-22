package org.jhighfun.util;


public final class Tuple2<F, S> {

    public F _1;
    public S _2;

    public Tuple2(F first, S second) {
        this._1 = first;
        this._2 = second;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tuple2)) return false;

        Tuple2 tuple2 = (Tuple2) o;

        if (_1 != null ? !_1.equals(tuple2._1) : tuple2._1 != null) return false;
        if (_2 != null ? !_2.equals(tuple2._2) : tuple2._2 != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = _1 != null ? _1.hashCode() : 0;
        result = 31 * result + (_2 != null ? _2.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Tuple2{" +
                "_1=" + _1 +
                ", _2=" + _2 +
                '}';
    }
}
