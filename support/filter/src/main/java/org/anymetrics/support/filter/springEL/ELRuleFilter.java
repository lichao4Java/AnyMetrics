package org.anymetrics.support.filter.springEL;


import org.anymetrics.core.datasource.callback.FetchData;
import org.anymetrics.core.rule.FiltersConfig;
import org.anymetrics.core.rule.filter.RuleFilter;
import org.anymetrics.core.task.PipelineTaskContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.Iterator;

/**
 * Expression Language Filter
 */
public class ELRuleFilter extends RuleFilter {

    private SpelExpressionParser parser = new SpelExpressionParser();

    public ELRuleFilter(FiltersConfig filtersConfig, RuleFilter next) {
        super(filtersConfig, next);
    }

    @Override
    public void filter() {

        PipelineTaskContext context = PipelineTaskContext.getContext();

        Iterator<FetchData> fetchCallbackData = context.getFetchCallbackData().iterator();

        while(fetchCallbackData.hasNext()) {
            try {
                FetchData next = fetchCallbackData.next();
                StandardEvaluationContext SpELContext = initSpELContext(next);
                Boolean filtered = parser.parseExpression(filtersConfig.getExpression()).getValue(SpELContext, Boolean.class);
                // do not match
                if(!filtered) {
                    fetchCallbackData.remove();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
