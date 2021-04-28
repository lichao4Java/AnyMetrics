package org.anymetrics.core.datasource.callback;

import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.Map;

public class FetchData {

    private Object fetchData;

    private Map<String, String> fetchDataVariable;

    // Spring EL context
    private StandardEvaluationContext ELContext;

    public Object getFetchData() {
        return fetchData;
    }

    public void setFetchData(Object fetchData) {
        this.fetchData = fetchData;
    }

    public Map<String, String> getFetchDataVariable() {
        return fetchDataVariable;
    }

    public void setFetchDataVariable(Map<String, String> fetchDataVariable) {
        this.fetchDataVariable = fetchDataVariable;
    }

    public StandardEvaluationContext getELContext() {
        return ELContext;
    }

    public void setELContext(StandardEvaluationContext ELContext) {
        this.ELContext = ELContext;
    }
}
