package org.jhighfun.util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

public final class TaskExecutionHandle {

    private final List<Future> futureList;
    private final List<Object> outList;
    private final long startTime;

    public TaskExecutionHandle(List<Future> futureList, long startTime) {
        this.futureList = futureList;
        this.startTime = startTime;
        outList = new ArrayList<Object>();
    }

    public void waitForAllTasks() {

        for (Future future : futureList) {
            try {
                outList.add(future.get());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public List<Object> getTaskOutput() {
        waitForAllTasks();
        return outList;
    }

    public void waitForNTasks(int taskCount) {

        if (taskCount < 1) {
            return;
        } else {
            for (int i = 0; i < taskCount; i++) {
                try {
                    futureList.get(i).get();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public int totalTasksCompleted() {
        int count = 0;
        for (Future future : futureList) {
            if (future.isDone()) {
                count++;
            }
        }
        return count;
    }
}
