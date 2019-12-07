package org.iu.engrcloudcomputing.mapreduce.mapred.dto;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Future;

public class TaskInfo {

    private String input;
    private Future<Integer> future;
    private ConcurrentMap<Boolean, Boolean> isTaskFinished = new ConcurrentHashMap<>();

    public TaskInfo(String input, Future<Integer> future) {
        this.input = input;
        this.future = future;
    }

    public String getInput() {
        return input;
    }

    public Future<Integer> getFuture() {
        return future;
    }

    public boolean getIsTaskFinished() {
        return isTaskFinished.containsKey(true);
    }

    public void setTaskFinished() {
        isTaskFinished.putIfAbsent(true, true);
    }
}
