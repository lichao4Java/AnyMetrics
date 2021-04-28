package org.anymetrics.core.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PipelineTaskLog {

    private static final Logger logger = LoggerFactory.getLogger("PipelineTaskLog");

    private String taskName;

    public PipelineTaskLog(String taskName) {
        this.taskName = taskName;
    }

    private static final int MAX_LOGS = 1000;

    private List<String> logs = new ArrayList<>(MAX_LOGS);

    public void trace(String log) {

        log = "[" + taskName + "] - " + log;

        logger.info(log);

        if(logs.size() >= MAX_LOGS) {
            logs.clear();
        }
        logs.add(String.format("%s - %s", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis())), log));
    }

}
