package org.anymetrics.support.collector.nightingale;

import java.util.Map;

public class NightingaleMetricsConfig {

    /**
     * counterType字段表示指标类型，支持GAUGE、COUNTER、SUBTRACT，如果不上报这个字段，默认为GAUGE，如果上报的指标是COUNTER或SUBTRACT类型，需要明确指定，另外，COUNTER和SUBTRACT类型的指标的计算逻辑是在agent模块实现的，所以，不要把COUNTER或SUBTRACT类型的指标直接推给transfer，只能推给agent。
     */
    private String counterType;

    /**
     * 监控指标名称
     */
    private String metric;

    private String nid;

    /**
     * 监控实体
     */
    private String endpoint;

    /**
     * 监控数据的属性标签
     */
    private String tags;

    /**
     * 监控指标的当前值
     * SpEl
     */
    private String value;

    /**
     * tags字段如果不想使用上面的字符串拼接方式，可以使用tagsMap字段
     */
    private Map<String, String> tagsMap;


    public void setNid(String nid) {
        this.nid = nid;
    }

    public String getNid() {
        return nid;
    }

    public String getCounterType() {
        return counterType;
    }

    public void setCounterType(String counterType) {
        this.counterType = counterType;
    }

    public String getMetric() {
        return metric;
    }

    public void setMetric(String metric) {
        this.metric = metric;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Map<String, String> getTagsMap() {
        return tagsMap;
    }

    public void setTagsMap(Map<String, String> tagsMap) {
        this.tagsMap = tagsMap;
    }
}
