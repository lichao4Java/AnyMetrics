package org.anymetrics.core.datasource.callback;


import org.anymetrics.core.task.PipelineTaskContext;

public abstract class FetchCallback {

    public abstract void callback(PipelineTaskContext context) throws Exception;

}


