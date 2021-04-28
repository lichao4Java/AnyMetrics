package org.anymetrics.core.rule.filter;


import org.anymetrics.core.rule.FiltersConfig;

public abstract class RuleFilter {

    protected RuleFilter next;
    protected FiltersConfig filtersConfig;

    public RuleFilter(FiltersConfig filtersConfig, RuleFilter next) {
        this.filtersConfig = filtersConfig;
        this.next = next;
    }

    public void doFilter() throws Exception{
        filter();
        if(next != null) {
            next.doFilter();
        }
    }

    public abstract void filter();

}
