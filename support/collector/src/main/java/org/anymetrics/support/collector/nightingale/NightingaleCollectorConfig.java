package org.anymetrics.support.collector.nightingale;

import org.anymetrics.core.collector.CollectorConfig;

import java.util.List;

public class NightingaleCollectorConfig extends CollectorConfig {


    private String agentAddr;

    private String transferAddr;

    private List<NightingaleMetricsConfig> metrics;

    public String getAgentAddr() {
        return agentAddr;
    }

    public void setAgentAddr(String agentAddr) {
        this.agentAddr = agentAddr;
    }

    public String getTransferAddr() {
        return transferAddr;
    }

    public void setTransferAddr(String transferAddr) {
        this.transferAddr = transferAddr;
    }

    public List<NightingaleMetricsConfig> getMetrics() {
        return metrics;
    }

    public void setMetrics(List<NightingaleMetricsConfig> metrics) {
        this.metrics = metrics;
    }
}
