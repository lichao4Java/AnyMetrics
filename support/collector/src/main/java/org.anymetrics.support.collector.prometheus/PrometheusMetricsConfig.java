package org.anymetrics.support.collector.prometheus;


public class PrometheusMetricsConfig {

    public String name;

    public String help;

    private String labelNames[];

    private String labels[];

    // el
    private String value;

    private String type;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHelp() {
        return help;
    }

    public void setHelp(String help) {
        this.help = help;
    }

    public String[] getLabelNames() {
        return labelNames;
    }

    public void setLabelNames(String[] labelNames) {
        this.labelNames = labelNames;
    }

    public String[] getLabels() {
        return labels;
    }

    public void setLabels(String[] labels) {
        this.labels = labels;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
