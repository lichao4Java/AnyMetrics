package org.anymetrics.support.datasource.http;

import com.alibaba.fastjson.JSONObject;
import org.anymetrics.core.datasource.bounded.ScheduleBoundedDataSource;
import org.anymetrics.core.datasource.callback.FetchCallback;
import org.anymetrics.core.datasource.callback.FetchData;
import org.anymetrics.core.rule.BoundedRuleConfig;
import org.anymetrics.core.task.PipelineTaskContext;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *  简单的 HTTP DataSource
 * @author lichao
 */
public class SimpleHTTPDataSource extends ScheduleBoundedDataSource<SimpleHTTPDataSourceConfig, BoundedRuleConfig> {


    @Override
    public void scheduleFetch(PipelineTaskContext context, FetchCallback dataCallback) {

        String url = getDataSourceConfig(context).getUrl();

        String content = null;
        int httpCode = 0;
        try {
            CloseableHttpResponse closeableHttpResponse = sendHTTP(url);
            httpCode = closeableHttpResponse.getStatusLine().getStatusCode();
            content = EntityUtils.toString(closeableHttpResponse.getEntity());
        } catch (Exception e) {
            e.printStackTrace();
            httpCode = 500;
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("httpCode", httpCode);
        jsonObject.put("content", content);

        String fetchDataStr = jsonObject.toJSONString();
        context.getLog().trace(fetchDataStr);
        List<FetchData> httpDatas = new ArrayList<>();
        FetchData fetchData = new FetchData();
        fetchData.setFetchData(fetchDataStr);
        httpDatas.add(fetchData);

        context.setFetchCallbackData(httpDatas);
        try {
            dataCallback.callback(context);
        } catch (Exception e) {
            e.printStackTrace();
            context.getLog().trace(e.toString());
        }
    }

    private static RequestConfig requestConfig = RequestConfig.custom()
            // 设置连接超时时间(单位毫秒)
            .setConnectTimeout(5000)
            // 设置请求超时时间(单位毫秒)
            .setConnectionRequestTimeout(5000)
            // socket读写超时时间(单位毫秒)
            .setSocketTimeout(5000)
            // 设置是否允许重定向(默认为true)
            .setRedirectsEnabled(true).build();

    private static CloseableHttpResponse sendHTTP(String url) {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();

        HttpGet httpGet = new HttpGet(url);
        httpGet.setConfig(requestConfig);

        CloseableHttpResponse response = null;
        try {
            // 由客户端执行(发送)Get请求
            response = httpClient.execute(httpGet);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                // 释放资源
                if (httpClient != null) {
                    httpClient.close();
                }
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return response;
    }


    @Override
    public void connect() {

    }
}
