package org.jhighfun.util;


/**
 * Executable business unit which contains group of statement to execute upon input passed.
 *
 * @author Piyush Katariya
 */

public interface Task<I> {

    public void execute(I input);

}
