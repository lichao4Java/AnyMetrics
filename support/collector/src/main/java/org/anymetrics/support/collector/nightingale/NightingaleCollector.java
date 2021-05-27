package org.anymetrics.support.collector.nightingale;

import com.alibaba.fastjson.JSON;
import org.anymetrics.core.collector.Collector;
import org.anymetrics.core.datasource.callback.FetchData;
import org.anymetrics.core.rule.BoundedRuleConfig;
import org.anymetrics.core.rule.RuleConfig;
import org.anymetrics.core.rule.UnboundedRuleConfig;
import org.anymetrics.core.task.PipelineTaskContext;
import org.anymetrics.support.collector.nightingale.simpleSDK.NightingaleCollectorRegistry;
import org.anymetrics.support.collector.nightingale.simpleSDK.NightingaleMetrics;
import org.anymetrics.support.collector.nightingale.simpleSDK.NightingalePushGateway;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NightingaleCollector extends Collector<NightingaleCollectorConfig> {

    @Override
    public void connect() {

        RuleConfig rule = PipelineTaskContext.getContext().getTask().getConfigTask().getRule();
        if(rule instanceof BoundedRuleConfig) {
            // 使用调度间隔时间
            this.step = ((BoundedRuleConfig)rule).getInterval();
        }
        else if (rule instanceof UnboundedRuleConfig) {
            // 使用时间窗口时间
            this.step = ((UnboundedRuleConfig)rule).getTimeWindow();
        }

        NightingaleCollectorConfig collectorConfig = getCollectorConfig();

        if(!StringUtils.isEmpty(collectorConfig.getAgentAddr())) {
            this.addr = String.format(agentAddr, collectorConfig.getAgentAddr());
        }
        else if(!StringUtils.isEmpty(collectorConfig.getTransferAddr())) {
            this.addr = String.format(transferAddr, collectorConfig.getTransferAddr());
        }
        else {
            throw new IllegalArgumentException("both agentAddr and transferAddr are empty!");
        }

        this.pushGateway = new NightingalePushGateway(this.addr);

    }

    @Override
    public void destory() {

    }

    private SpelExpressionParser parser = new SpelExpressionParser();

    private final static String agentAddr = "http://%s/v1/push";

    private final static String transferAddr = "http://%s/api/transfer/push";

    /**
     * step为监控数据的上报周期
     */
    private Integer step = 10;

    /**
     * agentAddr or transferAddr
     */
    private String addr;

    private NightingalePushGateway pushGateway;

    public NightingaleCollector() {

    }


    @Override
    public void collect() throws Exception {

        NightingaleCollectorConfig collectorConfig = getCollectorConfig();

        NightingaleCollectorRegistry registry = new NightingaleCollectorRegistry();

        try {
            PipelineTaskContext context = PipelineTaskContext.getContext();

            for (NightingaleMetricsConfig metricsConfig : collectorConfig.getMetrics()) {

                NightingaleMetrics.Builder builder = NightingaleMetrics.build()
                        .counterType(metricsConfig.getCounterType())
                        .metric(metricsConfig.getMetric())
                        .step(step)
                        .timestamp(System.currentTimeMillis() / 1000)
                        .register(registry);

                List<FetchData> fetchDatas = context.getFetchCallbackData();

                for (FetchData fetchData : fetchDatas) {

                    if (metricsConfig.getTagsMap() != null) {
                        Map<String, String> tagsMapTemp = new HashMap<>();
                        for (String key : metricsConfig.getTagsMap().keySet()) {
                            tagsMapTemp.put(key, getLabel(metricsConfig.getTagsMap().get(key), fetchData));
                        }
                        builder.tagMap(tagsMapTemp);
                    }

                    builder.endpoint(metricsConfig.getEndpoint())
                            .tags(getLabel(metricsConfig.getTags(), fetchData))
                            //SpEL
                            .incr(Double.valueOf(String.valueOf(getValue(metricsConfig, fetchData))));

                    context.getLog().trace("nightingaleCollector source metrics : " + JSON.toJSONString(metricsConfig) + " target metrics : " + JSON.toJSONString(builder.get()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            try {
                pushGateway.push(registry);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private String getLabel(String label, FetchData fetchData) {
        if(label == null) {
            return null;
        }
        Map<String, String> fetchDataVariable = fetchData.getFetchDataVariable();
        if(fetchDataVariable != null) {
            for(String index : fetchDataVariable.keySet()) {
                label = label.replace(index, fetchDataVariable.get(index));
            }
        }
        return label;
    }

    private Object getValue(NightingaleMetricsConfig metricsConfig, FetchData fetchData) {
        StandardEvaluationContext standardEvaluationContext = fetchData.getELContext();
        if(standardEvaluationContext != null) {
            Expression expression = parser.parseExpression(metricsConfig.getValue());
            Object value = expression.getValue(standardEvaluationContext);
            return value;
        }
        return metricsConfig.getValue();
    }
}
