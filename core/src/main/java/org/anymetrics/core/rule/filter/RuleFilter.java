package org.anymetrics.core.rule.filter;


import org.anymetrics.core.datasource.callback.FetchData;
import org.anymetrics.core.rule.FiltersConfig;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.HashMap;
import java.util.Map;

public abstract class RuleFilter {

    protected RuleFilter next;
    protected FiltersConfig filtersConfig;

    public RuleFilter(FiltersConfig filtersConfig, RuleFilter next) {
        this.filtersConfig = filtersConfig;
        this.next = next;
    }

    public void doFilter() throws Exception{
        filter();
        if(next != null) {
            next.doFilter();
        }
    }

    public abstract void filter();

    protected Map<String, String> initFetchDataVariable(FetchData fetchData) {
        Map<String, String> fetchDataVariable = fetchData.getFetchDataVariable() == null ? new HashMap<>() : fetchData.getFetchDataVariable();
        fetchData.setFetchDataVariable(fetchDataVariable);
        return fetchDataVariable;
    }

    protected StandardEvaluationContext initSpELContext(FetchData fetchData) {
        StandardEvaluationContext context = fetchData.getELContext() == null ? new StandardEvaluationContext() : fetchData.getELContext();
        fetchData.setELContext(context);
        return context;
    }

    protected StandardEvaluationContext appendSpELVariable(FetchData fetchData) {
        StandardEvaluationContext context = initSpELContext(fetchData);
        for(String key : fetchData.getFetchDataVariable().keySet()) {
            String value = fetchData.getFetchDataVariable().get(key);
            try {
                context.setVariable(key, Long.parseLong(value));
            } catch (Exception e) {
                context.setVariable(key, value);
            }
        }
        fetchData.setELContext(context);
        return context;
    }
}
