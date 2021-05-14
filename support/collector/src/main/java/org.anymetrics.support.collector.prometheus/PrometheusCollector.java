package org.anymetrics.support.collector.prometheus;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Counter;
import io.prometheus.client.Gauge;
import io.prometheus.client.Histogram;
import io.prometheus.client.exporter.PushGateway;
import org.anymetrics.core.collector.Collector;
import org.anymetrics.core.datasource.callback.FetchData;
import org.anymetrics.core.task.PipelineTaskContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


public class PrometheusCollector extends Collector<PrometheusCollectorConfig> {


    private PushGateway pushGateway;

    private SpelExpressionParser parser = new SpelExpressionParser();

    @Override
    public void connect() {
        pushGateway = new PushGateway(getCollectorConfig().getPushGateway());
    }

    @Override
    public void destory() {
    }

    @Override
    public void collect() throws Exception{

        PipelineTaskContext context = PipelineTaskContext.getContext();
        context.getLog().trace("PrometheusCollector start collect");

        PrometheusCollectorConfig collectorConfig = getCollectorConfig();

        if(collectorConfig == null) {
            context.getLog().trace("PrometheusCollector missing config");
            return;
        }
        List<PrometheusMetricsConfig> metricsConfigs = collectorConfig.getMetrics();

        if(metricsConfigs == null) {
            context.getLog().trace("PrometheusCollector missing mertrics");
            return;
        }

        CollectorRegistry registry  = new CollectorRegistry();
        Counter counter = null;
        Gauge gauge = null;
        Histogram histogram = null;

        boolean needPush = false;

        for(PrometheusMetricsConfig metricsConfig : metricsConfigs) {

            String labelNames[] = metricsConfig.getLabelNames() == null ? new String[]{} : metricsConfig.getLabelNames();
            String help = metricsConfig.getHelp() == null ? metricsConfig.getName() : metricsConfig.getHelp();

            String meterType = metricsConfig.getType();
            if(meterType.equalsIgnoreCase("counter")) {
                counter = Counter.build()
                        .name(metricsConfig.getName())
                        .help(help)
                        .labelNames(labelNames)
                        .register(registry);
            }
            else if(meterType.equalsIgnoreCase("gauge")) {
                gauge = Gauge.build().name(metricsConfig.getName())
                        .help(help)
                        .labelNames(labelNames)
                        .register(registry);
            }
            else if(meterType.equalsIgnoreCase("histogram")) {
                histogram = Histogram.build().name(metricsConfig.getName())
                        .help(help)
                        .labelNames(labelNames)
                        .register(registry);
            }
            List<FetchData> fetchDatas = context.getFetchCallbackData();
            for(FetchData fetchData : fetchDatas) {
                String[] labels = getLabels(metricsConfig, fetchData);
                Object value = getValue(metricsConfig, fetchData);
                if(counter != null) {
                    counter.labels(labels).inc(Double.valueOf(String.valueOf(value)));
                    needPush = true;
                }
                if(gauge != null) {
                    gauge.labels(labels).inc(Double.valueOf(String.valueOf(value)));
                    needPush = true;
                }
                if(histogram != null) {
                    histogram.labels(labels).observe(Double.valueOf(String.valueOf(value)));
                    needPush = true;
                }

                // 只保存最后一条
                context.getLog().trace("prometheusCollector source labels : " + Arrays.toString(metricsConfig.getLabels()) + " target labels : " + Arrays.toString(labels));
                context.getLog().trace("prometheusCollector source values : " + metricsConfig.getValue() + " target values : " + value);

            }
        }

        try {
            if(needPush) {
                pushGateway.push(registry, getCollectorConfig().getJob());
                context.getLog().trace("PrometheusCollector collect success");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            registry.clear();
        }
    }

    //{"httpCode":404,"content":"<!DOCTYPE html>,<html lang=\"en\"><head><meta charset=\"utf-8\"><title>Error</title></head><body><pre>Not Found</pre></body></html>"}
    private String[] getLabels(PrometheusMetricsConfig metricsConfig, FetchData fetchData) {
        if(metricsConfig.getLabels() == null) {
            return new String[]{};
        }
        String labels[] = new String[metricsConfig.getLabels().length];
        Map<String, String> fetchDataVariable = fetchData.getFetchDataVariable();

        if(fetchDataVariable != null) {
            for(int i = 0; i < metricsConfig.getLabels().length; i ++) {
                String labelTemp = new String(metricsConfig.getLabels()[i]);
                for(String index : fetchDataVariable.keySet()) {
                    labelTemp = labelTemp.replace(index, fetchDataVariable.get(index));
                }
                labels[i] = labelTemp;
            }
        }
        return labels;
    }

    private Object getValue(PrometheusMetricsConfig metricsConfig, FetchData fetchData) {
        StandardEvaluationContext standardEvaluationContext = fetchData.getELContext();
        if(standardEvaluationContext != null) {
            Expression expression = parser.parseExpression(metricsConfig.getValue());
            Object value = expression.getValue(standardEvaluationContext);
            return value;
        }
        return null;
    }
}
