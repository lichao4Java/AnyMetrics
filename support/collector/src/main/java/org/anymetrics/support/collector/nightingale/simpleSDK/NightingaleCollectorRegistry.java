package org.anymetrics.support.collector.nightingale.simpleSDK;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NightingaleCollectorRegistry {

    private List<NightingaleMetrics> registerMetrics = new ArrayList<>();

    private Set<String> nameToSet = new HashSet<>();

    public void register(NightingaleMetrics metrics) {

        if(nameToSet.contains(metrics.getMetric())) {
            throw new IllegalArgumentException("Collector already registered that provides metrics: " + metrics.getMetric());
        }
        registerMetrics.add(metrics);
        nameToSet.add(metrics.getMetric());
    }

    public List<NightingaleMetrics> getRegisterMetrics() {
        return registerMetrics;
    }
}
