package org.anymetrics.core.task;


import com.alibaba.fastjson.JSON;
import org.anymetrics.core.collector.Collector;
import org.anymetrics.core.collector.CollectorConfig;
import org.anymetrics.core.datasource.DataSource;
import org.anymetrics.core.datasource.DataSourceConfig;
import org.anymetrics.core.rule.FiltersConfig;
import org.anymetrics.core.rule.RuleConfig;
import org.anymetrics.core.rule.filter.RuleFilter;
import org.anymetrics.core.util.SPIUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class PipelineTaskManage {

    private static final Logger logger = LoggerFactory.getLogger("PipelineTaskManage");

    private static List<PipelineTask> tasks;


    static {

//        //从 ConfigTask 中加载出所有任务
        tasks = new ArrayList<>();
        List<ConfigTask> configTasks = ConfigTaskManage.loadConfigTasks();
        logger.info("configTasks : {}", JSON.toJSONString(configTasks));
        for (ConfigTask configTask : configTasks) {
            tasks.add(toPipelineTask(configTask));
        }

        //graceful shutdown
        Runtime.getRuntime().addShutdownHook(new Thread(){

            @Override
            public void run() {
                PipelineTaskManage.stop();
            }
        });
    }


    /**
     *  重新加载任务
     * @param newTaskJsonConfig
     */
    public static PipelineTask reloadTask(String newTaskJsonConfig, boolean start) {

        // 1 load new ConfigTask
        ConfigTask newConfigTask = ConfigTaskManage.loadConfigTask(newTaskJsonConfig);

        int taskCount = tasks.size();
        // 2 stop and remove task
        Iterator<PipelineTask> iterator = tasks.iterator();
        while(iterator.hasNext()) {
            PipelineTask task = iterator.next();
            if(task.getConfigTask().getId().equalsIgnoreCase(newConfigTask.getId())) {
                task.stop();
                iterator.remove();
            }
        }
        if(taskCount == tasks.size()) {
            throw new IllegalArgumentException("taskId not found : " + newConfigTask.getId());
        }
        // 3 convert to PipelineTask
        PipelineTask newPipelineTask = toPipelineTask(newConfigTask);

        tasks.add(newPipelineTask);

        if(start) {
            // 4 start task
            newPipelineTask.start();
        }

        return newPipelineTask;

    }

    /**
     * 创建新任务
     * @param newTaskJsonConfig
     */
    public static PipelineTask loadNewTask(String newTaskJsonConfig, boolean start) {

        // 1 load new ConfigTask
        ConfigTask newConfigTask = ConfigTaskManage.loadConfigTask(newTaskJsonConfig);

        // 防止重复加载
        Iterator<PipelineTask> iterator = tasks.iterator();
        while(iterator.hasNext()) {
            PipelineTask task = iterator.next();
            if(task.getConfigTask().getId().equalsIgnoreCase(newConfigTask.getId())) {
                return task;
            }
        }

        // 2 convert to PipelineTask
        PipelineTask newPipelineTask = toPipelineTask(newConfigTask);

        tasks.add(newPipelineTask);

        if(start) {
            // 3 start task
            newPipelineTask.start();
        }
        return newPipelineTask;
    }

    /**
     * 删除任务
     * @param taskId
     */
    public static void removeTask(String taskId) {
        Iterator<PipelineTask> iterator = tasks.iterator();
        while(iterator.hasNext()) {
            PipelineTask task = iterator.next();
            if(task.getConfigTask().getId().equalsIgnoreCase(taskId)) {
                task.stop();
                iterator.remove();
            }
        }
    }

    public static PipelineTask getTask(String taskId) {
        Iterator<PipelineTask> iterator = tasks.iterator();
        while(iterator.hasNext()) {
            PipelineTask task = iterator.next();
            if(task.getConfigTask().getId().equalsIgnoreCase(taskId)) {
                return task;
            }
        }
        return null;
    }

    /**
     * 启动所有任务
     */
    public static void start(boolean deplay) {
        for(PipelineTask task : tasks) {
            if(deplay) {
                try {
                    //延时 1 到 10秒启动
                    Thread.sleep((long)(new Random().nextInt(9000)) + 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            task.start();
        }
    }


    /**
     * 停止所有任务
     */
    public static void stop() {
        for(PipelineTask task : tasks) {
            task.stop();
        }
    }

    public List<PipelineTask> getAllTasks() {
        return Collections.unmodifiableList(tasks);
    }


    private static DataSource loadDataSource(DataSourceConfig dataSourceConfig) {

        Class dataSourceClass = SPIUtil.loadConfigClass(DataSource.class, dataSourceConfig.getType());
        try {
            return (DataSource) dataSourceClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static RuleFilter loadRuleFilterChain(RuleConfig ruleConfig) {
        List<FiltersConfig> filtersConfigs = ruleConfig.getFilters();
        if(filtersConfigs == null) {
            return null;
        }
        // 内置 LogFilter
        RuleFilter next = null;
        for(int i = filtersConfigs.size() - 1; i >= 0; i --) {
            Class ruleFilterClass = SPIUtil.loadConfigClass(RuleFilter.class, filtersConfigs.get(i).getType());
            try {
                next = (RuleFilter) ruleFilterClass.getConstructor(FiltersConfig.class, RuleFilter.class).newInstance(filtersConfigs.get(i), next);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return next;
    }

    private static Collector loadCollector(CollectorConfig collectorConfig) {
        Class collectorClass = SPIUtil.loadConfigClass(Collector.class, collectorConfig.getType());
        try {
            return (Collector) collectorClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static PipelineTask toPipelineTask(ConfigTask configTask) {
        PipelineTask pipelineTask = new PipelineTask();
        pipelineTask.setConfigTask(configTask);
        pipelineTask.setDataSource(loadDataSource(configTask.getDataSource()));
        pipelineTask.setFilter(loadRuleFilterChain(configTask.getRule()));
        pipelineTask.setCollector(loadCollector(configTask.getCollector()));
        return pipelineTask;
    }
}
