package org.anymetrics.core.rule;


public class UnboundedRuleConfig extends RuleConfig {

    private Integer timeWindow;


    public Integer getTimeWindow() {
        return timeWindow;
    }

    public void setTimeWindow(Integer timeWindow) {
        this.timeWindow = timeWindow;
    }
}
