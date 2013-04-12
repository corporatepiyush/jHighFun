package org.jhighfun.util;


public class Tuple4<F, S, T, FO> {

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
}
