package org.anymetrics.support.collector.nightingale.simpleSDK;



import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.DoubleAdder;

public class NightingaleMetrics {

    /**
     * counterType字段表示指标类型，支持GAUGE、COUNTER、SUBTRACT，如果不上报这个字段，默认为GAUGE，如果上报的指标是COUNTER或SUBTRACT类型，需要明确指定，另外，COUNTER和SUBTRACT类型的指标的计算逻辑是在agent模块实现的，所以，不要把COUNTER或SUBTRACT类型的指标直接推给transfer，只能推给agent。
     */
    private String counterType;

    /**
     * 监控指标名称
     */
    private String metric;

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
     */
    private DoubleAdder value = new DoubleAdder();

    /**
     * tags字段如果不想使用上面的字符串拼接方式，可以使用tagsMap字段
     */
    private Map<String, String> tagsMap;

    /**
     * 监控数据的上报周期
     */
    private Integer step;

    /**
     * 当前时间戳
     */
    private Long timestamp;

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

    public Double getValue() {
        return value.doubleValue();
    }

    public Map<String, String> getTagsMap() {
        return tagsMap;
    }

    public void setTagsMap(Map<String, String> tagsMap) {
        this.tagsMap = tagsMap;
    }

    public Integer getStep() {
        return step;
    }

    public void setStep(Integer step) {
        this.step = step;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public static Builder build() {
        return new Builder();
    }

    public static class Builder {

        NightingaleMetrics metrics;

        public Builder() {
            this.metrics = new NightingaleMetrics();
        }

        public Builder counterType(String counterType) {
            metrics.counterType = counterType;
            return this;
        }

        public Builder metric(String metric) {
            metrics.metric = metric;
            return this;
        }

        public Builder endpoint(String endpoint) {
            metrics.endpoint = endpoint;
            return this;
        }

        public Builder tags(String tags) {
            metrics.tags = tags;
            return this;
        }

        public Builder step(Integer step) {
            metrics.step = step;
            return this;
        }

        public Builder incr(Double value) {
            metrics.value.add(value);
            return this;
        }

        public Builder tagMap(String key, String value) {
            metrics.tagsMap.put(key, value);
            return this;
        }

        public Builder tagMap(Map<String, String> tagsMap) {
            metrics.tagsMap = tagsMap;
            return this;
        }

        public Builder timestamp(long timestamp) {
            metrics.timestamp = timestamp;
            return this;
        }

        public Builder register(NightingaleCollectorRegistry registry) {
            registry.register(metrics);
            return this;
        }

        public NightingaleMetrics get() {
            return metrics;
        }


    }

}

