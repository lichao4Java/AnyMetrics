package org.anymetrics.support.collector.loki;

import org.anymetrics.core.collector.CollectorConfig;


public class LokiCollectorConfig extends CollectorConfig {

    private String serverAddr;

    private LokiStreamConfig streams;

    public String getServerAddr() {
        return serverAddr;
    }

    public void setServerAddr(String serverAddr) {
        this.serverAddr = serverAddr;
    }

    public void setStreams(LokiStreamConfig streams) {
        this.streams = streams;
    }

    public LokiStreamConfig getStreams() {
        return streams;
    }
}


