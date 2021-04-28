package org.anymetrics.support.datasource.kafka;


import org.anymetrics.core.datasource.DataSourceConfig;

public class KafkaDataSourceConfig extends DataSourceConfig {

    private String kafkaAddress;

    private String topic;

    private String groupId;

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
}
