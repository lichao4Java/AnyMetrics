package org.anymetrics.core.task;

import org.anymetrics.core.collector.CollectorConfig;
import org.anymetrics.core.datasource.DataSourceConfig;
import org.anymetrics.core.rule.RuleConfig;

public class ConfigTask {

    private String id;

    private String name;

    private DataSourceConfig dataSource;

    private RuleConfig rule;

    private CollectorConfig collector;

    private String iframe;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DataSourceConfig getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSourceConfig dataSource) {
        this.dataSource = dataSource;
    }

    public RuleConfig getRule() {
        return rule;
    }

    public void setRule(RuleConfig rule) {
        this.rule = rule;
    }

    public CollectorConfig getCollector() {
        return collector;
    }

    public void setCollector(CollectorConfig collector) {
        this.collector = collector;
    }

    public String getIframe() {
        return iframe;
    }

    public void setIframe(String iframe) {
        this.iframe = iframe;
    }
}
