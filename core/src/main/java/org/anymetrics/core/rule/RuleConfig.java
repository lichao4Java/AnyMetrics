package org.anymetrics.core.rule;


import java.util.List;

public class RuleConfig {

    private List<FiltersConfig> filters;

    private String kind;


    public List<FiltersConfig> getFilters() {
        return filters;
    }

    public void setFilters(List<FiltersConfig> filters) {
        this.filters = filters;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }
}
