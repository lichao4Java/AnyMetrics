package org.anymetrics.support.datasource.kafka;


import org.anymetrics.core.datasource.DataSourceConfig;

public class KafkaDataSourceConfig extends DataSourceConfig {

    private String kafkaAddress;

    private String topic;

    private String groupId;

    private Integer workCorePoolsize;

    private Integer workMaxPoolsize;

    private Long workKeepAliveTime;

    private Integer workCapacity;

    private Integer pollTimeoutMs;

    public void setWorkCapacity(Integer workCapacity) {
        this.workCapacity = workCapacity;
    }

    public Integer getWorkCapacity() {
        return workCapacity;
    }

    public void setPollTimeoutMs(Integer pollTimeoutMs) {
        this.pollTimeoutMs = pollTimeoutMs;
    }

    public Integer getPollTimeoutMs() {
        return pollTimeoutMs;
    }

    public String getKafkaAddress() {
        return kafkaAddress;
    }

    public void setKafkaAddress(String kafkaAddress) {
        this.kafkaAddress = kafkaAddress;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public Integer getWorkCorePoolsize() {
        return workCorePoolsize;
    }

    public void setWorkCorePoolsize(Integer workCorePoolsize) {
        this.workCorePoolsize = workCorePoolsize;
    }

    public Integer getWorkMaxPoolsize() {
        return workMaxPoolsize;
    }

    public void setWorkMaxPoolsize(Integer workMaxPoolsize) {
        this.workMaxPoolsize = workMaxPoolsize;
    }

    public Long getWorkKeepAliveTime() {
        return workKeepAliveTime;
    }

    public void setWorkKeepAliveTime(Long workKeepAliveTime) {
        this.workKeepAliveTime = workKeepAliveTime;
    }
}
