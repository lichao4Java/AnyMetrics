package org.anymetrics.core.task;


import org.anymetrics.core.datasource.callback.FetchData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PipelineTaskContext {

    /**
     * 当前任务Fetch的数据
     */
    private List<FetchData> fetchCallbackData = new ArrayList<>();

    /**
     * 当前任务
     */
    private PipelineTask task;

    /**
     * 存储每个task的运行时详细状态
     */
    private PipelineTaskLog log;


    /**
     * 存储
     *
     * 任务ID ：Context
     */
    private static Map<String, PipelineTaskContext> taskContextMap = new HashMap<>();


    public PipelineTaskContext(PipelineTask task) {
        this.task = task;
        this.log = new PipelineTaskLog(task.getConfigTask().getName());
        setContext(this);
    }

    static ThreadLocal<PipelineTaskContext> contextThreadLocal = new ThreadLocal<>();

    public static PipelineTaskContext getContext() {
       return contextThreadLocal.get();
    }

    public static PipelineTaskContext setContext(PipelineTaskContext context) {
        contextThreadLocal.set(context);
        putTaskContextMap();
        return context;
    }

    public static void removeContext() {
        contextThreadLocal.remove();
    }


    private static void putTaskContextMap() {
        PipelineTaskContext context = getContext();
        taskContextMap.put(context.getTask().getConfigTask().getId(), getContext());
    }

    public static void removeTaskContextMap(String taskId) {
        taskContextMap.remove(taskId);
    }


    public static PipelineTaskContext getTaskContext(String taskId) {
        return taskContextMap.get(taskId);
    }

    public List<FetchData> getFetchCallbackData() {
        return fetchCallbackData;
    }

    public void setFetchCallbackData(List<FetchData> fetchCallbackData) {
        this.fetchCallbackData = fetchCallbackData;
    }

    public PipelineTask getTask() {
        return task;
    }

    public void setTask(PipelineTask task) {
        this.task = task;
    }

    public PipelineTaskLog getLog() {
        return log;
    }

    public void setLog(PipelineTaskLog log) {
        this.log = log;
    }
}
