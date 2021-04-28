package org.anymetrics.core.datasource.bounded;


import org.anymetrics.core.datasource.DataSource;
import org.anymetrics.core.datasource.DataSourceConfig;
import org.anymetrics.core.rule.BoundedRuleConfig;

public abstract class BoundedDataSource<T extends DataSourceConfig, E extends BoundedRuleConfig> extends DataSource<T, E> {

}
