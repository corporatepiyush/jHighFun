package org.jhighfun.internal;


public class Config {

    public static int getParallelDegree() {
        return Runtime.getRuntime().availableProcessors();
    }

}
