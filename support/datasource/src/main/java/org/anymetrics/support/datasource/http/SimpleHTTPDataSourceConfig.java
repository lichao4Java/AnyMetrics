package org.anymetrics.support.datasource.http;


import org.anymetrics.core.datasource.DataSourceConfig;

public class SimpleHTTPDataSourceConfig extends DataSourceConfig {

    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
