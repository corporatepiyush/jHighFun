package org.jhighfun.util;

public interface Predicate<T> {

    static Predicate TRUE = new Predicate() {
        public boolean evaluate(Object o) {
            return true;
        }
    };

    static Predicate FALSE = new Predicate() {
        public boolean evaluate(Object o) {
            return false;
        }
    };

    public boolean evaluate(T t);

}
