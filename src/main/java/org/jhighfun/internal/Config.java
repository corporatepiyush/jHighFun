package org.jhighfun.internal;

/**
 * Configuration parameters for jHighFun library
 *
 * @author Piyush Katariya
 */

public class Config {

    public static int getParallelDegree() {
        return Runtime.getRuntime().availableProcessors();
    }

}
