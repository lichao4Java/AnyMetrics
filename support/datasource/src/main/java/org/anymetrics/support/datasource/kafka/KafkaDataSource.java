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

    // millis
    private static int pollInterval = 100;


    @Override
    public void connect() {

        super.connect();

        logger.info("connect kafka");

        KafkaDataSourceConfig dataSourceConfig = getDataSourceConfig();

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
        ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(pollInterval));
        for (ConsumerRecord<String, String> record : records) {
            fetchData.add(record.value());
        }

        return fetchData;
    }


}
