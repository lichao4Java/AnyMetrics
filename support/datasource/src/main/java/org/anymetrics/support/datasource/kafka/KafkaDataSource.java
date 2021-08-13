package org.anymetrics.support.datasource.kafka;


import org.anymetrics.core.datasource.unbounded.TimeWindowUnBoundedDataSource;
import org.anymetrics.core.rule.UnboundedRuleConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * Kafka 数据源
 */
public class KafkaDataSource extends TimeWindowUnBoundedDataSource<KafkaDataSourceConfig, UnboundedRuleConfig> {

    private KafkaConsumer<String, String> consumer;

    /**
     * kafka consumer poll interval
     */
    private int pollTimeoutMs = 2000;

    @Override
    public void connect() {

        super.connect();

        logger.info("connect kafka");

        KafkaDataSourceConfig dataSourceConfig = getDataSourceConfig();

        pollTimeoutMs = dataSourceConfig.getPollTimeoutMs() == null ? pollTimeoutMs : dataSourceConfig.getPollTimeoutMs();

        if(dataSourceConfig.getWorkCorePoolsize() != null) {
            super.setWorkCorePoolSize(dataSourceConfig.getWorkCorePoolsize());
        }
        if(dataSourceConfig.getWorkMaxPoolsize() != null) {
            super.setWorkMaxPoolSize(dataSourceConfig.getWorkMaxPoolsize());
        }
        if(dataSourceConfig.getWorkKeepAliveTime() != null) {
            super.setWorkKeepAliveTime(dataSourceConfig.getWorkKeepAliveTime());
        }
        if(dataSourceConfig.getWorkCapacity() != null) {
            super.setWorkCapacity(dataSourceConfig.getWorkCapacity());
        }

        Properties props = new Properties();
        props.setProperty("bootstrap.servers", dataSourceConfig.getKafkaAddress());
        props.setProperty("group.id", dataSourceConfig.getGroupId());
        props.setProperty("enable.auto.commit", "true");
        props.setProperty("auto.commit.interval.ms", "1000");
        props.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        consumer = new KafkaConsumer<>(props);

        consumer.subscribe(Arrays.asList(dataSourceConfig.getTopic()));

    }

    @Override
    public void destory() {
        super.destory();

        logger.info("destory kafka");

        if(consumer != null) {
            consumer.close();
        }

    }

    @Override
    public List<Object> poll() {

        List<Object> fetchData = new ArrayList<>();
        ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(pollTimeoutMs));
        for (ConsumerRecord<String, String> record : records) {
            fetchData.add(record.value());
        }

        return fetchData;
    }


}
