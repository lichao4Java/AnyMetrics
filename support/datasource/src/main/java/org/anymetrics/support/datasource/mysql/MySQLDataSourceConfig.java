package org.anymetrics.support.datasource.mysql;


import org.anymetrics.core.datasource.DataSourceConfig;

public class MySQLDataSourceConfig extends DataSourceConfig {

    private String jdbcurl;

    private String username;

    private String password;

    private String sql;


    public String getJdbcurl() {
        return jdbcurl;
    }

    public void setJdbcurl(String jdbcurl) {
        this.jdbcurl = jdbcurl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }
}
