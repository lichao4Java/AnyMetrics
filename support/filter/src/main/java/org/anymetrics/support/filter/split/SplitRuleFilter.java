package org.anymetrics.support.filter.split;

import com.alibaba.fastjson.JSON;
import org.anymetrics.core.datasource.callback.FetchData;
import org.anymetrics.core.rule.FiltersConfig;
import org.anymetrics.core.rule.filter.RuleFilter;
import org.anymetrics.core.task.PipelineTaskContext;

import java.util.Iterator;
import java.util.Map;

/**
 * Split Rule Filter
 *
 * fetchData.split(expression)
 */
public class SplitRuleFilter extends RuleFilter {

    public SplitRuleFilter(FiltersConfig filtersConfig, RuleFilter next) {
        super(filtersConfig, next);
    }

    @Override
    public void filter() {

        PipelineTaskContext context = PipelineTaskContext.getContext();

        Iterator<FetchData> fetchCallbackData = context.getFetchCallbackData().iterator();

        while(fetchCallbackData.hasNext()) {
            try {
                FetchData fetchData = fetchCallbackData.next();

                Map<String, String> fetchDataVariable = initFetchDataVariable(fetchData);

                String[] split = String.valueOf(fetchData.getFetchData()).split(filtersConfig.getExpression());

                int k = 1;
                for(String s : split) {
                    fetchDataVariable.put("$" + k, s);
                    k++;
                }

                appendSpELVariable(fetchData);

                if(!fetchDataVariable.isEmpty()) {
                    context.getLog().trace("SplitRuleFilter - variable : " + JSON.toJSONString(fetchDataVariable));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
