package org.anymetrics.core.datasource.unbounded;


import org.anymetrics.core.datasource.DataSource;
import org.anymetrics.core.datasource.DataSourceConfig;
import org.anymetrics.core.rule.UnboundedRuleConfig;

public abstract class UnboundedDataSource<T extends DataSourceConfig, E extends UnboundedRuleConfig> extends DataSource<T, E> {


}
