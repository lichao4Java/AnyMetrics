package org.anymetrics.support.collector.nightingale.simpleSDK;



import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
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
     * nid
     */
    private String nid;

    /**
     * 监控数据的上报周期
     */
    private Integer step;

    /**
     * 当前时间戳
     */
    private Long timestamp;


    protected final ConcurrentMap<List<String>, Child> children = new ConcurrentHashMap();

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

    public void setNid(String nid) {
        this.nid = nid;
    }

    public String getNid() {
        return nid;
    }

    public ConcurrentMap<List<String>, Child> getChildren() {
        return children;
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

        public Child tags(String tags) {
            if(tags == null) {
                throw new IllegalArgumentException("tags can not be null");
            }
            List<String> keys = new ArrayList<>();
            List<String> values = new ArrayList<>();
            String[] kvs = tags.split(",");
            if(kvs.length == 0) {
                throw new IllegalArgumentException("tags format must be key=value,key=value");
            }
            for(String kv : kvs) {
                String[] tag = kv.split("=");
                if(tag.length != 2) {
                    throw new IllegalArgumentException("tag format must be key=value");
                }
                keys.add((tag[0]));
                values.add(tag[1]);
            }
            return getChild(keys, values);
        }

        public Builder step(Integer step) {
            metrics.step = step;
            return this;
        }

        public Child tagMap(LinkedHashMap<String, String> tagsMap) {
            if(tagsMap == null) {
                throw new IllegalArgumentException("tagsMap can not be null");
            }
            List<String> keys = Arrays.asList(tagsMap.keySet().toArray(new String[]{}));
            List<String> values = Arrays.asList(tagsMap.values().toArray(new String[]{}));
            return getChild(keys, values);
        }

        public Builder timestamp(long timestamp) {
            metrics.timestamp = timestamp;
            return this;
        }

        public Builder nid(String nid) {
            metrics.nid = nid;
            return this;
        }

        public Builder register(NightingaleCollectorRegistry registry) {
            registry.register(metrics);
            return this;
        }

        public NightingaleMetrics get() {
            return metrics;
        }

        private Child getChild(List<String> keys, List<String> values) {
            Child child = metrics.getChildren().get(values);
            if(child == null) {
                child = new Child(keys, values);
                metrics.getChildren().putIfAbsent(values, child);
                return child;
            }
            return child;
        }
    }

    public static class Child {

        /**
         * key=value 监控指标的当前值
         */
        private DoubleAdder value = new DoubleAdder();

        /**
         * key1
         * key2
         */
        private List<String> keyNames;

        /**
         * value1
         * value2
         */
        private List<String> keyValues;

        public Child(List<String> keyNames, List<String> keyValues) {
            this.keyNames = keyNames;
            this.keyValues = keyValues;
        }

        public Child incr(Double v) {
            value.add(v);
            return this;
        }

        public double getValue() {
            return value.sum();
        }

        public List<String> getKeyNames() {
            return keyNames;
        }

        public List<String> getKeyValues() {
            return keyValues;
        }
    }

}

