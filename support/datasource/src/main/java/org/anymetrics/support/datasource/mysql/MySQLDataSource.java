package org.anymetrics.support.datasource.mysql;

import com.alibaba.fastjson.JSON;
import org.anymetrics.core.datasource.bounded.ScheduleBoundedDataSource;
import org.anymetrics.core.datasource.callback.FetchCallback;
import org.anymetrics.core.datasource.callback.FetchData;
import org.anymetrics.core.rule.BoundedRuleConfig;
import org.anymetrics.core.task.PipelineTaskContext;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MySQLDataSource extends ScheduleBoundedDataSource<MySQLDataSourceConfig, BoundedRuleConfig> {

    private Connection conn;

    @Override
    public void connect() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            conn = DriverManager.getConnection(getDataSourceConfig().getJdbcurl(), getDataSourceConfig().getUsername(), getDataSourceConfig().getPassword());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void destory() {
        super.destory();

        if(conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void scheduleFetch(PipelineTaskContext context, FetchCallback dataCallback) {

        PreparedStatement preparedStatement = null;

        List<FetchData> fetchDatas = new ArrayList<>();
        try {
            preparedStatement = conn.prepareStatement(getDataSourceConfig(context).getSql());
            ResultSet resultSet = preparedStatement.executeQuery();
            ResultSetMetaData md = resultSet.getMetaData();
            int columnCount = md.getColumnCount();
            while (resultSet.next()) {
                Map<String, Object> rowData = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    rowData.put(md.getColumnName(i), resultSet.getObject(i));
                }

                FetchData fetchData = new FetchData();
                fetchData.setFetchData(JSON.toJSONString(rowData));
                fetchDatas.add(fetchData);

            }
            context.setFetchCallbackData(fetchDatas);
            dataCallback.callback(context);
        } catch (Exception e) {
            e.printStackTrace();
            context.getLog().trace(e.toString());
        } finally {
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

    }

}
