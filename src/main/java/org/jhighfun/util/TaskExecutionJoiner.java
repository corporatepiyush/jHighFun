package org.jhighfun.util;

public interface TaskExecutionJoiner<OUTCOLL, INCOLL> {

    public void join(OUTCOLL taskOutputCollection, INCOLL inputCollection);
}
