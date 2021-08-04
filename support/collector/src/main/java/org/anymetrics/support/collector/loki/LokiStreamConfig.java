package org.anymetrics.support.collector.loki;

import java.util.Map;

public class LokiStreamConfig {

    private Map<String, Object> stream;

    /**
     * nullable
     */
    private String value;

    public void setStream(Map<String, Object> stream) {
        this.stream = stream;
    }

    public Map<String, Object> getStream() {
        return stream;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
