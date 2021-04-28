package org.anymetrics.support.filter.regular;


import com.alibaba.fastjson.JSON;
import org.anymetrics.core.datasource.callback.FetchData;
import org.anymetrics.core.rule.FiltersConfig;
import org.anymetrics.core.rule.filter.RuleFilter;
import org.anymetrics.core.task.PipelineTaskContext;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *  Regular Filter
 */
public class RegularRuleFilter extends RuleFilter {

    public RegularRuleFilter(FiltersConfig filtersConfig, RuleFilter next) {
        super(filtersConfig, next);
    }

    @Override
    public void filter() {

        PipelineTaskContext context = PipelineTaskContext.getContext();

        Pattern p = Pattern.compile(filtersConfig.getExpression());
        Iterator<FetchData> fetchCallbackData = context.getFetchCallbackData().iterator();

        while(fetchCallbackData.hasNext()) {
            FetchData fetchData = fetchCallbackData.next();
            Matcher matcher = p.matcher(String.valueOf(fetchData.getFetchData()));
            // matched
            if(matcher.find()) {
                // matched variable
                // $1 $2 $3 ...
                Map<String, String> fetchDataVariable = fetchData.getFetchDataVariable() == null ? new HashMap<>() : fetchData.getFetchDataVariable();
                int k = 1;
                while (k <= matcher.groupCount()) {
                    String group = matcher.group(k);
                    fetchDataVariable.put("$" + k, group);
                    k++;
                }
                fetchData.setFetchDataVariable(fetchDataVariable);
                fetchData.setELContext(initELVariable(fetchData, fetchDataVariable));

                if(!fetchDataVariable.isEmpty()) {
                    context.getLog().trace("RegularRuleFilter - variable : " + JSON.toJSONString(fetchDataVariable));
                }
            } else {
                // do not match
                fetchCallbackData.remove();
            }
        }
    }

    private StandardEvaluationContext initELVariable(FetchData fetchData, Map<String, String> fetchDataVariable) {
        StandardEvaluationContext context = fetchData.getELContext() == null ? new StandardEvaluationContext() : fetchData.getELContext();
        for(String index : fetchDataVariable.keySet()) {
            String value = fetchDataVariable.get(index);
            try {
                context.setVariable(index, Long.parseLong(value));
            } catch (Exception e) {
                context.setVariable(index, value);
            }
        }
        return context;
    }

}
