package org.anymetrics.support.collector.prometheus;


import org.anymetrics.core.collector.CollectorConfig;

import java.util.List;

public class PrometheusCollectorConfig extends CollectorConfig {

    private String pushGateway;

    private List<PrometheusMetricsConfig> metrics;

    private String job;

    public String getPushGateway() {
        return pushGateway;
    }

    public void setPushGateway(String pushGateway) {
        this.pushGateway = pushGateway;
    }

    public List<PrometheusMetricsConfig> getMetrics() {
        return metrics;
    }

    public void setMetrics(List<PrometheusMetricsConfig> metrics) {
        this.metrics = metrics;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }
}
